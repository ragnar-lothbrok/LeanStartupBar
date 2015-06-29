package com.test.primaseller.log;

import java.io.FileWriter;
import java.io.PrintWriter;

public class FileLog {

	private static PrintWriter printWriter = null;

	private FileLog() {

	}

	private static void intializeInstance() {
		try {
			if (printWriter == null) {
				synchronized (FileLog.class) {
					if (printWriter == null) {
						printWriter = new PrintWriter(new FileWriter(
								"../OrderProcessed.txt",true));
					}
				}
			}
		} catch (Exception exception) {

		}
	}

	public static void writeToFile(String line) {
		try {
			intializeInstance();
			printWriter.append(line+"\n");
			printWriter.close();
		} catch (Exception exception) {

		}
		printWriter = null;
	}

}
