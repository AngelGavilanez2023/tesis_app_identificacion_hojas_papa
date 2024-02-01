package com.example.base4.modelo;

public class Resultados {
    int id;
    String enfermedad;
    String accuracy;
    String fecha_hora;

    public Resultados(int id, String enfermedad, String accuracy, String fecha_hora) {
        this.id = id;
        this.enfermedad = enfermedad;
        this.accuracy = accuracy;
        this.fecha_hora = fecha_hora;
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

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
}
