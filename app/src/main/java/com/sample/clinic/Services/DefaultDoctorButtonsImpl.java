package com.sample.clinic.Services;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;

import androidx.appcompat.app.AlertDialog;

import com.sample.clinic.Interfaces.DoctorActivityListener;
import com.sample.clinic.MainActivity;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.R;
import com.sample.clinic.databinding.DialogSettingsBinding;
import com.sample.clinic.databinding.DialogTermsBinding;

public class DefaultDoctorButtonsImpl {

    DialogSettingsBinding settingsBinding;

    DialogTermsBinding termsBinding;
    AlertDialog settingsDialog, termsConditionDialog;
    DoctorActivityListener listener;

    Context mContext;

    public DefaultDoctorButtonsImpl(Context mContext, DoctorActivityListener l) {
        this.mContext = mContext;
        this.listener = l;
    }

    public void showSettings() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        settingsBinding = DialogSettingsBinding.inflate(LayoutInflater.from(mContext), null, false);
        mBuilder.setView(settingsBinding.getRoot());
        setSettingsDialogListeners();
        settingsDialog = mBuilder.create();
        settingsDialog.show();
    }

    private void setSettingsDialogListeners() {
        settingsBinding.relLogout.setOnClickListener(v -> {
            android.app.AlertDialog.Builder nBuilder = new android.app.AlertDialog.Builder(mContext);
            DialogInterface.OnClickListener dListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        new MyUserPreferrence(mContext).saveUser(new Users());
                        settingsDialog.dismiss();
                        listener.onLogout();
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.cancel();
                        break;
                }
            };
            nBuilder.setMessage("Do you want to proceed signinig out?")
                    .setNegativeButton("Yes", dListener)
                    .setPositiveButton("No", dListener)
                    .setCancelable(false);
            nBuilder.show();
        });
        settingsBinding.relTC.setOnClickListener(v -> {
            settingsDialog.dismiss();
            termsBinding = DialogTermsBinding.inflate(LayoutInflater.from(mContext), null, false);
            initializeTermDiag();
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            mBuilder.setView(termsBinding.getRoot());
            termsConditionDialog = mBuilder.create();
            termsConditionDialog.show();
        });
    }

    private void initializeTermDiag() {
        WebSettings ws = termsBinding.myweb.getSettings();
        ws.setJavaScriptEnabled(true);
        termsBinding.myweb.loadUrl("https://www.termsandconditionsgenerator.com/live.php?token=u1AIL7yApHsxy5rrqyfgNqJ4ijs6yEj2");
    }
}
