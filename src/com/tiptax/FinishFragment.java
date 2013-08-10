package com.tiptax;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by koy on 09/08/13.
 */
public class FinishFragment extends ListFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.finish, container, false);
        setListAdapter(((MainActivity) getActivity()).getFinishPersonAdapter());

        return view;
    }


}
