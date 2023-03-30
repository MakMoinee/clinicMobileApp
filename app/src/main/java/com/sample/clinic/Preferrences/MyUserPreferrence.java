package com.sample.clinic.Preferrences;

import android.content.Context;
import android.content.SharedPreferences;

import com.sample.clinic.Models.Users;

public class MyUserPreferrence {

    private Context context;
    SharedPreferences pref;

    public MyUserPreferrence(Context mContext) {
        this.context = mContext;
        this.pref = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    public void saveUser(Users users) {

        SharedPreferences.Editor editor = pref.edit();
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
        users.setDocID(pref.getString("docID", ""));
        users.setEmail(pref.getString("email", ""));
        users.setPassword(pref.getString("password", ""));
        users.setFirstName(pref.getString("firstName", ""));
        users.setMiddleName(pref.getString("middleName", ""));
        users.setLastName(pref.getString("lastName", ""));
        users.setSecret(pref.getString("secret", ""));
        users.setUserType(pref.getInt("userType", 0));
        return users;
    }

    public String getDocID(){
        return pref.getString("docID","");
    }
}
