package com.example.pfms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pfms.Utils.MyDialog;
import com.example.pfms.database.DataBaseHelper;
import com.example.pfms.fragments.StatsFragment;
import com.example.pfms.fragments.HomeFragment;
import com.example.pfms.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.pfms.notification.App.CHANNEL_1_ID;

/**
 * This is the base activity that holds all the fragments and dialogs
 * @author zarif
 *
 */
public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;
    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.nv_navigate);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        notificationManager = NotificationManagerCompat.from(this);
        db = new DataBaseHelper(this);

        if (db.fetchRemainingDays()<=0) {
            new MyDialog(this, db.fetchRemarks()).show();
        }

        if (!db.fetchRemarks()) {
            sendOnChannel1();
//            sendMail();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment,new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.it_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.it_detail:
                            selectedFragment = new StatsFragment();
                            break;
                        case R.id.it_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment,selectedFragment).commit();
                    return true;
                }
            };

    /**
     * notifies the user on over expense
     */
    public void sendOnChannel1() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_error_24)
                .setContentTitle("Exceeding")
                .setContentText("Your Current Rate of Expense is surpassing Required Rate")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(1, notification);
    }

//    public void sendMail () {
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("message/rfc822");
//        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{db.fetchMail()});
//        i.putExtra(Intent.EXTRA_SUBJECT, "Exceeding");
//        i.putExtra(Intent.EXTRA_TEXT   , "Your Current Rate of Expense is surpassing Required Rate");
//        try {
//            startActivity(Intent.createChooser(i, "Send mail..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!db.fetchRemarks()) {
            sendOnChannel1();
//            sendMail();
        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}