package com.poptech.popap.adapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.poptech.popap.PhotoActivity;
import com.poptech.popap.R;
import com.poptech.popap.bean.PhotoBean;
import com.poptech.popap.database.PopapDatabase;
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

        /*viewHolder.mPhotoRipple.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if (mContext instanceof HomeActivityDelegate) {
                    ((HomeActivityDelegate) mContext).onStartPhotoDetailFragment(mPhotoList.get(position).getPhotoId());
                }
            }

        });*/
        viewHolder.mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof HomeActivityDelegate) {
                    ((HomeActivityDelegate) mContext).onStartPhotoDetailFragment(mPhotoList.get(position).getPhotoId());
                }
            }
        });
        viewHolder.mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence options[] = new CharSequence[]{"Remove", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Remove Item");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (PopapDatabase.getInstance(mContext).checkPhotoExist(mPhotoList.get(position).getPhotoId())) {
                                PopapDatabase.getInstance(mContext).deletePhoto(mPhotoList.get(position).getPhotoId());
                            }
                            mPhotoList.remove(position);
                            notifyDataSetChanged();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
        Glide.with(mContext)
                .load(mPhotoList.get(position).getPhotoPath())
                .override(Utils.getDisplayWidth((Activity) mContext) / 3, Utils.getDisplayWidth((Activity) mContext) / 3)
                .centerCrop()
                .thumbnail(0.5f)
                .placeholder(R.color.white)
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
