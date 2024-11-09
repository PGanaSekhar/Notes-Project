package com.example.notesproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class EditNoteActivity extends AppCompatActivity {

    private EditText etTitle, etContent, etCategory;
    private NotesDatabaseHelper dbHelper;
    private int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etCategory = findViewById(R.id.etCategory);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);

        dbHelper = new NotesDatabaseHelper(this);

        // Get note ID passed from MainActivity
        noteId = getIntent().getIntExtra("noteId", -1);
        loadNoteDetails();

        btnUpdate.setOnClickListener(v -> updateNote());
        btnDelete.setOnClickListener(v -> deleteNote());
    }

    private void loadNoteDetails() {
        // Load the note details based on note ID
        Note note = dbHelper.getNoteById(noteId);
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());
            etCategory.setText(note.getCategory());
        } else {
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String category = etCategory.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = dbHelper.updateNote(noteId, title, content, category);
        if (isUpdated) {
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNote() {
        boolean isDeleted = dbHelper.deleteNoteById(noteId);
        if (isDeleted) {
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show();
        }
    }
}
