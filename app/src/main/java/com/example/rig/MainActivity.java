package com.example.rig;

import android.os.Bundle;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_TAB_ID = "extra_tab_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        int selectedTabId = getIntent().getIntExtra(EXTRA_TAB_ID, R.id.tab_market);
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(selectedTabId);
            switchToTab(selectedTabId);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            switchToTab(id);
            return true;
        });
    }

    private void switchToTab(int tabId) {
        Fragment fragment;
        if (tabId == R.id.tab_market) {
            fragment = new MarketFragment();
        } else if (tabId == R.id.tab_orders) {
            fragment = new OrdersFragment();
        } else if (tabId == R.id.tab_selling) {
            fragment = new SellingFragment();
        } else {
            fragment = new ProfileFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}