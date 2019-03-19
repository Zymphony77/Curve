package utility.csv;

import java.io.*;
import java.util.*;

public class CSVHandler {
	private static Object convertType(String s) {
		try {
			return Integer.parseInt(s);
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
				
				for (int i = 0; i <= line.length(); ++i) {
					if (i == line.length()) {
						String cell = line.substring(prev, i);
						token.add(convertType(cell));
					} else if (line.charAt(i) == ',' && !inQuote) {
						token.add(convertType(line.substring(prev, i)));
						prev = i + 1;
					} else if (line.charAt(i) == '"') {
						inQuote = !inQuote;
					}
				}
				
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
	
	private static void write​LineCSV(String file, Vector<Object> line, String sep, boolean append) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
			
			String sentence = "";
			
			for(int i = 0; i < line.size(); ++i) {
				if (i > 0) {
					sentence += ",";
				}
				
				System.out.println(">>> " + line.elementAt(i) + " " + line.elementAt(i).getClass());
				
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
		write​LineCSV(file, line, ",", false);
	}
	
	public static void writeLineCSV(String file, Vector<Object> line, String sep) {
		write​LineCSV(file, line, sep, false);
	}
	
	public static void writeCSV(String file, Vector<Vector<Object>> data) {
		writeCSV(file, data, ",");
	}
	
	public static void writeCSV(String file, Vector<Vector<Object>> data, String sep) {
		if (data.size() > 0) {
			write​LineCSV(file, data.elementAt(0), sep, false);
		}
		
		for (int i = 1; i < data.size(); ++i) {
			write​LineCSV(file, data.elementAt(i), sep, true);
		}
	}
	
	public static void appendLineCSV(String file, Vector<Object> line) {
		write​LineCSV(file, line, ",", true);
	}
	
	public static void appendLineCSV(String file, Vector<Object> line, String sep) {
		write​LineCSV(file, line, sep, true);
	}
	
	public static void appendToCSV(String file, Vector<Vector<Object>> data) {
		appendToCSV(file, data, ",");
	}
	
	public static void appendToCSV(String file, Vector<Vector<Object>> data, String sep) {
		for (int i = 0; i < data.size(); ++i) {
			write​LineCSV(file, data.elementAt(i), sep, true);
		}
	}
}