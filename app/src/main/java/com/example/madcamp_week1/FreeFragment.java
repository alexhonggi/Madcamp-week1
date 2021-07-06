package com.example.madcamp_week1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class FreeFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    private static final int REQUEST_CODE_PERMISSIONS = 2021;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    public GoogleMap map;
    FirebaseDatabase database = FirebaseDatabase.getInstance();// ...
    DatabaseReference myRef = database.getReference();
    int childNum = 0;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            LatLng seoulStation = new LatLng(37.55526, 126.97082);
            Marker tmpMark = googleMap.addMarker(new MarkerOptions().position(seoulStation).title("서울역"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoulStation));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
            tmpMark.setTag(0);
            map.setOnMarkerClickListener(FreeFragment.this::onMarkerClick);
        }

        /*public void movingCamera(GoogleMap googleMap, LatLng newPlace) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(newPlace));
        }*/
    };

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            String title = marker.getTitle();
            String phoneNumber = marker.getSnippet();
            if (phoneNumber == null){
                Toast.makeText(getActivity(), marker.getTitle() + "은(는) 번호가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            }else{
                final LinearLayout addPlaceLinear = (LinearLayout) getView().inflate(getContext(), R.layout.dialog_place,null);
                new AlertDialog.Builder(getContext()).setView(addPlaceLinear).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newNumber = phoneNumber.substring(4);
                        Data newUser = new Data(title, "0"+newNumber);
                        myRef.child("users").child(String.valueOf(childNum+1)).setValue(newUser);
                        dialog.dismiss();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getData();
        return inflater.inflate(R.layout.fragment_free, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        while (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSIONS);
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (!Places.isInitialized()){
            String apikey = getString(R.string.google_maps_key);
            Places.initialize(getContext(), apikey);
        }

        ImageButton searchMapBtn = (ImageButton) view.findViewById(R.id.search_map_btn);
        searchMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                        Place.Field.PHONE_NUMBER);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("KR")
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng newLatLng = place.getLatLng();
                Marker tmpMarker = map.addMarker(new MarkerOptions().position(newLatLng).title(place.getName()).snippet(place.getPhoneNumber()));
                map.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
                map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                tmpMarker.setTag(0);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e("error -> ", status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_PERMISSIONS:
                if ((grantResults.length > 0) && (
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)){

                }else{
                    Toast.makeText(getContext(), "권한 허용 필요", Toast.LENGTH_SHORT).show();
                }
        }
    }
    private void getData() {
        // 임의의 데이터입니다.
        myRef.child("number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                childNum = 0;
                if(dataSnapshot.getValue() != null){
                    childNum = Integer.valueOf((String) dataSnapshot.getValue());
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}