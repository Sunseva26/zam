package com.simplenotes.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTES = "notes";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NOTES + "(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT," +
            "content TEXT," +
            "created_date TEXT," +
            "reminder_time INTEGER)";
        db.execSQL(createTable);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
    
    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("created_date", note.getCreatedDate());
        values.put("reminder_time", note.getReminderTime());
        db.insert(TABLE_NOTES, null, values);
        db.close();
    }
    
    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        values.put("reminder_time", note.getReminderTime());
        db.update(TABLE_NOTES, values, "id = ?", new String[]{String.valueOf(note.getId())});
        db.close();
    }
    
    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, null, null, null, null, "id DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                note.setCreatedDate(cursor.getString(3));
                note.setReminderTime(cursor.getLong(4));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }
    
    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, null, "id = ?", 
            new String[]{String.valueOf(id)}, null, null, null);
        
        if (cursor != null) {
            cursor.moveToFirst();
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setContent(cursor.getString(2));
            note.setCreatedDate(cursor.getString(3));
            note.setReminderTime(cursor.getLong(4));
            cursor.close();
            return note;
        }
        return null;
    }
              }
