package com.sample.clinic.Preferrences;

import android.content.Context;
import android.content.SharedPreferences;

import com.sample.clinic.Models.Users;

public class MyUserPreferrence {

    private Context context;

    public MyUserPreferrence(Context mContext) {
        this.context = mContext;
    }

    public void saveUser(Users users) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("docID", users.getDocID());
        editor.putString("email", users.getEmail());
        editor.putString("password", users.getPassword());
        editor.putString("firstName", users.getFirstName());
        editor.putString("middleName", users.getMiddleName());
        editor.putString("lastName", users.getLastName());
        editor.putString("secret", users.getSecret());
        editor.putInt("userType", users.getUserType());
        editor.commit();
        editor.apply();

    }

    public Users getUsers() {
        Users users = new Users();
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        users.setDocID(sharedPreferences.getString("docID", ""));
        users.setEmail(sharedPreferences.getString("email", ""));
        users.setPassword(sharedPreferences.getString("password", ""));
        users.setFirstName(sharedPreferences.getString("firstName", ""));
        users.setMiddleName(sharedPreferences.getString("middleName", ""));
        users.setLastName(sharedPreferences.getString("lastName", ""));
        users.setSecret(sharedPreferences.getString("secret", ""));
        users.setUserType(sharedPreferences.getInt("userType", 0));
        return users;
    }
}
