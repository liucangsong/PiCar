package robin.picar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config extends Properties{

	private static final long serialVersionUID = 7016296447151067456L;
 
	public Config() {
	}
	/**
	 * ���ļ�ϵͳ�ϼ��������ļ�
	 * @param file
	 */
	public Config(File file) {
		try {
			load(new FileInputStream(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * ��classpath �ϼ��������ļ�
	 * @param classpath
	 */
	public Config(String classpath) {
		try {
			load(ClassLoader.getSystemResourceAsStream(classpath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getInt(String key){
		return Integer.parseInt(getProperty(key).trim());
	}
	
	public float getFloat(String key){
		return Float.parseFloat(getProperty(key).trim());
	}

	public String getString(String key){
		return getProperty(key).trim();
	}
	
	public double getDouble(String key){
		return Double.parseDouble(getProperty(key).trim());
	}
	
}
