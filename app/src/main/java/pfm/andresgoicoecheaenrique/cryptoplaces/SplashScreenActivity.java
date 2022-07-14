package pfm.andresgoicoecheaenrique.cryptoplaces;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        waitSomeSecs();
    }

    private void waitSomeSecs(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, GoogleMapsActivity.class);//LoginActivity
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}