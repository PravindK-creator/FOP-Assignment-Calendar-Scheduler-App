/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
            System.out.println("8. Add Recurring Event");
            System.out.println("9. Expand Recurring Event");
            System.out.println("10. View Calendar (CLI Mode)");
            System.out.println("11. View Calendar (GUI Mode)");
            System.out.println("12. Show Weekly Statistics ");
            System.out.println("13. Backup/Restore System ");
            System.out.println("14. Clear Event CSV File");
            System.out.println("15. Clear Recurring CSV File");
            System.out.println("16. Clear Additional CSV File");
            System.out.println("17. Clear Calendar Backup File");
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
                    addRecurringEvent();
                    break;
                case "9":
                    expandRecurringEvent();
                    break;
                case "10":
                    openCalendarCLI();
                    break;
                case "11":
                    openCalendarGUI();
                    break;
                case "12":
                    new Statistics().showStatistics(manager.getAllEvents());
                    break;
                case "13":
                    System.out.println("1. Create Backup\n2. Restore Backup");
                    String subChoice = scanner.nextLine();
                    Backup backupTool = new Backup();
                    if (subChoice.equals("1")) {
                        backupTool.createBackup("calendar_backup.txt");
                    } else {
                        backupTool.restoreBackup("calendar_backup.txt");
                        manager.loadEvents("event.csv"); 
                    }
                    break;
                case "14":
                    clearEventCSV();
                    break;
                case "15":
                    clearRecurringCSV();
                    break;
                case "16":
                    clearAdditionalCSV();
                    break;
                case "17":
                    clearBackupFile();
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
    
    //open calendar GUI method
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
    
    //create event method
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
            manager.createEvent(e, loc, att, cat);
            
            System.out.println("Event and additional details saved!");
            System.out.println("Event created: " + e);

        } catch (Exception ex) {
            System.out.println("Invalid date format or error: " + ex.getMessage());
        }
    }
    
    //update event method
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
            System.out.print("New Start (yyyy-MM-dd HH:mm): ");
            String s=scanner.nextLine();
            System.out.print("New End (yyyy-MM-dd HH:mm): ");
            String e=scanner.nextLine();
            System.out.print("New Location: ");
            String loc = scanner.nextLine();
            System.out.print("New Attendees: ");
            String att = scanner.nextLine();
            System.out.print("New Category: ");
            String cat = scanner.nextLine();
            
            LocalDateTime startDateTime=LocalDateTime.parse(s,formatter);
            LocalDateTime endDateTime=LocalDateTime.parse(e,formatter);

            Event updated = new Event(id, title, description, startDateTime, endDateTime);
            manager.updateEvent(id, updated,loc,att,cat);
           
            System.out.println("Event updated.");
        } catch (NumberFormatException ex) {
            System.out.println("Invalid ID.");
        }
    }
    
    //delete event method
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
    
    //view all events method
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
    
    //get next event method
    private static void getNextEvent() {
        Event next = manager.getNextEvent();
        if (next != null) {
            System.out.println("Next Event: " + next);
        } else {
            System.out.println("No upcoming events.");
        }
    }

    //clear event.csv file method
    private static void clearEventCSV() {
        manager.clearEventsFile();
        System.out.println("Event CSV file cleared.");
    }

    //add recurring event method based on eventID
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

    //expand recurring event
    private static void expandRecurringEvent() {
        manager.expandRecurringEvents();
        System.out.println("Recurring events expanded into actual event instances");
    }

    //clear recurring.csv file
    private static void clearRecurringCSV() {
        manager.clearRecurringFile();
        System.out.println("Recurring CSV file cleared.");
    }

    //open calendar CLI format
    private static void openCalendarCLI(){
        List<Event> events=manager.getAllEvents();
        CalendarCLI calendarCLI=new CalendarCLI(events);
        System.out.print("Monthly view or weekly view? (Enter M or monthly, W for weekly): ");
        String view=scanner.nextLine();
        
        if(view.equalsIgnoreCase("M")){
            try{
                System.out.print("Year: ");
                String year=scanner.nextLine().trim();
                System.out.print("Month: ");
                String month=scanner.nextLine().trim();
        
                int yearInt=Integer.parseInt(year);
                int monthInt=Integer.parseInt(month);
        
                calendarCLI.printMonthView(yearInt, monthInt);
                calendarCLI.printEventsForMonth(yearInt, monthInt);
            }
            catch(NumberFormatException e){
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
        
        if(view.equalsIgnoreCase("W")){
            System.out.print("Enter start date of week (yyyy-MM-dd): ");
            String date=scanner.nextLine();
            LocalDate startDate=LocalDate.parse(date);
            LocalDate sunday=startDate.minusDays(startDate.getDayOfWeek().getValue() % 7);
            calendarCLI.printWeeklyListView(sunday);
        }
    }
    
    //method to clear additional.csv file
    private static void clearAdditionalCSV(){
        manager.clearAdditionalFile();
        System.out.println("Additional CSV file cleared.");
    }
    
    //method to clear calendar backup file
    private static void clearBackupFile(){
        manager.clearBackupFile();
        System.out.println("Calendar Backup file cleared.");
    }
}

