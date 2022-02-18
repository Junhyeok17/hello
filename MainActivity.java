package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button, button2, button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button3 = (Button)findViewById(R.id.place);
        button = (Button) findViewById(R.id.weather);
        button2 = (Button) findViewById(R.id.shopping);

        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if(view==button3)
            startActivity(new Intent(this, PlayingActivity.class));
        else if(view==button)
            startActivity(new Intent(this, WeatherActivity.class));
        else if(view==button2)
            startActivity(new Intent(this, ShoppingActivity.class));
    }

}