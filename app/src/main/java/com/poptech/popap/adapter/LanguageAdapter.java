
package com.poptech.popap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.poptech.popap.PhotoActivity;
import com.poptech.popap.R;
import com.poptech.popap.database.PopapDatabase;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LanguageAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private List<String> mLangList;
    private List<String> mLangFilter;
    private List<Boolean> mLangChecked;
    private String mItemId;
    private ValueFilter mValueFilter;
    private String mSearch;

    public LanguageAdapter(Context context, String id, List<String> langList, List<Boolean> checked) {
        this.mContext = context;
        this.mLangList = langList;
        this.mLangFilter = langList;
        this.mLangChecked = checked;
        this.mItemId = id;
    }

    @Override
    public int getCount() {
        return mLangList.size();
    }

    @Override
    public Object getItem(int position) {
        return mLangList.get(position);
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
            holder.mCheck = (ImageView) convertView.findViewById(R.id.checked_item_iv_id);
            holder.mLanguage = (RelativeLayout) convertView.findViewById(R.id.language_item_rl_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mText.setText(highlight(mSearch, mLangList.get(position)));
        if (mLangChecked.get(position)) {
            holder.mCheck.setVisibility(View.VISIBLE);
        } else {
            holder.mCheck.setVisibility(View.GONE);
        }
        holder.mLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mLangChecked.size(); i++) {
                    mLangChecked.set(i, false);
                }
                mLangChecked.set(position, true);
                notifyDataSetChanged();
                PopapDatabase.getInstance(mContext).updateLanguageActive(mItemId, mLangList.get(position));
                ((PhotoActivity) mContext).onBackPressed();
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mValueFilter == null) {
            mValueFilter = new ValueFilter();
        }
        return mValueFilter;
    }

    private class ViewHolder {

        private TextView mText;

        private ImageView mCheck;

        private RelativeLayout mLanguage;
    }


    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            mSearch = constraint.toString();

            if (constraint != null && constraint.length() > 0) {
                List<String> filterList = new ArrayList<>();
                for (String language : mLangFilter) {
                    String search = constraint.toString();
                    if (hasContains(search, language)) {
                        filterList.add(language);
                    }
                }

                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mLangFilter.size();
                results.values = mLangFilter;
            }
            return results;
        }


        private boolean hasContains(String search, String text) {
            return Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE).matcher(text).find();
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mLangList = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }

    private CharSequence highlight(String search, String originalText) {
        if (search == null || search.isEmpty()) {
            return originalText;
        }
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        int start = normalizedText.indexOf(search.toLowerCase());
        if (start < 0) {
            return originalText;
        } else {
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());
                highlighted.setSpan(new BackgroundColorSpan(Color.parseColor("#BFFFC6")),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = normalizedText.indexOf(search.toLowerCase(), spanEnd);
            }
            return highlighted;
        }
    }
}
