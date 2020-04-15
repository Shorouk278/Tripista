package com.iti.intake40.tripista.note;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Note;

public class AddNote extends AppCompatActivity implements AddNoteContract.ViewInterface {
    private FireBaseCore core;
    private AddNoteContract.PresenterInterface addNotePrsenter;
    private String tripID;
    private EditText etDescripe;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Note");
        setContentView(R.layout.activity_add_note);
        etDescripe = findViewById(R.id.et_note_desc);
        core = FireBaseCore.getInstance();
        addNotePrsenter = new AddNotePrsenter(core, this);
        if (getIntent() != null)
            tripID = getIntent().getExtras().getString("ID");


    }


    @Override
    public void sentMessage(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_LONG).show();

    }

    @Override
    public void sentError(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_LONG).show();

    }

    public void addNote(View view) {
        String description = etDescripe.getText().toString();
        if (description != null) {
           note =new Note(description,0);
           addNotePrsenter.addNote(note,tripID);
           finish();
        }
        else
        {
            Toast.makeText(this,"empty note not allowed",Toast.LENGTH_LONG).show();
        }
    }
}
