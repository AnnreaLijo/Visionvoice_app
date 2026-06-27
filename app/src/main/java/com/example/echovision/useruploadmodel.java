package com.example.echovision;

public class useruploadmodel {
    String id,name,phoneno,personname,personimage;

    public useruploadmodel(String id, String name, String phoneno, String personname, String personimage) {
        this.id = id;
        this.name = name;
        this.phoneno = phoneno;
        this.personname = personname;
        this.personimage = personimage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getPersonname() {
        return personname;
    }

    public String getPersonimage() {
        return personimage;
    }
}
