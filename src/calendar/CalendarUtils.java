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
    // å®šä¹‰æ–‡ä»¶å��
    private static final String ADDITIONAL_FILE = "additional.csv";
    private static final String EVENT_FILE = "event.csv";

    private static Map<Integer, String> additionalDataMap = new HashMap<>();

    public static void loadAdditionalData() {
        additionalDataMap.clear();
        File file = new File(ADDITIONAL_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // è·³è¿‡æ ‡é¢˜
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
        // æ›´æ–°å†…å­˜
        String info = String.format(" [Loc: %s | Att: %s | Cat: %s]", loc, att, cat);
        additionalDataMap.put(eventId, info);

        // å†™å…¥æ–‡ä»¶
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADDITIONAL_FILE, true))) {
            File file = new File(ADDITIONAL_FILE);
            if (file.length() == 0) {
                bw.write("eventId,location,attendees,category"); 
                bw.newLine();
            }
            bw.write(eventId + "," + loc + "," + att + "," + cat);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error saving additional data: " + e.getMessage());
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
                    // : (StartA < EndB) && (EndA > StartB)
                    if (start.isBefore(e.getEndDateTime()) && end.isAfter(e.getStartDateTime())) {
                        System.out.println("â�Œ å†²çª�è­¦å‘Š: è¯¥æ—¶é—´æ®µä¸Žäº‹ä»¶ '" + e.getTitle() + "' é‡�å� ï¼�");
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
