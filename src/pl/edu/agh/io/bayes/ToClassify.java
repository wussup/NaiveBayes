package pl.edu.agh.io.bayes;

import java.util.ArrayList;
import java.util.List;

/**
 * klasa obiektu który chcemy zaklasyfikowac
 * 
 * @author Kuba
 * 
 */
public class ToClassify {

	/**
	 * znane parametry obiektu, gdzie parametr to: String [2] param : param[0] -
	 * nazwa parametru param[1] - wartosc parametru
	 */
	private List<String[]> paramValue;

	public ToClassify() {
		paramValue = new ArrayList<String[]>();
	}

	public List<String[]> getParamValue() {
		return paramValue;
	}

	public void setParamValue(List<String[]> paramValue) {
		this.paramValue = paramValue;
	}

	public void putNewParamValue(String param, String value) {
		String[] parameter = new String[2];
		parameter[0] = param;
		parameter[1] = value;
		paramValue.add(parameter);
	}
}
