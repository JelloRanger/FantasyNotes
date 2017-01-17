package com.jelloranger.worldbuilder.fantasynotes.presentation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelloranger.worldbuilder.fantasynotes.R;
import com.jelloranger.worldbuilder.fantasynotes.business.EntityRepository;
import com.jelloranger.worldbuilder.fantasynotes.dagger.components.ApplicationComponent;
import com.jelloranger.worldbuilder.fantasynotes.data.model.Entity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EntityListFragment extends BaseFragment implements FragmentListener {

    public static final String TAG = EntityListFragment.class.getSimpleName();

    private static final String ENTITY_TAG = "com.jelloranger.worldbuilder.fantasynotes.presentation.ENTITY_TAG";

    @BindView(R.id.entity_list_recycler_view)
    RecyclerView mRecyclerView;

    @Inject
    EntityRepository mEntityRepository;

    private List<Entity> mEntityList;

    private EntityListAdapter mEntityListAdapter;

    private Callbacks mCallbacks;

    public static EntityListFragment newInstance() {
        return new EntityListFragment();
    }

    public static EntityListFragment newInstance(final Entity entity) {
        final EntityListFragment entityListFragment = newInstance();

        final Bundle arguments = new Bundle();
        arguments.putSerializable(ENTITY_TAG, entity);
        entityListFragment.setArguments(arguments);

        return entityListFragment;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        try {
            mCallbacks = (Callbacks) context;
        } catch (final ClassCastException cce) {
            throw new ClassCastException(context.toString() + " must implement Callbacks");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);

        mEntityList = new ArrayList<>();
        mEntityRepository.getEntities()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Completed fetching list of entities");
                    }

                    @Override
                    public void onError(final Throwable e) {
                        Log.e(TAG, "Error fetching list of entities", e);
                    }

                    @Override
                    public void onNext(final Entity entity) {
                        mEntityList.add(entity);
                    }
                });

        final Bundle arguments = getArguments();
        if (arguments != null) {
            mEntityList.add((Entity) arguments.getSerializable(ENTITY_TAG));
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.entity_list_fragment, container, false);
        ButterKnife.bind(this, view);

        mEntityListAdapter = new EntityListAdapter(mEntityList);
        mEntityListAdapter.setOnEntityClickedListener(new EntityListAdapter.OnEntityClickedListener() {
            @Override
            public void onEntityClicked(final Entity entity) {
                mCallbacks.editEntity(entity);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mEntityListAdapter);

        return view;
    }

    public void addEntity(final Entity entity) {
        mEntityList.add(entity);
        mEntityListAdapter.notifyItemInserted(mEntityList.size() - 1);
    }

    @Override
    public void onFabClicked(final View view) {
        mCallbacks.addEntity();
    }

    @Override
    public void onBackPressed() {
        // no op
    }

    public interface Callbacks {
        void addEntity();
        void editEntity(final Entity entity);
    }
}
