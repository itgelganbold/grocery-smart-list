package com.example.john.smartlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity implements LoginFragment.OnFragmentInteractionListener, SignUpFragment.OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new SplashFragment(), "splash_tag")
                .commit();

        findViewById(R.id.container).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(ParseUser.getCurrentUser() != null){ //logged in
                    successfulLogin();
                } else{ //not logged in
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new LoginFragment(), "login_tag")
                            .commit();
                }
            }
        }, 1000);
    }

    @Override
    public void successfulLogin() {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void cancelSignup() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void doSignup() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignUpFragment(), "signup_tag")
                .addToBackStack(null)
                .commit();
    }
}
