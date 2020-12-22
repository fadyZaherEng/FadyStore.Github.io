package com.example.notes;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowNote extends AppCompatActivity {

    @BindView(R.id.tv_Note_Title)
    TextView tvNoteTitle;
    @BindView(R.id.tv_Note_Content)
    TextView tvNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        ButterKnife.bind(this);

        tvNoteTitle.setText(getIntent().getStringExtra("title"));
        tvNoteContent.setText(getIntent().getStringExtra("content"));

    }

}