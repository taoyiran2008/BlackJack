package util;

import java.util.Scanner;

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
}
