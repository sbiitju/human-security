package com.asad.humansecurity;

public class Profile {
    private String name,email,priority1,priority2,priority3;

    public Profile(String name, String email, String priority1, String priority2, String priority3) {
        this.name = name;
        this.email = email;
        this.priority1 = priority1;
        this.priority2 = priority2;
        this.priority3 = priority3;
    }

    public Profile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String number) {
        this.email = number;
    }

    public String getPriority1() {
        return priority1;
    }

    public void setPriority1(String priority1) {
        this.priority1 = priority1;
    }

    public String getPriority2() {
        return priority2;
    }

    public void setPriority2(String priority2) {
        this.priority2 = priority2;
    }

    public String getPriority3() {
        return priority3;
    }

    public void setPriority3(String priority3) {
        this.priority3 = priority3;
    }
}
