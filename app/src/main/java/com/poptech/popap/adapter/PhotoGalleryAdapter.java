package com.poptech.popap.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.poptech.popap.PhotoActivity;
import com.poptech.popap.R;
import com.poptech.popap.listener.HomeActivityDelegate;
import com.poptech.popap.utils.Database;
import com.poptech.popap.utils.Utils;

import java.util.ArrayList;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder> {
    private ArrayList<String> image_urls;
    private Context mContext;
    private int mPhotoId;

    public PhotoGalleryAdapter(Context context, int photo_id, ArrayList<String> image_urls) {
        this.image_urls = image_urls;
        this.mPhotoId = photo_id;
        this.mContext = context;
    }

    @Override
    public PhotoGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoGalleryAdapter.ViewHolder viewHolder, int position) {

        viewHolder.mPhotoRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if (mContext instanceof PhotoActivity) {
                    Database.getInstance().updatePhotoList(mPhotoId, image_urls.get(position));
                    ((PhotoActivity) mContext).onBackPressed();
                }
            }

        });

        Glide.with(mContext)
                .load(image_urls.get(position))
                .override(Utils.getDisplayWidth((Activity) mContext) / 3, Utils.getDisplayWidth((Activity) mContext) / 3)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.mPhotoView);

    }

    @Override
    public int getItemCount() {
        return image_urls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoView;
        private RippleView mPhotoRipple;

        public ViewHolder(View view) {
            super(view);
            mPhotoView = (ImageView) view.findViewById(R.id.photo_img_id);
            mPhotoRipple = (RippleView) view.findViewById(R.id.photo_ripple_id);

        }
    }
}
