package com.sample.clinic.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.Models.Users;
import com.sample.clinic.R;
import com.sample.clinic.Services.LocalFireStore;
import com.sample.clinic.Services.LocalFirestoreImpl;
import com.sample.clinic.Services.LocalHash;

public class CreateAccountFragment extends Fragment {

    private Context mContext;
    private Button btnCreateAccount;
    private EditText editEmail, editPassword, editConfirmPassword, editFN, editLN, editMN, editSecret;
    private RadioGroup radioTerms;
    private RadioButton checkTerms;
    private Boolean isReadTerms = false;
    private FragmentFinish fn;
    private LocalFireStore fs;
    private ProgressDialog ps;
    private WebView wv;
    private WebSettings ws;
    private AlertDialog dialog;

    public CreateAccountFragment(Context context, FragmentFinish newFN, ProgressDialog pss) {
        mContext = context;
        fn = newFN;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.fragment_create_account, container, false);
        initViews(mView);
        initListeners();
        return mView;
    }

    private void initListeners() {
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editPassword.getText().toString();
                String repassword = editConfirmPassword.getText().toString();

                if (editEmail.getText().toString().equals("") ||
                        editPassword.getText().toString().equals("") ||
                        editConfirmPassword.getText().toString().equals("") ||
                        editFN.getText().toString().equals("") ||
                        editMN.getText().toString().equals("") ||
                        editLN.getText().toString().equals("") ||
                        editSecret.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
                } else {
                    if(isReadTerms){
                        ps.show();
                        if (password.equals(repassword)) {
                            Users user = new Users();
                            user.setEmail(editEmail.getText().toString());
                            user.setPassword(editPassword.getText().toString());
                            user.setFirstName(editFN.getText().toString());
                            user.setMiddleName(editMN.getText().toString());
                            user.setLastName(editLN.getText().toString());
                            user.setSecret(editSecret.getText().toString());
                            user.setUserType(2); // userType is 2 for client
                            fs.insertUserRecord(user, new FireStoreListener() {
                                @Override
                                public void onAddUserSuccess(Users users) {
                                    Toast.makeText(mContext, "Successfully Created Account", Toast.LENGTH_SHORT).show();
                                    ps.dismiss();
                                    fn.onFinishFirstFragment();
                                }

                                @Override
                                public void onAddUserError(Exception e) {
                                    ps.dismiss();
                                    Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(mContext, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
                            ps.dismiss();
                        }
                    }else{
                        Toast.makeText(mContext, "Please read and check first Terms and Conditions", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        radioTerms.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.checkTerms:
                    isReadTerms=true;
                    break;
                default:
                    isReadTerms=false;
                    break;
            }
        });

        checkTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                View sView = LayoutInflater.from(mContext).inflate(R.layout.dialog_terms,null,false);
                initializeTermDiag(sView);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                mBuilder.setView(sView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private void initializeTermDiag(View sView) {
        wv = sView.findViewById(R.id.myweb);
        ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        wv.loadUrl("https://www.termsandconditionsgenerator.com/live.php?token=u1AIL7yApHsxy5rrqyfgNqJ4ijs6yEj2");
    }

    private void initViews(View mView) {
        radioTerms = mView.findViewById(R.id.radioTerms);
        checkTerms = mView.findViewById(R.id.checkTerms);
        editEmail = mView.findViewById(R.id.editEmail);
        editPassword = mView.findViewById(R.id.editPassword);
        editConfirmPassword = mView.findViewById(R.id.editConfirmPassword);
        btnCreateAccount = mView.findViewById(R.id.btnCreateAccount);
        editFN = mView.findViewById(R.id.editFN);
        editLN = mView.findViewById(R.id.editLN);
        editMN = mView.findViewById(R.id.editMN);
        editSecret = mView.findViewById(R.id.editSecret);
        fs = new LocalFirestoreImpl(mView.getContext());
        ps = new ProgressDialog(mView.getContext());
        ps.setCancelable(false);
        ps.setMessage("Sending Request ...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
