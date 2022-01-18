package com.example.krastaffapp.ui.notifications;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.krastaffapp.R;
import com.example.krastaffapp.databinding.FragmentNotificationsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    RequestQueue rq;
    List<SliderUtils> sliderImg, sliderName;
    ViewPagerAdapter viewPagerAdapter;

    String request_url = "http://10.151.1.114/imgfetch.php";


    private FragmentNotificationsBinding binding;
    public static NotificationsDB notificationsDB;

    public RecyclerView recyclerView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationsDB = Room.databaseBuilder(requireActivity(),NotificationsDB.class,"notificationsDB")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();


        // Add the following lines to create RecyclerView
        recyclerView = root.findViewById(R.id.rvPostsLis);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        rq = CustomVolleyRequest.getInstance(context).getRequestQueue();

        sliderImg = new ArrayList<>();
        sliderName = new ArrayList<>();

        viewPager = root.findViewById(R.id.viewPager);

//        sliderDotspanel = root.findViewById(R.id.SliderDots);

        sendRequest();


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

//                for(int i = 0; i< dotscount; i++){
//                    dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.nonactive_dot));
//                }
//
//                dots[position].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getData();

        return root;
    }

    Context context;


    public void sendRequest(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, request_url, null,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0; i < response.length(); i++){

                    SliderUtils sliderUtils = new SliderUtils();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        sliderUtils.setSliderImageUrl(jsonObject.getString("imgUrl"));
                        sliderUtils.setSliderName(jsonObject.getString("imgDesc"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sliderImg.add(sliderUtils);


                }

                viewPagerAdapter = new ViewPagerAdapter(sliderImg, sliderName, context);

                viewPager.setAdapter(viewPagerAdapter);

                startAutoSlider(viewPagerAdapter.getCount());



            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        CustomVolleyRequest.getInstance(context).addToRequestQueue(jsonArrayRequest);

    }
    private Runnable runnable = null;
    private final Handler handler = new Handler();

    private void startAutoSlider(final int count) {

        runnable = () -> {
            int pos = viewPager.getCurrentItem();
            pos = pos + 1;
            if (pos >= count) pos = 0;
            viewPager.setCurrentItem(pos);
            handler.postDelayed(runnable, 3000);
        };
        handler.postDelayed(runnable, 3000);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);

        binding = null;
    }

    private void getData() {
        @SuppressLint("StaticFieldLeak")
        class GetData extends AsyncTask<Void,Void, List<NotificationsList>> {


            @Override
            protected List<NotificationsList> doInBackground(Void... voids) {

                List<NotificationsList> myData = NotificationsFragment.notificationsDB.notificationsDAO().getMyData();
                return myData;


            }

            @Override
            protected void onPostExecute(List<NotificationsList>nlist) {

                NotificationsAdapter adapter=new NotificationsAdapter(nlist, notificationsDB, recyclerView);
                recyclerView.setAdapter(adapter);

                super.onPostExecute(nlist);
            }
        }
        GetData gd=new GetData();
        gd.execute();

    }




    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        setup();
    }
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    private void setup() {
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("custom-listener"));
    }


    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            NotificationsFragment fragment = new NotificationsFragment();
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, fragment).commit();

            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("KRA:Receiver", "Got message: " + message);
        }
    };




}
