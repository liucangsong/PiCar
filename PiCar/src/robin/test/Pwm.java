package robin.test;
import java.util.Scanner;
import java.util.Timer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.SoftPwm;


public class Pwm {
	long[] s;
	int ms = 1000000;
	long ss;
	
	final GpioController gpio;
	final GpioPinDigitalOutput pin ;
	
	Thread thread;
	
	public Pwm() {
		
		s = new long[]{~(-1L>>>1), //0.5
				~(-1L>>>2), //1
				~(-1L>>>3), //1.5
				~(-1L>>>4), //2
				~(-1L>>>5), //2.5
				};
		
		ss = s[0]; 
		
		gpio = GpioFactory.getInstance();
		pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		 
		pin.setState(PinState.LOW);
		
	}
	
	public void start(){
		thread = new Thread(){
			@Override
			public void run() {
				while(true){
					for(int i=0; i<40; i++){
						long mask = Long.MIN_VALUE>>>i;
						long n = ss & mask; 
						if(n==0){
							//System.out.print("0");
							pin.setState(PinState.LOW);
						}else{
							//System.out.print("1");
							pin.setState(PinState.HIGH);
						}
						
						try {
							Thread.sleep(0, ms/2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		};
		
		thread.start();
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.print("1,2,3,4,5,0");
			int i = in.nextInt();
			if(i==0){
				System.out.println("Bye!");
				gpio.shutdown();
				System.exit(0);
			}
			ss = s[i%s.length];
			System.out.println(Long.toBinaryString(ss));
		}
	}
	
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Pwm pwm = new Pwm();
		pwm.start();
		
	}
	
}






