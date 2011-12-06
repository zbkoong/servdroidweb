package org.servDroid.web;

import org.servDroid.Preference.AccessPreferences;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class AdFrame {

	private static AdView adView;
	private static String MY_AD_UNIT_ID = null;

	public static void load(ViewGroup layout, Activity context) {
		if (AccessPreferences.getShowAds() && MY_AD_UNIT_ID != null) {
			if (adView == null) {
				adView = new AdView(context, AdSize.BANNER, MY_AD_UNIT_ID);
				adView.setVisibility(View.VISIBLE);
				layout.setVisibility(View.VISIBLE);
				layout.invalidate();

				AdRequest request = new AdRequest();
				adView.loadAd(request);
				layout.addView(adView);
			}
		} else if (adView != null) {
			adView.setVisibility(View.INVISIBLE);
			layout.setVisibility(View.INVISIBLE);
			layout.invalidate();
		}
	}

}
