package robin.test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class TestSero {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("Command", RemoteCommand.FORWARD);
		//map.put("value", 100);
		
		//oos.writeObject(map);
		
		//oos.writeObject(new CarState());
		oos.close();
		byte[] ary = out.toByteArray();
		System.out.println(org.apache.commons.codec.binary.Hex.encodeHex(ary));
		System.out.println(ary.length);
	}
}

 
