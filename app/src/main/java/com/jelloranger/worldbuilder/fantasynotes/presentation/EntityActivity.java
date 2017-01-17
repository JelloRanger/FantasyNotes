package com.jelloranger.worldbuilder.fantasynotes.presentation;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.jelloranger.worldbuilder.fantasynotes.R;
import com.jelloranger.worldbuilder.fantasynotes.business.EntityRepository;
import com.jelloranger.worldbuilder.fantasynotes.dagger.components.ApplicationComponent;
import com.jelloranger.worldbuilder.fantasynotes.data.DbHelper;
import com.jelloranger.worldbuilder.fantasynotes.data.model.Entity;

import javax.inject.Inject;

import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EntityActivity extends BaseActivity implements
        EntityListFragment.Callbacks,
        CreateEntityFragment.Callbacks {

    private static final String TAG = EntityActivity.class.getSimpleName();

    @Inject
    EntityRepository mEntityRepository;

    private FragmentListener mFragmentListener;

    private EntityListFragment mEntityListFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);

        if (savedInstanceState == null) {
            final EntityListFragment entityListFragment = EntityListFragment.newInstance();
            mEntityListFragment = entityListFragment;
            setFragmentListener(entityListFragment);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_content, entityListFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "MyFragment", mEntityListFragment);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mEntityListFragment = (EntityListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MyFragment");
        setFragmentListener(mEntityListFragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFragmentListener.onBackPressed();
    }

    // EntityListFragment callbacks
    @Override
    public void addEntity() {
        startCreateEntityFragment(null);
    }

    @Override
    public void editEntity(final Entity entity) {
        startCreateEntityFragment(entity);
    }

    private void startCreateEntityFragment(final Entity entity) {
        moveFabForEntityCreation();

        final CreateEntityFragment createEntityFragment;
        if (entity != null) {
            createEntityFragment = CreateEntityFragment.newInstance(entity);
        } else {
            createEntityFragment = CreateEntityFragment.newInstance();
        }
        setFragmentListener(createEntityFragment);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_content, createEntityFragment, CreateEntityFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    // CreateEntityFragment callbacks
    @Override
    public void createEntity(final Entity entity) {
        mEntityRepository.insertEntity(entity)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Entity inserted");
                    }

                    @Override
                    public void onError(final Throwable e) {
                        Log.e(TAG, "Error inserting entity", e);
                    }

                    @Override
                    public void onNext(final Entity entity) {

                    }
                });

        startEntityListFragment(entity);
    }

    @Override
    public void saveEditEntity(final Entity entity) {
        mEntityRepository.updateEntity(entity)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Entity updated");
                    }

                    @Override
                    public void onError(final Throwable e) {
                        Log.e(TAG, "Error updating entity", e);
                    }

                    @Override
                    public void onNext(final Entity entity) {

                    }
                });

        startEntityListFragment(null);
    }

    @Override
    public void onCancel() {
        startEntityListFragment(null);
    }

    private void startEntityListFragment(final Entity entity) {
        moveFabForEntityList();

        if (entity != null && mEntityListFragment != null) {
            mEntityListFragment.addEntity(entity);
        }
        setFragmentListener(mEntityListFragment);
        getSupportFragmentManager().popBackStackImmediate();
    }

    @OnClick(R.id.fab)
    public void onFabClicked(final View view) {
        if (mFragmentListener != null) {
            mFragmentListener.onFabClicked(view);
        }
    }

    private void setFragmentListener(final FragmentListener fragmentListener) {
        mFragmentListener = fragmentListener;
    }

    private void setFabIcon(final int resId) {
        Log.i("Jacob", "this is real life");
        /*mFloatingActionButton.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(final FloatingActionButton fab) {
                Log.i("Jacob", "hello world gjwiegiwgjeg");
                fab.setImageResource(resId);
                fab.show();
            }
        });*/
        final CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) mFloatingActionButton.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        mFloatingActionButton.setLayoutParams(p);
        mFloatingActionButton.hide();
    }

    private void moveFabForEntityList() {
        //setFabIcon(R.drawable.ic_add_black_24dp);
        mFloatingActionButton.setImageResource(R.drawable.ic_add_black_24dp);
        mToolbarScrim.setVisibility(View.GONE);

        // Move FAB to anchor app bar
        final CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) mFloatingActionButton.getLayoutParams();
        layoutParams.anchorGravity = Gravity.BOTTOM | Gravity.END;
        layoutParams.setAnchorId(R.id.app_bar_layout);
        layoutParams.gravity = Gravity.NO_GRAVITY;
        mFloatingActionButton.setLayoutParams(layoutParams);

        mAppBarLayout.setExpanded(true);
        mToolbarBackgroundImage.setImageBitmap(null);
    }

    private void moveFabForEntityCreation() {
        //setFabIcon(R.drawable.ic_done_black_24dp);
        mFloatingActionButton.setImageResource(R.drawable.ic_done_black_24dp);

        // Move FAB to bottom right
        final CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) mFloatingActionButton.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        layoutParams.anchorGravity = Gravity.NO_GRAVITY;
        layoutParams.setAnchorId(View.NO_ID);
        mFloatingActionButton.setLayoutParams(layoutParams);
    }

    public ApplicationComponent getApplicationComponent() {
        return ((FantasyNotesApplication) getApplication()).getApplicationComponent();
    }
}
