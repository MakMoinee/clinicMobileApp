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

import com.sample.clinic.Interfaces.BuildingListener;
import com.sample.clinic.Models.Buildings;
import com.sample.clinic.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    Context context;
    List<Buildings> buildingsList;
    BuildingListener buildingListener;

    public BuildingAdapter(Context mContext, List<Buildings> b, BuildingListener listener) {
        this.buildingsList = b;
        context = mContext;
        this.buildingListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_buildings, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Buildings buildings = buildingsList.get(position);
        Uri uri = Uri.parse(buildings.getPicturePath());
        String buildName = buildings.getBuildingName().replaceAll(".jpg", "");
//        holder.itemView.setVisibility(View.INVISIBLE);
        Picasso.get().invalidate(uri);
        Picasso.get().load(uri)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imgBuildingPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.txtBuildingName.setText(buildName);
//                        holder.itemView.setVisibility(View.VISIBLE);
                        holder.pb.setVisibility(View.GONE);
                        holder.imgBuildingPoster.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return this.buildingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgBuildingPoster;
        public TextView txtBuildingName;
        public ProgressBar pb;

        public ViewHolder(View itemView) {
            super(itemView);
            imgBuildingPoster = itemView.findViewById(R.id.imgBuildingPoster);
            txtBuildingName = itemView.findViewById(R.id.txtBuildingName);
            pb = itemView.findViewById(R.id.pb);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildingListener.OnClickListener(v, getAdapterPosition());
                }
            });
        }


    }
}
