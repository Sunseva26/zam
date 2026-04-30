package com.simplenotes.app;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> notesList;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAdd;
    private AlarmManager alarmManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dbHelper = new DatabaseHelper(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        loadNotes();
        
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
            startActivity(intent);
        });
        
        createNotificationChannel();
    }
    
    private void loadNotes() {
        notesList = dbHelper.getAllNotes();
        adapter = new NotesAdapter(notesList);
        recyclerView.setAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "notes_channel",
                "Напоминания заметок",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Канал для напоминаний о заметках");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
        private List<Note> notes;
        
        public NotesAdapter(List<Note> notes) {
            this.notes = notes;
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Note note = notes.get(position);
            holder.titleTextView.setText(note.getTitle());
            holder.contentPreview.setText(note.getContent().length() > 50 ? 
                note.getContent().substring(0, 50) + "..." : note.getContent());
            holder.dateTextView.setText(note.getCreatedDate());
            
            int[] colors = {0xFFE3F2FD, 0xFFF3E5F5, 0xFFE8F5E9, 0xFFFFF3E0, 0xFFFDEDEC};
            int colorIndex = position % colors.length;
            holder.cardView.setBackgroundColor(colors[colorIndex]);
            
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);
                intent.putExtra("note_id", note.getId());
                startActivity(intent);
            });
            
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(note);
                return true;
            });
        }
        
        @Override
        public int getItemCount() {
            return notes.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, contentPreview, dateTextView;
            LinearLayout cardView;
            
            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                contentPreview = itemView.findViewById(R.id.contentPreview);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                cardView = itemView.findViewById(R.id.cardView);
            }
        }
    }
    
    private void showDeleteDialog(Note note) {
        new AlertDialog.Builder(this)
            .setTitle("Удалить заметку")
            .setMessage("Вы уверены, что хотите удалить эту заметку?")
            .setPositiveButton("Да", (dialog, which) -> {
                dbHelper.deleteNote(note.getId());
                loadNotes();
            })
            .setNegativeButton("Нет", null)
            .show();
    }
    
    public void setReminder(Note note) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(note.getReminderTime());
        
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("note_title", note.getTitle());
        intent.putExtra("note_content", note.getContent());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this, note.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
  }
