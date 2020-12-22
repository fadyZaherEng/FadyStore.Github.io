package com.example.notes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogAddNote extends androidx.fragment.app.DialogFragment {

    private onPositiveClickListner onPositiveClickListner;

    public DialogAddNote() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onPositiveClickListner){
            onPositiveClickListner= (onPositiveClickListner) context;
        }else {
            throw new RuntimeException("Please implement interface of onPositiveClickLister");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.dialog_add_note,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText et_title=view.findViewById(R.id.et_title);
        TextView counter=view.findViewById(R.id.count);
        et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             String text=et_title.getText().toString();
             int sum=text.length();
             counter.setText(sum+"/22");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText et_content=view.findViewById(R.id.et_content);
        Button positiveButton=view.findViewById(R.id.btn_addNote);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onPositiveClickListner.onPositiveClicked(et_title.getText().toString(),et_content.getText().toString());
                if (!et_title.getText().toString().isEmpty()&&!et_content.getText().toString().isEmpty()){
                    dismiss();
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPositiveClickListner=null;
    }
}
interface onPositiveClickListner {
    void onPositiveClicked(String title,String content);
}