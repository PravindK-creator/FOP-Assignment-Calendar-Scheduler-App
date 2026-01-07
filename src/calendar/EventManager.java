/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

/**
 *
 * @author pravi
 */
import java.io.*;//import java I/O for reading/writing files
import java.time.LocalDateTime;//import LocalDateTime class for handling date/time
import java.util.*;//import utility classes like list and ArrayList
public class EventManager {
    private static final String EVENT_FILE = "event.csv";
    private static final String RECURRING_FILE = "recurring.csv";

    private List<Event> events = new ArrayList<>();
    private List<RecurringEvent> recurringEvents = new ArrayList<>();

    // ================= Event Management =================

    // Create and save a new event
    public void createEvent(Event event) {
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
    public void updateEvent(int eventId, Event updatedEvent) {
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

    // Clear events file
    public void clearEventsFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENT_FILE))) {
            pw.print("");
        } catch (IOException e) {
            System.err.println("Error clearing events file: " + e.getMessage());
        }
        events.clear();  
    }
    
    //clear recurring file
    public void clearRecurringFile(){
        try (PrintWriter pw = new PrintWriter(new FileWriter(RECURRING_FILE))) {
            pw.print("");
        } catch (IOException e) {
            System.err.println("Error clearing events file: " + e.getMessage());
        }
        recurringEvents.clear();
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

// Load recurring events from CSV file
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

// Save recurring events to CSV file
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

// Add a new recurring event rule
public void addRecurringEvent(int eventId, String interval, int times, LocalDateTime endDate) {
    // Check if rule already exists
    for (RecurringEvent re : recurringEvents) {
        if (re.getEventId() == eventId &&
            re.getRecurrentInterval().equals(interval) &&
            re.getRecurrentTimes() == times &&
            Objects.equals(re.getRecurrentEndDate(), endDate)) {
            System.out.println("Recurring rule already exists for event " + eventId);
            return;
        }
    }

    RecurringEvent re = new RecurringEvent(eventId, interval, times, endDate);
    recurringEvents.add(re);
    saveRecurringEvents("recurring.csv");
    System.out.println("Recurring rule added for event " + eventId);
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
        List<Event> existingEvents = getAllEvents(); // ✅ cache once

        while (true) {
            if (re.getRecurrentEndDate() != null && !dateTime.isBefore(re.getRecurrentEndDate())) break;
            if (re.getRecurrentTimes() > 0 && count >= re.getRecurrentTimes()) break;

            if (count > 0) {
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
                    createEvent(copy);
                    existingEvents.add(copy); //update cache
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
    switch (interval) {
        case "1d": return dateTime.plusDays(1);
        case "1w": return dateTime.plusWeeks(1);
        case "2w": return dateTime.plusWeeks(2);
        case "1m": return dateTime.plusMonths(1);
        default:
            System.err.println("Unknown interval: " + interval);
            return dateTime;
    }
}


}

