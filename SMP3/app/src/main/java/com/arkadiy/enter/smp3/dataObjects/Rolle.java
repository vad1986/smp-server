package com.arkadiy.enter.smp3.dataObjects;

public class Rolle {
    private int idRolle;
    private String nameRolle;

    public Rolle(int idRolle, String nameRolle) {
        this.idRolle = idRolle;
        this.nameRolle = nameRolle;
    }

    public Rolle(Rolle rolle){
        this.idRolle = rolle.idRolle;
        this.nameRolle = rolle.nameRolle;
    }

    public int getIdRolle() {
        return idRolle;
    }

    public void setIdRolle(int idRolle) {
        this.idRolle = idRolle;
    }

    public String getNameRolle() {
        return nameRolle;
    }

    public void setNameRolle(String nameRolle) {
        this.nameRolle = nameRolle;
    }
}
