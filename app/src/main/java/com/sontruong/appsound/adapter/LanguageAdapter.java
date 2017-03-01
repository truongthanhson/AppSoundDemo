
package com.sontruong.appsound.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sontruong.appsound.R;

import java.util.List;

public class LanguageAdapter extends BaseAdapter {

    private Context mContext;

    private List<String> mLanguages;

    private List<Boolean> mChecked;

    public LanguageAdapter(Context context, List<String> languages, List<Boolean> checked) {
        this.mContext = context;
        this.mLanguages = languages;
        this.mChecked = checked;

    }

    @Override
    public int getCount() {
        return mLanguages.size();
    }

    @Override
    public Object getItem(int position) {
        return mLanguages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_language_layout, parent, false);

            holder.mText = (TextView) convertView.findViewById(R.id.language_item_tv_id);
            holder.mCheck = (ImageView) convertView.findViewById(R.id.check_item_tv_id);
            holder.mLanguage = (RelativeLayout) convertView.findViewById(R.id.language_item_rl_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mText.setText(mLanguages.get(position));
        if (mChecked.get(position)) {
            holder.mCheck.setVisibility(View.VISIBLE);
        } else {
            holder.mCheck.setVisibility(View.GONE);
        }
        holder.mLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mChecked.size(); i++) {
                    mChecked.set(i, false);
                }
                mChecked.set(position, true);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class ViewHolder {

        private TextView mText;

        private ImageView mCheck;

        private RelativeLayout mLanguage;
    }


}
