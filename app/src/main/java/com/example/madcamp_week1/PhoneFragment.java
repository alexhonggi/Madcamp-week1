package com.example.madcamp_week1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class PhoneFragment extends Fragment {

    private RecyclerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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


        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2){
            if(data != null){
                String name = data.getStringExtra("name");
                String number = data.getStringExtra("number");
                Log.e("name", name);
                Log.e("number", number);
                Data newUser = new Data();
                newUser.setName(name);
                newUser.setNumber(number);
                adapter.addItem(newUser);
                adapter.notifyDataSetChanged();
            }

        }

    }

    public String getJsonString(){
        String json = "";
        try {
            InputStream is = getActivity().getAssets().open("phone_book.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return json;
    }
    private void init(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        // 임의의 데이터입니다.
        String json = getJsonString();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray userArray = jsonObject.getJSONArray("Users");
            for(int i = 0; i < userArray.length(); i++){
                JSONObject userObject = userArray.getJSONObject(i);
                Data user = new Data();

                user.setName(userObject.getString("name"));
                user.setNumber(userObject.getString("number"));
                adapter.addItem(user);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}