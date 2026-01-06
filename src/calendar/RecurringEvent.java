/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

/**
 *
 * @author pravi
 */
import java.time.LocalDateTime;

public class RecurringEvent {
    private int eventId;                // ID of the base event
    private String recurrentInterval;   // e.g. "1d", "1w", "2w", "1m"
    private int recurrentTimes;         // number of repetitions (0 = unlimited until endDate)
    private LocalDateTime recurrentEndDate; // optional end date

    // Constructor
    public RecurringEvent(int eventId, String interval, int times, LocalDateTime endDate) {
        this.eventId = eventId;
        this.recurrentInterval = interval;
        this.recurrentTimes = times;
        this.recurrentEndDate = endDate;
    }

    // Getters
    public int getEventId() { 
        return eventId; }
    public String getRecurrentInterval() { 
        return recurrentInterval; }
    public int getRecurrentTimes() { 
        return recurrentTimes; }
    public LocalDateTime getRecurrentEndDate() { 
        return recurrentEndDate; }

    // Convert to CSV string
    public String toCSV() {
        return eventId + "," + recurrentInterval + "," + recurrentTimes + "," +
               (recurrentEndDate != null ? recurrentEndDate.toString() : "");
    }

    // Parse from CSV string
    public static RecurringEvent fromCSV(String line) {
        try {
            String[] parts = line.split(",");
            int eventId = Integer.parseInt(parts[0]);
            String interval = parts[1];
            int times = Integer.parseInt(parts[2]);
            LocalDateTime endDate = parts[3].isEmpty() ? null : LocalDateTime.parse(parts[3]);
            return new RecurringEvent(eventId, interval, times, endDate);
        } catch (Exception e) {
            System.err.println("Error parsing recurring event: " + e.getMessage());
            return null;
        }
    }
}

