package org.example;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String ailment;

    public Patient(String name, int age, String ailment) {
        this.name = name;
        this.age = age;
        this.ailment = ailment;
    }

    public Patient(int id, String name, int age, String ailment) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.ailment = ailment;
    }
    // Getter for the ID column mapping factory
    public int getId() { return id; }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getAilment() { return ailment; }
}