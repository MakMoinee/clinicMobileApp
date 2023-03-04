package com.sample.clinic.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.auth.User;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.ProfileListener;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Preferrences.MyUserPreferrence;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFireStore;
import com.sample.clinic.Services.LocalFirestoreImpl;

public class ProfileFragment extends Fragment {

    Context mContext;
    ProfileListener listener;
    private MaterialToolbar toolbar;
    private TextInputLayout inputEmail;
    private TextInputEditText editEmail, editFN, editLN, editMN;
    private TextView txtFullName;
    private Users users;
    private LocalFireStore fs;
    private ProgressDialog pd;
    private ImageButton btnEditFull;
    private AlertDialog profileNemDialog;
    private Button btnUpdateProfileNem;


    public ProfileFragment(Context context, ProfileListener pListener) {
        this.mContext = context;
        this.listener = pListener;
        users = new MyUserPreferrence(context).getUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_profile, container, false);
        initViews(mView);
        initListeners(mView);
        return mView;
    }

    private void initListeners(View mView) {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBackPressed();
            }
        });

        inputEmail.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editEmail.isEnabled()) {
                    inputEmail.setEndIconDrawable(R.drawable.ic_check);
                    editEmail.setEnabled(true);
                    editEmail.requestFocus();
                } else {
                    pd.show();
                    Users eUser = new Users();
                    eUser = users;
                    eUser.setEmail(editEmail.getText().toString());
                    fs.updateUserRecord(eUser, new FireStoreListener() {
                        @Override
                        public void onAddUserSuccess(Users nUser) {
                            pd.dismiss();
                            editEmail.setEnabled(false);
                            inputEmail.setEndIconDrawable(R.drawable.ic_edit);
                            users = nUser;
                            new MyUserPreferrence(mContext).saveUser(users);
                            Toast.makeText(mContext, "Successfully Update Email", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAddUserError(Exception e) {
                            pd.dismiss();
                            Toast.makeText(mContext, "Failed to update user email", Toast.LENGTH_SHORT).show();
                            editEmail.setText(users.getEmail());
                            editEmail.setEnabled(false);
                            inputEmail.setEndIconDrawable(R.drawable.ic_edit);
                        }
                    });

                }
            }
        });
        btnEditFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_profile_name, null, false);
                initProfileDiag(mView);
                initProfileListener(mView);
                mBuilder.setView(mView);
                profileNemDialog = mBuilder.create();
                profileNemDialog.show();
            }
        });
    }

    private void initProfileListener(View mView) {
        btnUpdateProfileNem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editFN.getText().toString().equals("") ||
                        editMN.getText().toString().equals("") ||
                        editLN.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Please Dont' Leave Empty Fields", Toast.LENGTH_SHORT).show();
                } else {
                    pd.show();
                    Users nUser = new Users();
                    nUser = users;
                    nUser.setFirstName(editFN.getText().toString());
                    nUser.setMiddleName(editMN.getText().toString());
                    nUser.setLastName(editLN.getText().toString());

                    fs.updateUserRecord(nUser, new FireStoreListener() {
                        @Override
                        public void onAddUserSuccess(Users sUser) {
                            pd.dismiss();
                            users = sUser;
                            new MyUserPreferrence(mContext).saveUser(users);
                            txtFullName.setText(users.getFirstName() + " " + users.getMiddleName() + " " + users.getLastName());
                            Toast.makeText(mContext, "Successfully updated profile name", Toast.LENGTH_SHORT).show();
                            profileNemDialog.dismiss();
                        }

                        @Override
                        public void onAddUserError(Exception e) {
                            pd.dismiss();
                            Toast.makeText(mContext, "Failed to update name", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initProfileDiag(View mView) {
        editFN = mView.findViewById(R.id.editFN);
        editMN = mView.findViewById(R.id.editMN);
        editLN = mView.findViewById(R.id.editLN);
        btnUpdateProfileNem = mView.findViewById(R.id.btnUpdateName);

        editFN.setText(users.getFirstName());
        editMN.setText(users.getMiddleName());
        editLN.setText(users.getLastName());
    }

    private void initViews(View mView) {
        toolbar = mView.findViewById(R.id.tbUserProfile);
        inputEmail = mView.findViewById(R.id.tfUserProfileEmail);
        editEmail = mView.findViewById(R.id.editEmail);
        btnEditFull = mView.findViewById(R.id.ibUpdateUsername);
        fs = new LocalFirestoreImpl(mContext);
        editEmail.setText(users.getEmail());
        pd = new ProgressDialog(mContext);
        pd.setMessage("Sending Request ...");
        pd.setCancelable(false);

        txtFullName = mView.findViewById(R.id.tvUserProfileUsername);
        txtFullName.setText(users.getFirstName() + " " + users.getMiddleName() + " " + users.getLastName());
    }
}
