package com.example.notes;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements  onPositiveClickListner,onItemClickListenerRv,onItemLongClickListenerRv {

    @BindView(R.id.rv_Note)
    RecyclerView rvNote;
    @BindView(R.id.add_note)
    FloatingActionButton addNote;

    //firebase Real Time Database

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    ArrayList<Note> notes;
    AdapterNotes adapterNotes;
    FirebaseAuth auth;
    String currentUserId;
    RadioButton radioButton=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        auth=FirebaseAuth.getInstance();
        notes = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Notes");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         finish();
    }

    private void getCurrentUser(){
        if (auth.getCurrentUser()==null||!auth.getCurrentUser().isEmailVerified()){
            Intent intent=new Intent(this,SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void createAdapterRV(){
        adapterNotes = new AdapterNotes(MainActivity.this, notes);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this);
        rvNote.setHasFixedSize(true);
        rvNote.setLayoutManager(manager);
        rvNote.setAdapter(adapterNotes);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        notes.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Note note = dataSnapshot.getValue(Note.class);
                            notes.add(0, note);
                        }
                        createAdapterRV();
                        adapterNotes.notifyDataSetChanged();
                    }
                    else {
                        createAdapterRV();
                        adapterNotes.clearList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @OnClick(R.id.add_note)
    public void onViewClicked()
    {
        DialogAddNote dialogAddNote = new DialogAddNote();
        dialogAddNote.show(getSupportFragmentManager(), null);
    }

    protected String getCurrentTime()
    {
        String timeStamp = new SimpleDateFormat("EEEE hh:mm a ").format(Calendar.getInstance().getTime());
        String timeStampDate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        return timeStampDate + " " + timeStamp;
    }

    @Override
    public void onPositiveClicked(String title, String content)
    {

        if (auth.getCurrentUser() != null)
        {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CheckInternetConnection cic = new CheckInternetConnection(getApplicationContext());
            Boolean ch = cic.isConnectingToInternet();
            if (!ch) {

                Toast.makeText(this, R.string.notConnection, Toast.LENGTH_LONG).show();
            }
            else
            {
                if (!title.isEmpty() && !content.isEmpty()) {
                    String id = reference.child(currentUserId).push().getKey();
                    String TimeStamp = getCurrentTime();
                    Note note = new Note(title, content, TimeStamp, id);
                    reference.child(currentUserId).child(id).setValue(note);
                    Toast.makeText(this, R.string.addMsg, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.emptyDetails, Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    @Override
    public void onItemClicked(int position)
    {
        Intent SendNote = new Intent(this, ShowNote.class);
        SendNote.putExtra("title", notes.get(position).getTitle());
        SendNote.putExtra("content", notes.get(position).getContent());
        startActivity(SendNote);
    }


    @Override
    public void onItemLongClicked(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //inflate
        View view = LayoutInflater.from(this).inflate(R.layout.delete_update_dialog, null, false);
        EditText et_title = view.findViewById(R.id.et_title);
        EditText et_content = view.findViewById(R.id.et_content);
        TextView numberOfCharachter = view.findViewById(R.id.counter);
        //عشان احدد عدد الحروف داخل ال edit text
        et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = et_title.getText().toString();
                numberOfCharachter.setText(text.length() + "/22");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //set data
        String title_update = notes.get(position).getTitle();
        String content_update = notes.get(position).getContent();
        et_title.setText(notes.get(position).getTitle());
        et_content.setText(notes.get(position).getContent());

        builder.setView(view);
        builder.create();
        final AlertDialog alertDialog = builder.show();
        if (auth.getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            view.findViewById(R.id.btn_updateNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckInternetConnection cic = new CheckInternetConnection(getApplicationContext());
                    Boolean ch = cic.isConnectingToInternet();

                    if (!ch) {
                        Toast.makeText(MainActivity.this, R.string.notConnection, Toast.LENGTH_LONG).show();
                    }
                    else {

                        String title = et_title.getText().toString();
                        String content = et_content.getText().toString();
                        if (title.equals(title_update) && content.equals(content_update)) {
                            Toast.makeText(MainActivity.this, "You Do Not Make Any Change!!", Toast.LENGTH_LONG).show();
                        } else if (!title.isEmpty() && !content.isEmpty()) {
                            String id = notes.get(position).getId();
                            String TimeStamp = getCurrentTime();
                            Note note = new Note(title, content, TimeStamp, id);
                            reference.child(currentUserId).child(id).setValue(note);
                            Toast.makeText(MainActivity.this, R.string.updateMsg, Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.emptyDetails, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            view.findViewById(R.id.btn_deleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckInternetConnection cic = new CheckInternetConnection(getApplicationContext());
                    Boolean ch = cic.isConnectingToInternet();

                    if (!ch) {
                        Toast.makeText(MainActivity.this, R.string.notConnection, Toast.LENGTH_LONG).show();
                    } else {
                        reference.child(currentUserId).child(notes.get(position).getId()).removeValue();
                        Toast.makeText(MainActivity.this, R.string.deleteMsg, Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                }
            });
        }
    }
    //Option Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.out:

                finish();
                break;
            case R.id.LogOut:

                FirebaseAuth.getInstance().signOut();
                Intent  SignIn=new Intent(this,SignInActivity.class);
                startActivity(SignIn);
                break;

            case R.id.help:

                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                View  view=LayoutInflater.from(this).inflate(R.layout.dialog_help,null,false);
                builder.setView(view);
                builder.create();
               final AlertDialog alertDialog= builder.show();
               view.findViewById(R.id.btnOkHelp).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       alertDialog.dismiss();
                   }
               });
               break;

            case R.id.language:

                AlertDialog.Builder build=new AlertDialog.Builder(this);
                View  v=LayoutInflater.from(this).inflate(R.layout.dialog_language,null,false);
                build.setView(v);
                build.create();
                final AlertDialog Dialog= build.show();
                RadioGroup  RadioGroup =v.findViewById(R.id.RG_language);
                v.findViewById(R.id.btnOkLanguage).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedID = RadioGroup.getCheckedRadioButtonId();
                        if (selectedID !=-1) {
                            radioButton = RadioGroup.findViewById(selectedID);
                            if (radioButton.getText().toString().equals("Arabic")) {
                                setLocals("ar");
                                Toast.makeText(MainActivity.this, R.string.changedArabic, Toast.LENGTH_LONG).show();
                            } else {
                                setLocals("en");
                                Toast.makeText(MainActivity.this, R.string.changedEnglish, Toast.LENGTH_LONG).show();
                            }
                            Dialog.dismiss();
                        }
                        else {
                            Toast.makeText(MainActivity.this, R.string.selectLangauge, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;

            default:
                break;
        }

        return true;
    }
     //change Language
    private void setLocals(String locals){
        Locale locale=new Locale(locals);
        Resources resource =getResources();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        Configuration configuration=resource.getConfiguration();
        configuration.locale=locale;
        resource.updateConfiguration(configuration,displayMetrics);
    }

}
