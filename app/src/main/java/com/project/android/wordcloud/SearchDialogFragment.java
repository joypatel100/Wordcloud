package com.project.android.wordcloud;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)

public class SearchDialogFragment extends DialogFragment {


    private final String LOG_TAG = DialogFragment.class.getSimpleName();



    public static SearchDialogFragment newInstance(int title) {
        SearchDialogFragment frag = new SearchDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_search, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(v)
                .setPositiveButton("Search",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText pin = (EditText) v.findViewById(R.id.insert_pin);
                                String input = pin.getText().toString();
                                Log.v(LOG_TAG, input);
                                ((MainActivity) getActivity()).doPositiveClick(input);



                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MainActivity) getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }

}
