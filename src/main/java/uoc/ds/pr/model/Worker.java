package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;

import java.time.LocalDate;

public class Worker {

    private String dni;
    private String name;
    private String surname;
    private Role role;
    private String roleId;
    private LocalDate birthDay;

    public Worker(String dni, String name, String surname, LocalDate birthDay, String roleId) {
        this.dni = dni;
        this.name = name;
        this.surname = surname;
        this.birthDay = birthDay;
        this.roleId = roleId;
    }

    public static void removeWorker(List<Worker> workers, Worker worker) {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setRole(Role role){
        this.role = role;
        this.role.addWorker(this);
    }

    public String getName(){
        return name;
    }

    public String getDni() {
        return dni;
    }

    public String getSurname() {
        return surname;
    }

    public String getRoleId() {
        return roleId;
    }

    public Role getRole() {
        return role;
    }

    public Boolean sameAs(String workerId) {
       return this.dni.equals(workerId);
    }
}


