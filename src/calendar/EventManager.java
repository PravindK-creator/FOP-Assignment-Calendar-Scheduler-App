/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class EventManager {
    private static final String EVENT_FILE = "event.csv";
    private static final String RECURRING_FILE = "recurring.csv";
    private static final String ADDITIONAL_FILE = "additional.csv";
    private static final String BACKUP_FILE = "calendar_backup.txt";

    private List<Event> events = new ArrayList<>();
    private List<RecurringEvent> recurringEvents = new ArrayList<>();
    private List<Backup> calendarBackup = new ArrayList<>();

    // ================= Event Management =================

    // Create and save a new event (with extra info)
    public void createEvent(Event event, String loc, String att, String cat) {
        int newId = getNextEventId();
        event.setEventId(newId);

        if (event.getEndDateTime().isBefore(event.getStartDateTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENT_FILE, true))) {
            bw.write(event.toCSV());
            bw.newLine();
            System.out.println("Event created successfully: " + event);
        } catch (IOException e) {
            System.err.println("Error writing event: " + e.getMessage());
        }

        events.add(event);

        CalendarUtils.saveAdditionalData(event.getEventId(), loc, att, cat);
    }

    // Get next event ID
    private int getNextEventId() {
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                int id = Integer.parseInt(parts[0]);
                if (id > maxId) maxId = id;
            }
        } catch (IOException e) {
            // file may not exist yet
        }
        return maxId + 1;
    }

    // Update event
    public void updateEvent(int eventId, Event updatedEvent, String newLoc, String newAtt, String newCat) {
        List<Event> temp = new ArrayList<>();
        if (updatedEvent.getEndDateTime().isBefore(updatedEvent.getStartDateTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Event e = Event.fromCSV(line);
                if (e != null) {
                    if (e.getEventId() == eventId) {
                        temp.add(updatedEvent);
                    } else {
                        temp.add(e);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading events: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENT_FILE))) {
            for (Event e : temp) {
                bw.write(e.toCSV());
                bw.newLine();
            }
            System.out.println("Event updated successfully: " + updatedEvent);
        } catch (IOException e) {
            System.err.println("Error writing events: " + e.getMessage());
        }

        this.events = temp;

        //  Sync additional.csv
        CalendarUtils.updateAdditionalData(eventId, newLoc, newAtt, newCat);
    }

    // Delete event
    public void deleteEvent(int eventId) {
        List<Event> temp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Event e = Event.fromCSV(line);
                if (e != null && e.getEventId() != eventId) {
                    temp.add(e);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading events: " + e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENT_FILE))) {
            for (Event e : temp) {
                bw.write(e.toCSV());
                bw.newLine();
            }
            System.out.println("Event deleted successfully: ID " + eventId);
        } catch (IOException e) {
            System.err.println("Error writing events: " + e.getMessage());
        }

        this.events = temp;

        //Remove from additional.csv
        CalendarUtils.removeAdditionalData(eventId);
    }

    // Get all events
    public List<Event> getAllEvents() {
        List<Event> temp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Event e = Event.fromCSV(line);
                if (e != null) temp.add(e);
            }
        } catch (IOException e) {
            System.err.println("Error reading events: " + e.getMessage());
        }
        this.events = temp;
        return temp;
    }

    // Get next upcoming event
    public Event getNextEvent() {
        List<Event> all = getAllEvents();
        LocalDateTime now = LocalDateTime.now();
        Event nextEvent = null;

        for (Event e : all) {
            if (e.getStartDateTime().isAfter(now)) {
                if (nextEvent == null || e.getStartDateTime().isBefore(nextEvent.getStartDateTime())) {
                    nextEvent = e;
                }
            }
        }
        return nextEvent;
    }

    // Reminders
    public void scheduleReminders() {
        scheduleReminders(30);
    }

    public void scheduleReminders(int minutesBefore) {
        List<Event> all = getAllEvents();
        LocalDateTime now = LocalDateTime.now();

        for (Event e : all) {
            LocalDateTime reminderTime = e.getStartDateTime().minusMinutes(minutesBefore);
            if (reminderTime.isAfter(now)) {
                System.out.println("Reminder: Event '" + e.getTitle() +
                        "' starts at " + e.getStartDateTime() +
                        " (" + minutesBefore + " minutes left!)");
            }
        }
    }

    // Clear files
    public void clearEventsFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.print("");
        } catch (IOException e) {
            System.err.println("Error clearing events file: " + e.getMessage());
        }
        events.clear();
    }

    public void clearRecurringFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RECURRING_FILE))) {
            pw.print("");
        } catch (IOException e) {
            System.err.println("Error clearing recurring file: " + e.getMessage());
        }
        recurringEvents.clear();
    }

    public void clearAdditionalFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ADDITIONAL_FILE))) {
            pw.print("");
        } catch (IOException e) {
            System.err.println("Error clearing additional file: " + e.getMessage());
        }
    }

    public void clearBackupFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BACKUP_FILE))) {
            pw.print("");
        } catch (IOException e) {
            System.err.println("Error clearing backup file: " + e.getMessage());
        }
        calendarBackup.clear();
    }

    // Get event by ID
    public Event getEventById(int id) {
        List<Event> all = getAllEvents();
        for (Event e : all) {
            if (e.getEventId() == id) return e;
        }
        return null;
    }

    // Load events from file
    public void loadEvents(String filename) {
        events.clear();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    Event e = Event.fromCSV(line);
                    if (e != null) events.add(e);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
    }

    // ================= Recurring Events =================

    public void loadRecurringEvents(String filename) {
        recurringEvents.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                RecurringEvent re = RecurringEvent.fromCSV(line);
                if (re != null) recurringEvents.add(re);
            }
        } catch (IOException e) {
            System.err.println("Error loading recurring events: " + e.getMessage());
        }
    }

    public void saveRecurringEvents(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (RecurringEvent re : recurringEvents) {
                writer.write(re.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving recurring events: " + e.getMessage());
        }
    }
    

    // Validate interval format (e.g., "1d", "2w", "3m", "1y")
private boolean isValidInterval(String interval) {
    return interval != null && interval.matches("\\d+[dwmy]");
}

// Add a new recurring event rule
public void addRecurringEvent(int eventId, String interval, int times, LocalDateTime endDate) {
    if (!isValidInterval(interval)) {
        System.out.println("Invalid interval format. Use e.g. 1d, 2w, 3m, 1y.");
        return;
    }

    if (times < 0) {
        System.out.println("Recurrent times must be non-negative.");
        return;
    }

    Event base = getEventById(eventId);
    if (base == null) {
        System.out.println("Base event not found for ID: " + eventId);
        return;
    }

    for (RecurringEvent re : recurringEvents) {
        boolean sameEnd = Objects.equals(re.getRecurrentEndDate(), endDate);
        if (re.getEventId() == eventId &&
            re.getRecurrentInterval().equals(interval) &&
            re.getRecurrentTimes() == times &&
            sameEnd) {
            System.out.println("Recurring rule already exists for event " + eventId);
            return;
        }
    }

    RecurringEvent re = new RecurringEvent(eventId, interval, times, endDate);
    recurringEvents.add(re);

    saveRecurringEvents(RECURRING_FILE);

    System.out.println("Recurring rule added for event " + eventId +
            " [interval=" + interval +
            ", times=" + times +
            ", endDate=" + (endDate != null ? endDate : "none") + "]");
}

    // Expand recurring events into actual Event instances
public void expandRecurringEvents() {
    for (RecurringEvent re : recurringEvents) {
        Event base = getEventById(re.getEventId());
        if (base == null) {
            System.err.println("Base event not found for ID: " + re.getEventId());
            continue;
        }

        LocalDateTime dateTime = base.getStartDateTime();
        long durationMinutes = java.time.Duration.between(
                base.getStartDateTime(), base.getEndDateTime()).toMinutes();

        int count = 0;
        List<Event> existingEvents = getAllEvents(); // cache once

        while (true) {
            // Stop conditions
            if (re.getRecurrentEndDate() != null && !dateTime.isBefore(re.getRecurrentEndDate())) break;
            if (re.getRecurrentTimes() > 0 && count >= re.getRecurrentTimes()) break;

            if (count > 0) { // skip base event itself
                boolean exists = false;
                for (Event e : existingEvents) {
                    if (e.getTitle().equals(base.getTitle()) &&
                        e.getStartDateTime().equals(dateTime)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    Event copy = new Event(
                        base.getTitle(),
                        base.getDescription(),
                        dateTime,
                        dateTime.plusMinutes(durationMinutes)
                    );
                    // Create new event
                    createEvent(copy, "DefaultLoc", "DefaultAtt", "DefaultCat");

                    // Copy extra info from base event into additional.csv
                    CalendarUtils.copyAdditionalData(base.getEventId(), copy.getEventId());

                    existingEvents.add(copy); // update cache
                } else {
                    System.out.println("Skipped duplicate event on: " + dateTime);
                }
            }

            count++;
            dateTime = incrementDateTime(dateTime, re.getRecurrentInterval());
        }
    }
}

    // Helper method to increment LocalDateTime based on recurrence interval
private LocalDateTime incrementDateTime(LocalDateTime dateTime, String interval) {
    try {
        // Extract number and unit (e.g. "3d", "2w", "6m", "1y")
        int value = Integer.parseInt(interval.substring(0, interval.length() - 1));
        char unit = interval.charAt(interval.length() - 1);

        switch (unit) {
            case 'd': return dateTime.plusDays(value);
            case 'w': return dateTime.plusWeeks(value);
            case 'm': return dateTime.plusMonths(value);
            case 'y': return dateTime.plusYears(value);
            default:
                System.err.println("Unknown interval: " + interval);
                return dateTime;
        }
    } catch (Exception e) {
        System.err.println("Invalid interval format: " + interval);
        return dateTime;
    }
}

}


   

