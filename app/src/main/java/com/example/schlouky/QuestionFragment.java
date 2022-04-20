package com.example.schlouky;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

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
            if (player.photoUri != null) {
                photosNb++;
            }
        }
        if (photosNb == 0) {
            ((ViewManager) view.findViewById(R.id.container)).removeView(photoLayout);
            return;
        }
        for (Player player : question.players) {
            if (player.photoUri != null) {
                ImageView photoView = new ImageView(getContext());
                Bitmap photo = loadPhoto(player.photoUri, player.photoPath);

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

    private Bitmap loadPhoto(Uri uri, String path) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), uri);
            ExifInterface exif = null;
            try {
                File pictureFile = new File(path);
                exif = new ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateBitmap(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateBitmap(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateBitmap(bitmap, 270);
                    break;
            }

            return bitmap;

        } catch (IOException e) {
            return null;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}