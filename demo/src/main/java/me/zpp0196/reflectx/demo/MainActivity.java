package me.zpp0196.reflectx.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.zpp0196.reflectx.demo.test.AndroidProxyTest;
import me.zpp0196.reflectx.demo.test.JavaProxyTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JavaProxyTest.sTestAll();
        AndroidProxyTest.sTestAll();
    }
}
