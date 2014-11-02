package pl.edu.agh.io.bayes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
			int classColumn) throws IOException {
		List<ObjectRow> objects = new ArrayList<ObjectRow>();
		List<String> rawData = Files.readLines(file, Charsets.UTF_8);
		for (String s : rawData) {
			ObjectRow raw = new ObjectRow();
			int i = 1;
			String afterTrim = s.trim().replaceAll(" +", " ");
			afterTrim = afterTrim.replaceAll("	", " ");
			for (String prop : afterTrim.split(" ")) {
				if (i == classColumn) {
					raw.setClassName(prop);
				} else {
					raw.getParameters().put(String.valueOf(i), prop);
				}
				i++;
			}
			objects.add(raw);
		}
		System.out.println("Data readed");
		return objects;
	}

	/**
	 * Oblicza prawdopodobie�stwo wystapienia danego parametru o zadanej
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
		int inClass = 0;
		int withParameterInClass = 0;
		for (ObjectRow raw : objects) {
			if (raw.getClassName() != null
					&& raw.getClassName().equals(className)) {
				inClass++;
				Map<String, String> map = raw.getParameters();
				for (String param : map.keySet()) {
					String value = map.get(param);
					if (param.equals(parameter) && value.equals(parameterValue)) {
						withParameterInClass++;
					}
				}
			}
		}

		return ((double) withParameterInClass / inClass) == 1.0 ? 1 :  ((double) withParameterInClass / inClass) * 100;
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
			int numOfAttributes) throws IOException {
		List<ToClassify> objects = new ArrayList<ToClassify>();
		List<String> rawData = Files.readLines(file, Charsets.UTF_8);
		for (String s : rawData) {
			ToClassify raw = new ToClassify();
			int i = 1;
			String afterTrim = s.trim().replaceAll(" +", " ");
			afterTrim = afterTrim.replaceAll("	", " ");
			for (String prop : afterTrim.split(" ")) {
				if (i == (numOfAttributes + 1)) {
					// ignore
				} else {
					raw.putNewParamValue(String.valueOf(i), prop);
				}
				i++;
			}
			objects.add(raw);
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
		int numOfAttributes = 561;
		String teachingData = "human_train.txt";
		String classifyData = "hs.txt";
		long start = System.currentTimeMillis();
		List<String> results = classify(
				readClassifyDataFromFile(new File(classifyData),
						numOfAttributes),
				readTeachingDataFromFile(new File(teachingData),
						numOfAttributes + 1));
		System.out.println("Pomiar czasu: " + (System.currentTimeMillis()-start)/1000F + " sekund.");
		writeOutputOnConsole(results);
		writeOutput("output.txt", results);
		System.err.println(compareResults(
				results,
				readTeachingDataFromFile(new File(classifyData),
						numOfAttributes + 1))
				+ "% wynikow prawidlowo zakwalifikowano");
	}

}
