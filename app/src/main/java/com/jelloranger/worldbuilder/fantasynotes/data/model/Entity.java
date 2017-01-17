package com.jelloranger.worldbuilder.fantasynotes.data.model;

import android.graphics.Bitmap;

import com.jelloranger.worldbuilder.fantasynotes.data.BitmapDataObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Entity implements Serializable {

    public enum Type {
        PERSON("Person"),
        PLACE("Place"),
        EVENT("Event"),
        WEAPON("Weapon"),
        NATION("Nation");

        private final String typeName;

        Type(final String typeName) {
            this.typeName = typeName;
        }

        @Override
        public String toString() {
            return typeName;
        }
    }

    private String id;

    private Type type;

    private String name;

    private String description;

    private BitmapDataObject image;

    private List<String> connections = new ArrayList<>();

    public Entity(final Type type, final String name, final String description) {
        this();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Entity() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        if (image == null) {
            return null;
        }
        return image.getBitmap();
    }

    public void setImage(final Bitmap image) {
        this.image = new BitmapDataObject(image);
    }

    public List<String> getConnections() {
        return this.connections;
    }

    public void addConnection(final String id) {
        this.connections.add(id);
    }

    public void removeConnection(final String id) {
        this.connections.remove(id);
    }
}
