package uoc.ds.pr;

import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.sequential.DictionaryArrayImpl;
import edu.uoc.ds.adt.sequential.QueueArrayImpl;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.model.*;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.util.DictionaryOrderedVector;
import uoc.ds.pr.util.OrderedVector;

import java.time.LocalDate;


public class SportEvents4ClubImpl implements SportEvents4Club {

    private DictionaryAVLImpl<String, Player> players;
    private HashTable<String, OrganizingEntity> organizingEntities;
    private PriorityQueue<File> files;
    private Integer totalFiles;
    private Integer rejectedFiles;
    private DictionaryAVLImpl<String, SportEvent> sportEvents;
    private HashTable<String, Worker> workers;
    private Role[] roles;
    private Player mostActivePlayer;
    private OrderedVector<SportEvent> bestSportEvent;

    public SportEvents4ClubImpl() {
        players = new DictionaryAVLImpl<>();
        organizingEntities = new HashTable<>(MAX_NUM_ORGANIZING_ENTITIES);
        sportEvents = new DictionaryAVLImpl<>();
        files = new PriorityQueue<>(File.CMP);
        totalFiles = 0;
        rejectedFiles = 0;
        workers = new HashTable<>();
        roles = new Role[MAX_ROLES];
        mostActivePlayer = null;
        bestSportEvent = new OrderedVector<SportEvent>(MAX_NUM_SPORT_EVENTS, SportEvent.CMP_V);
    }

    @Override
    public void addPlayer(String id, String name, String surname, LocalDate dateOfBirth) {
        Player u = getPlayer(id);
        if (u != null) {
            u.setName(name);
            u.setSurname(surname);
            u.setBirthday(dateOfBirth);
        } else {
            u = new Player(id, name, surname, dateOfBirth);
            addUser(u);
        }
    }

    private void addUser(Player u) {
        u.getId();
        players.put(u.getId(), u);
    }

    @Override
    public void addOrganizingEntity(String id, String name, String description) {
        OrganizingEntity organizingEntity = getOrganizingEntity(id);
        if (organizingEntity != null) {
            organizingEntity.setName(name);
            organizingEntity.setDescription(description);
        } else {
            organizingEntities.put(id, new OrganizingEntity(id, name, description));
        }
    }

    @Override
    public void addFile(String id, String eventId, String orgId, String description, Type type, byte resources, int max, LocalDate startDate, LocalDate endDate) throws OrganizingEntityNotFoundException {

        OrganizingEntity organization = getOrganizingEntity(orgId);
        if (organization == null) {
            throw new OrganizingEntityNotFoundException();
        }

        files.add(new File(id, eventId, description, type, startDate, endDate, resources, max, organization));
        totalFiles++;
    }

    @Override
    public File updateFile(Status status, LocalDate date, String description) throws NoFilesException {
        File file = files.poll();
        if (file  == null) {
            throw new NoFilesException();
        }

        file.update(status, date, description);
        if (file.isEnabled()) {
            SportEvent sportEvent = file.newSportEvent();
            sportEvents.put(sportEvent.getEventId(), sportEvent);
        }
        else {
            rejectedFiles++;
        }

        return file;
    }

