package robin.test;
import java.util.Timer;
import java.util.TimerTask;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class Test {
	public static void main(String[] args) {
		final GpioController gpio = GpioFactory.getInstance();
		final Timer timer = new Timer();
		final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09);
		 
		pin.setState(PinState.LOW);
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {   
				pin.toggle();
			}
		}, 0, 500);
		
		timer.schedule(new TimerTask() {  
			
			@Override
			public void run() {
				timer.cancel();
			}
		}, 1000*20);
		System.out.println("Bye!");
	}
}
