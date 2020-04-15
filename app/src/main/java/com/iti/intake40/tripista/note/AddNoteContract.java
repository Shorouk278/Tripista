package com.iti.intake40.tripista.note;

import com.google.firebase.auth.FirebaseUser;
import com.iti.intake40.tripista.core.model.Note;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.core.model.UserModel;

import java.util.List;

public interface AddNoteContract {
    interface PresenterInterface {
        void replyByMessage(int message);

        void replyByError(int message);

        void addNote(Note note, String tripID);
    }

    interface ViewInterface {
        void sentMessage(int message);

        void sentError(int message);


    }
}
