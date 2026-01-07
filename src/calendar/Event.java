/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

/**
 *
 * @author pravi
 */

//EVENT CREATION!!

//import the LocalDateTime class
import java.time.LocalDateTime;//store start and end date/time
import java.time.format.DateTimeFormatter;
public class Event {
    private int eventId;                 // unique identifier for each event
    private String title;                // title of the event
    private String description;          // details and explanation of the event
    private LocalDateTime startDateTime; // exact date/time when the event starts
    private LocalDateTime endDateTime;   // exact date/time when the event ends
    private static int counter = 0;      // auto-increment counter

    // Full constructor: used when we already know the eventId
    public Event(int eventId, String title, String description,
                 LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    // Constructor without eventId (auto-generated)
    public Event(String title, String description,
                 LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.eventId = ++counter;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    // Getters & Setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    // Nicely formatted string output
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Event{" +
                "eventId=" + eventId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime.format(fmt) +
                ", endDateTime=" + endDateTime.format(fmt) +
                '}';
    }

    // CSV export (using | delimiter for safety)
    public String toCSV() {
        return eventId + "|" + title + "|" + description + "|" +
               startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "|" +
               endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // CSV import
    public static Event fromCSV(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 5) {
                throw new IllegalArgumentException("Invalid CSV line: " + line);
            }
            int id = Integer.parseInt(parts[0].trim());
            String title = parts[1].trim();
            String description = parts[2].trim();
            LocalDateTime start = LocalDateTime.parse(parts[3].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(parts[4].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            Event e = new Event(id, title, description, start, end);
            if (id > counter) {
                counter = id; // keep counter in sync with highest ID
            }
            return e;
        } catch (Exception ex) {
            System.err.println("Error parsing CSV line: " + line);
            ex.printStackTrace();
            return null;
        }
    }
}

