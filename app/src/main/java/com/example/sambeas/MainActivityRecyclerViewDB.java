package com.example.sambeas;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sambeas.database.MyDatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivityRecyclerViewDB extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton floatingAddButton;

    MyDatabaseHelper myDB;
    ArrayList<String> book_id, book_title, book_author, book_pages;

    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler_view_db);

        recyclerView = findViewById(R.id.recyclerViewing);
        floatingAddButton = findViewById(R.id.floatingAddButton);

        floatingAddButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivityRecyclerViewDB.this, AddActivity.class);
            startActivity(intent);

        });

        myDB = new MyDatabaseHelper(MainActivityRecyclerViewDB.this);
        book_id = new ArrayList<>();
        book_author = new ArrayList<>();
        book_title = new ArrayList<>();
        book_pages = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivityRecyclerViewDB.this, book_id, book_author, book_title, book_pages);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivityRecyclerViewDB.this))
        ;

    }

    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                book_id.add(cursor.getString(0));
                book_title.add(cursor.getString(1));
                book_author.add(cursor.getString(2));
                book_pages.add(cursor.getString(3));
            }
        }
    }
}