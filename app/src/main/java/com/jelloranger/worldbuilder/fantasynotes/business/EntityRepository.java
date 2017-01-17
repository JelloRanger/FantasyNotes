package com.jelloranger.worldbuilder.fantasynotes.business;

import com.jelloranger.worldbuilder.fantasynotes.data.model.Entity;

import java.util.List;

import rx.Observable;

public interface EntityRepository {

    Observable<Entity> insertEntity(final Entity entity);

    Observable<Entity> updateEntity(final Entity entity);

    Observable<Entity> getEntities();

}
