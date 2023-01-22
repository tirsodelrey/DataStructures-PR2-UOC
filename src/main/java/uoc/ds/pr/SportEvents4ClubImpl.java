package uoc.ds.pr;

import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.sequential.DictionaryArrayImpl;
import edu.uoc.ds.adt.sequential.QueueArrayImpl;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;
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
    //private OrderedVector<SportEvent> bestSportEventByAttendersVec;
    private SportEvent bestSportEventByAttenders;
    private OrderedVector<OrganizingEntity> best5OrganizingEntities;
    private int numRoles;

    public SportEvents4ClubImpl() {
        players = new DictionaryAVLImpl<>();
        organizingEntities = new HashTable<>(MAX_NUM_ORGANIZING_ENTITIES);
        sportEvents = new DictionaryAVLImpl<>();
        files = new PriorityQueue<>(File.CMP);
        totalFiles = 0;
        rejectedFiles = 0;
        workers = new HashTable<>();
        roles = new Role[MAX_ROLES];
        numRoles = 0;
        mostActivePlayer = null;
        bestSportEvent = new OrderedVector<SportEvent>(MAX_NUM_SPORT_EVENTS, SportEvent.CMP_V);
        //bestSportEventByAttendersVec = new OrderedVector<SportEvent>(MAX_NUM_SPORT_EVENTS, SportEvent.CMP_A);
        //bestSportEventByAttenders = bestSportEventByAttendersVec.last();
        bestSportEventByAttenders = null;
        best5OrganizingEntities = new OrderedVector<>(MAX_ORGANIZING_ENTITIES_WITH_MORE_ATTENDERS, OrganizingEntity.COMP_ATTENDERS);
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
        Role r = getRole(roleId);
        if (r != null) {
            r.setDescription(description);
        } else {
            r = new Role(roleId, description);
            addRole(r);
        }
    }

    public void addRole(Role role){
        roles[numRoles++] = role;
    }

    @Override
    public void addWorker(String dni, String name, String surname, LocalDate birthDay, String roleId) {
        Worker worker = getWorker(dni);
        Role role = getRole(roleId);
        if (worker != null){
            worker.setName(name);
            worker.setSurname(surname);
            worker.setBirthDay(birthDay);

            if (!worker.getRoleId().equals(roleId)) {
                worker.getRole().removeWorker(worker);
                worker.setRole(role);
            }
        }
        else {
            worker = new Worker(dni, name, surname, birthDay, roleId);
            workers.put(dni, worker);
            worker.setRole(role);
        }
    }

    @Override
    public void assignWorker(String dni, String eventId) throws WorkerNotFoundException, WorkerAlreadyAssignedException, SportEventNotFoundException {
        Worker wk = getWorker(dni);
        SportEvent se = getSportEvent(eventId);

        if(se == null){
            throw new SportEventNotFoundException();
        }
        if(wk == null){
            throw new WorkerNotFoundException();
        }
        if(se.workerExists(dni)){
            throw new WorkerAlreadyAssignedException();
        }
        se.addWorker(wk);

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
        Player player = getPlayer(playerId);
        if(player == null){
            throw new PlayerNotFoundException();
        }
        return player.getLevel();
    }

    @Override
    public Iterator<Enrollment> getSubstitutes(String eventId) throws SportEventNotFoundException, NoSubstitutesException {
        return null;
    }

    @Override
    public void addAttender(String phone, String name, String eventId) throws AttenderAlreadyExistsException, SportEventNotFoundException, LimitExceededException {
       SportEvent se = getSportEvent(eventId);
       if(se == null){
           throw new SportEventNotFoundException();
       }
       Attender att = se.getAttender(phone);
       if(att != null) {
           throw new AttenderAlreadyExistsException();
       }
       if(!se.isFull()){
           att = new Attender(phone, name, eventId);
           se.addAttender(phone, att);
           updateBestSportEventByAttenders(se);
           OrganizingEntity oe = se.getOrganizingEntity();
           updateBest5OrganizingEntity(oe);
       } else {
           throw new LimitExceededException();
       }

    }

    private void updateBest5OrganizingEntity(OrganizingEntity oe) {
        if(!alreadyInBest5(oe)){
            best5OrganizingEntities.update(oe);
        }
    }

    private boolean alreadyInBest5(OrganizingEntity oe1) {
        Iterator<OrganizingEntity> it = best5OrganizingEntities.values();
        boolean found = false;
        while(it.hasNext() && found == false){
            OrganizingEntity oe2 = it.next();
            if(oe1.getOrganizationId().equals(oe2.getOrganizationId())){
                found = true;
            }
        }
        return found;
    }

    //added
    private void updateBestSportEventByAttenders(SportEvent se) {
        if(bestSportEventByAttenders == null){
            bestSportEventByAttenders = se;
        } else if (se.numAttenders() > bestSportEventByAttenders.numAttenders()){
            bestSportEventByAttenders = se;
        }

    }

    @Override
    public Attender getAttender(String phone, String sportEventId) throws SportEventNotFoundException, AttenderNotFoundException {
       SportEvent se = getSportEvent(sportEventId);
       if(se == null){
           throw new SportEventNotFoundException();
       } else {
           Attender attender = se.getAttender(phone);
           if (attender == null){
               throw new AttenderNotFoundException();
           } else {
               return attender;
           }
       }
    }

    @Override
    public Iterator<Attender> getAttenders(String eventId) throws SportEventNotFoundException, NoAttendersException {
        SportEvent sEvent = getSportEvent(eventId);
        if(sEvent == null){
            throw new SportEventNotFoundException();
        }
        if(sEvent.numAttenders() == 0){
            throw new NoAttendersException();
        }
        Iterator<Attender> attenderIt = sEvent.getAttenders();
        return attenderIt;
    }

    @Override
    public Iterator<OrganizingEntity> best5OrganizingEntities() throws NoAttendersException {
        return best5OrganizingEntities.values();
    }

    @Override
    public SportEvent bestSportEventByAttenders() throws NoSportEventsException {
        return bestSportEventByAttenders;
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
        return numRoles;
    }

    @Override
    public Role getRole(String roleId) {
        for (Role r : roles) {
            if (r == null) {
                return null;
            } else if (r.isSame(roleId)){
                return r;
            }
        }
        return null;
    }

    @Override
    public int numWorkers() {
        return 0;
    }

    @Override
    public Worker getWorker(String dni) {
        return workers.get(dni);
    }

    @Override
    public int numWorkersByRole(String roleId) {
        Role role = getRole(roleId);
        if(role != null){
            return role.getNumWorkers();
        }
        return 0;
    }

    @Override
    public int numWorkersBySportEvent(String sportEventId) {
        SportEvent sEvent = getSportEvent(sportEventId);
        if(sEvent != null){
            return sEvent.getNumWorkers();
        }
        return 0;
    }

    @Override
    public int numRatings(String playerId) {
        Player player = getPlayer(playerId);
        return (player!=null?player.numRatings():0);

    }

    @Override
    public int numAttenders(String sportEventId) {
        int numA = 0;
        SportEvent event = getSportEvent(sportEventId);
        if(event != null){
            numA = event.numAttenders();
        }
        return  numA;
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
