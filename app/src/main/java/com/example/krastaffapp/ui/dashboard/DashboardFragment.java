package com.example.krastaffapp.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.krastaffapp.R;
import com.example.krastaffapp.databinding.FragmentDashboardBinding;

import java.util.Objects;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;


    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences pref = requireActivity().getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        String sname = pref.getString("KEY_STAFFNAME", null);
        String sno = pref.getString("KEY_STAFFNUMBER", null);
        String sdept = pref.getString("KEY_STAFFDEPT", null);
        String stitle = pref.getString("KEY_STAFFTITLE", null);


        TextView staffNo = (TextView)root.findViewById(R.id.staffNoView);
        TextView staffName = (TextView)root.findViewById(R.id.staffNameView);
        TextView staffDept = (TextView)root.findViewById(R.id.staffDeptView);
        TextView staffTitle = (TextView)root.findViewById(R.id.staffTitleView);

        staffNo.setText("Staff Number: " + sno);
        staffName.setText("Name: " + sname);
        staffDept.setText("Department: " + sdept);
        staffTitle.setText("Title: " + stitle);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}