package com.project.android.wordcloud;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;


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

        LayoutInflater inflater = getActivity().getLayoutInflater();

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(inflater.inflate(R.layout.dialog_search, null))
                .setPositiveButton("Search",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MainActivity) getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MainActivity)getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }

}
