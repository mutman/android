package org.meruvian.midas.core.drawer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.meruvian.midas.core.R;
import org.meruvian.midas.core.drawer.Navigation.NavigationType;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Navigation> navigations = new ArrayList<Navigation>();
	
	public NavigationDrawerAdapter(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void addNavigation(Navigation navigation) {
		navigations.add(navigation);
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return navigations.size();
	}

	public Object getItem(int position) {
		return navigations.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public int getItemViewType(int position) {
		return navigations.get(position).getType().ordinal();
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();

			if (getItemViewType(position) == NavigationType.TITLE.ordinal()) {
                convertView = inflater.inflate(R.layout.bookmark_divider, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.bookmark_item, parent, false);
                holder.icon = (ImageView) convertView.findViewById(R.id.bookmark_item_icon);
                holder.label = (TextView) convertView.findViewById(R.id.bookmark_item_count);
            }

			holder.name = (TextView) convertView.findViewById(R.id.bookmark_item_label);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(navigations.get(position).getName());

        if (getItemViewType(position) == NavigationType.MENU.ordinal()) {
            holder.icon.setImageResource(navigations.get(position).getImage());
            holder.label.setText(navigations.get(position).getLabel());

            if (navigations.get(position).getLabel().equalsIgnoreCase("0")) {
                holder.label.setVisibility(View.GONE);
            } else {
                holder.label.setVisibility(View.VISIBLE);
            }

            Log.e("count", navigations.get(position).getLabel());
        }

		return convertView;
	}

	private class ViewHolder {
		public TextView name;
        public TextView label;
        public ImageView icon;
	}
}
