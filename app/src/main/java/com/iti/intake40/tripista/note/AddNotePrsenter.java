package com.iti.intake40.tripista.note;

import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Note;

public class AddNotePrsenter implements AddNoteContract.PresenterInterface {
   private FireBaseCore core;
   private AddNoteContract.ViewInterface addNote;

    public AddNotePrsenter(FireBaseCore core, AddNoteContract.ViewInterface addNote) {
        this.core = core;
        this.addNote = addNote;
    }

    @Override
    public void replyByMessage(int message) {
        addNote.sentMessage(message);
    }

    @Override
    public void replyByError(int message) {
      addNote.sentError(message);
    }

    @Override
    public void addNote(Note note, String tripID) {
     core.addNote(note,tripID,this);
    }


}
