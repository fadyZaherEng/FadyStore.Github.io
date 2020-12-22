package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterNotes extends RecyclerView.Adapter<AdapterNotes.noteHolder> {

 private Context context;
 private ArrayList<Note> notes;
 private onItemClickListenerRv onItemClickListenerRv;
 private onItemLongClickListenerRv onItemLongClickListenerRv;

 public AdapterNotes(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
        if (context instanceof onItemClickListenerRv){
            onItemClickListenerRv= (com.example.notes.onItemClickListenerRv) context;
        }else {
         throw new RuntimeException("Please implement onItemClickListenerRv interface");
        }
        if (context instanceof onItemLongClickListenerRv){
            onItemLongClickListenerRv= (com.example.notes.onItemLongClickListenerRv) context;
        }else {
            throw new RuntimeException("Please implement onItemLongClickListenerRv interface");
        }
    }
    public void addItem(Note note){
        notes.add(note);
        notifyDataSetChanged();
    }
    public void deleteItm(int Pos){
     notes.remove(Pos);
     notifyDataSetChanged();
    }
    public Note getItem(int position){
       return notes.get(position);
    }
    public void updateItem(int position,Note note){
         notes.set(position,note);
    }
    public void clearList(){
        notes.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public noteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_rv_note,null,false);
        noteHolder holder=new noteHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull noteHolder holder, int position) {
       Note note=notes.get(position);
       holder.tv_Title.setText(note.getTitle());
       holder.tv_Date.setText(note.getTimestamp());
       holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListenerRv.onItemClicked(position);
            }
        });
       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               onItemLongClickListenerRv.onItemLongClicked(position);
               return true;
           }
       });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class noteHolder extends RecyclerView.ViewHolder{
     TextView tv_Title,tv_Date;
     public noteHolder(@NonNull View itemView) {
         super(itemView);
         tv_Title=itemView.findViewById(R.id.tv_title);
         tv_Date=itemView.findViewById(R.id.tv_date);
     }
 }
}
interface onItemClickListenerRv    {
    void onItemClicked(int position);
}
interface onItemLongClickListenerRv {
    void onItemLongClicked(int position);
}