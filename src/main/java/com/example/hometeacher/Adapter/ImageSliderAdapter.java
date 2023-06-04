package com.example.hometeacher.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometeacher.ArraylistForm.CharacterForm;
import com.example.hometeacher.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>{
    private Context context;
    private ArrayList<String> sliderImage;


    public ImageSliderAdapter(Context context, ArrayList<String> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public ImageSliderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderAdapter.ViewHolder holder, int position) {
       // holder.bindSliderImage(sliderImage[position]);
        holder.onBind(sliderImage.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }



        void onBind(String item){
            Log.d("-------sendimg-------", String.valueOf(item));



            Picasso.get()
                    .load(item) // string or uri 상관없음
                    .resize(200, 200)
                    .centerCrop()
                    .into(mImageView);
        }



    }
}
