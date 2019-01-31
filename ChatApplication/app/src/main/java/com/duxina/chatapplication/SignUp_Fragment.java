package com.duxina.chatapplication;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp_Fragment extends Fragment implements OnClickListener {
	private static View view;
	private static EditText fullName, emailId, mobileNumber, location,
			password, confirmPassword;
	private static TextView login;
	private static Button signUpButton;
	private static CheckBox terms_conditions;

	private FirebaseAuth firebaseAuth;
	private DatabaseReference databaseReference;


	public SignUp_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    firebaseAuth = FirebaseAuth.getInstance();
	    databaseReference = FirebaseDatabase.getInstance().getReference();

		view = inflater.inflate(R.layout.signup_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize all views
	private void initViews() {

		fullName = (EditText) view.findViewById(R.id.fullName);
		emailId = (EditText) view.findViewById(R.id.userEmailId);
		mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
		location = (EditText) view.findViewById(R.id.location);
		password = (EditText) view.findViewById(R.id.password);
		confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);

		// Setting text selector over textviews
		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			login.setTextColor(csl);
			terms_conditions.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	private void registerUser(){
	    String emailSave = emailId.getText().toString();
	    String passwordSave = password.getText().toString();

	    firebaseAuth.createUserWithEmailAndPassword(emailSave, passwordSave)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //user successfully registered and logged in
                            saveUserInformation();

                        }else{
                        	if(!task.isSuccessful()){
                        		try{
                        			throw task.getException();
								}catch(FirebaseAuthUserCollisionException existEmail){
									Toast.makeText(getActivity(), "Email is already exist!", Toast.LENGTH_SHORT).show();
								}catch(Exception ex){
									Toast.makeText(getActivity(), "Could not register .. please Try Again", Toast.LENGTH_SHORT).show();
								}
							}

                        }
                    }
                });
    }
    private void saveUserInformation(){
        String fullNameSave = fullName.getText().toString().trim();
        String emailIdSave = emailId.getText().toString().trim();
        String mobileNumberSave = mobileNumber.getText().toString().trim();
        String locationSave = location.getText().toString().trim();

        UserInformation userInformation = new UserInformation(fullNameSave, emailIdSave, mobileNumberSave, locationSave);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("USER_DATA").child(user.getUid()).setValue(userInformation);

        Toast.makeText(getActivity(), "Information Saved...", Toast.LENGTH_SHORT).show();

		Toast.makeText(getActivity(), "Registered Successfully", Toast.LENGTH_SHORT).show();

		Intent mainActivity = new Intent(getActivity(), MainActivity.class);
		startActivity(mainActivity);
		getActivity().finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signUpBtn:

			// Call checkValidation method
			checkValidation();
			break;

		case R.id.already_user:

			// Replace login fragment
			new LoginActivity().replaceLoginFragment();
			break;
		}

	}

	// Check Validation Method
	private void checkValidation() {

		// Get all edittext texts
		String getFullName = fullName.getText().toString();
		String getEmailId = emailId.getText().toString();
		String getMobileNumber = mobileNumber.getText().toString();
		String getLocation = location.getText().toString();
		String getPassword = password.getText().toString();
		String getConfirmPassword = confirmPassword.getText().toString();

		// Pattern match for email id
		Pattern p = Pattern.compile(Utils.regEx);
		Matcher m = p.matcher(getEmailId);

		// Check if all strings are null or not
		if (getFullName.equals("") || getFullName.length() == 0
				|| getEmailId.equals("") || getEmailId.length() == 0
				|| getMobileNumber.equals("") || getMobileNumber.length() == 0
				|| getLocation.equals("") || getLocation.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0
				|| getConfirmPassword.equals("")
				|| getConfirmPassword.length() == 0) {
			new CustomToast().Show_Toast(getActivity(), view,
					"All fields are required.");
		}
		// Check if email id valid or not
		else if (!m.find()){
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
		}


		// Check if both password should be equal
		else if (!getConfirmPassword.equals(getPassword)){
			new CustomToast().Show_Toast(getActivity(), view,
					"Both password doesn't match.");
		}

		// Make sure user should check Terms and Conditions checkbox
		else if (!terms_conditions.isChecked()){
			new CustomToast().Show_Toast(getActivity(), view,
					"Please select Terms and Conditions.");
		}

		// Else do signup or do your stuff
		else  {
			registerUser();

		}

	}

}
