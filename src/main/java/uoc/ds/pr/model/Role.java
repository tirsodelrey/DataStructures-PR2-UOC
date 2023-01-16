package uoc.ds.pr.model;

public class Role {
    private String roleId;
    private String description;

    public Role(String roleId, String description) {
        this.roleId = roleId;
        this.description = description;
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


}
