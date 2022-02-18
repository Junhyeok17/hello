package com.example.myapplication;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class ShoppingItem{
    ImageView imageView;
    Uri uri;
    String link;
    TextView textView;
    String name;

    ShoppingItem(){}
    ShoppingItem(String link, String name){
        this.link = link;
        this.name = name;
    }
    void setUri(Uri uri){
        this.uri = uri;
    }
    void setLink(String link){
        this.link = link;
    }
    void setName(String name){
        this.name = name;
    }
    Uri getUri(){ return uri; }
    String getLink() { return link; }
    String getName() { return name; }
}

public class ShoppingActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    HashMap<String, String> map;
    String degree = "";
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        List<ShoppingItem> list = new ArrayList<>();

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

        Log.d("route", "Clothes/" + degree+"/clothes"+1+"-1.png");

        firebaseDatabase.getReference().child("Clothes").child(degree).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Log.d("asdf", dataSnapshot.toString());
                        map = (HashMap<String, String>) dataSnapshot.getValue();
                        for(int i=0;i<map.size()/2;i++){
                            String name = map.get("clothes"+(i+1)+"_2");
                            String link = map.get("clothes"+(i+1)+"_1");

                            list.add(new ShoppingItem(link, name));
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingActivity.this));
                        recyclerView.setAdapter(new CustomAdapter(list));
                    }
                });
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        ShoppingItem items;

        ViewHolder(View view){
            super(view);

            items = new ShoppingItem();
            items.imageView = view.findViewById(R.id.imageView);
            items.textView = view.findViewById(R.id.name);
        }

        ImageView getImageView() { return items.imageView; }
        TextView getTextView() { return items.textView; }
    }

    private class CustomAdapter extends RecyclerView.Adapter<ViewHolder>{
        private List<ShoppingItem> dataSet;

        CustomAdapter(List<ShoppingItem> dataSet) { this.dataSet = dataSet; }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.shoppinglist, viewGroup, false);
            return new ViewHolder(view);
        }

        public int getItemCount() { return dataSet.size(); }

        public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") int position){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    storageReference.child("Clothes").child(degree).child("clothes"+(position+1)+"-1.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getApplication()).load(uri).into(viewHolder.getImageView());
                        }
                    });
                }
            }).start();
            Log.d("size", dataSet.size()+"");
            viewHolder.getTextView().setText(dataSet.get(position).name);
            viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSet.get(position).link));
                    startActivity(intent);
                }
            });
        }
    }
}