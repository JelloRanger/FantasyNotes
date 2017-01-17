package com.jelloranger.worldbuilder.fantasynotes.dagger.components;

import com.jelloranger.worldbuilder.fantasynotes.dagger.modules.ApplicationModule;
import com.jelloranger.worldbuilder.fantasynotes.presentation.CreateEntityFragment;
import com.jelloranger.worldbuilder.fantasynotes.presentation.EntityActivity;
import com.jelloranger.worldbuilder.fantasynotes.presentation.EntityListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(final EntityActivity entityActivity);
    void inject(final EntityListFragment entityListFragment);
    void inject(final CreateEntityFragment createEntityFragment);
}
