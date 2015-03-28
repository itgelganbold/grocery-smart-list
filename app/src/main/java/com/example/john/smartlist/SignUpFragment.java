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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private Activity mActivity;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mActivity.findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) mActivity.findViewById(R.id.editTextEmail);
                EditText name = (EditText) mActivity.findViewById(R.id.editTextName);
                EditText password = (EditText) mActivity.findViewById(R.id.editTextPassword);

                if(TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Enter Email");
                } else if(TextUtils.isEmpty(name.getText().toString())){
                    name.setError("Enter Your Name");
                } else if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Enter Password");
                } else{
                    ParseUser user = new ParseUser();
                    user.setUsername(email.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.put("name", name.getText().toString());

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(mActivity, "Successful Signup!", Toast.LENGTH_SHORT).show();
                                mListener.successfulLogin();
                            } else {
                                Toast.makeText(mActivity, "Error Signing Up!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mActivity.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelSignup();
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
        public void cancelSignup();
    }

}
