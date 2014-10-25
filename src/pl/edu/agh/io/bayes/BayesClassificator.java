package pl.edu.agh.io.bayes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class BayesClassificator {
	static int objectsNumber = 0;

	/**
	 * Czyste dane z pliku
	 */
	static List<String> rawData = new ArrayList<String>();

	/**
	 * Kazdy wiersz jako obiekt z parametrami i nazwa klasy
	 */
	static List<ObjectRaw> objects = new ArrayList<ObjectRaw>();

	/**
	 * Metoda wczytujaca dane z pliku
	 * 
	 * @param file
	 *            sciezka do pliku
	 * @param classificator
	 *            nr parametru bedacego klasa
	 * @throws IOException
	 */
	public static void readDataFromFile(File file, int classificator)
			throws IOException {
		rawData = Files.readLines(file, Charsets.UTF_8);
		for (String s : rawData) {
			ObjectRaw raw = new ObjectRaw();
			int i = 1;
			for (String prop : s.split(",")) {
				if (i == classificator) {
					raw.setClassName(prop);
				} else {
					String[] property = new String[2];
					property[0] = String.valueOf(i);
					property[1] = prop;
					raw.getParameters().add(property);
				}
				i++;
			}
			objects.add(raw);
		}

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
	 * @return
	 */
	private static double countProbabilityParameterInClass(String className,
			String parameter, String parameterValue) {
		int inClass = 0;
		int withParameterInClass = 0;
		for (ObjectRaw raw : objects) {
			if (raw.getClassName() != null
					&& raw.getClassName().equals(className)) {
				inClass++;
				for (String[] param : raw.getParameters()) {
					if (param[0].equals(parameter)
							&& param[1].equals(parameterValue)) {
						withParameterInClass++;
					}
				}
			}
		}

		return ((double) withParameterInClass / inClass) * 100;
	}

	/**
	 * Metoda klasyfikujaca nowy obiekt
	 * 
	 * @param tc
	 *            nowy obiekt do zaklasyfikowania
	 * @return nazwa klasy do jakiej zosta³ zaklasyfikowany
	 */
	public static String classify(ToClassify tc) {
		List<String> classNames = new ArrayList<String>();

		String bestClass = null;
		Double percent = 0.0;

		for (ObjectRaw raw : objects) {
			if (raw.getClassName() != null
					&& !classNames.contains(raw.getClassName())) {
				classNames.add(raw.getClassName());
			}
		}
		for (String clazz : classNames) {
			Double probability = 1.0;
			for (String[] param : tc.getParamValue()) {
				probability *= countProbabilityParameterInClass(clazz,
						param[0], param[1]);
			}
			if (probability > percent) {
				bestClass = clazz;
				percent = probability;
			}
		}

		return bestClass;
	}

	public static void main(String[] args) throws IOException {
		// wczytujemy dane z pliku iris.data i okreslamy, ze klasyfikator to
		// piaty parametr
		readDataFromFile(new File("iris.data"), 5);
		// chcemy zaklasyfikowac obiekt, ktorego pierwszy param ma wart 4.8, a
		// drugi 3.0
		ToClassify tc = new ToClassify();
		tc.putNewParamValue("1", "6.3");
		tc.putNewParamValue("2", "2.7");
		tc.putNewParamValue("3", "4.9");
		tc.putNewParamValue("4", "1.9");
		System.out.println("Obiekt zostal zaklasyfikowany jako: "
				+ classify(tc));
	}
}
