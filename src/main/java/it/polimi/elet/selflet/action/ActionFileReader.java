package it.polimi.elet.selflet.action;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Returns a string containing the file parsed at the given filePath
 * 
 * @author Nicola Calcavecchia <calcavecchia@gmail.com>
 * */
public class ActionFileReader {

	private static final Logger LOG = Logger.getLogger(ActionFileReader.class);

	private final String filePath;

	public ActionFileReader(String filePath) {
		this.filePath = nullToEmpty(filePath);
	}

	public String loadCode() {

		if (filePath.isEmpty()) {
			return "";
		}

		// Read the dynamic code stored in the file
		FileInputStream file = null;
		String fileContent = null;
		DataInputStream in = null;
		try {
			file = new FileInputStream(filePath);
			in = new DataInputStream(file);
			byte[] b = new byte[in.available()];
			in.readFully(b);
			in.close();
			fileContent = new String(b, 0, b.length, "Cp850");

		} catch (FileNotFoundException e) {
			LOG.error("IO error while loading the action file " + filePath, e);
			return "";
		} catch (IOException e) {
			LOG.error("IO error while loading the action file " + filePath, e);
			return "";
		} finally {

			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					LOG.error("IO error while loading the action file " + filePath, e);
					return "";

				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.error("IO error while loading the action file " + filePath, e);
					return "";
				}
			}

		}

		return fileContent;
	}

}