    @Override
    public void signUpEvent(String playerId, String eventId) throws PlayerNotFoundException, SportEventNotFoundException, LimitExceededException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }

        player.addEvent(sportEvent);
        if (!sportEvent.isFull()) {
            sportEvent.addEnrollment(player);
        }
        else {
            sportEvent.addEnrollmentAsSubstitute(player);
            throw new LimitExceededException();
        }
        updateMostActivePlayer(player);
    }

    @Override
    public double getRejectedFiles() {
        return (double) rejectedFiles / totalFiles;
    }

    @Override
    public Iterator<SportEvent> getSportEventsByOrganizingEntity(String organizationId) throws NoSportEventsException {
        OrganizingEntity organizingEntity = getOrganizingEntity(organizationId);

        if (organizingEntity==null || !organizingEntity.hasActivities()) {
            throw new NoSportEventsException();
        }
        return organizingEntity.sportEvents();
    }

    @Override
    public Iterator<SportEvent> getAllEvents() throws NoSportEventsException {
        Iterator<SportEvent> it = sportEvents.values();
        if (!it.hasNext()) throw new NoSportEventsException();
        return it;
    }

    @Override
    public Iterator<SportEvent> getEventsByPlayer(String playerId) throws NoSportEventsException {
        Player player = getPlayer(playerId);
        if (player==null || !player.hasEvents()) {
            throw new NoSportEventsException();
        }
        Iterator<SportEvent> it = player.getEvents();

        return it;
    }

    @Override
    public void addRating(String playerId, String eventId, Rating rating, String message) throws SportEventNotFoundException, PlayerNotFoundException, PlayerNotInSportEventException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }

        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        if (!player.isInSportEvent(eventId)) {
            throw new PlayerNotInSportEventException();
        }

        sportEvent.addRating(rating, message, player);
        player.addRating(rating, message,player);
        updateBestSportEvent(sportEvent);
    }

    //Added
    private void updateBestSportEvent(SportEvent sportEvent) {
        bestSportEvent.delete(sportEvent);
        bestSportEvent.update(sportEvent);
    }

    @Override
    public Iterator<uoc.ds.pr.model.Rating> getRatingsByEvent(String eventId) throws SportEventNotFoundException, NoRatingsException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent  == null) {
            throw new SportEventNotFoundException();
        }

        if (!sportEvent.hasRatings()) {
            throw new NoRatingsException();
        }

        return sportEvent.ratings();
    }

    @Override
    public Player mostActivePlayer() throws PlayerNotFoundException {
        if (mostActivePlayer == null) {
            throw new PlayerNotFoundException();
        }

        return mostActivePlayer;
    }

    //added
    private void updateMostActivePlayer(Player player) {
        if (mostActivePlayer == null) {
            mostActivePlayer = player;
        }
        else if (player.numSportEvents() > mostActivePlayer.numSportEvents()) {
            mostActivePlayer = player;
        }
    }

    @Override
    public SportEvent bestSportEvent() throws SportEventNotFoundException {
        if (bestSportEvent.size() == 0) {
            throw new SportEventNotFoundException();
        }

        return bestSportEvent.elementAt(0);
    }

    @Override
    public void addRole(String roleId, String description) {

    }

    @Override
    public void addWorker(String dni, String name, String surname, LocalDate birthDay, String roleId) {

    }

    @Override
    public void assignWorker(String dni, String eventId) throws WorkerNotFoundException, WorkerAlreadyAssignedException, SportEventNotFoundException {

    }

    @Override
    public Iterator<Worker> getWorkersBySportEvent(String eventId) throws SportEventNotFoundException, NoWorkersException {
        return null;
    }

    @Override
    public Iterator<Worker> getWorkersByRole(String roleId) throws NoWorkersException {
        return null;
    }

    @Override
    public Level getLevel(String playerId) throws PlayerNotFoundException {
        return null;
    }

    @Override
    public Iterator<Enrollment> getSubstitutes(String eventId) throws SportEventNotFoundException, NoSubstitutesException {
        return null;
    }

    @Override
    public void addAttender(String phone, String name, String eventId) throws AttenderAlreadyExistsException, SportEventNotFoundException, LimitExceededException {

    }

    @Override
    public Attender getAttender(String phone, String sportEventId) throws SportEventNotFoundException, AttenderNotFoundException {
        return null;
    }

    @Override
    public Iterator<Attender> getAttenders(String eventId) throws SportEventNotFoundException, NoAttendersException {
        return null;
    }

    @Override
    public Iterator<OrganizingEntity> best5OrganizingEntities() throws NoAttendersException {
        return null;
    }

    @Override
    public SportEvent bestSportEventByAttenders() throws NoSportEventsException {
        return null;
    }

    @Override
    public void addFollower(String playerId, String playerFollowerId) throws PlayerNotFoundException {

    }

    @Override
    public Iterator<Player> getFollowers(String playerId) throws PlayerNotFoundException, NoFollowersException {
        return null;
    }

    @Override
    public Iterator<Player> getFollowings(String playerId) throws PlayerNotFoundException, NoFollowingException {
        return null;
    }

    @Override
    public Iterator<Player> recommendations(String playerId) throws PlayerNotFoundException, NoFollowersException {
        return null;
    }

    @Override
    public Iterator<Post> getPosts(String playerId) throws PlayerNotFoundException, NoPostsException {
        return null;
    }

    @Override
    public int numPlayers() {
        return players.size();
    }

    @Override
    public int numOrganizingEntities() {
        return organizingEntities.size();
    }

    @Override
    public int numFiles() {
        return totalFiles;
    }

    @Override
    public int numRejectedFiles() {
        return rejectedFiles;
    }

    @Override
    public int numPendingFiles() {
        return files.size();
    }

    @Override
    public int numSportEvents() {
        return sportEvents.size();
    }

    @Override
    public int numSportEventsByPlayer(String playerId) {
        Player player = getPlayer(playerId);

        return (player!=null?player.numEvents():0);
    }

    @Override
    public int numPlayersBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);

        return (sportEvent!=null?sportEvent.numPlayers() + sportEvent.getNumSubstitutes(): 0);
    }

    @Override
    public int numSportEventsByOrganizingEntity(String orgId) {
        OrganizingEntity organization = getOrganizingEntity(orgId);

        return (organization!=null? organization.numEvents():0);
    }

    @Override
    public int numSubstitutesBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);

        return (sportEvent!=null?sportEvent.getNumSubstitutes():0);
    }

    @Override
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    @Override
    public SportEvent getSportEvent(String eventId) {
        return sportEvents.get(eventId);
    }

    @Override
    public OrganizingEntity getOrganizingEntity(String id) {
       return organizingEntities.get(id);
    }

    @Override
    public File currentFile() {
        return (files.size() > 0 ? files.peek() : null);
    }

    @Override
    public int numRoles() {
        return roles.length;
    }

    @Override
    public Role getRole(String roleId) {
        return null;
    }

    @Override
    public int numWorkers() {
        return 0;
    }

    @Override
    public Worker getWorker(String dni) {
        return null;
    }

    @Override
    public int numWorkersByRole(String roleId) {
        return 0;
    }

    @Override
    public int numWorkersBySportEvent(String sportEventId) {
        return 0;
    }

    @Override
    public int numRatings(String playerId) {
        Player player = getPlayer(playerId);
        return (player!=null?player.numRatings():0);

    }

    @Override
    public int numAttenders(String sportEventId) {
        return 0;
    }

    @Override
    public int numFollowers(String playerId) {
        return 0;
    }

    @Override
    public int numFollowings(String playerId) {
        return 0;
    }


}
