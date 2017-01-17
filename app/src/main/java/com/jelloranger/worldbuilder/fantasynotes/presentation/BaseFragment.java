package com.jelloranger.worldbuilder.fantasynotes.presentation;

import android.support.v4.app.Fragment;

import com.jelloranger.worldbuilder.fantasynotes.dagger.components.ApplicationComponent;

public class BaseFragment extends Fragment {

    public ApplicationComponent getApplicationComponent() {
        return ((FantasyNotesApplication) getActivity().getApplication()).getApplicationComponent();
    }
}
