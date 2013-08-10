package com.tiptax;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by koy on 09/08/13.
 */
public class MainFragment extends ListFragment {

    MainListSelectedListener callBack;

    public interface MainListSelectedListener {
        public void onListItemClick(View v, int pos);

        public boolean onContextItemSelected(int id);

        public void onCreateContextMenu(ContextMenu menu, int pos);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);

        registerForContextMenu(view.findViewById(android.R.id.list)); // need to fetch view
        setListAdapter(((MainActivity) getActivity()).getMainPersonAdapter());

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callBack = (MainListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MainListSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        callBack.onListItemClick(v, pos);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = (int) getListAdapter().getItemId(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);

        return callBack.onContextItemSelected(id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        callBack.onCreateContextMenu(menu, info.position);
    }


}
