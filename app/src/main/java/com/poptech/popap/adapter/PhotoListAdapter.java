package com.poptech.popap.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.poptech.popap.R;
import com.poptech.popap.bean.PhotoBean;
import com.poptech.popap.listener.HomeActivityDelegate;
import com.poptech.popap.utils.StringUtils;
import com.poptech.popap.utils.Utils;

import java.util.ArrayList;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> {
    private ArrayList<PhotoBean> mPhotoList;
    private Context mContext;

    public PhotoListAdapter(Context context, ArrayList<PhotoBean> photoList) {
        this.mPhotoList = photoList;
        this.mContext = context;
    }

    @Override
    public PhotoListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoListAdapter.ViewHolder viewHolder, int position) {

        viewHolder.mPhotoRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if (mContext instanceof HomeActivityDelegate) {
                    ((HomeActivityDelegate) mContext).onStartPhotoDetailFragment(mPhotoList.get(position).getPhotoId());
                }
            }

        });

        if (StringUtils.isNullOrEmpty(mPhotoList.get(position).getPhotoPath())) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    Utils.getDisplayWidth((Activity) mContext) / 3,
                    Utils.getDisplayWidth((Activity) mContext) / 3);
            viewHolder.mPhotoView.setLayoutParams(layoutParams);
        } else {
            Glide.with(mContext)
                    .load(mPhotoList.get(position).getPhotoPath())
                    .override(Utils.getDisplayWidth((Activity) mContext) / 3, Utils.getDisplayWidth((Activity) mContext) / 3)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mPhotoView);
        }

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
