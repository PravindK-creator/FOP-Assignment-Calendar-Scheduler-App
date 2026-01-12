/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

/**
 *
 * @author pravi
 */
import java.time.*;
import java.time.format.TextStyle;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
public class CalendarCLI {
    //instance variable
    private List<Event> allEvents;//List all of the events
    
    //Constructor
    public CalendarCLI(List<Event> events){
        this.allEvents=events;
    }
    
    //print calendar for the given month and year
    //Example: January 2026
public void printMonthView(int year, int month) {
    YearMonth ym = YearMonth.of(year, month);

    // Print header: each column is fixed width (3 chars)
    System.out.println("\n=== " + ym.getMonth() + " " + ym.getYear() + " ===");
    System.out.printf("%-3s%-3s%-3s%-3s%-3s%-3s%-3s%n", 
                      "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa");

    LocalDate firstDay = ym.atDay(1);

    //Sunday-first offset: Sunday=0, Monday=1, ..., Saturday=6
    int offset = firstDay.getDayOfWeek().getValue() % 7;

    //Print leading blanks (3 spaces per column)
    for (int i = 0; i < offset; i++) {
        System.out.print("   ");
    }

    //Print each day of the month
    for (int day = 1; day <= ym.lengthOfMonth(); day++) {
        LocalDate date = ym.atDay(day);
        boolean hasEvent = hasEventOnDate(date);

        //Each cell is exactly 3 spaces wide
        if (hasEvent) {
            System.out.printf("%2d*", day);  // e.g."10*"
        } else {
            System.out.printf("%2d ", day);  // e.g."1"
        }

        //Break line after Saturday
        if ((day + offset) % 7 == 0) {
            System.out.println();
        }
    }

    System.out.println(); //Final newline
}

    
    //check if there is any event on that date
    private boolean hasEventOnDate(LocalDate date){
        for(Event e:allEvents){
            if(e.getStartDateTime().toLocalDate().equals(date)){
                return true;
            }
        }
        return false;
    }
    
    //print the events on a specific month
    public void printEventsForMonth(int year, int month){
        boolean found=false;
        for(Event e:allEvents){
            int eventYear=e.getStartDateTime().getYear();
            int eventMonth=e.getStartDateTime().getMonthValue();
            
            if(eventYear==year && eventMonth==month){
                System.out.println("* "+e.getTitle()+" on "+e.getStartDateTime().toLocalDate()+" at "+e.getStartDateTime().toLocalTime());
                found=true;
            }
        }
        if(!found){
            System.out.println("No events found.");
        }
    }
    
    //prints weekly list view that has events
    public void printWeeklyListView(LocalDate startOfWeek){
        System.out.println("\n=== Week of "+startOfWeek+" ===");
        
        for(int i=0;i<7;i++){
            LocalDate currentDate=startOfWeek.plusDays(i);
            DayOfWeek dayOfWeek= currentDate.getDayOfWeek();//get the day based on current date
            String dayLabel=dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);//e.g. Mon, Tue
            
            //get events for this day
            List<Event> eventsForDay=new ArrayList<>();
            for(Event e:allEvents){
                if(e.getStartDateTime().toLocalDate().equals(currentDate)){
                    eventsForDay.add(e);
                }
            }
            
            //print the day and events
            String header=String.format("%-3s %2d:", dayLabel, currentDate.getDayOfMonth());
            System.out.print(header+" ");
            
            if(eventsForDay.isEmpty()){
                System.out.println("No events.");
            }
            else{
                for(int j=0;j<eventsForDay.size();j++){
                    Event e=eventsForDay.get(j);
                    System.out.print(e.getTitle()+" ("+e.getStartDateTime().toLocalTime()+")");
                    if(j<eventsForDay.size()-1){
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }
       
    }
}
