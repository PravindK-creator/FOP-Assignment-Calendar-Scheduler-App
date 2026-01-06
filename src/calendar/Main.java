/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        boolean running = true;

        while (running) {
            System.out.println("\nCalendar Scheduler Menu:");
            System.out.println("1. Create Event");
            System.out.println("2. Update Event");
            System.out.println("3. Delete Event");
            System.out.println("4. View All Events");
            System.out.println("5. Get Next Event");
            System.out.println("6. Trigger Reminders");
            System.out.println("7. Clear CSV File");
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
                    clearCSV();
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

        System.out.print("New Title: ");
        String title = scanner.nextLine();
        System.out.print("New Description: ");
        String description = scanner.nextLine();

        Event original = manager.getEventById(id);
        if (original == null) {
            System.out.println("Event not found.");
            return;
        }

        Event updated = new Event(title, description, original.getStartDateTime(), original.getEndDateTime());
        updated.setEventId(id);
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

    private static void clearCSV() {
        manager.clearEventsFile();
        System.out.println("CSV file cleared.");
    }
}

