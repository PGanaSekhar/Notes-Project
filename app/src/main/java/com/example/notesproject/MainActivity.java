package com.example.notesproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
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
        FloatingActionButton fab = findViewById(R.id.fab);
        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);

        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));

        setupCategoryFilterSpinner();
        loadNotes(null); // Load all notes initially
    }

    private void setupCategoryFilterSpinner() {
        // Retrieve categories from database and add an "All" option
        ArrayList<String> categories = dbHelper.getAllCategories();
        categories.add(0, "All");

        // Set up ArrayAdapter for the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryFilterSpinner.setAdapter(adapter);

        // Set listener to filter notes based on the selected category
        categoryFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if (selectedCategory.equals("All")) {
                    loadNotes(null); // Load all notes
                } else {
                    loadNotes(selectedCategory); // Load notes by selected category
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void loadNotes(String category) {
        notes = new ArrayList<>();
        Cursor cursor = (category == null) ? dbHelper.getAllNotes() : dbHelper.getNotesByCategory(category);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String cat = cursor.getString(cursor.getColumnIndex("category"));
                notes.add(new Note(id, title, content, cat));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (noteAdapter == null) {
            noteAdapter = new NoteAdapter(this, notes);
            recyclerView.setAdapter(noteAdapter);
        } else {
            noteAdapter.updateNotes(notes);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes(null); // Reload all notes when returning to this activity
    }
}
