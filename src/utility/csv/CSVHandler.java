package utility.csv;

import java.io.*;
import java.util.*;

public class CSVHandler {
	private static Object convertType(String s) {
		try {
			Long num = Long.parseLong(s);
			
			if (num.longValue() > Integer.MAX_VALUE) {
				return num;
			} else {
				return Integer.parseInt(s);
			}
		} catch (Exception e) {
			String t = new String(s);
			t = t.replace("\"\"", "\"");
			t = t.substring(1, t.length() - 1);
			return t;
		}
	}
	
	public static Vector<Vector<Object>> readCSV(String file) throws FileNotFoundException {
		return readCSV(file, ",");
	}
	
	public static Vector<Vector<Object>> readCSV(String file, String sep) throws FileNotFoundException {
		Vector<Vector<Object>> data = new Vector<>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			while (true) {
				Vector<Object> token = new Vector<>();
				String line = br.readLine();
				
				if (line == null) {
					break;
				}
				
				boolean inQuote = false;
				int prev = 0;
				
				for (int i = 0; i < line.length(); ++i) {
					if (line.charAt(i) == ',' && !inQuote) {
						token.add(convertType(line.substring(prev, i)));
						prev = i + 1;
					} else if (line.charAt(i) == '"') {
						inQuote = !inQuote;
					}
				}
				
				String cell = line.substring(prev);
				token.add(convertType(cell));
				
				data.add(token);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			System.out.println("IO Error");
		}
		return data;
	}
	
	public static void createFile(String file) {
		try {
			File dir = new File("data/");
			if (!dir.exists()) {
				dir.mkdir();
			}
			
			File f = new File(file);
			if (!f.exists()) {
				f.createNewFile();
			}
		} catch (Exception e) {}
	}
	
	private static void writeLineCSV(String file, Vector<Object> line, String sep, boolean append) {
		try {
			createFile(file);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
			
			String sentence = "";
			
			for(int i = 0; i < line.size(); ++i) {
				if (i > 0) {
					sentence += ",";
				}
				
				if (line.elementAt(i) instanceof Number) {
					sentence += line.elementAt(i);
				} else {
					sentence += "\"" + ((String) line.elementAt(i)).replace("\"", "\"\"") + "\"";
				}
			}
			sentence += "\n";
			
			bw.write(sentence);
			
			bw.close();
		} catch (IOException e) {
			System.out.println("IO Error");
		}
	}
	
	public static void writeLineCSV(String file, Vector<Object> line) {
		writeLineCSV(file, line, ",", false);
	}
	
	public static void writeLineCSV(String file, Vector<Object> line, String sep) {
		writeLineCSV(file, line, sep, false);
	}
	
	public static void writeCSV(String file, Vector<Vector<Object>> data) {
		writeCSV(file, data, ",");
	}
	
	public static void writeCSV(String file, Vector<Vector<Object>> data, String sep) {
		if (data.size() > 0) {
			writeLineCSV(file, data.elementAt(0), sep, false);
		} else {
			writeLineCSV(file, new Vector<Object>(), sep, false);
		}
		
		for (int i = 0; i < data.size(); ++i) {
			writeLineCSV(file, data.elementAt(i), sep, true);
		}
	}
	
	public static void appendLineToCSV(String file, Vector<Object> line) {
		writeLineCSV(file, line, ",", true);
	}
	
	public static void appendLineToCSV(String file, Vector<Object> line, String sep) {
		writeLineCSV(file, line, sep, true);
	}
	
	public static void appendToCSV(String file, Vector<Vector<Object>> data) {
		appendToCSV(file, data, ",");
	}
	
	public static void appendToCSV(String file, Vector<Vector<Object>> data, String sep) {
		for (int i = 0; i < data.size(); ++i) {
			writeLineCSV(file, data.elementAt(i), sep, true);
		}
	}
}