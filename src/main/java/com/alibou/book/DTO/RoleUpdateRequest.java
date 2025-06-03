package com.alibou.book.DTO;

import java.util.List;

public class RoleUpdateRequest {
    private List<String> roleNames;

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }
}
