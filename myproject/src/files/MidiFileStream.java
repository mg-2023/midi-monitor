package files;

import java.io.InputStream;

public class MidiFileStream {
	InputStream input;
	
	public MidiFileStream(String filename) {
		this.input = this.getClass().getResourceAsStream(filename);
	}
	
	public InputStream getInputStream() {
		return this.input;
	}
}
