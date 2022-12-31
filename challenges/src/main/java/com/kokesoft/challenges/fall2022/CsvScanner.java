package com.kokesoft.challenges.fall2022;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;

public class CsvScanner implements Closeable {
	
	protected InputStream is;
	
	public CsvScanner(InputStream is) {
		this.is = is;
	}
	
	protected String readChunk() throws IOException {
		if (is==null)
			throw new IOException("already closed");
		final StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		while(is.available()>0) {
			int ch = is.read();
			if (ch=='\r')
				continue;
			if (ch==',' || ch=='\n')
				break;
			sb.append((char)ch);
		}
		return sb.toString();
	}
	
	public int nextInt() throws IOException {
		try {
			return NumberFormat.getIntegerInstance().parse(readChunk()).intValue();
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}
	
	public void close() throws IOException {
		if (is!=null) {
			is.close();
			is = null;
		}
	}

}
