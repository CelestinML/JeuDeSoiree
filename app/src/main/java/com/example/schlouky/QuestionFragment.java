package com.example.schlouky;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class QuestionFragment extends Fragment {

    private Question question;
    private LinearLayout photoLayout;

    private int layoutWidth;
    ArrayList<Uri> uriToDisplay = new ArrayList<>();
    ViewManager viewManager;

    public QuestionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_question, container, false);
        root.post(new Runnable() {
            @Override
            public void run() {
                layoutWidth = root.getMeasuredWidth();
                if (uriToDisplay.size() == 0) {
                    viewManager.removeView(photoLayout);
                } else {
                    int maxImageWidth = (layoutWidth - 4 * 15) / uriToDisplay.size();
                    for (Uri uri : uriToDisplay) {
                        ImageView photoView = new ImageView(getContext());
                        loadPhoto(uri, photoView);

                        photoView.setAdjustViewBounds(true);
                        photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                        photoLayout.addView(photoView);

                        ViewGroup.LayoutParams params = photoView.getLayoutParams();
                        params.width = maxImageWidth;
                        photoView.setLayoutParams(params);
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        photoLayout = (LinearLayout) view.findViewById(R.id.photos);
        question = requireArguments().getParcelable("question");
        TextView questionView = (TextView) view.findViewById(R.id.question);
        questionView.setText(question.text);

        for (Player player : question.players) {
            if (player.photoUri != null) {
                uriToDisplay.add(player.photoUri);
            }
        }

        viewManager = ((ViewManager) view);
    }

    private void loadPhoto(Uri uri, ImageView targetImageView) {
        Glide.with(this)
                .asBitmap()
                .load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        targetImageView.setImageBitmap(resource);
                    }
                });
    }
}