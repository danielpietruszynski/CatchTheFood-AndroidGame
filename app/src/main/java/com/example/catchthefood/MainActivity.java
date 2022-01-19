package com.example.catchthefood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView scoreLabel, startLabel;
    private ImageView plane, food, food1, bomb;

    // Rozmiar
    private int screenWidth;
    private int frameHeight;
    private int planeSize;

    // Pozycja
    private float planeY;
    private float foodX, foodY;
    private float food1X, food1Y;
    private float bombX, bombY;

    // Szybkość
    private int planeSpeed, foodSpeed, food1Speed, bombSpeed;

    // Wynik
    private int score;

    private Timer timer = new Timer();
    private Handler handler = new Handler();

    // Naciśnięcie ekranu przez użytkownika
    private boolean action_flg = false;

    // Rozpoczęto grę
    private boolean start_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        plane = findViewById(R.id.plane);
        food = findViewById(R.id.food);
        food1 = findViewById(R.id.food1);
        bomb = findViewById(R.id.bomb);

        // Rozmiar ekranu
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        int screenHeight = size.y;

        planeSpeed = Math.round(screenHeight / 60.0f);
        foodSpeed = Math.round(screenWidth / 60.0f);
        food1Speed = Math.round(screenWidth / 36.0f);
        bombSpeed = Math.round(screenWidth / 45.0f);

        // Pozycja początkowa
        food.setX(-150.0f);
        food.setY(-150.0f);
        food1.setX(-150.0f);
        food1.setY(-150.0f);
        bomb.setX(-150.0f);
        bomb.setY(-150.0f);

        // scoreLabel.setText("Wynik : " + score);
        scoreLabel.setText(getString(R.string.score, score));
    }

    public void changePos() {

        hitCheck();

        // food
        foodX -= foodSpeed;
        if (foodX < 0) {
            foodX = screenWidth + 20;
            foodY = (float)Math.floor(Math.random() * (frameHeight - food.getHeight()));
        }
        food.setX(foodX);
        food.setY(foodY);

        // bomb
        bombX -= bombSpeed;
        if (bombX < 0) {
            bombX = screenWidth + 10;
            bombY = (float)Math.floor(Math.random() * (frameHeight - bomb.getHeight()));
        }
        bomb.setX(bombX);
        bomb.setY(bombY);

        // food1
        food1X -= food1Speed;
        if (food1X < 0) {
            food1X = screenWidth + 5000;
            food1Y = (float)Math.floor(Math.random() * (frameHeight - food1.getHeight()));
        }
        food1.setX(food1X);
        food1.setY(food1Y);

        // plane
        if (action_flg) {
            planeY -= planeSpeed;
        } else {
            planeY += planeSpeed;
        }

        if (planeY < 0) planeY = 0;
        if (planeY > frameHeight - planeSize) planeY = frameHeight - planeSize;

        plane.setY(planeY);

        // scoreLabel.setText("Wynik : " + score);
        scoreLabel.setText(getString(R.string.score, score));
    }

    public void hitCheck() {

        // food
        float foodCenterX = foodX + food.getWidth() / 2.0f;
        float foodCenterY = foodY + food.getHeight() / 2.0f;

        if (0 <= foodCenterX && foodCenterX <= planeSize && planeY <= foodCenterY &&
                foodCenterY <= planeY + planeSize) {
            foodX = -100.0f;
            score += 5;
        }

        // food1
        float food1CenterX = food1X + food1.getWidth() / 2.0f;
        float food1CenterY = food1Y + food1.getHeight() / 2.0f;

        if (0 <= food1CenterX && food1CenterX <= planeSize && planeY <= food1CenterY &&
                food1CenterY <= planeY + planeSize) {
            food1X = -100.0f;
            score += 15;
        }

        // bomb
        float bombCenterX = bombX + bomb.getWidth() / 2.0f;
        float bombCenterY = bombY + bomb.getHeight() / 2.0f;

        if (0 <= bombCenterX && bombCenterX <= planeSize && planeY <= bombCenterY &&
                bombCenterY <= planeY + planeSize) {

            // Przegrana
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            // Wyniki
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("Wynik", score);
            startActivity(intent);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!start_flg) {
            start_flg = true;

            // FrameHeight
            FrameLayout frameLayout = findViewById(R.id.frame);
            frameHeight = frameLayout.getHeight();

            planeY = plane.getY();
            planeSize = plane.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> changePos());
                }
            }, 0, 20);

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {}
}