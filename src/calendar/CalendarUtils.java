/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CalendarUtils {
    private static final String ADDITIONAL_FILE = "additional.csv";
    private static final String EVENT_FILE = "event.csv";
    private static Map<Integer, String> additionalDataMap = new HashMap<>();

    public static void loadAdditionalData() {
        additionalDataMap.clear();
        File file = new File(ADDITIONAL_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[0].trim());
                    String loc = parts[1].trim();
                    String att = parts[2].trim();
                    String cat = parts[3].trim();
     
                    String info = String.format(" [Loc: %s | Att: %s | Cat: %s]", loc, att, cat);
                    additionalDataMap.put(id, info);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading additional data.");
        }
    }

    public static void saveAdditionalData(int eventId, String loc, String att, String cat) {
        String info = String.format(" [Loc: %s | Att: %s | Cat: %s]", loc, att, cat);
        additionalDataMap.put(eventId, info);
        updateAdditionalFile(); // always rewrite cleanly
    }
    
    private static void updateAdditionalFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADDITIONAL_FILE))) {
            bw.write("eventId,location,attendees,category");
            bw.newLine();
            for (Map.Entry<Integer, String> entry : additionalDataMap.entrySet()) {
                int id = entry.getKey();
                String info = entry.getValue();
                String[] parts = info.replace("[", "").replace("]", "").split("\\|");
                String loc = parts[0].replace("Loc:", "").trim();
                String att = parts[1].replace("Att:", "").trim();
                String cat = parts[2].replace("Cat:", "").trim();
                bw.write(id + "," + loc + "," + att + "," + cat);
                bw.newLine();
            }
    }   catch (IOException e) {
            System.err.println("Error updating additional.csv: " + e.getMessage());
        }
    }
    
    public static void updateAdditionalData(int eventId, String loc, String att, String cat) {
        String info = String.format(" [Loc: %s | Att: %s | Cat: %s]", loc, att, cat);
        additionalDataMap.put(eventId, info);
        updateAdditionalFile();
    }

    public static void removeAdditionalData(int eventId) {
        additionalDataMap.remove(eventId);
        updateAdditionalFile();
    }
    
    public static void copyAdditionalData(int sourceEventId, int targetEventId) {
        String extraInfo = additionalDataMap.get(sourceEventId);
        if (extraInfo != null) {
            String[] parts = extraInfo.replace("[", "").replace("]", "").split("\\|");
            String loc = parts[0].replace("Loc:", "").trim();
            String att = parts[1].replace("Att:", "").trim();
            String cat = parts[2].replace("Cat:", "").trim();
            saveAdditionalData(targetEventId, loc, att, cat);
        }
    
    
    }
    
    public static boolean hasConflict(LocalDateTime start, LocalDateTime end) {
        File file = new File(EVENT_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
               
                if (line.isEmpty() || !Character.isDigit(line.charAt(0))) continue;

                Event e = Event.fromCSV(line); 
                if (e != null) {
                  
                    if (start.isBefore(e.getEndDateTime()) && end.isAfter(e.getStartDateTime())) {
                        System.out.println("Event created has conflict with event '" + e.getTitle()+"'");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void searchByDate(String dateStr) {
        LocalDate searchDate = LocalDate.parse(dateStr);
        System.out.println("\n--- Search Results for: " + searchDate + " ---");
        boolean found = false;

        File file = new File(EVENT_FILE);
        if (!file.exists()) {
            System.out.println("No events found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || !Character.isDigit(line.charAt(0))) continue;

                Event e = Event.fromCSV(line);
                if (e != null && e.getStartDateTime().toLocalDate().equals(searchDate)) {
                    System.out.print(e.toString()); 
                    String extra = additionalDataMap.getOrDefault(e.getEventId(), " [No Extra Info]");
                    System.out.println(extra);
                    found = true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading events.");
        }

        if (!found) System.out.println("No events found on this date.");
    }
}
