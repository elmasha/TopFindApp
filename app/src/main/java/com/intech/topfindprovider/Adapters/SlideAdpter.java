package com.intech.topfindprovider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.intech.topfindprovider.R;


public class SlideAdpter extends PagerAdapter {

    Context mContext;
    LayoutInflater layoutInflater;

    public SlideAdpter(Context context) {
        mContext = context;
    }

    public int[] slideIamage = {

            R.drawable.b1,
            R.drawable.b2,
    };

    public String[] slideHeadings = {
            "TopFind",
            "Select below.",

    };


    @Override
    public int getCount() {
        return slideHeadings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImage = (ImageView) view.findViewById(R.id.SlideImage);
        TextView slideHeading = (TextView) view.findViewById(R.id.Slide_Heading);

        slideImage.setImageResource(slideIamage[position]);
        slideHeading.setText(slideHeadings[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
