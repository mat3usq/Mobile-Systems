package com.example.pbmobilnezadanie9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDetailsActivity extends AppCompatActivity {
    private TextView bookTitleTextView;
    private TextView bookAuthorTextView;
    private TextView numberOfPagesTextView;
    private ImageView bookCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookTitleTextView = findViewById(R.id.book_title);
        bookAuthorTextView = findViewById(R.id.book_author);
        numberOfPagesTextView = findViewById(R.id.number_of_pages);
        bookCover = findViewById(R.id.img_cover);

        Intent starter = getIntent();
        bookTitleTextView.setText(starter.getStringExtra("book_title"));
        bookAuthorTextView.setText(starter.getStringExtra("book_author"));
        numberOfPagesTextView.setText(starter.getStringExtra("book_number_og_pages"));
        String cover = starter.getStringExtra("book_cover");
        if (cover != null)
            Picasso.with(this)
                    .load(MainActivity.IMAGE_URL_BASE + cover + "-L.jpg")
                    .placeholder(R.drawable.baseline_menu_book_24)
                    .into(bookCover);
        else
            bookCover.setImageResource(R.drawable.baseline_menu_book_24);
    }
}