package com.example.krastaffapp.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.ImageLoader;
import com.example.krastaffapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private final Context context;
    private final List<SliderUtils> sliderImg, sliderName;


    public ViewPagerAdapter(List sliderImg, List sliderName,Context context) {
        this.sliderImg = sliderImg;
        this.sliderName = sliderName;
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderImg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {


        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.imageholder, null);

        SliderUtils utils = sliderImg.get(position);

        ShapeableImageView imageView = (ShapeableImageView) view.findViewById(R.id.imgview);

        TextView imageName = (TextView) view.findViewById(R.id.imgTitle);

        imageView.setShapeAppearanceModel(imageView.getShapeAppearanceModel()
        .toBuilder()
        .setAllCorners(CornerFamily.ROUNDED, 15)
        .build());
        ImageLoader imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(utils.getSliderImageUrl(), ImageLoader.getImageListener(imageView, R.drawable.kra_icon, android.R.drawable.ic_dialog_alert));

        imageName.setText(utils.getSliderName());
        Log.d("KRA:IMG", utils.getSliderName());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position == 0){
//                    Toast.makeText(context, "Slide 1 Clicked", Toast.LENGTH_SHORT).show();
                } else if(position == 1){
//                    Toast.makeText(context, "Slide 2 Clicked", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(context, "Slide 3 Clicked", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }



    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
