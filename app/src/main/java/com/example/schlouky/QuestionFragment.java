package com.example.schlouky;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class QuestionFragment extends Fragment {

    private Question question;
    private LinearLayout photoLayout;

    public QuestionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        photoLayout = (LinearLayout) view.findViewById(R.id.photos);
        question = requireArguments().getParcelable("question");
        TextView questionView = (TextView) view.findViewById(R.id.question);
        questionView.setText(question.text);
        int photosNb = 0;
        for (Player player : question.players) {
            if (!player.photoPath.equals("")) {
                photosNb++;
            }
        }
        if (photosNb == 0) {
            return;
        }
        for (Player player : question.players) {
            if (!player.photoPath.equals("")) {
                ImageView photoView = new ImageView(getContext());
                Bitmap photo = loadPhoto(player.photoPath);

                photoView.setImageBitmap(photo);
                photoView.setAdjustViewBounds(true);

                photoLayout.addView(photoView);

                // Gets the layout params that will allow you to resize the layout
                ViewGroup.LayoutParams params = photoView.getLayoutParams();

                // We use the whole layout's height
                params.height = ActionBar.LayoutParams.MATCH_PARENT;

                //We update the dimensions
                photoView.setLayoutParams(params);
            }
        }
    }

    private Bitmap loadPhoto(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        } else {
            return null;
        }
    }
}