package com.iti.intake40.tripista.core.model;

import java.util.HashMap;

public class Trip {

    /*
    private fields
     */
    private String tripId;
    private String title;
    private String date;
    private String time;
    private String startPoint;
    private String endPoint;
    private String backDate;
    private String backTime;
    private String backStartPoint;
    private String backEndPoint;
    private Status status; //upcomming / done / cancelled
    private Type type; //one way / round trip
    private Repeatation repeatation; //none, daily, weekly, monthly
    private double distance;
    //ID's to cancel the alaram
    private int cancelID;
    private int backCancelID;
    private HashMap<String,Note> notes ;
    private double startLat;
    private double startLg;
    private double endLat;
    private double endLg;

    /*
    enums
     */
    public enum Type {
        ONE_WAY,
        ROUND_TRIP
    }

    public enum Status {
        UPCOMMING,
        DONE,
        CANCELLED,
        IN_PROGRESS
    }

    public enum Repeatation {
        NONE,
        DAILY,
        WEEKLY,
        MONTHLY
    }

    /*
    constructors
     */

    public Trip() {
    }

    public Trip(String tripId, String title, String date, String time, String startPoint, String endPoint, String backDate, String backTime, String backStartPoint, String backEndPoint, Status status, Type type, Repeatation repeatation, int distance, int cancelID, int backCancelID, HashMap<String, Note> notes) {
        this.tripId = tripId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.backDate = backDate;
        this.backTime = backTime;
        this.backStartPoint = backStartPoint;
        this.backEndPoint = backEndPoint;
        this.status = status;
        this.type = type;
        this.repeatation = repeatation;
        this.distance = distance;
        this.cancelID = cancelID;
        this.backCancelID = backCancelID;
        this.notes = notes;
    }

    // one way trip without notes
    public Trip(String tripId, String title,
                String date, String time, String startPoint, String endPoint,
                Status status, Type type) {
        this.tripId = tripId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.status = status;
        this.type = type;
    }

    //round trip without notes
    public Trip(String tripId, String title,
                String date, String time, String startPoint, String endPoint,
                String backDate, String backTime, String backStartPoint, String backEndPoint,
                Status status, Type type) {
        this.tripId = tripId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.backDate = backDate;
        this.backTime = backTime;
        this.backStartPoint = backStartPoint;
        this.backEndPoint = backEndPoint;
        this.status = status;
        this.type = type;
    }

    /*
    getters and setters
     */

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLg() {
        return startLg;
    }

    public void setStartLg(double startLg) {
        this.startLg = startLg;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLg() {
        return endLg;
    }

    public void setEndLg(double endLg) {
        this.endLg = endLg;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getBackStartPoint() {
        return backStartPoint;
    }

    public void setBackStartPoint(String backStartPoint) {
        this.backStartPoint = backStartPoint;
    }

    public String getBackEndPoint() {
        return backEndPoint;
    }

    public void setBackEndPoint(String backEndPoint) {
        this.backEndPoint = backEndPoint;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getBackTime() {
        return backTime;
    }

    public void setBackTime(String backTime) {
        this.backTime = backTime;
    }

    public String getBackDate() {
        return backDate;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate;
    }

    public HashMap<String, Note> getNotes() {
        return notes;
    }

    public void setNotes(HashMap<String, Note> notes) {
        this.notes = notes;
    }

    public int getCancelID() {
        return cancelID;
    }

    public void setCancelID(int cancelID) {
        this.cancelID = cancelID;
    }

    public int getBackCancelID() {
        return backCancelID;
    }

    public void setBackCancelID(int backCancelID) {
        this.backCancelID = backCancelID;
    }

    public Repeatation getRepeatation() {
        return repeatation;
    }

    public void setRepeatation(Repeatation repeatation) {
        this.repeatation = repeatation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId='" + tripId + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", backDate='" + backDate + '\'' +
                ", backTime='" + backTime + '\'' +
                ", backStartPoint='" + backStartPoint + '\'' +
                ", backEndPoint='" + backEndPoint + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", repeatation=" + repeatation +
                ", distance=" + distance +
                ", cancelID=" + cancelID +
                ", backCancelID=" + backCancelID +
                ", notes=" + notes +
                '}';
    }
}
