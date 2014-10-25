package pl.edu.agh.io.bayes;

import java.util.ArrayList;
import java.util.List;

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
	 * lista parametrow obiektu, gdzie parametr to String [2] param : param[0] -
	 * nazwa parametru param[1] - wartosc parametru
	 */
	private List<String[]> parameters;

	public ObjectRaw() {
		parameters = new ArrayList<String[]>();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String[]> getParameters() {
		return parameters;
	}

	public void setParameters(List<String[]> parameters) {
		this.parameters = parameters;
	}
}
