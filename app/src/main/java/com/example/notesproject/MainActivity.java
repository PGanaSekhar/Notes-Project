package com.example.notesproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Spinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private NotesDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notes;
    private Spinner categoryFilterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new NotesDatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));

        loadNotes();
    }

    private void loadNotes() {
        notes = new ArrayList<>();
        Cursor cursor = dbHelper.getAllNotes();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                notes.add(new Note(id, title, content, category));
            } while (cursor.moveToNext());
        }
        cursor.close();

        noteAdapter = new NoteAdapter(this, notes);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes(); // Reload notes when returning to this activity
    }
}
