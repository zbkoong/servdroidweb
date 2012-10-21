/*
 * Copyright (C) 2010 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class NetworkIp {

	private static Pattern pattern;
	private static Matcher matcher;

	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	/**
	 * Validate IP address with regular expression
	 * 
	 * @param ip
	 *            IP address for validation
	 * @return true valid IP address, false invalid IP address
	 */
	public boolean validateIP(final String ip) {
		if (pattern == null) {
			pattern = Pattern.compile(IPADDRESS_PATTERN);
		}
		matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	/**
	 * Get wifi IP
	 * 
	 * @param wifiManager
	 * @return wifi IP
	 */
	public static String getWifiIp(WifiManager wifiManager) {
		// WifiManager wifiManager = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);

//		int ip = wifiManager.getConnectionInfo().getIpAddress();
//		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
//				+ ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
		
		List<WifiConfiguration> l =  wifiManager.getConfiguredNetworks(); 
		
		
		if(l.size() > 0){
			WifiConfiguration wc = l.get(0); 
			return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
		}
		
		return getLocalIpAddress();
		
		
	}
	
	/**
	 * Get the IP used for the system. It doesn't matter what interface is being used
	 * 
	 * @return The IP of your device
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() /* && (inetAddress instanceof Inet4Address)*/) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			//Log.e(TAG, ex.toString());
		}
		return null;
	}
}