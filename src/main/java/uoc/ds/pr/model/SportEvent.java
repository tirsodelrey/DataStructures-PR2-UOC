package uoc.ds.pr.model;

import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.adt.sequential.Queue;
import edu.uoc.ds.adt.sequential.QueueArrayImpl;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;
import uoc.ds.pr.SportEvents4Club;

import java.time.LocalDate;
import java.util.Comparator;

import static uoc.ds.pr.SportEvents4Club.MAX_NUM_ENROLLMENT;

public class SportEvent implements Comparable<SportEvent>{
    public static final Comparator<SportEvent> CMP_V = (se1, se2)->Double.compare(se1.rating(), se2.rating());

    private String eventId;
    private String description;
    private SportEvents4Club.Type type;
    private LocalDate startDate;
    private LocalDate endDate;
    private int max;
    private File file;
    private List<Rating> ratings;
    private double sumRating;
    private Queue<Player> enrollments;
    private Queue<Enrollment> substitues;
    private HashTable<String, Attender> attenders;
    private List<Worker> workers;
    private OrganizingEntity organizingEntity;

    public SportEvent(String eventId, String description, SportEvents4Club.Type type,
                      LocalDate startDate, LocalDate endDate, int max, File file, OrganizingEntity organizingEntity) {

        setEventId(eventId);
        setDescription(description);
        setStartDate(startDate);
        setEndDate(endDate);
        setType(type);
        setMax(max);
        setFile(file);
        setOrganizingEntity(organizingEntity);
        this.enrollments = new QueueArrayImpl<>(MAX_NUM_ENROLLMENT);
        this.substitues = new PriorityQueue<>(Enrollment.CMP_E);
        this.ratings = new LinkedList<>();
        this.attenders = new HashTable<>();
        this.workers = new LinkedList<>();

    }
    //Added
    private void setOrganizingEntity(OrganizingEntity organizingEntity) {
        this.organizingEntity = organizingEntity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SportEvents4Club.Type getType() {
        return type;
    }

    public void setType(SportEvents4Club.Type type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public double rating() {
        return (this.ratings.size()>0?(sumRating / this.ratings.size()):0);
    }

    public void addRating(SportEvents4Club.Rating rating, String message, Player player) {
        Rating newRating = new Rating(rating, message, player);
        ratings.insertEnd(newRating);
        sumRating+=rating.getValue();
    }

    public boolean hasRatings() {
        return ratings.size()>0;
    }

    public Iterator<Rating> ratings() {
        return ratings.values();
    }


    public void addEnrollment(Player player) {
        enrollments.add(player);
    }

    public boolean is(String eventId) {
        return this.eventId.equals(eventId);
    }

    @Override
    public int compareTo(SportEvent se2) {
        return Double.compare(rating(), se2.rating() );
    }

    public boolean isFull() {
        return (enrollments.size() + attenders.size() >=max);
    }

    public int numPlayers() {
        return enrollments.size();
    }


    public void addEnrollmentAsSubstitute(Enrollment enrollment) {
        substitues.add(enrollment);
    }

    public int getNumSubstitutes() {
        return substitues.size();
    }

    //added
    public OrganizingEntity getOrganizingEntity() {
        return organizingEntity;
    }

    public int numAttenders() {
        return attenders.size();
    }

    public Attender getAttender(String phone) {
        return attenders.get(phone);
    }

    public void addAttender(String phone, Attender attender){
        attenders.put(phone, attender);
    }

    public Boolean workerExists(String dni){
        Traversal<Worker> it = workers.positions();
        Position<Worker> pos = null;
        Boolean exists = false;

        while(it.hasNext() && !exists){
            pos = it.next();
            exists = pos.getElem().sameAs(dni);
        }
        return exists;
    }
    //added
    public void addWorker(Worker wk) {
        workers.insertEnd(wk);
    }

    public int getNumWorkers() {
        return workers.size();
    }

    public Iterator<Attender> getAttenders(){
        return attenders.values();
    }

    public boolean hasWorkers() {
        return (workers.size() > 0);
    }

    public Iterator<Worker> getWorkers() {
        return workers.values();
    }

    public boolean hasSubstitutes() {
        return (substitues.size() > 0);
    }

    public Iterator<Enrollment> getSubstitutes() {
        return substitues.values();
    }
}
