package pl.edu.agh.io.bayes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * Klasyfikator Naiwna Metoda Bayesa
 * 
 * @author Taras Melon & Jakub Kolodziej
 * 
 */
public class BayesClassificator {

	public static Map<String, HashMap<String, HashMap<String, Double>>> probabilityMap = new HashMap<String, HashMap<String, HashMap<String, Double>>>();
	public static Map<String, Long> classes = new HashMap<String, Long>();

	public static List<ObjectRow> learnedData = new ArrayList<ObjectRow>();
	public static List<ToClassify> testingData = new ArrayList<ToClassify>();
	public static StringBuilder format = new StringBuilder ("#.");
	public static DecimalFormat df;
	
	private static final String SEPARATOR = " +";

	/**
	 * Metoda wczytujaca dane z pliku
	 * 
	 * @param file
	 *            plik
	 * @param classColumn
	 *            nr kolumny w danych do uczenia, w ktorej znajduje sie klasa,
	 *            do ktorej nalezy obiekt
	 * @return lista wczytanych obiektow
	 * @throws IOException
	 *             problem z odczytaniem pliku
	 */
	public static List<ObjectRow> readTeachingDataFromFile(File file,
			int classColumn, String separator) throws IOException {
		List<ObjectRow> objects = new ArrayList<ObjectRow>();
		List<String> rawData = Files.readLines(file, Charsets.UTF_8);
		for (String s : rawData) {
			ObjectRow raw = new ObjectRow();
			int i = 1;
			if (s.startsWith("  ")) {
				s = s.substring(2);
			}
			String afterTrim = s.trim().replaceAll(SEPARATOR, " ");
			afterTrim = afterTrim.replaceAll("\t", "");
			afterTrim = afterTrim.replaceAll("\n", "");
			afterTrim = afterTrim.replaceAll("	", "");
			for (String prop : afterTrim.split(separator)) {
				if (i == classColumn) {
					raw.setClassName(prop);
				} else {
					raw.getParameters().put(String.valueOf(i), df.format(Double.valueOf(prop)));
				}
				i++;
			}
			objects.add(raw);

			if (classes.get(raw.getClassName()) == null) {
				classes.put(raw.getClassName(), 1L);
			} else {
				Long previous = classes.get(raw.getClassName());
				classes.put(raw.getClassName(), previous + 1L);
			}

			if (probabilityMap.get(raw.getClassName()) == null) {
				HashMap<String, HashMap<String, Double>> pMap = new HashMap<String, HashMap<String, Double>>();
				probabilityMap.put(raw.getClassName(), pMap);
			}
			for (String paramName : raw.getParameters().keySet()) {
				if (probabilityMap.get(raw.getClassName()).get(paramName) == null) {
					HashMap<String, Double> paramMap = new HashMap<String, Double>();
					paramMap.put(
							replaceString(raw.getParameters().get(paramName)),
							1.0);
					probabilityMap.get(raw.getClassName()).put(paramName,
							paramMap);
				} else {
					if (probabilityMap.get(raw.getClassName()).get(paramName)
							.get(raw.getParameters().get(paramName)) == null) {
						probabilityMap.get(raw.getClassName()).get(paramName)
								.put(raw.getParameters().get(paramName), 1.0);
					} else {
						Double previous = ((Double) probabilityMap
								.get(raw.getClassName()).get(paramName)
								.get(raw.getParameters().get(paramName)));
						probabilityMap
								.get(raw.getClassName())
								.get(paramName)
								.put(raw.getParameters().get(paramName),
										previous + 1.0);
					}
				}

			}
		}
		return objects;
	}

	private static String replaceString(String s) {
		if (s == null)
			return s;
		try {
			return String
					.valueOf(((double) Math.round(new Double(s) * 10) / 10));
		} catch (NumberFormatException e) {
			// ignore
		}
		return s;
	}

	/**
	 * Oblicza prawdopodobieñstwo wystapienia danego parametru o zadanej
	 * wartosci w danej klasie
	 * 
	 * @param className
	 *            nazwa klasy
	 * @param parameter
	 *            nazwa parametru
	 * @param parameterValue
	 *            wartosc parametru
	 * @param objects
	 *            dane do uczenia
	 * @return prawdopodobienstwo wystapienia danego parametru o zadanej
	 *         wartosci w danej klasie
	 */
	private static double countProbabilityParameterInClass(String className,
			String parameter, String parameterValue, List<ObjectRow> objects) {
		if (probabilityMap.get(className).get(parameter).get(parameterValue) == null) {
			return 0;
		} else {
		return probabilityMap.get(className).get(parameter).get(parameterValue)
				/ classes.get(className) * 1000000;
		}
	}

