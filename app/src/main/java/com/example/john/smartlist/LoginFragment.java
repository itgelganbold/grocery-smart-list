package com.example.john.smartlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Activity mActivity;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity.findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) mActivity.findViewById(R.id.editTextEmail);
                EditText password = (EditText) mActivity.findViewById(R.id.editTextPassword);
                if(TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Enter Email");
                } else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Enter Password");
                } else {
                    ParseUser.logInInBackground(email.getText().toString(), password.getText().toString(), new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                Toast.makeText(mActivity, "Login Successful", Toast.LENGTH_SHORT).show();
                                mListener.successfulLogin();
                            } else {
                                Toast.makeText(mActivity, "Unable to login!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mActivity.findViewById(R.id.textViewSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.doSignup();
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void successfulLogin();
        public void doSignup();
    }

}
