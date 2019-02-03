package com.mcgill.ecse428.foodme.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.mcgill.ecse428.foodme.R;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Handles logging in and signing up
 */
public class LoginActivity extends AppCompatActivity  {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private final static String KEY_USER = "userID";

    // UI references.

    private View mProgressView;
    private View mLoginFormView;
    private View mRegisterFormView;
    private Space progressSpace;
    private ImageView logoImage;
    private Button signInPrompt, registerPrompt, signInButton, registerButton;
    private AutoCompleteTextView signInName, registerEmail, registerPhone;
    private EditText signInPassword, registerName, registerPassword;
    private TextView noAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // if the user is already signed in, skip this and open the main activity
        if (prefs.getString("userID", null) != null) {
            Intent I = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(I);
            finish();
        }


        signInPrompt = findViewById(R.id.signInPrompt);
        registerPrompt = findViewById(R.id.registerPrompt);
        signInName = findViewById(R.id.signInName);
        signInPassword = findViewById(R.id.signInPassword);
        signInButton = findViewById(R.id.signInButton);
        registerPrompt = findViewById(R.id.registerPrompt);
        registerEmail = findViewById(R.id.registerEmail);
        registerName = findViewById(R.id.registerName);
        registerPhone = findViewById(R.id.registerPhone);
        registerPassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);
        noAccount = findViewById(R.id.noAccount);


        // Handling registration or log in options

        signInPrompt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setElementVisibility("signIn", false);

            }
        });
        registerPrompt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setElementVisibility("register", false);


            }
        });
        noAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                prefs.edit().putString(KEY_USER, "noAccount").apply(); // to indicate no account
                Intent I = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(I);
                finish();

            }
        });


        // Set up the login form.


        signInPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        registerPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.login_progress);
        logoImage = findViewById(R.id.logoImage);
        progressSpace = findViewById(R.id.progressSpace);


    }


    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors such as an empty field, no log in attempt is made
     */
    private void attemptLogin() {

        // Reset errors.
        signInName.setError(null);
        signInPassword.setError(null);

        // Store values at the time of the login attempt.
        String username = signInName.getText().toString();
        String password = signInPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(password)) {
            signInPassword.setError(getString(R.string.error_field_required));
            focusView = signInPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            signInName.setError(getString(R.string.error_field_required));
            focusView = signInName;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showSignInProgress(true);
            userLogInTask(username, password);
        }
    }


    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors such as missing fields, no register attempt is made.
     */
    private void attemptRegistration() {


        // Reset errors.
        registerName.setError(null);
        registerEmail.setError(null);
        registerPhone.setError(null);
        registerPassword.setError(null);


        // Store values at the time of the registration attempt.
        String email = registerEmail.getText().toString();
        String name = registerName.getText().toString();
        String phoneNumber = registerPhone.getText().toString();
        String password = registerPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            registerName.setError(getString(R.string.error_field_required));
            focusView = registerName;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            registerEmail.setError(getString(R.string.error_field_required));
            focusView = registerEmail;
            cancel = true;
        }


        if (TextUtils.isEmpty(phoneNumber)) {
            registerPhone.setError(getString(R.string.error_field_required));
            focusView = registerPhone;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            registerPassword.setError(getString(R.string.error_field_required));
            focusView = registerPassword;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            showRegistrationProgress(true);
            userRegisterTask(email, name, password, phoneNumber);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    private void showSignInProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        // added by me
        logoImage.setVisibility(show ? View.GONE : View.VISIBLE);
        progressSpace.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mLoginFormView.getWindowToken(), 0);
        // MOVING PROGRESS BAR TO THE MIDDLE

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        progressSpace.getLayoutParams().height = (int) (height / 2.5);
        progressSpace.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

            }
        });

    }


    /**
     * Shows the progress UI and hides the registration form.
     */
    private void showRegistrationProgress(final boolean show) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            // added by me
            logoImage.setVisibility(show ? View.GONE : View.VISIBLE);
            progressSpace.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mRegisterFormView.getWindowToken(), 0);
            // MOVING PROGRESS BAR TO THE MIDDLE

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            progressSpace.getLayoutParams().height = (int) (height / 2.5);
            progressSpace.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

                }
            });

    }




    /**
     * Attempts to authorize the user account and log in through an asynchronous task
     *
     * @param mName     the username
     * @param mPassword the password
     */
    public void userLogInTask(String mName, String mPassword) {

        // ASYNC TASK
        showSignInProgress(false);


        if(mName.equals("admin") && mPassword.equals("password")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            prefs.edit().putString(KEY_USER, mName).apply(); // adding userID to preferences for future use
            Intent I = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(I);
            finish();
        }



    }


    /**
     * Attempts to register the user account through an asynchronous task
     *
     * @param mEmail    user email
     * @param mName     username
     * @param mPassword user password
     * @param mPhone    user phone number
     */
    public void userRegisterTask(String mEmail, String mName, String mPassword, String mPhone) {

        // missing async task will be added when backend is set up

        Toast.makeText(LoginActivity.this, "There was a network error, try again later.", Toast.LENGTH_LONG).show(); // generic network error
        showRegistrationProgress(false);



    }


    // This is added to return the main page when you are in the process of signing up/registering
    @Override
    public void onBackPressed() {
        if (signInButton.getVisibility() == View.VISIBLE) {
            setElementVisibility("signIn", true);
        } else if (registerButton.getVisibility() == View.VISIBLE) {
            setElementVisibility("register", true);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * This method handles the visibility of elements on the logIn activity.
     *
     * @param mode    signIn or Registration depends on the user's button selection
     * @param reverse This is to complete the opposite action when the back button is pressed, i.e, go back to main page
     */
    private void setElementVisibility(String mode, Boolean reverse) {
        if (mode.equals("signIn")) {
            if (!reverse) {
                registerPrompt.setVisibility(View.GONE);
                signInPrompt.setVisibility(View.GONE);
                noAccount.setVisibility(View.GONE);
                mRegisterFormView.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);
                signInName.setVisibility(View.VISIBLE);
                signInPassword.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.VISIBLE);
            } else {
                registerPrompt.setVisibility(View.VISIBLE);
                signInPrompt.setVisibility(View.VISIBLE);
                noAccount.setVisibility(View.VISIBLE);
                mLoginFormView.setVisibility(View.GONE);
                mRegisterFormView.setVisibility(View.GONE);
                signInName.setVisibility(View.GONE);
                signInName.setText("");
                signInName.setError(null);
                signInPassword.setVisibility(View.GONE);
                signInPassword.setText("");
                signInPassword.setError(null);
                signInButton.setVisibility(View.GONE);
            }
        } else if (mode.equals("register")) {
            if (!reverse) {
                registerPrompt.setVisibility(View.GONE);
                signInPrompt.setVisibility(View.GONE);
                noAccount.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.GONE);
                mRegisterFormView.setVisibility(View.VISIBLE);
                registerName.setVisibility(View.VISIBLE);
                registerEmail.setVisibility(View.VISIBLE);
                registerPassword.setVisibility(View.VISIBLE);
                registerPhone.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.VISIBLE);
            } else {
                registerPrompt.setVisibility(View.VISIBLE);
                signInPrompt.setVisibility(View.VISIBLE);
                noAccount.setVisibility(View.VISIBLE);
                mLoginFormView.setVisibility(View.GONE);
                mRegisterFormView.setVisibility(View.GONE);
                registerName.setVisibility(View.GONE);
                registerName.setText("");
                registerName.setError(null);
                registerEmail.setVisibility(View.GONE);
                registerEmail.setText("");
                registerEmail.setError(null);
                registerPassword.setVisibility(View.GONE);
                registerPassword.setText("");
                registerPassword.setError(null);
                registerPhone.setVisibility(View.GONE);
                registerPhone.setText("");
                registerPhone.setError(null);
                registerButton.setVisibility(View.GONE);

            }
        }

    }
}


