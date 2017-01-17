package com.jelloranger.worldbuilder.fantasynotes.dagger.modules;

import android.app.Application;
import android.content.Context;

import com.jelloranger.worldbuilder.fantasynotes.business.EntityRepository;
import com.jelloranger.worldbuilder.fantasynotes.business.impl.EntityRepositoryImpl;
import com.jelloranger.worldbuilder.fantasynotes.data.DbHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(final Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    DbHelper provideDbHelper() {
        return DbHelper.getInstance(mApplication.getApplicationContext());
    }

    @Provides
    @Singleton
    EntityRepository providesEntityRepository(final EntityRepositoryImpl entityRepository) {
        return entityRepository;
    }
}
