package com.simplenotes.app;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;

public class NoteEditorActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText;
    private Button saveButton, setReminderButton;
    private DatabaseHelper dbHelper;
    private int noteId = -1;
    private Note currentNote;
    private Calendar reminderCalendar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        
        dbHelper = new DatabaseHelper(this);
        
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        saveButton = findViewById(R.id.saveButton);
        setReminderButton = findViewById(R.id.setReminderButton);
        
        Intent intent = getIntent();
        if (intent.hasExtra("note_id")) {
            noteId = intent.getIntExtra("note_id", -1);
            loadNote();
        }
        
        saveButton.setOnClickListener(v -> saveNote());
        
        setReminderButton.setOnClickListener(v -> showDateTimePicker());
    }
    
    private void loadNote() {
        currentNote = dbHelper.getNote(noteId);
        if (currentNote != null) {
            titleEditText.setText(currentNote.getTitle());
            contentEditText.setText(currentNote.getContent());
        }
    }
    
    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        
        if (title.isEmpty()) {
            titleEditText.setError("Введите заголовок");
            return;
        }
        
        if (currentNote == null) {
            currentNote = new Note();
        }
        
        currentNote.setTitle(title);
        currentNote.setContent(content);
        
        if (noteId == -1) {
            dbHelper.addNote(currentNote);
        } else {
            currentNote.setId(noteId);
            dbHelper.updateNote(currentNote);
        }
        
        finish();
    }
    
    private void showDateTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, year, month, dayOfMonth) -> {
                reminderCalendar = Calendar.getInstance();
                reminderCalendar.set(year, month, dayOfMonth);
                
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view1, hourOfDay, minute) -> {
                        reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        reminderCalendar.set(Calendar.MINUTE, minute);
                        reminderCalendar.set(Calendar.SECOND, 0);
                        
                        if (currentNote == null) {
                            currentNote = new Note();
                        }
                        currentNote.setReminderTime(reminderCalendar.getTimeInMillis());
                        
                        Toast.makeText(this, "Напоминание установлено", Toast.LENGTH_SHORT).show();
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    true);
                timePickerDialog.show();
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
