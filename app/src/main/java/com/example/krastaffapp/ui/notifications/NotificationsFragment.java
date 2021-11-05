package com.example.krastaffapp.ui.notifications;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.krastaffapp.R;
import com.example.krastaffapp.databinding.FragmentNotificationsBinding;

import java.util.List;

public class NotificationsFragment extends Fragment {


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

        getData();

        return root;
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


    Context context;
    BroadcastReceiver br;


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
            Log.d("receiver", "Got message: " + message);
        }
    };




}
