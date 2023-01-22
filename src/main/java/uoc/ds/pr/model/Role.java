package uoc.ds.pr.model;


import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;
import edu.uoc.ds.traversal.TraversalArrrayImpl;

public class Role {
    private String roleId;
    private String description;
    private List<Worker> workers;

    public Role(String roleId, String description) {
        this.roleId = roleId;
        this.description = description;
        this.workers = new LinkedList<>();
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isSame(String roleId) {
        return this.roleId.equals(roleId);
    }

    public void addWorker(Worker worker) {
        workers.insertEnd(worker);
    }

    public void removeWorker(Worker w) {
        Traversal<Worker> it = workers.positions();
        Position<Worker> pos = null;
        Boolean found = false;
        while(it.hasNext() && !found){
            pos = it.next();
            found = pos.getElem().sameAs(w.getDni());
        }

        if(found){
            workers.delete(pos);
        }
    }

    //added
    public int getNumWorkers() {
       return workers.size();
    }
}
