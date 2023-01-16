package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;

public class OrganizingEntity {
    private String id;
    private String description;
    private String name;
    private List<SportEvent> events;

    public OrganizingEntity(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        events = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public String getOrganizationId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterator<SportEvent> activities() {
        return events.values();
    }

    public void addEvent(SportEvent sportEvent) {
        events.insertEnd(sportEvent);
    }

    public int numEvents() {
        return events.size();
    }

    public boolean hasActivities() {
        return events.size() > 0;
    }

    public Iterator<SportEvent> sportEvents() {
        return events.values();
    }
}
