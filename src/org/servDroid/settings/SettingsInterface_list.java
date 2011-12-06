package org.servDroid.settings;

import java.util.ArrayList;

import org.servDroid.web.R;
import org.servDroid.db.ServdroidDbAdapter;
import org.servDroid.db.SettingsAdapter;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsInterface_list extends ListActivity {
	private ArrayList<Local> locals = null;
	private IconListViewAdapter mAdapter;

	private ServdroidDbAdapter mDbHelper;
	private SettingsAdapter settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_list);

		mDbHelper = new ServdroidDbAdapter(this);
		mDbHelper.open();

		settings = new SettingsAdapter(this);

		locals = new ArrayList<Local>();
		this.mAdapter = new IconListViewAdapter(this, R.layout.row_settings,
				locals);
		setListAdapter(this.mAdapter);

		fillData();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Local local = (Local) l.getItemAtPosition(position);

		//Toast.makeText(this, local.getLocalName(), Toast.LENGTH_LONG).show();

		SettingsDialogHandler dialog = new SettingsDialogHandler(this);
		dialog.setTitle(local.getLocalName());
		dialog.setSettingsAdapter(settings);
		dialog.setSettingsInterface(this);
		dialog.show();

		// Log.d("ServDroid",dialog.getString());
	}

	/*
	 * Inicializacion del mapa
	 */

	public void fillData() {

		locals = new ArrayList<Local>();

		// port
		Local o1 = new Local();
		o1.setLocalName(R.string.settings_port);
		o1.setLocalSummary("" + settings.getPort());

		// max clients
		Local o2 = new Local();
		o2.setLocalName(R.string.settings_max_clients);
		o2.setLocalSummary("" + settings.getMaxClients());

		// enabled password
		Local o3 = new Local();
		o3.setLocalName(R.string.settings_password);
		if (settings.isPasswordEnabled()) {
			o3.setLocalSummary(this.getResources().getString(R.string.enabled));
		} else {
			o3
					.setLocalSummary(this.getResources().getString(
							R.string.disabled));
		}

		// enabled white list
		Local o4 = new Local();
		o4.setLocalName(R.string.settings_white_list);
		if (settings.isWhiteListEnabled()) {
			o4.setLocalSummary(this.getResources().getString(R.string.enabled));
		} else {
			o4
					.setLocalSummary(this.getResources().getString(
							R.string.disabled));
		}

		// Black list
		Local o5 = new Local();
		o5.setLocalName(R.string.settings_black_list);
		if (settings.isBlackListEnabled()) {
			o5.setLocalSummary(this.getResources().getString(R.string.enabled));
		} else {
			o5
					.setLocalSummary(this.getResources().getString(
							R.string.disabled));
		}

		// www path
		Local o6 = new Local();
		o6.setLocalName(R.string.settings_www_path);
		o6.setLocalSummary(settings.getWwwPath());

		// Vibration
		Local o7 = new Local();
		o7.setLocalName(R.string.settings_vibrate);
		if (settings.isVibrateEnabled()) {
			o7.setLocalSummary(this.getResources().getString(R.string.enabled));
		} else {
			o7
					.setLocalSummary(this.getResources().getString(
							R.string.disabled));
		}

		locals.add(o1);
		locals.add(o2);
		locals.add(o3);
		locals.add(o4);
		locals.add(o5);
		locals.add(o6);
		locals.add(o7);

		mAdapter.clear();
		if (locals != null && locals.size() > 0) {
			for (int i = 0; i < locals.size(); i++)
				mAdapter.add(locals.get(i));
		}

		// setListAdapter(adapter);
		mAdapter.notifyDataSetChanged();

	}

	public class IconListViewAdapter extends ArrayAdapter<Local> {

		private ArrayList<Local> items;

		public IconListViewAdapter(Context context, int textViewResourceId,
				ArrayList<Local> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_settings, null);
			}
			Local o = items.get(position);
			if (o != null) {

				// poblamos la lista de elementos

				TextView tt = (TextView) v.findViewById(R.id.settings_title);
				TextView tt2 = (TextView) v.findViewById(R.id.settings_summary);
				// ImageView im = (ImageView) v.findViewById(R.id.icon);

				// if (im != null) {
				// im.setImageResource(o.getLocalImage());
				// }
				if (tt != null) {
					tt.setText(o.getLocalName());
				}
				if (tt2 != null) {
					tt2.setText(o.getLocalSummary());
				}
			}
			return v;
		}
	}

}