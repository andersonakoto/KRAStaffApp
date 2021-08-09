package com.example.krastaffapp.ui.embeddedapps;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.krastaffapp.R;
import com.example.krastaffapp.databinding.FragmentAppsBinding;


public class AppsFragment extends Fragment {

    private FragmentAppsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAppsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button equity_app = root.findViewById(R.id.equity_app);

        equity_app.setOnClickListener(view -> {
            Intent equityapp = new Intent(getActivity(), EquityAppMain.class);
            startActivity(equityapp);
//            this.finish();

        });

        Button nhif_app = root.findViewById(R.id.nhif_app);

        nhif_app.setOnClickListener(view -> {
            Intent nhifapp = new Intent(getActivity(), NHIFAppMain.class);
            startActivity(nhifapp);
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