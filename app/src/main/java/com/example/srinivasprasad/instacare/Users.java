package com.example.srinivasprasad.instacare;

public class Users {

    public String name;
    public String image;
    public String thumb_url;
    public String blood_group;
    public String locality;
    public String doc_id;

    public Users(){}
    public Users(String name, String image, String thumb_url, String blood_group, String locality,String doc_id) {
        this.name = name;
        this.image = image;
        this.thumb_url = thumb_url;
        this.blood_group = blood_group;
        this.locality = locality;
        this.doc_id = doc_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

}
