package com.sample.clinic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.Categories;
import com.sample.clinic.R;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    Context mContext;
    List<Categories> categoriesList;
    AdapterListener listener;

    public AdminCategoryAdapter(Context mContext, List<Categories> categoriesList, AdapterListener listener) {
        this.mContext = mContext;
        this.categoriesList = categoriesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryAdapter.ViewHolder holder, int position) {
        Categories category = categoriesList.get(position);
        holder.imgCategory.setImageResource(category.getCategoryPhotoId());
        holder.txtCategory.setText(category.getCategory());
        holder.itemView.setOnClickListener(v -> listener.onClick(holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView txtCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategory = itemView.findViewById(R.id.txtCategoryName);
        }
    }
}
