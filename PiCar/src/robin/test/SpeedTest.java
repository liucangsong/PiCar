package robin.test;

import java.io.IOException;

import robin.picar.Hardwares;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class SpeedTest {
	static int c;
	public static void main(String[] args) throws IOException {
		Hardwares hw = new Hardwares();
		GpioPinDigitalInput in = hw.speedIn;
		in.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent arg0) {
				c++;
				System.out.println(c); 
			}
			
		});
		
		System.in.read();
	}
	

}
