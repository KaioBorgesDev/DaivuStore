package com.example.entity;

public class IdUser {
    private static Integer iduser;

    public static Integer getIduser() {
        return iduser;
    }

    public static void setIduser(Integer iduser) {
        IdUser.iduser = iduser;
    }
}
