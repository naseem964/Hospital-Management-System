package org.example;

/**
 * MAGI SYSTEM: Subject (Patient) Data Blueprint with Personnel Sync-Link
 */
public class Patient {
    private int id;
    private String name;
    private int age;
    private String ailment;
    private int doctorId; // NEW: The Synchro-Link to the assigned Agent

    // Constructor for registering NEW subjects (Requires assigned personnel)
    public Patient(String name, int age, String ailment, int doctorId) {
        this.name = name;
        this.age = age;
        this.ailment = ailment;
        this.doctorId = doctorId;
    }

    // OVERLOADED CONSTRUCTOR: Hydrates existing subjects from the Central Dogma
    public Patient(int id, String name, int age, String ailment, int doctorId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.ailment = ailment;
        this.doctorId = doctorId;
    }

    // --- MAGI Data Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getAilment() { return ailment; }
    public void setAilment(String ailment) { this.ailment = ailment; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
}