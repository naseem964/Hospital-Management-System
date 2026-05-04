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

    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getAilment() { return ailment; }
}