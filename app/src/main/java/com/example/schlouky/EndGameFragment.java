package com.example.schlouky;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class EndGameFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_end_game, container, false);
        Button replayButton = (Button) view.findViewById(R.id.replay);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartSetupActivity();
            }
        });
        return view;
    }

    private void StartSetupActivity() {
        Intent intent = new Intent(getContext(), SetupActivity.class);
        startActivity(intent);
    }

}