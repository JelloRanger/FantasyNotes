package com.jelloranger.worldbuilder.fantasynotes.business.impl;

import com.jelloranger.worldbuilder.fantasynotes.business.EntityRepository;
import com.jelloranger.worldbuilder.fantasynotes.data.DbHelper;
import com.jelloranger.worldbuilder.fantasynotes.data.model.Entity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

@Singleton
public class EntityRepositoryImpl implements EntityRepository {

    private DbHelper mDbHelper;

    @Inject
    public EntityRepositoryImpl(final DbHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    @Override
    public Observable<Entity> insertEntity(final Entity entity) {
        return Observable.create(new Observable.OnSubscribe<Entity>() {
            @Override
            public void call(final Subscriber<? super Entity> subscriber) {
                mDbHelper.insertEntity(entity);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Entity> updateEntity(final Entity entity) {
        return Observable.create(new Observable.OnSubscribe<Entity>() {
            @Override
            public void call(final Subscriber<? super Entity> subscriber) {
                mDbHelper.updateEntity(entity);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Entity> getEntities() {
        return Observable.create(new Observable.OnSubscribe<Entity>() {
            @Override
            public void call(final Subscriber<? super Entity> subscriber) {
                final List<Entity> entities = mDbHelper.getEntities();

                for (final Entity entity : entities) {
                    subscriber.onNext(entity);
                }
                subscriber.onCompleted();
            }
        });
    }


}
