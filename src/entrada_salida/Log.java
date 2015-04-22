package entrada_salida;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Log {

	public static void deleteLog(String path) {
		File f = null;

		try {
			f = new File(path);
			f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printLog(String path, String text) {
		FileWriter file = null;
		PrintWriter pw = null;

		try {
			file = new FileWriter(path, true);
			pw = new PrintWriter(file);

			pw.println(text);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {

				// Make sure that closes the file
				if (file != null)
					file.close();

			} catch (Exception e2) {

				e2.printStackTrace();

			}
		}
	}
}
