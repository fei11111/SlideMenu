package com.fei.slidemenu;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void menuBg(View view) {
        Toast.makeText(this, "菜单栏背景", Toast.LENGTH_SHORT).show();
    }

    public void contentBg(View view) {
        Toast.makeText(this, "内容页背景", Toast.LENGTH_SHORT).show();
    }

    public void menuName(View view) {
        Toast.makeText(this, "菜单栏名字", Toast.LENGTH_SHORT).show();
    }

    public void menuGallery(View view) {
        Toast.makeText(this, "菜单栏相册", Toast.LENGTH_SHORT).show();
    }

    public void menuCollection(View view) {
        Toast.makeText(this, "菜单栏收藏", Toast.LENGTH_SHORT).show();
    }

    public void menuPersonal(View view) {
        Toast.makeText(this, "菜单栏个人装扮", Toast.LENGTH_SHORT).show();
    }

    public void menuMoney(View view) {
        Toast.makeText(this, "菜单栏钱包", Toast.LENGTH_SHORT).show();
    }

    public void menuMenber(View view) {
        Toast.makeText(this, "菜单栏会员", Toast.LENGTH_SHORT).show();
    }
}