package org.meruvian.midas.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewFragment extends ListFragment {

	private int selectedPosition = -1;
	String[] s = new String[] { "Lorem", "Ipsum", "dolor", "sit", "amet" };
	private OnListSelectedListener listSelectedListener;

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (position == selectedPosition) {
			return;
		}

		listSelectedListener.onListSelected("http://www.google.com/search?q="
				+ s[position]);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, s));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listSelectedListener = (OnListSelectedListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	public interface OnListSelectedListener {
		public void onListSelected(String tutUrl);
	}
}
