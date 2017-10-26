package com.example.ljuni.lapitchat.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ljuni.lapitchat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SolicitacoesFragment extends Fragment {


    public SolicitacoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_solicitacoes, container, false);
    }

}
