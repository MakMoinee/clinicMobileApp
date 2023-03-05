package com.sample.clinic.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.clinic.Interfaces.AdapterListener;
import com.sample.clinic.Models.NearPlacesResponse;
import com.sample.clinic.Models.Photos;
import com.sample.clinic.R;
import com.sample.clinic.databinding.ItemBuildingsBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    ItemBuildingsBinding binding;
    Context mContext;
    List<NearPlacesResponse> nearPlacesResponses;
    AdapterListener listener;


    public HospitalAdapter(Context mContext, List<NearPlacesResponse> nearPlacesResponses, AdapterListener l) {
        this.mContext = mContext;
        this.nearPlacesResponses = nearPlacesResponses;
        this.listener = l;
    }

    @NonNull
    @Override
    public HospitalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemBuildingsBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalAdapter.ViewHolder holder, int position) {
        NearPlacesResponse resp = nearPlacesResponses.get(position);
        if (resp.getPhotos() != null) {
            Photos photos = resp.getPhotos().get(0);
            Uri uri = Uri.parse(photos.getPhotoUrl());
            Picasso.get().invalidate(uri);
            Picasso.get().load(uri)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.imgBuildingPoster, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.txtBuildingName.setText(resp.getName());
//                        holder.itemView.setVisibility(View.VISIBLE);
                            holder.pb.setVisibility(View.GONE);
                            holder.imgBuildingPoster.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.txtBuildingName.setText(resp.getName());
                            holder.pb.setVisibility(View.GONE);
                            holder.imgBuildingPoster.setImageResource(R.drawable.default_hospital);
                            holder.imgBuildingPoster.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            holder.txtBuildingName.setText(resp.getName());
            holder.pb.setVisibility(View.GONE);
            holder.imgBuildingPoster.setImageResource(R.drawable.default_hospital);
            holder.imgBuildingPoster.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            NearPlacesResponse np = nearPlacesResponses.get(holder.getAbsoluteAdapterPosition());
            listener.onClick(np);
        });
    }

    @Override
    public int getItemCount() {
        return nearPlacesResponses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgBuildingPoster;
        public TextView txtBuildingName;
        public ProgressBar pb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBuildingPoster = itemView.findViewById(R.id.imgBuildingPoster);
            txtBuildingName = itemView.findViewById(R.id.txtBuildingName);
            pb = itemView.findViewById(R.id.pb);
        }
    }
}
