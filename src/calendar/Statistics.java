/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Statistics {

    public void showStatistics(List<Event> allEvents) {
        if (allEvents == null || allEvents.isEmpty()) {
            System.err.println("Error: No event data available for statistics.");
            return;
        }

        int[] dayCounts = new int[7];
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        
        LocalDate minDate = allEvents.get(0).getStartDateTime().toLocalDate();
        LocalDate maxDate = allEvents.get(0).getStartDateTime().toLocalDate();

        for (Event e : allEvents) {
            LocalDate eventDate = e.getStartDateTime().toLocalDate();
            if (eventDate.isBefore(minDate)) minDate = eventDate;
            if (eventDate.isAfter(maxDate)) maxDate = eventDate;

            int dayIndex = e.getStartDateTime().getDayOfWeek().getValue() - 1;
            dayCounts[dayIndex]++;
        }

        System.out.println("\n--- Weekly Event Distribution ---");
        for (int i = 0; i < dayCounts.length; i++) {
            System.out.printf("%-4s: ", dayNames[i]);
            for (int j = 0; j < dayCounts[i]; j++) {
                System.out.print("*");
            }
            System.out.println(" (" + dayCounts[i] + ")");
        }

        System.out.println("\n--- Key Performance Indicators (KPIs) ---");
        
        long totalEvents = allEvents.size();
        long totalDays = ChronoUnit.DAYS.between(minDate, maxDate) + 1;
        
        double avgPerDay = (double) totalEvents / totalDays;
        
        double totalWeeks = totalDays / 7.0;
        double totalMonths = totalDays / 30.44; // Average month length in days

        System.out.printf("Total Timespan: %d days (From %s to %s)\n", totalDays, minDate, maxDate);
        System.out.printf("Average Events/Day: %.2f\n", avgPerDay);
        
        if (totalWeeks >= 1) {
            System.out.printf("Average Events/Week: %.2f\n", totalEvents / totalWeeks);
        } else {
            System.out.println("Average Events/Week: (Data spans less than a week)");
        }

        if (totalMonths >= 1) {
            System.out.printf("Average Events/Month: %.2f\n", totalEvents / totalMonths);
        } else {
            System.out.println("Average Events/Month: (Data spans less than a month)");
        }
        System.out.println("----------------------------------------");
        
        
    }
}
