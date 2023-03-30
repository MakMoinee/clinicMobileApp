package com.sample.clinic.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sample.clinic.AdminActivity;
import com.sample.clinic.BuildConfig;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFireStore;
import com.sample.clinic.Services.LocalFirestoreImpl;

public class SecondFragment extends Fragment {

    private Context mContext;
    private Button btnLogin;
    private EditText editEmail, editPassword;
    private TextInputEditText editSecret, editPW, editConfirmPW, editForgotEmail;
    private TextInputLayout layoutPW, layoutConfirmPW, layoutSecret;
    private Button btnSave, txtForgotPassword;
    private FragmentFinish fn;
    private TextView txtCreateAccount;
    private LocalFireStore fs;
    private ProgressDialog pd;
    private AlertDialog fpAlertDialog;
    private Boolean showPW = true, showCPW = true, showSecret = true;

    public SecondFragment(Context context, FragmentFinish finish) {
        mContext = context;
        fn = finish;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.fragment_second, container, false);
        initViews(mView);
        initListener();
        return mView;
    }

    private void initListener() {
        btnLogin.setOnClickListener(v -> {
            if (editEmail.getText().toString().equals("") || editPassword.getText().toString().equals("")) {
                Toast.makeText(mContext, "Please don't leave empty fields", Toast.LENGTH_SHORT).show();
            } else {
                pd.show();
                Users users = new Users();
                users.setEmail(editEmail.getText().toString());
                users.setPassword(editPassword.getText().toString());
                if (users.getEmail().equals(BuildConfig.ADMIN_EMAIL) && users.getPassword().equals(BuildConfig.ADMIN_PASSWORD)) {
                    pd.dismiss();
                    String uuid = BuildConfig.ADMIN_UUID;
                    users.setDocID(uuid);
                    Log.e("DATA", users.getDocID());
                    new MyUserPreferrence(mContext).saveUser(users);
                    Toast.makeText(mContext, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, AdminActivity.class);
                    mContext.startActivity(intent);
                    fn.onAdminFragment();
                } else {
                    fs.getLogin(users, new FireStoreListener() {
                        @Override
                        public void onAddUserSuccess(Users users) {
                            pd.dismiss();
                            Toast.makeText(mContext, "Login Successfully", Toast.LENGTH_SHORT).show();
                            new MyUserPreferrence(mContext).saveUser(users);
                            fn.onLoginFinish();
                        }

                        @Override
                        public void onAddUserError(Exception e) {
                            Log.e("LOGIN_ERR", e.getMessage());
                            pd.dismiss();
                            Toast.makeText(mContext, "Wrong username or Password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });
        txtCreateAccount.setOnClickListener(v -> fn.onFinishSecondFragment());
        txtForgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            View tView = LayoutInflater.from(mContext).inflate(R.layout.dialog_forgot_pass, null, false);
            initForgotViews(tView);
            initForgotListeners(tView);
            mBuilder.setView(tView);
            fpAlertDialog = mBuilder.create();
            fpAlertDialog.show();
        });
    }

    private void initForgotListeners(View tView) {
        layoutPW.setEndIconOnClickListener(v -> {
            if (showPW) {
                layoutPW.setEndIconDrawable(R.drawable.ic_eye_off);
                editPW.setInputType(InputType.TYPE_CLASS_TEXT);
                showPW = false;
            } else {
                layoutPW.setEndIconDrawable(R.drawable.ic_eye);
                editPW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPW = true;
            }
        });

        layoutConfirmPW.setEndIconOnClickListener(v -> {
            if (showCPW) {
                layoutConfirmPW.setEndIconDrawable(R.drawable.ic_eye_off);
                editConfirmPW.setInputType(InputType.TYPE_CLASS_TEXT);
                showCPW = false;
            } else {
                layoutConfirmPW.setEndIconDrawable(R.drawable.ic_eye);
                editConfirmPW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showCPW = true;
            }
        });

        layoutSecret.setEndIconOnClickListener(v -> {
            if (showSecret) {
                layoutSecret.setEndIconDrawable(R.drawable.ic_eye_off);
                editSecret.setInputType(InputType.TYPE_CLASS_TEXT);
                showSecret = false;
            } else {
                layoutSecret.setEndIconDrawable(R.drawable.ic_eye);
                editSecret.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showSecret = true;
            }
        });

        btnSave.setOnClickListener(v -> {
            if (editSecret.getText().toString().equals("") ||
                    editPW.getText().toString().equals("") ||
                    editConfirmPW.getText().toString().equals("") ||
                    editForgotEmail.getText().toString().equals("")) {
                Toast.makeText(mContext, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                if (editPW.getText().toString().equals(editConfirmPW.getText().toString())) {
                    pd.show();
                    Users mUser = new Users();
                    mUser.setEmail(editForgotEmail.getText().toString());
                    mUser.setPassword(editPW.getText().toString());
                    mUser.setSecret(editSecret.getText().toString());
                    fs.forgotPassword(mUser, new FireStoreListener() {
                        @Override
                        public void onAddUserSuccess(Users users) {
                            pd.dismiss();
                            fpAlertDialog.dismiss();
                            Toast.makeText(mContext, "Successfully Reset Password", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAddUserError(Exception e) {
                            pd.dismiss();
                            Toast.makeText(mContext, "Failed to Reset Password", Toast.LENGTH_SHORT).show();
                            Log.e("FORGOT_PASSW_ERR", e.getMessage().toString());
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initForgotViews(View tView) {
        editSecret = tView.findViewById(R.id.editSecret);
        editPW = tView.findViewById(R.id.editPassword);
        editConfirmPW = tView.findViewById(R.id.editConfirmPassword);
        btnSave = tView.findViewById(R.id.btnSave);
        editForgotEmail = tView.findViewById(R.id.editForgotEmail);
        layoutPW = tView.findViewById(R.id.layoutPassword);
        layoutConfirmPW = tView.findViewById(R.id.layoutConfirmPassword);
        layoutSecret = tView.findViewById(R.id.layoutSecret);
    }

    private void initViews(View mView) {
        btnLogin = mView.findViewById(R.id.btnLogin);
        editEmail = mView.findViewById(R.id.editEmail);
        editPassword = mView.findViewById(R.id.editPassword);
        txtCreateAccount = mView.findViewById(R.id.txtCreateAccount);
        txtForgotPassword = mView.findViewById(R.id.txtForgotPassword);
        fs = new LocalFirestoreImpl(mContext);
        pd = new ProgressDialog(mContext);
        pd.setCancelable(false);
        pd.setMessage("Sending Request ...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}