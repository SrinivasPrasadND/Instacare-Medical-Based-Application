package com.example.srinivasprasad.instacare;

public class Diseases {

    public String dis_name;
    public String doc_id;

    public Diseases(){

    }
    public Diseases(String dis_name, String doc_id) {
        this.dis_name = dis_name;
        this.doc_id = doc_id;
    }

    public String getDis_name() {
        return dis_name;
    }

    public void setDis_name(String dis_name) {
        this.dis_name = dis_name;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }
}
