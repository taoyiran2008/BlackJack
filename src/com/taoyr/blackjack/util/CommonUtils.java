package com.taoyr.blackjack.util;

import java.util.Scanner;

import android.content.Context;
import android.widget.Toast;

public class CommonUtils {
	public static int readCommandFromConsole() {
		int ret = -1;
		final Scanner s = new Scanner(System.in);
		String cmd = s.nextLine();
		try {
			ret = Integer.parseInt(cmd);
		} catch (NumberFormatException e) {
		}
/*		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				s.close();
			}
		});*/
		return ret;
	}
	
	public static void showToast(Context context, String msg) {
	    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public static void sleepInSec(int sec) {
	    try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
