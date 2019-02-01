package com.duxina.chatapplication.utils;

import android.support.v7.app.AppCompatActivity;

public class Utils extends AppCompatActivity {

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";

    //combiningName
    public String combineName(String user1, String user2){
        String temp;
        String combineName = "";
        String names[] = new String[2];
        names[0] = user1.toLowerCase();
        names[1] = user2.toLowerCase();
        for (int i = 0; i < 2; i++)
        {
            for (int j = i + 1; j < 2; j++)
            {
                if (names[i].compareTo(names[j])>0)
                {
                    temp = names[i];
                    names[i] = names[j];
                    names[j] = temp;
                }
            }
        }
        for (int i = 0; i < 2 - 1; i++)
        {
            combineName+= names[i];
        }
        combineName+=names[2 - 1];
        return combineName;
    }
}
