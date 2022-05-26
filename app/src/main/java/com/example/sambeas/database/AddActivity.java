package com.example.sambeas.database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sambeas.R;

public class AddActivity extends AppCompatActivity {

    EditText titleInput, authorInput, pagesInput;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        titleInput.findViewById(R.id.title_input);
        authorInput.findViewById(R.id.author_input);
        pagesInput.findViewById(R.id.pages_input);

        addButton.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                myDB.addBook(titleInput.getText().toString().trim(),
                        authorInput.getText().toString().trim(),
                        Integer.parseInt(pagesInput.getText().toString().trim()));
            }
        });
    }
}