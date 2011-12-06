package org.servDroid.settings;

import org.servDroid.web.R;
import org.servDroid.db.ServdroidDbAdapter;
import org.servDroid.db.SettingsAdapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingsInterface extends Activity {

	private Button mButtonCancel;
	private Button mButtonAccept;
	private Button mButtonResetSettings;

	private EditText mInputTextPort;
	private EditText mInputTextMaxClients;
	private EditText mInputTextWwwPath;
	private EditText mInputTextErrorPath;
	private EditText mInputTextLogPath;

	private CheckBox mCheckBoxVibrate;

	private SettingsAdapter mSettings;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		mSettings = new SettingsAdapter(this);

		// Buttons
		mButtonAccept = (Button) findViewById(R.id.settingsAcceptButton);
		mButtonCancel = (Button) findViewById(R.id.settingsCancelButton);
		mButtonResetSettings = (Button) findViewById(R.id.settingsResetSettings);

		// InputText
		mInputTextPort = (EditText) findViewById(R.id.inputPort);
		mInputTextMaxClients = (EditText) findViewById(R.id.inputMaxClients);
		mInputTextWwwPath = (EditText) findViewById(R.id.inputWwwPath);
		mInputTextErrorPath = (EditText) findViewById(R.id.inputErrorPage);
		mInputTextLogPath = (EditText) findViewById(R.id.inputlogPath);

		// Others
		mCheckBoxVibrate = (CheckBox) findViewById(R.id.checkBoxVibrate);
		
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.dialogButtonLayout);
		rl.setBackgroundColor(Color.GRAY);

		fillData();

		mButtonAccept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!mSettings.setPort(mInputTextPort.getText().toString())) {
					mInputTextPort.setText("" + mSettings.getPort());
				}
				if (!mSettings.setMaxClients(mInputTextMaxClients.getText()
						.toString())) {
					mInputTextMaxClients
							.setText("" + mSettings.getMaxClients());
				}
				if (!mSettings.setWwwPath(mInputTextWwwPath.getText()
						.toString())) {
					mInputTextWwwPath.setText(mSettings.getWwwPath());
				}
				if (!mSettings.setErrorPage(mInputTextErrorPath.getText()
						.toString())) {
					mInputTextErrorPath.setText(mSettings.getErrorPage());
				}
				if (!mSettings.setLogPath(mInputTextLogPath.getText()
						.toString())) {
					mInputTextLogPath.setText(mSettings.getLogPath());
				}

				if (mCheckBoxVibrate.isChecked()) {
					mSettings.enableVibrate(true);
				} else {
					mSettings.enableVibrate(false);
				}

				finish();
				Toast.makeText(SettingsInterface.this, R.string.settings_saved,
						Toast.LENGTH_LONG).show();

			}
		});
		mButtonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();

			}
		});

		mButtonResetSettings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mSettings.setDefaultSettings();
				
				finish();

				Toast.makeText(SettingsInterface.this, R.string.settings_saved,
						Toast.LENGTH_LONG).show();
			}
		});

	}

	private void fillData() {
		mInputTextPort.setText("" + mSettings.getPort());
		mInputTextMaxClients.setText("" + mSettings.getMaxClients());
		mInputTextWwwPath.setText("" + mSettings.getWwwPath());
		mInputTextErrorPath.setText("" + mSettings.getErrorPage());
		mInputTextLogPath.setText("" + mSettings.getLogPath());
		if (mSettings.isVibrateEnabled()) {
			mCheckBoxVibrate.setChecked(true);

		}

	}
}