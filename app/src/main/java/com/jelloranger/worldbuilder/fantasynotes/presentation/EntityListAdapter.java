package com.jelloranger.worldbuilder.fantasynotes.presentation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jelloranger.worldbuilder.fantasynotes.R;
import com.jelloranger.worldbuilder.fantasynotes.data.model.Entity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntityListAdapter extends RecyclerView.Adapter<EntityListAdapter.EntityViewHolder> {

    private List<Entity> mEntityList;

    private OnEntityClickedListener mEntityClickedListener;

    public EntityListAdapter(final List<Entity> entityList) {
        mEntityList = entityList;
    }

    public void setOnEntityClickedListener(final OnEntityClickedListener listener) {
        mEntityClickedListener = listener;
    }

    @Override
    public EntityViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entity_list_item, parent, false);
        return new EntityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EntityViewHolder holder, final int position) {
        final Entity entity = mEntityList.get(position);
        if (entity.getImage() != null) {
            holder.mImageView.setImageBitmap(entity.getImage());
        }
        holder.mTitle.setText(entity.getName());
        holder.mDescription.setText(entity.getDescription());
        holder.mNumConnections.setText(String.valueOf(entity.getConnections().size()));
    }

    @Override
    public int getItemCount() {
        return mEntityList.size();
    }

    public class EntityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.entity_list_item_image) ImageView mImageView;
        @BindView(R.id.entity_list_item_title) TextView mTitle;
        @BindView(R.id.entity_list_item_description) TextView mDescription;
        @BindView(R.id.entity_list_item_number_of_connections) TextView mNumConnections;

        public EntityViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            if (mEntityClickedListener != null) {
                mEntityClickedListener.onEntityClicked(mEntityList.get(getAdapterPosition()));
            }
        }
    }

    public interface OnEntityClickedListener {
        void onEntityClicked(final Entity entity);
    }
}
