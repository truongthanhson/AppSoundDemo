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
import com.poptech.popap.bean.PhotoBean;
import com.poptech.popap.database.PopapDatabase;
import com.poptech.popap.listener.HomeActivityDelegate;
import com.poptech.popap.utils.Database;
import com.poptech.popap.utils.Utils;

import java.util.ArrayList;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder> {
    private ArrayList<String> mPhotoList;
    private Context mContext;
    private String mPhotoId;

    public PhotoGalleryAdapter(Context context, String photo_id, ArrayList<String> photoList) {
        this.mPhotoList = photoList;
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
                    PopapDatabase.getInstance(mContext).updatePhotoPath(mPhotoId, mPhotoList.get(position));
                    ((PhotoActivity) mContext).onBackPressed();
                }
            }

        });

        Glide.with(mContext)
                .load(mPhotoList.get(position))
                .override(Utils.getDisplayWidth((Activity) mContext) / 3, Utils.getDisplayWidth((Activity) mContext) / 3)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.mPhotoView);

    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
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
