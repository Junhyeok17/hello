package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class PlayingItem{
    ImageView imageView;
    TextView textView;
    Button button;
    Button button2;
    String url;
    String name;

    PlayingItem(){}
    ImageView getImageView(){ return imageView; }
    TextView getTextView() { return textView; }
    Button getButton() { return button; }
    Button getButton2() { return  button2; }
}

public class PlayingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    List<PlayingItem> list;
    String degree = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        RecyclerView recyclerView = findViewById(R.id.playingRecycler);

        int celsius = -3;

        if(celsius<=4)
            degree+="~4";
        else if(4<celsius && celsius<=8)
            degree+="4~8";
        else if(8<celsius && celsius<=12)
            degree+="8~12";
        else if(12<celsius && celsius<=16)
            degree+="12~16";
        else if(16<celsius && celsius<=20)
            degree+="16~20";
        else if(20<celsius && celsius<=22)
            degree+="20~22";
        else if(22<celsius && celsius<=27)
            degree+="22~27";
        else if(27<celsius)
            degree+="27~";

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        list = new LinkedList<>();
        firebaseDatabase.getReference().child("Places").child(degree).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();

                        for(int i=0;i<map.size()/2;i++){
                            PlayingItem item = new PlayingItem();
                            item.name = map.get("places"+(i+1)+"_1");
                            item.url = map.get("places"+(i+1)+"_2");
                            //Log.d("contents", item.name+item.url);
                            list.add(item);
                        }

                        Log.d("size", list.size()+"");
                        recyclerView.setLayoutManager(new LinearLayoutManager(PlayingActivity.this));
                        recyclerView.setAdapter(new CustomAdapter(list));
                        recyclerView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(seoul);
        markerOptions.title("서울");
        gMap.addMarker(markerOptions);

        gMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        PlayingItem items;

        ViewHolder(View view){
            super(view);

            items = new PlayingItem();
            items.imageView = view.findViewById(R.id.playingImage);
            items.textView = view.findViewById(R.id.placeName);
            items.button = view.findViewById(R.id.lookMap);
            items.button2 = view.findViewById(R.id.lookSite);
        }

        ImageView getImageView() { return items.imageView; }
        TextView getTextView() { return items.textView; }
        Button getButtonView() { return items.button; }
        Button getButton2View() { return items.button2; }
    }

    private class CustomAdapter extends RecyclerView.Adapter<ViewHolder>{
        private List<PlayingItem> dataSet;

        CustomAdapter(List<PlayingItem> dataSet) { this.dataSet = dataSet; }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.playinglist, viewGroup, false);
            return new ViewHolder(view);
        }

        public int getItemCount() { return dataSet.size(); }

        public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            PlayingItem item = dataSet.get(position);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    storageReference.child("Places").child(degree).child("places"+(position+1)+"-1.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplication()).load(uri).into(viewHolder.getImageView());
                        }
                    });
                }
            }).start();
            viewHolder.getTextView().setText(item.name);
            viewHolder.getButtonView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            viewHolder.getButton2View().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSet.get(position).url));
                    startActivity(intent);
                }
            });
        }
    }
}