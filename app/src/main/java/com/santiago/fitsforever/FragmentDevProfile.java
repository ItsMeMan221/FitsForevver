package com.santiago.fitsforever;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDevProfile extends Fragment {
    Button linkedIn, gmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dev_profile, container, false);
        linkedIn = (Button) view.findViewById(R.id.linkedIn);
        gmail = (Button) view.findViewById(R.id.gmail);

        linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedIn();
            }
        });
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGmail();
            }
        });
        return view;
    }

    private void openLinkedIn() {
        String url = "https://www.linkedin.com/in/santiago-01843815a/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void openGmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {"igosantiago91@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject text here...");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the content here...");
        intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }
}
