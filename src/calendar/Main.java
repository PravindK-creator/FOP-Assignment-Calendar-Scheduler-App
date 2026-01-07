/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author pravi
 */
public class Main {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final EventManager manager = new EventManager();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // Load existing events and recurring rules at startup
        manager.loadEvents("event.csv");
        manager.loadRecurringEvents("recurring.csv");
        CalendarUtils.loadAdditionalData();
        boolean running = true;

        while (running) {
            System.out.println("\nCalendar Scheduler Menu:");
            System.out.println("1. Create Event");
            System.out.println("2. Update Event");
            System.out.println("3. Delete Event");
            System.out.println("4. View All Events");
            System.out.println("5. Get Next Event");
            System.out.println("6. Search Events ");
            System.out.println("7. Trigger Reminders");
            System.out.println("8. Clear Event CSV File");
            System.out.println("9. Add Recurring Event");
            System.out.println("10. Expand Recurring Event");
            System.out.println("11. Clear Recurring CSV File");
            System.out.println("12. View Calendar (GUI Mode)");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createEvent();
                    break;
                case "2":
                    updateEvent();
                    break;
                case "3":
                    deleteEvent();
                    break;
                case "4":
                    viewAllEvents();
                    break;
                case "5":
                    getNextEvent();
                    break;
                case "6":
                    System.out.print("Enter date to search (yyyy-MM-dd): ");
                    String searchDate = scanner.nextLine();
                    CalendarUtils.searchByDate(searchDate);
                    break;
                case "7":
                    manager.scheduleReminders();
                    break;
                case "8":
                    clearEventCSV();
                    break;
                case "9":
                    addRecurringEvent();
                    break;
                case "10":
                    expandRecurringEvent();
                    break;
                case "11":
                    clearRecurringCSV();
                    break;
                case "12":
                    openCalendarGUI();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        System.out.println("Exiting Calendar Scheduler. Goodbye!");
        System.exit(0);
    }

    // å�¯åŠ¨ GUI çš„æ–¹æ³•
    private static void openCalendarGUI() {
        System.out.println("Launching Calendar GUI...");

        List<Event> allEvents = manager.getAllEvents();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            new CalendarGUI(allEvents).setVisible(true);
        });
    }

    private static void createEvent() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Start (yyyy-MM-dd HH:mm): ");
        
        try {
            LocalDateTime start = LocalDateTime.parse(scanner.nextLine(), formatter);
            System.out.print("End (yyyy-MM-dd HH:mm): ");
            LocalDateTime end = LocalDateTime.parse(scanner.nextLine(), formatter);

            // æ£€æŸ¥å†²çª�
            if (CalendarUtils.hasConflict(start, end)) {
                System.out.println("Creation aborted due to conflict.");
                return;
            }

            System.out.print("Location: ");
            String loc = scanner.nextLine();
            System.out.print("Attendees: ");
            String att = scanner.nextLine();
            System.out.print("Category: ");
            String cat = scanner.nextLine();

            Event e = new Event(title, description, start, end);
            manager.createEvent(e);
            
            CalendarUtils.saveAdditionalData(e.getEventId(), loc, att, cat);
            System.out.println("âœ… Event and additional details saved!");
            System.out.println("Event created: " + e);

        } catch (Exception ex) {
            System.out.println("Invalid date format or error: " + ex.getMessage());
        }
    }

    private static void updateEvent() {
        try {
            System.out.print("Event ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            Event original = manager.getEventById(id);
            if (original == null) {
                System.out.println("Event not found.");
                return;
            }

            System.out.print("New Title: ");
            String title = scanner.nextLine();
            System.out.print("New Description: ");
            String description = scanner.nextLine();

            Event updated = new Event(id, title, description,
                    original.getStartDateTime(), original.getEndDateTime());
            manager.updateEvent(id, updated);
            System.out.println("Event updated.");
        } catch (NumberFormatException ex) {
            System.out.println("Invalid ID.");
        }
    }

    private static void deleteEvent() {
        try {
            System.out.print("Event ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());
            manager.deleteEvent(id);
            System.out.println("Event deleted (if it existed).");
        } catch (NumberFormatException ex) {
            System.out.println("Invalid ID.");
        }
    }

    private static void viewAllEvents() {
        List<Event> events = manager.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found.");
        } else {
            System.out.println("All Events:");
            for (Event e : events) {
                System.out.println(e);
            }
        }
    }

    private static void getNextEvent() {
        Event next = manager.getNextEvent();
        if (next != null) {
            System.out.println("Next Event: " + next);
        } else {
            System.out.println("No upcoming events.");
        }
    }

    private static void clearEventCSV() {
        manager.clearEventsFile();
        System.out.println("Event CSV file cleared.");
    }

    private static void addRecurringEvent() {
        try {
            System.out.print("Event ID to make recurring: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Interval (e.g. 1d, 1w, 1m): ");
            String interval = scanner.nextLine();
            System.out.print("Number of times (0 if using end date): ");
            int times = Integer.parseInt(scanner.nextLine());

            System.out.print("End date (yyyy-MM-dd HH:mm) or leave blank: ");
            String endInput = scanner.nextLine();
            LocalDateTime endDate = endInput.isEmpty() ? null : LocalDateTime.parse(endInput, formatter);

            manager.addRecurringEvent(id, interval, times, endDate);
        } catch (Exception ex) {
            System.out.println("Invalid input.");
        }
    }

    private static void expandRecurringEvent() {
        manager.expandRecurringEvents();
        System.out.println("Recurring events expanded into actual event instances");
    }

    private static void clearRecurringCSV() {
        manager.clearRecurringFile();
        System.out.println("Recurring CSV file cleared.");
    }

}
