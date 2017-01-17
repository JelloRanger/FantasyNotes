package com.jelloranger.worldbuilder.fantasynotes.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jelloranger.worldbuilder.fantasynotes.R;
import com.jelloranger.worldbuilder.fantasynotes.business.EntityRepository;
import com.jelloranger.worldbuilder.fantasynotes.data.model.Entity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CreateEntityFragment extends BaseFragment implements FragmentListener {

    public static final String TAG = CreateEntityFragment.class.getSimpleName();

    private static final int GET_FROM_GALLERY = 1;

    private Entity mEntity;

    private Callbacks mCallbacks;

    private boolean mEditMode;

    private static final String ENTITY_TAG = "com.jelloranger.worldbuilder.fantasynotes.presentation.ENTITY_TAG";

    @BindView(R.id.create_entity_root_layout)
    LinearLayout mRootLayout;

    @BindView(R.id.create_entity_type_spinner)
    Spinner mEntityTypeSpinner;

    @BindView(R.id.create_entity_connections_spinner)
    Spinner mConnectionsSpinner;

    @BindView(R.id.create_entity_name)
    TextInputEditText mEntityName;

    @BindView(R.id.create_entity_description)
    TextInputEditText mEntityDescription;

    @Inject
    EntityRepository mEntityRepository;

    public static CreateEntityFragment newInstance() {
        return new CreateEntityFragment();
    }

    public static CreateEntityFragment newInstance(final Entity entity) {
        final CreateEntityFragment createEntityFragment = newInstance();

        final Bundle arguments = new Bundle();
        arguments.putSerializable(ENTITY_TAG, entity);
        createEntityFragment.setArguments(arguments);

        return createEntityFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);

        final Bundle arguments = getArguments();
        if (arguments != null) {
            mEntity = (Entity) arguments.getSerializable(ENTITY_TAG);
            mEditMode = true;
        } else {
            mEntity = new Entity();
            mEditMode = false;
        }
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.create_entity_fragment, container, false);
        ButterKnife.bind(this, view);

        setEntityFields();

        return view;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            final Uri selectedImage = data.getData();
            final Bitmap bitmap;
            try {
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                bitmap = BitmapFactory.decodeStream(imageStream);
                mEntity.setImage(bitmap);
                ((BaseActivity) getActivity()).mToolbarBackgroundImage.setImageBitmap(bitmap);
                ((BaseActivity) getActivity()).mToolbarScrim.setVisibility(View.VISIBLE);
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
                Snackbar.make(mRootLayout, "An error occurred uploading the image selected", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else {
            Snackbar.make(mRootLayout, "Please select an image", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @OnClick(R.id.create_entity_upload_image)
    public void onUploadImageClicked() {
        startActivityForResult(
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    private void setEntityFields() {
        final ArrayAdapter<Entity.Type> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, Entity.Type.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEntityTypeSpinner.setAdapter(adapter);

        final Map<String, String> possibleConnections = new HashMap<>();
        mEntityRepository.getEntities()
                .filter(new Func1<Entity, Boolean>() {
                    @Override
                    public Boolean call(final Entity entity) {
                        return !entity.getId().equals(mEntity.getId());
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Finished fetching entities");
                        final ArrayAdapter<String> connectionAdapter = new ArrayAdapter<>(
                                getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(possibleConnections.values()));
                        connectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mConnectionsSpinner.setAdapter(connectionAdapter);
                    }

                    @Override
                    public void onError(final Throwable e) {
                        Log.e(TAG, "Error fetching entities");
                    }

                    @Override
                    public void onNext(final Entity entity) {
                        Log.d(TAG, String.format("Here %s", entity.getName()));
                        possibleConnections.put(entity.getId(), entity.getName());
                    }
                });

        if (!mEditMode) {
            return;
        }

        mEntityName.setText(mEntity.getName());
        mEntityDescription.setText(mEntity.getDescription());


        Log.d(TAG, String.format("blah: %s", adapter.getPosition(mEntity.getType())));
        mEntityTypeSpinner.setSelection(adapter.getPosition(mEntity.getType()));



        if (mEntity.getImage() != null) {
            ((BaseActivity) getActivity()).mToolbarBackgroundImage.setImageBitmap(mEntity.getImage());
            ((BaseActivity) getActivity()).mToolbarScrim.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFabClicked(final View view) {
        saveEntityFields();
        if (mEditMode) {
            mCallbacks.saveEditEntity(mEntity);
        } else {
            mCallbacks.createEntity(mEntity);
        }
    }

    @Override
    public void onBackPressed() {
        mCallbacks.onCancel();
    }

    private void saveEntityFields() {
        mEntity.setName(mEntityName.getText().toString());
        mEntity.setDescription(mEntityDescription.getText().toString());
        mEntity.setType(Entity.Type.valueOf(mEntityTypeSpinner.getSelectedItem().toString().toUpperCase()));
    }

    public interface Callbacks {
        void createEntity(final Entity entity);
        void saveEditEntity(final Entity entity);
        void onCancel();
    }
}
