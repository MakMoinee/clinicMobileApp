package com.sample.clinic.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sample.clinic.Adapters.AdminCategoryAdapter;
import com.sample.clinic.Adapters.CategoryAdapter;
import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Interfaces.AdminListener;
import com.sample.clinic.Interfaces.FireStoreListener;
import com.sample.clinic.Models.Categories;
import com.sample.clinic.Models.Users;
import com.sample.clinic.Services.LocalFirestore2;
import com.sample.clinic.databinding.DialogAddCategoryBinding;
import com.sample.clinic.databinding.FragmentCategoriesBinding;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {
    Context mContext;
    FragmentCategoriesBinding binding;

    AdminListener listener;
    LocalFirestore2 fs;
    AdminCategoryAdapter adapter;
    DialogAddCategoryBinding addCategoryBinding;

    AlertDialog dialogAddCategory;
    List<Categories> categoriesList = new ArrayList<>();

    ProgressDialog pdLoad;

    public CategoriesFragment(Context mContext, AdminListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.fs = new LocalFirestore2(mContext);
        this.pdLoad = new ProgressDialog(mContext);
        this.pdLoad.setMessage("Sending Request ...");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(LayoutInflater.from(mContext), container, false);
        loadCategories();
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.btnAddCategory.setOnClickListener(v -> {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
            addCategoryBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(mContext), null, false);
            mBuilder.setView(addCategoryBinding.getRoot());
            setDialogListeners();
            dialogAddCategory = mBuilder.create();
            dialogAddCategory.show();
        });
    }

    private void setDialogListeners() {
        addCategoryBinding.btnProceed.setOnClickListener(v -> {
            String categoryName = addCategoryBinding.editCategory.getText().toString();
            if (categoryName.equals("")) {
                Toast.makeText(mContext, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                pdLoad.show();
                Categories categories = new Categories();
                categories.setCategory(categoryName);
                fs.addCategory(categories, new FireStoreListener() {
                    @Override
                    public void onSuccess() {
                        pdLoad.dismiss();
                        Toast.makeText(mContext, "Successfully Added Category", Toast.LENGTH_SHORT).show();
                        binding.recycler.setAdapter(null);
                        categoriesList = new ArrayList<>();
                        loadCategories();

                    }

                    @Override
                    public void onError() {
                        pdLoad.dismiss();
                        Toast.makeText(mContext, "Failed to add category", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadCategories() {
        fs.getCategories(new FireStoreListener() {

            @Override
            public void onSuccessCategories(List<Categories> c) {
                categoriesList = c;
                adapter = new AdminCategoryAdapter(mContext, categoriesList, new AdapterListener() {
                    @Override
                    public void onClick(int position) {
                        if (categoriesList.size() == 1) {
                            Toast.makeText(mContext, "Can't Delete Category, 1 Category must be retain", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder tBuilder = new AlertDialog.Builder(mContext);
                            DialogInterface.OnClickListener dListener = (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Categories category = categoriesList.get(position);
                                        fs.deleteCategory(category, new FireStoreListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(mContext, "Successfully Deleted Category", Toast.LENGTH_SHORT).show();
                                                binding.recycler.setAdapter(null);
                                                categoriesList = new ArrayList<>();
                                                loadCategories();
                                            }

                                            @Override
                                            public void onError() {
                                                Toast.makeText(mContext, "Failed to delete category, Please Try Again Later", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    default:
                                        dialog.dismiss();
                                        break;
                                }
                            };
                            tBuilder.setMessage("Do You Want To Delete This Category?")
                                    .setNegativeButton("Yes", dListener)
                                    .setPositiveButton("No", dListener)
                                    .show();
                        }

                    }
                });
                binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
                binding.recycler.setAdapter(adapter);
            }

            @Override
            public void onAddUserSuccess(Users users) {
                Toast.makeText(mContext, "There are no categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
