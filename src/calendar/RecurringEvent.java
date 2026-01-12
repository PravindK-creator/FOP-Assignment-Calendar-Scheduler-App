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
import java.time.format.DateTimeFormatter;

public class RecurringEvent {
    private int eventId;                 // ID of the base event
    private String recurrentInterval;    // e.g. "1d", "1w", "1m"
    private int recurrentTimes;          // number of occurrences (0 if using end date)
    private LocalDateTime recurrentEndDate; // optional end date

    // Constructor
    public RecurringEvent(int eventId, String interval, int times, LocalDateTime endDate) {
        this.eventId = eventId;
        this.recurrentInterval = interval;
        this.recurrentTimes = times;
        this.recurrentEndDate = endDate;
    }

    // Getters & Setters
    public int getEventId() { 
        return eventId; }
    public void setEventId(int eventId) { 
        this.eventId = eventId; }

    public String getRecurrentInterval() { 
        return recurrentInterval; }
    public void setRecurrentInterval(String recurrentInterval) { 
        this.recurrentInterval = recurrentInterval; }

    public int getRecurrentTimes() { 
        return recurrentTimes; }
    public void setRecurrentTimes(int recurrentTimes) { 
        this.recurrentTimes = recurrentTimes; }

    public LocalDateTime getRecurrentEndDate() { 
        return recurrentEndDate; }
    public void setRecurrentEndDate(LocalDateTime recurrentEndDate) { 
        this.recurrentEndDate = recurrentEndDate; }

    // Nicely formatted string output
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "RecurringEvent{" +"eventId=" + eventId +", interval='" + recurrentInterval + '\'' +", times=" + recurrentTimes +", endDate=" + (recurrentEndDate != null ? recurrentEndDate.format(fmt) : "none") +'}';
    }

    // CSV export (using | delimiter for safety)
    public String toCSV() {
        return eventId + "|" + recurrentInterval + "|" + recurrentTimes + "|" +(recurrentEndDate != null ? recurrentEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
    }

    // CSV import
    public static RecurringEvent fromCSV(String line) {
        try {
            String[] parts = line.contains("|") ? line.split("\\|") : line.split(",");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid CSV line: " + line);
            }
            int id = Integer.parseInt(parts[0].trim());
            String interval = parts[1].trim();
            int times = Integer.parseInt(parts[2].trim());
            LocalDateTime endDate = null;
            if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                endDate = LocalDateTime.parse(parts[3].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            return new RecurringEvent(id, interval, times, endDate);
        } catch (Exception ex) {
            System.err.println("Error parsing recurring CSV line: " + line);
            ex.printStackTrace();
            return null;
        }
    }
}

