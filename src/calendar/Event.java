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
    private int eventId;//unique identifier for each event
    private String title;//title of the event
    private String description;//details and explanation of the event
    private LocalDateTime startDateTime;//store exact date and time when the event starts
    private LocalDateTime endDateTime;//store exact date and time when the event ends
    private LocalDateTime dateTime;
    private static int counter=0;//auto increment counter
    
    //add contructor
    //Full constructor: used when we already know the eventId
    public Event(int eventId, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.eventId=eventId;//Assign the given eventId to this object's eventId field
        this.title=title;
        this.description=description;
        this.startDateTime=startDateTime;
        this.endDateTime=endDateTime;
    }
    
    //contructor without eventId
    //the eventId will be auto generated later by EventManager
    public Event(String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.eventId=++counter;
        this.title=title;
        this.description=description;
        this.startDateTime=startDateTime;
        this.endDateTime=endDateTime;
    }
    
    public Event(String title, String description, LocalDateTime dateTime){
        this.title=title;
        this.description=description;
        this.dateTime=dateTime;
        this.eventId=++counter;
    }
    
    //add getter & setters
    public int getEventId(){//getter for eventId
        return eventId;
    }
    public void setEventId(int eventId){//setter for eventId
        this.eventId=eventId;//updates the eventId field
    }
    public String getTitle(){//getter for title
        return title;
    }
    public void setTitle(String title){//setter for title
        this.title=title;//update the title field with the given value
    }
    public String getDescription(){//getter for description
        return description;
    }
    public void setDescription(String description){//setter for description
        this.description=description;//update description field with given value
    }
    public LocalDateTime getStartDateTime(){//getter for startDateTime
        return startDateTime;
    }
    public void setStartDateTime(LocalDateTime startDateTime){//setter for startDateTime
        this.startDateTime=startDateTime;//update the startDateTime field with given value
    }
    public LocalDateTime getEndDateTime(){//getter for endDateTime
        return endDateTime;
    }
    public void setEndDateTime(LocalDateTime endDateTime){//setter for endDateTime
        this.endDateTime=endDateTime;//update the endDateTime field with given value
    }
    
    @Override
    public String toString(){
        return "Event{"+//Starts building the string with the word "Event{"
                "eventId= "+eventId+", title= '"+title+'\''+", description= '"+description+'\''+", startDateTime= "+startDateTime+", endDateTime= "+endDateTime+'}';
    }   
    
    public String toCSV(){
        return eventId+","+title+","+description+","+startDateTime+","+endDateTime;
    }
    
    public static Event fromCSV(String line){
        try{
           String[]parts=line.split(",");
            if(parts.length<5){
                throw new IllegalArgumentException("Invalid CSV line: "+line);
            }
            int id=Integer.parseInt(parts[0].trim());
            String title=parts[1].trim();
            String description=parts[2].trim();
            LocalDateTime start=LocalDateTime.parse(parts[3].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end=LocalDateTime.parse(parts[4].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            Event e=new Event(id,title,description,start,end);
            if(id>counter){
                counter=id;//keep counter in sync with the highest id
            }
            return e;
        }
        catch(Exception ex){
            System.err.println("Error parsing CSV line: " + line);
            ex.printStackTrace();
            return null; // return null so caller can decide how to handle
        }
  
    }
    
    public LocalDateTime getDateTime(){
        return dateTime;
    }
    
}
