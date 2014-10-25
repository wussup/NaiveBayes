package pl.edu.agh.io.bayes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Klasa pojedynczego obiektu ktory jest reprezentacja wiersza pobranego z pliku
 * 
 * @author Kuba
 * 
 */
public class ObjectRaw {
	/**
	 * nazwa klasy do jakiej obiekt nalezy
	 */
	private String className;

	/**
	 * lista parametrow obiektu
	 */
	private Map<String, String> parameters;

	public ObjectRaw() {
		parameters = new LinkedHashMap<String, String>();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

}
