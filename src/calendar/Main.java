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

/**
 *
 * @author pravi
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EventManager manager = new EventManager();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // ✅ Load existing events and recurring rules at startup
        manager.loadEvents("event.csv");
        manager.loadRecurringEvents("recurring.csv");

        boolean running = true;

        while (running) {
            System.out.println("\nCalendar Scheduler Menu:");
            System.out.println("1. Create Event");
            System.out.println("2. Update Event");
            System.out.println("3. Delete Event");
            System.out.println("4. View All Events");
            System.out.println("5. Get Next Event");
            System.out.println("6. Trigger Reminders");
            System.out.println("7. Clear Event CSV File");
            System.out.println("8. Add Recurring Event");
            System.out.println("9. Expand Recurring Event");
            System.out.println("10. Clear Recurring CSV File");
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
                    manager.scheduleReminders();
                    break;
                case "7":
                    clearEventCSV();
                    break;
                case "8":
                    addRecurringEvent();
                    break;
                case "9":
                    expandRecurringEvent();
                    break;
                case "10":
                    clearRecurringCSV();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        System.out.println("Exiting Calendar Scheduler. Goodbye!");
    }

    private static void createEvent() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Start (yyyy-MM-dd HH:mm): ");
        LocalDateTime start = LocalDateTime.parse(scanner.nextLine(), formatter);
        System.out.print("End (yyyy-MM-dd HH:mm): ");
        LocalDateTime end = LocalDateTime.parse(scanner.nextLine(), formatter);

        Event e = new Event(title, description, start, end);
        manager.createEvent(e);
        System.out.println("Event created: " + e);
    }

    private static void updateEvent() {
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
    }

    private static void deleteEvent() {
        System.out.print("Event ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        manager.deleteEvent(id);
        System.out.println("Event deleted (if it existed).");
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
