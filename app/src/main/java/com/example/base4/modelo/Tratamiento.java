package com.example.base4.modelo;

public class Tratamiento {
    int id;
    String enfermedad;
    String aplicar;

    public Tratamiento(int id, String enfermedad, String aplicar) {
        this.id = id;
        this.enfermedad = enfermedad;
        this.aplicar = aplicar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(String enfermedad) {
        this.enfermedad = enfermedad;
    }

    public String getAplicar() {
        return aplicar;
    }

    public void setAplicar(String aplicar) {
        this.aplicar = aplicar;
    }
}