	/**
	 * Metoda klasyfikujaca nowe obiekty
	 * 
	 * @param tcs
	 *            nowe obiekty
	 * @param objects
	 *            dane do uczenia
	 * @return lista przypisanych klas obiektom
	 */
	public static List<String> classify(List<ToClassify> tcs,
			List<ObjectRow> objects) {
		
		List<String> classNames = new ArrayList<String>();

		for (ObjectRow raw : objects) {
			if (raw.getClassName() != null
					&& !classNames.contains(raw.getClassName())) {
				classNames.add(raw.getClassName());
			}
		}

		List<String> bestClasses = new ArrayList<String>();
		for (ToClassify tc : tcs) {
			String bestClass = null;
			Double percent = 0.0;
			for (String clazz : classNames) {
				Double probability = 1.0;
				Map<String, String> map = tc.getParamValue();
				for (String param : map.keySet()) {
					String value = map.get(param);
					probability *= countProbabilityParameterInClass(clazz,
							param, value, objects);
				}
				if (probability > percent) {
					bestClass = clazz;
					percent = probability;
				}
			}
			bestClasses.add(bestClass);
		}
		return bestClasses;
	}

	/**
	 * Wypisuje wyniki klasyfikacji na konsole
	 * 
	 * @param results
	 *            lista wynikow
	 */
	private static void writeOutputOnConsole(List<String> results) {
		for (String result : results) {
			System.out.println("Obiekt zostal zaklasyfikowany jako: " + result);
		}
	}

	/**
	 * Wczytuje dane do klasyfikacji z pliku
	 * 
	 * @param file
	 *            plik
	 * @param numOfAttributes
	 *            ilosc atrybutow obiektu
	 * @return liste obiektow do klasyfikacji
	 * @throws IOException
	 *             problem z plikiem
	 */
	private static List<ToClassify> readClassifyDataFromFile(File file,
			int numOfAttributes, String separator) throws IOException {
		List<ToClassify> objects = new ArrayList<ToClassify>();
		List<String> rawData = Files.readLines(file, Charsets.UTF_8);
		int y = 1;
		for (String s : rawData) {
			ToClassify raw = new ToClassify();
			int i = 1;
			if (s.startsWith("  ")) {
				s = s.substring(2);
			}
			String afterTrim = s.trim().replaceAll(SEPARATOR, " ");
			afterTrim = afterTrim.replaceAll("\t", "");
			afterTrim = afterTrim.replaceAll("\n", "");
			afterTrim = afterTrim.replaceAll("	", "");
			for (String prop : afterTrim.split(separator)) {
				if (i == (numOfAttributes + 1)) {
					// ignore
				} else {
					raw.putNewParamValue(String.valueOf(i), df.format(Double.valueOf(prop)));
				}
				i++;
			}
			objects.add(raw);
			y++;
		}
		return objects;
	}

	/**
	 * Zapisuje wyniki do pliku
	 * 
	 * @param filename
	 *            nazwa pliku
	 * @param results
	 *            wyniki
	 * @throws IOException
	 *             problem z plikiem
	 */
	private static void writeOutput(String filename, List<String> results)
			throws IOException {
		FileWriter writer = new FileWriter(filename, false);
		for (String str : results) {
			writer.write((str != null ? str : "null") + "\n");
		}
		writer.close();
	}

	/**
	 * Porownuje wyniki z rzeczywistymi danymi
	 * 
	 * @param results
	 *            wyniki
	 * @param readTeachingDataFromFile
	 *            rzeczywiste dane
	 * @return procent poprawnych wynikow
	 */
	private static double compareResults(List<String> results,
			List<ObjectRow> readTeachingDataFromFile) {
		if (results.size() != readTeachingDataFromFile.size())
			throw new RuntimeException("Zle podane argumenty");
		int size = results.size();
		int sum = 0;
		for (int i = 0; i < size; i++) {
			String countedClass = results.get(i);
			String realClass = readTeachingDataFromFile.get(i).getClassName();
			if (countedClass != null && countedClass.equals(realClass))
				sum++;
		}
		return (double) (Math.round(100 * ((double) sum / (double) size) * 100)) / 100;
	}


	public static void main(String[] args) throws IOException {
		int precision = 5;
		for (int i=0; i<precision; i++){
			format.append("#");
		}
		df = new DecimalFormat(format.toString());
		int numOfAttributes = 561;
		String teachingData = "human_train.txt";
		String classifyData = "human_test.txt";
		
		long start = System.currentTimeMillis();
		learnedData = readTeachingDataFromFile(new File(teachingData),
				numOfAttributes + 1, SEPARATOR);
		System.out.println("Data learned in " + (System.currentTimeMillis()-start)/1000F + " seconds");
		testingData = readClassifyDataFromFile(new File(classifyData),
				numOfAttributes, SEPARATOR);
		start = System.currentTimeMillis();
		List<String> results = classify(testingData, learnedData);
		float time = (System.currentTimeMillis() - start) / 1000F;
		System.out.println("Data classified: " + time + " sekund.");
		System.err.println(compareResults(
				results,
				readTeachingDataFromFile(new File(classifyData),
						numOfAttributes + 1, SEPARATOR))
				+ "% wynikow prawidlowo zakwalifikowano\n");
		

	}

}
