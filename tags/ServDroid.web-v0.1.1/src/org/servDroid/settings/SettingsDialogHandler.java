package org.servDroid.settings;

import java.net.InetAddress;
import java.util.regex.Pattern;

import org.servDroid.web.R;
import org.servDroid.db.SettingsAdapter;
import org.servDroid.util.NetworkIp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsDialogHandler extends Dialog {

	private Button mOkButton;
	private Button mCancelButton;
	private EditText mInputText;
	private TextView mTitleTextView;
	private LinearLayout mLinearLayoutEnable;
	private CheckBox mCheckBox;
	private TextView mTextEnable;
	private Button mAddHost;
	private Button mRemoveHost;

	private int mTitle_id;

	private SettingsAdapter settings;
	private SettingsInterface_list settingsinterface;

	public SettingsDialogHandler(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Set the title by id (strings.xml)
	 * 
	 * @param title
	 *            Text identifier.
	 */
	public void setTitle(int title) {
		mTitle_id = title;
	}

	/**
	 * Set settings interface (if it is not set, the data update in the screen
	 * is not possible)
	 * 
	 * @param settingsinterface
	 */
	public void setSettingsInterface(SettingsInterface_list settingsinterface) {
		this.settingsinterface = settingsinterface;
	}

	/**
	 * Update the values in the screen if it is possible
	 */
	private void fillData() {
		if (settingsinterface != null) {
			settingsinterface.fillData();
		}

	}

	/**
	 * Create a new SettingsAdapter object.
	 */
	private void createSettingsAdapter() {
		// TODO Falta
	}

	/**
	 * Set the SettingsAdapter object for use it in this dialog
	 * 
	 * @param settings
	 *            SettingsAdapter object
	 */
	public void setSettingsAdapter(SettingsAdapter settings) {
		this.settings = settings;

	}

	// Function for especific settings
	/**
	 * Set the dialog for black list option
	 */
	private void blackList() {
		setContentView(R.layout.settings_dialog_2);

		// Obligatoris
		mInputText = (EditText) findViewById(R.id.dialogInputText);
		mTitleTextView = (TextView) findViewById(R.id.dialogTitle);

		// Altres
		mLinearLayoutEnable = (LinearLayout) findViewById(R.id.dialogEnableLayout);
		mCheckBox = (CheckBox) findViewById(R.id.dialogCheckBox);
		mTextEnable = (TextView) findViewById(R.id.dialogTextenable);
		mAddHost = (Button) findViewById(R.id.dialogAddHost);
		mRemoveHost = (Button) findViewById(R.id.dialogRemoveHost);

		mAddHost.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (NetworkIp.validate(mInputText.getText().toString())) {
					settings.addHostBlackList(mInputText.getText().toString());
				}
				mInputText.setText("");
			}
		});

		mRemoveHost.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (NetworkIp.validate(mInputText.getText().toString())) {
					settings.deleteHostFromBlackList(mInputText.getText()
							.toString());
				}
				mInputText.setText("");
			}
		});
	}

	// ////////////////////////////////////////
	// ////////////////////////////////////////
	// ////////////////////////////////////////
	// ////////////////////////////////////////

	// @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mOkButton = (Button) findViewById(R.id.dialogOkButton);
		mCancelButton = (Button) findViewById(R.id.dialogCancelButton);

		switch (mTitle_id) {
		case R.string.settings_black_list:

			blackList();

			break;
		case R.string.settings_cookies:

			break;
		case R.string.settings_error_page:

			break;
		case R.string.settings_max_clients:

			setContentView(R.layout.settings_dialog_1);

			// Obligatoris
			mOkButton = (Button) findViewById(R.id.dialogOkButton);
			mCancelButton = (Button) findViewById(R.id.dialogCancelButton);
			mInputText = (EditText) findViewById(R.id.dialogInputText);
			mTitleTextView = (TextView) findViewById(R.id.dialogTitle);

			mInputText.setText("" + settings.getMaxClients());
			// mLinearLayoutEnable.setVisibility(android.view.View.INVISIBLE);
			// mCheckBox.setVisibility(android.view.View.INVISIBLE);
			// mTextEnable.setVisibility(android.view.View.INVISIBLE);

			break;
		case R.string.settings_password:

			break;
		case R.string.settings_port:

			setContentView(R.layout.settings_dialog_1);

			// Obligatoris
			mOkButton = (Button) findViewById(R.id.dialogOkButton);
			mCancelButton = (Button) findViewById(R.id.dialogCancelButton);
			mInputText = (EditText) findViewById(R.id.dialogInputText);
			mTitleTextView = (TextView) findViewById(R.id.dialogTitle);

			mInputText.setText("" + settings.getPort());
			// mLinearLayoutEnable.setVisibility(android.view.View.INVISIBLE);
			// mCheckBox.setVisibility(android.view.View.INVISIBLE);
			// mTextEnable.setVisibility(android.view.View.INVISIBLE);

			break;
		case R.string.settings_vibrate:

			break;
		case R.string.settings_white_list:

			break;
		case R.string.settings_www_path:
			mInputText.setText("" + settings.getWwwPath());

			break;

		default:
			break;
		}

		Log.d("ServDroid", "Dialog created");

		mTitleTextView.setText(mTitle_id);

		mOkButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (mTitle_id) {
				case R.string.settings_black_list:

					break;
				case R.string.settings_cookies:

					break;
				case R.string.settings_error_page:

					break;
				case R.string.settings_max_clients:
					settings.setMaxClients(mInputText.getText().toString());
					fillData();
					break;
				case R.string.settings_password:

					break;
				case R.string.settings_port:
					settings.setPort(mInputText.getText().toString());
					fillData();
					break;
				case R.string.settings_vibrate:

					break;
				case R.string.settings_white_list:

					break;
				case R.string.settings_www_path:
					settings.setWwwPath(mInputText.getText().toString());
					fillData();

					break;

				default:
					break;
				}
				// value = nameEditText.getText().toString();
				dismiss();
			}
		});
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cancel();
			}
		});

	}

}
