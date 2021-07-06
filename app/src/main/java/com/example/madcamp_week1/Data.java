package com.example.madcamp_week1;

public class Data {

    private String user_name;
    private String user_number;
    //private int resId;

    public Data(){
    }
    public Data(String name, String num){
        this.user_name = name;
        this.user_number = num;
    }
    public String getName() {
        return user_name;
    }

    public void setName(String name) {
        this.user_name = name;
    }

    public String getNumber() {
        return user_number;
    }

    public void setNumber(String number) {
        this.user_number = number;
    }

    @Override
    public String toString(){
        return "User{" +
                "userName='" + user_name + '\'' +
                ", email='" + user_number + '\'' +
                '}';
    }
}