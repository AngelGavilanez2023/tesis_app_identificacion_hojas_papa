package com.example.base4.modelo;

import java.io.Serializable;
public class Tratamiento implements Serializable {
    int id;
    String enfermedad;
    String aplicar;
    String modo;
    String fungicida;
    String dosis;


    public Tratamiento(int id, String enfermedad, String aplicar, String modo, String fungicida, String dosis) {
        this.id = id;
        this.enfermedad = enfermedad;
        this.aplicar = aplicar;
        this.modo = modo;
        this.fungicida = fungicida;
        this.dosis = dosis;
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

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getFungicida() {
        return fungicida;
    }

    public void setFungicida(String fungicida) {
        this.fungicida = fungicida;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }


}
