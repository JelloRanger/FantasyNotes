package com.jelloranger.worldbuilder.fantasynotes.presentation;

import android.app.Application;

import com.jelloranger.worldbuilder.fantasynotes.dagger.components.ApplicationComponent;
import com.jelloranger.worldbuilder.fantasynotes.dagger.components.DaggerApplicationComponent;
import com.jelloranger.worldbuilder.fantasynotes.dagger.modules.ApplicationModule;

public class FantasyNotesApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
    }

    private void initializeInjector() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
