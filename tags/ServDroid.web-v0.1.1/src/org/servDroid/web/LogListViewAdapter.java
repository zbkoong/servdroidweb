package org.servDroid.web;

import java.util.ArrayList;
import java.util.Date;

import org.servDroid.web.R;
import org.servDroid.db.LogLocal;
import org.servDroid.settings.Local;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LogListViewAdapter extends ArrayAdapter<LogLocal> {

	private LayoutInflater mLayoutInflater;
	private ArrayList<LogLocal> mItems;

	public LogListViewAdapter(Context context, int textViewResourceId,
			LayoutInflater layoutInflater) {
		super(context, textViewResourceId);
		this.mLayoutInflater = layoutInflater;

		// TODO Auto-generated constructor stub
	}

	public void setItems(ArrayList<LogLocal> items) {
		this.mItems = items;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = mLayoutInflater.inflate(R.layout.row_log, null);
		}
		LogLocal o = mItems.get(position);
		if (o != null) {

			// poblamos la lista de elementos

			TextView tt = (TextView) v.findViewById(R.id.textLog);
			// TextView tt2 = (TextView) v.findViewById(R.id.settings_summary);
			// ImageView im = (ImageView) v.findViewById(R.id.icon);

			// if (im != null) {
			// im.setImageResource(o.getLocalImage());
			// }
			if (tt != null) {
				String line = "";

				String begining = o.getLocalInfoBegining();

				if (!begining.equals("") || begining == null) {
					line = line + "[" + begining + "] ";
				}

				line = line + o.getLocalIp() + " ";

				Date timeStamp = new Date(o.getLocalTimeStamp());

				line = line + "[" + timeStamp.toGMTString() + "] ";

				line = line + "\"" + o.getLocalPath() + "\"";

				String end = o.getLocalInfoEnd();

				if (!end.equals("")) {
					line = line + " -- " + end + "";
				}

				tt.setText(line);
			}
			// if (tt2 != null) {
			// tt2.setText(o.getLocalSummary());
			// }
		}
		return v;
	}

}
