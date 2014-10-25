package pl.edu.agh.io.bayes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * klasa obiektu ktory chcemy zaklasyfikowac
 * 
 * @author Taras Melon & Jakub Kolodziej
 * 
 */
public class ToClassify {

	/**
	 * znane parametry obiektu
	 */
	private Map<String, String> paramValue;

	public ToClassify() {
		paramValue = new LinkedHashMap<String, String>();
	}

	public Map<String, String> getParamValue() {
		return paramValue;
	}

	public void setParamValue(Map<String, String> paramValue) {
		this.paramValue = paramValue;
	}

	public void putNewParamValue(String param, String value) {
		paramValue.put(param, value);
	}
}
