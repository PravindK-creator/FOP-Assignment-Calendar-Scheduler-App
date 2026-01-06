/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;

/**
 *
 * @author pravi
 */
import java.io.*;//import java I/O for reading/writing files
import java.time.LocalDateTime;//import LocalDateTime class for handling date/time
import java.util.*;//import utility classes like list and ArrayList
public class EventManager {
    private static final String EVENT_FILE="event.csv";
    //constant for event file name.Using relative path so it works on any pc
    private List<Event> events=new ArrayList<>();
    
    //method to create and save a new event into event.csv
    public void createEvent(Event event){
        int newId=getNextEventId();//generate new eventId by checking the files
        event.setEventId(newId);//assign auto generated ID to event
        if(event.getEndDateTime().isBefore(event.getStartDateTime())){
            throw new IllegalArgumentException("End time must be after start time");
        }
        
        try(BufferedWriter bw=new BufferedWriter(new FileWriter(EVENT_FILE, true))){
            //write event details in CSV format.
            bw.write(event.toCSV());
            
            //move to the next line for the next event
            bw.newLine();
            System.out.println("Event created successfully: "+event);
        }
        catch(IOException e){
            e.printStackTrace();//Print error if file writing fails
        }
        
    }
    
        //helper method to get the next eventId
        private int getNextEventId(){//reads the file, finds the highestID, and add 1
            int maxId=0;
            try(BufferedReader br=new BufferedReader(new FileReader(EVENT_FILE))){
                String line;
                while((line=br.readLine())!=null){
                    String []parts=line.split(",");
                    //Split CSV line into fields
                    int id=Integer.parseInt(parts[0]);
                    //First column is eventId
                    if(id>maxId){
                        maxId=id;
                    }
                }
            }
            catch(IOException e){
                //if file doesnt exist yet,first event will get ID=1
            }
            return maxId +1;
        }
        
