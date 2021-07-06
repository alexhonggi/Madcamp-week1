package com.example.madcamp_week1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class PhoneFragment extends Fragment {
    private static final int REQUEST_CODE_PERMISSION = 2020;
    int childNum;
    FirebaseDatabase database = FirebaseDatabase.getInstance();// ...
    DatabaseReference myRef = database.getReference();

    private RecyclerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        while (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.INTERNET},
                    REQUEST_CODE_PERMISSION);
        }
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_phone, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        init(recyclerView);
        getData();

        ImageButton imageButton = rootView.findViewById(R.id.addUserBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AdduserActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        ImageButton searchButton = rootView.findViewById(R.id.searchUserBtn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout searchLinear = (LinearLayout) rootView.inflate(getContext(), R.layout.dialog_user,null);
                final LinearLayout showLinear = (LinearLayout) rootView.inflate(getContext(), R.layout.dialog_show,null);
                new AlertDialog.Builder(getContext()).setView(searchLinear).setPositiveButton("검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText name = (EditText) searchLinear.findViewById(R.id.searchUserDial);
                        String value = name.getText().toString();
                        String number = adapter.getItemNumber(value);
                        TextView resultText = (TextView) showLinear.findViewById(R.id.showNumberResult);
                        TextView result = (TextView) showLinear.findViewById(R.id.showNumberText);
                        resultText.setText("Result");
                        result.setText(value + " - " + number);
                        new AlertDialog.Builder(getContext()).setView(showLinear).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog2, int which) {
                                TextView resultText = (TextView) showLinear.findViewById(R.id.showNumberResult);
                                TextView result = (TextView) showLinear.findViewById(R.id.showNumberText);
                                resultText.setText("");
                                result.setText("");
                                dialog2.dismiss();
                            }
                        }).show();
                        dialog.dismiss();
                    }
                }).show();
            }
        });


        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2){
            if(data != null){
                String name = data.getStringExtra("name");
                String number = data.getStringExtra("number");
                //writeNewUser(String.valueOf(adapter.getItemCount()+1), name, number);
                Data newUser = new Data();
                newUser.setName(name);
                newUser.setNumber(number);
                myRef.child("users").child(String.valueOf(childNum+1)).setValue(newUser);
                myRef.child("number").setValue(String.valueOf(childNum + 1));
                adapter.addItem(newUser);
                adapter.notifyDataSetChanged();
            }

        }

    }

    /*private void writeNewUser(String userId, String name, String email) {
        Data user = new Data(name, email);

        myRef.child("users").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }*/
    private void init(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }
    /*private void readUser(String userID){
        myRef.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(dataSnapshot.getValue(Data.class) != null){
                    Data post = dataSnapshot.getValue(Data.class);
                    adapter.addItem(post);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "데이터 없음...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }*/

    private void getData() {
        // 임의의 데이터입니다.
        myRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                adapter.clear();
                childNum = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Data dData = snapshot.getValue(Data.class);
                    adapter.addItem(dData);
                    childNum++;
                    Log.e("TAG", "onDataChange:"+ dData.getName());
                }
                adapter.notifyDataSetChanged();
                myRef.child("number").setValue(String.valueOf(childNum));
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}