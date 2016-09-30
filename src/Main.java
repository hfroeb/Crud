import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by halleyfroeb on 9/26/16.
 */
public class Main {

    static HashMap<String, User> users = new HashMap<>();
    public static ArrayList<Event> events = new ArrayList<>();

    public static void main(String[] args) {

        Spark.init();

        Spark.get("/",
                ((request, response) -> {
                    HashMap m = new HashMap<>();
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    String userName1 = session.attribute("userName1");
                    User user = users.get(userName);

                    for (Event event : events) {
                        if (event.author.equalsIgnoreCase(userName) || event.author.equalsIgnoreCase(userName1)) {
                            event.viewable = "yes";
                        } else {
                            event.viewable = null;
                        }
                    }
                    for (Event event: events) {
                        if (event.index < 0) { // event is new, was just created
                            event.index = 0;
                            for (int i = 0; i < events.size(); i++) {
                                event.index = (i);
                            }
                        }
                    }
                    if (user == null) {
                        m.put("events", events);
                        return new ModelAndView(m, "home.html");

                    } else {
                        m.put("events", events);
                        m.put("userName", userName);
                        return new ModelAndView(m, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/editEvent", // edit event page
                (request, response) -> {
                    HashMap m = new HashMap();
                    Session session = request.session();
                    int index = session.attribute("editIndex");
                    Event event = events.get(index);
                        m.put("event", event);
                        m.put("index", index);
                        return new ModelAndView(m, "editEvent.html");

                },
                new MustacheTemplateEngine()
        );


        Spark.post("/login",
                ((request, response) -> {
                    String userName = request.queryParams("loginName");
                    if (userName == null) {
                        throw new Exception("login name not found");
                    }

                    User user = users.get(userName);
                    if (user == null) {
                        user = new User(userName);
                        users.put(userName, user);
                    }

                    Session session = request.session();
                    session.attribute("userName", userName);

                    response.redirect("/");
                    return "";
                })
        );
        Spark.post("/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post("/create-event",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");
                    User user = users.get(userName);
                    if (user == null) {
                        throw new Exception("User is not logged in");
                    }
                    String title = request.queryParams("eventTitle");
                    String location = request.queryParams("eventLocation");
                    String timeAndDate = request.queryParams("timeAndDate");
                    String eventType = request.queryParams("eventType");
                    String description = request.queryParams("description");
                    String author = request.queryParams("author");
                    int index = -1; //indicates new event
                    String viewable = null;

                    Event m = new Event(index, title, location, timeAndDate, eventType, description, author, viewable);
                    events.add(m);
                    session.attribute("events", events);
                    response.redirect("/");
                    return "";

                });

        Spark.post("/delete-event",
                ((request, response) -> {
                    int index = Integer.parseInt(request.queryParams("index"));
                    events.remove(index);
                    response.redirect("/");
                    return "";
                }));
        Spark.post( //Post to home page to get to edit page (button)
                "/edit-event",
                ((request, response) -> {
                    int editIndex = Integer.parseInt(request.queryParams("editIndex"));
                    Session session = request.session();
                    session.attribute("editIndex", editIndex);
                    response.redirect("/editEvent");
                    return "";
                }));
        Spark.post(
                "/editEvent", // Post to edit event page
                ((request, response) -> {
                    Session session = request.session();
                    int editIndex = session.attribute("editIndex");
                    events.remove(editIndex);

                    String title = request.queryParams("editTitle");
                    String location = request.queryParams("editLocation");
                    String timeAndDate = request.queryParams("editTimeAndDate");
                    String eventType = request.queryParams("editType");
                    String description = request.queryParams("editDescription");
                    String author = request.queryParams("editAuthor");
                    String viewable = "yes";
                    String userName1 = request.queryParams("editAuthor");

                    session.attribute("userName1", userName1);

                    Event m = new Event(editIndex, title, location, timeAndDate, eventType, description, author, viewable);
                    events.add(m);
                    response.redirect("/");
                    return "";
                }));

    }
}

