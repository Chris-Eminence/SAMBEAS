package com.example.sambeas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    ArrayList book_id, book_title, book_author, book_pages;

    CustomAdapter(Context context, ArrayList book_id,
                  ArrayList book_title,
                  ArrayList book_author,
                  ArrayList book_pages){

        this.context = context;
        this.book_id = book_id;
        this.book_title = book_title;
        this.book_author = book_author;
        this.book_pages = book_pages;
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
          holder._book_id.setText(String.valueOf(book_id.get(position)));
          holder._book_title.setText(String.valueOf(book_title.get(position)));
          holder._book_author.setText(String.valueOf(book_author.get(position)));
          holder._book_pages.setText(String.valueOf(book_pages.get(position)));
    }

    @Override
    public int getItemCount() {

        return book_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView _book_id, _book_title, _book_author, _book_pages;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            _book_id = itemView.findViewById(R.id.book_id_text);
            _book_title = itemView.findViewById(R.id.book_title_text);
            _book_author = itemView.findViewById(R.id.book_title_author);
            _book_pages = itemView.findViewById(R.id.book_pages_text);
        }
    }
}
