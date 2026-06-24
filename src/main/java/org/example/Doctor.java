package org.example;

/**
 * MAGI SYSTEM: Operational Agent (Doctor/Staff) Data Blueprint
 */
public class Doctor {
    private int id;
    private String name;
    private String specialty;

    // Constructor for registering NEW personnel (No ID assigned yet)
    public Doctor(String name, String specialty) {
        this.name = name;
        this.specialty = specialty;
    }

    // OVERLOADED CONSTRUCTOR: Hydrates existing personnel from the Central Dogma
    public Doctor(int id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    // --- MAGI Data Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}