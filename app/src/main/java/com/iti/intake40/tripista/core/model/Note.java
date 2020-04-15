package com.iti.intake40.tripista.core.model;

public class Note {
    String noteDesc;
    int noteState;
    String noteID ;

    public Note() {
    }

    public Note(String noteDesc, int noteState) {
        this.noteDesc = noteDesc;
        this.noteState = noteState;
    }

    public Note(String noteDesc) {
        this.noteDesc = noteDesc;
    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public String getNoteDesc() {
        return noteDesc;
    }

    public void setNoteDesc(String noteDesc) {
        this.noteDesc = noteDesc;
    }

    public int getNoteState() {
        return noteState;
    }

    public void setNoteState(int noteState) {
        this.noteState = noteState;
    }
}
