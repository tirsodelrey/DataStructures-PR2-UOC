package uoc.ds.pr.model;

import java.time.LocalDate;

public class Worker {

    private String dni;
    private String name;
    private String surname;
    private String roleId;

    public Worker(String dni, String name, String surname, String roleId) {
        this.dni = dni;
        this.name = name;
        this.surname = surname;
        this.roleId = roleId;
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
}
