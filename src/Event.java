/**
 * Created by halleyfroeb on 9/26/16.
 */
public class Event {
    int index;
    String title;
    String location;
    String timeAndDate;
    String eventType;
    String description;
    String author;
    String viewable;

    public Event(int index, String title, String location, String timeAndDate, String eventType, String description, String author, String viewable) {
        this.index = index;
        this.title = title;
        this.location = location;
        this.timeAndDate = timeAndDate;
        this.eventType = eventType;
        this.description = description;
        this.author = author;
        this.viewable = viewable;

    }

}
