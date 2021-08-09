package com.example.krastaffapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.krastaffapp.R;
import com.example.krastaffapp.checkinout.CheckinoutActivity;
import com.example.krastaffapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button check_in_out = root.findViewById(R.id.check_in_out);

        check_in_out.setOnClickListener(view -> {
            Intent checkinoutscreen = new Intent(getActivity(), CheckinoutActivity.class);
            startActivity(checkinoutscreen);
//            this.finish();

        });
        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}