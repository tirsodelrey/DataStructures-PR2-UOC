package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;

import java.util.Comparator;

public class OrganizingEntity implements Comparable<OrganizingEntity>{
    private String id;
    private String description;
    private String name;
    private List<SportEvent> events;
    public static final Comparator<OrganizingEntity> COMP_ATTENDERS =  (OrganizingEntity o1, OrganizingEntity o2)->Double.compare(o2.numAttenders(), o1.numAttenders());

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

    //added
    public int numAttenders() {
        int numAttenders = 0;
        Iterator it = events.values();
        while (it.hasNext()){
            SportEvent event = (SportEvent) it.next();
            numAttenders += event.numAttenders();
        }
        return numAttenders;
    }

    @Override
    public int compareTo(OrganizingEntity o) {
        return Double.compare(o.numAttenders(), numAttenders());
    }
}