        //Method to update an existing event
        //Reads all event, replaces the one that matches eventId
        // Method to update an existing event
public void updateEvent(int eventId, Event updatedEvent) {
    List<Event> events = new ArrayList<>();
    // Temporary list to store all events.
    if(updatedEvent.getEndDateTime().isBefore(updatedEvent.getStartDateTime())){
            throw new IllegalArgumentException("End time must be after start time");
    }
    
    try(BufferedReader br=new BufferedReader(new FileReader(EVENT_FILE))){
        String line;
        while((line=br.readLine()) != null){
            Event e=Event.fromCSV(line);
            if(e != null){
                if(e.getEventId()==eventId){
                    events.add(updatedEvent);//replace with updated
                }
                else{
                    events.add(e);//remain the same no changes
                }
            }
        }
    }
    catch(IOException e){
        e.printStackTrace();
    }
    
    try(BufferedWriter bw=new BufferedWriter(new FileWriter(EVENT_FILE))){
        for(Event e:events){
            bw.write(e.toCSV());
            bw.newLine();
        }
        System.out.println("Event updated successfully: "+updatedEvent);
    }
    catch(IOException e){
        e.printStackTrace();
    }
    
}

// Method to delete an existing event
public void deleteEvent(int eventId) {
    List<Event> events = new ArrayList<>();
    // Temporary list to store all events except the one to delete.

    try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE))) {
        String line;
        while ((line = br.readLine()) != null) {
            Event e=Event.fromCSV(line);
            if(e!=null && e.getEventId()!=eventId){
                events.add(e);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Rewrite the file without the deleted event
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENT_FILE))) {
        for (Event e : events) {
            bw.write(e.toCSV());
            bw.newLine();
        }
        System.out.println("Event deleted successfully: ID " + eventId);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

//method to get all the events
    public List<Event> getAllEvents(){
        List<Event> events=new ArrayList<>();
        try(BufferedReader br=new BufferedReader(new FileReader(EVENT_FILE))){
            String line;
            while((line=br.readLine()) != null){
                Event e=Event.fromCSV(line);
                if(e!=null){
                    events.add(e);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return events;
    }
    
    //method to find the nearest upcoming event
    public Event getNextEvent(){
        List<Event> events=getAllEvents();
        LocalDateTime now=LocalDateTime.now();
        Event nextEvent=null;
        
        for(Event e: events){
            if(e.getStartDateTime().isAfter(now)){
                if(nextEvent==null || e.getStartDateTime().isBefore(nextEvent.getStartDateTime())){
                    nextEvent=e;
                }
            }
        }
        return nextEvent;
    }
    
    //Reminder notification system
    public void scheduleReminders(){
        scheduleReminders(30);//set default 30 minutes before
    }
    
    //Overloaded reminder system with custom minutes
    public void scheduleReminders(int minutesBefore){
        List<Event>events=getAllEvents();
        LocalDateTime now=LocalDateTime.now();
        
        for(Event e: events){
            LocalDateTime reminderTime=e.getStartDateTime().minusMinutes(minutesBefore);
            if((reminderTime.isAfter(now))){
                System.out.println("Reminder: Event '"+e.getTitle()+"' starts at "+e.getStartDateTime()+" ("+minutesBefore+" minutes left!)");
            }
        }
    }
    
    //clears the csv file
    public void clearEventsFile(){
        try(PrintWriter pw=new PrintWriter(new FileWriter(EVENT_FILE))){
            pw.print("");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public Event getEventById(int id) {
    List<Event> events = getAllEvents();
    for (Event e : events) {
        if (e.getEventId() == id) {
            return e;
        }
    }
    return null; // if not found
}
    
    // ================= Recurring Events Support =================

// Store recurring event rules
private List<RecurringEvent> recurringEvents = new ArrayList<>();

// Load recurring events from CSV file
public void loadRecurringEvents(String filename) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            RecurringEvent re = RecurringEvent.fromCSV(line);
            if (re != null) recurringEvents.add(re);
        }
    } catch (IOException e) {
        System.err.println("Error loading recurring events: " + e.getMessage());
    }
}

// Save recurring events to CSV file
public void saveRecurringEvents(String filename) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
        for (RecurringEvent re : recurringEvents) {
            writer.write(re.toCSV());
            writer.newLine();
        }
    } catch (IOException e) {
        System.err.println("Error saving recurring events: " + e.getMessage());
    }
}

// Add a new recurring event rule
public void addRecurringEvent(int eventId, String interval, int times, LocalDateTime endDate) {
    RecurringEvent re = new RecurringEvent(eventId, interval, times, endDate);
    recurringEvents.add(re);
}

// Expand recurring events into actual Event instances
public void expandRecurringEvents() {
    for (RecurringEvent re : recurringEvents) {
        Event base = findEventById(re.getEventId());
        if (base == null) {
            System.err.println("Base event not found for ID: " + re.getEventId());
            continue;
        }

        LocalDateTime dateTime = base.getDateTime(); // assumes Event has getDateTime()
        int count = 0;

        while (true) {
            if (count > 0) {
                // Create a copy of the base event with new dateTime
                Event copy = new Event(base.getTitle(), base.getDescription(), dateTime);
                events.add(copy);
            }

            count++;
            if (re.getRecurrentTimes() > 0 && count >= re.getRecurrentTimes()) break;
            if (re.getRecurrentEndDate() != null && dateTime.isAfter(re.getRecurrentEndDate())) break;

            dateTime = incrementDateTime(dateTime, re.getRecurrentInterval());
        }
    }
}

// Helper method to increment LocalDateTime based on recurrence interval
private LocalDateTime incrementDateTime(LocalDateTime dateTime, String interval) {
    switch (interval) {
        case "1d": return dateTime.plusDays(1);
        case "1w": return dateTime.plusWeeks(1);
        case "2w": return dateTime.plusWeeks(2);
        case "1m": return dateTime.plusMonths(1);
        default:
            System.err.println("Unknown interval: " + interval);
            return dateTime;
    }
}

// Helper to find event by ID (adjust if your Event uses a different getter)
private Event findEventById(int id) {
    for (Event e : events) {
        if (e.getEventId() == id) return e;
    }
    return null;
}


}

