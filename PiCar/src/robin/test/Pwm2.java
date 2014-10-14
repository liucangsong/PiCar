package robin.test;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class Pwm2 {
	
	//ExecutorService pool = Executors.newCachedThreadPool();
	
	ScheduledExecutorService timer = Executors.newScheduledThreadPool(4);
	
	int ms = 1000000;
	
	int location = 0;
	
	long center = ms + ms/2; // 1.5 ∫¡√Î
	long left = ms; //min
	long right = ms + ms; //max

	long current = center;
	
	GpioController gpio;
	GpioPinDigitalOutput pin ;
	
	public Pwm2() {
		gpio = GpioFactory.getInstance();
		pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		pin.setState(PinState.LOW);
	}
	
	class Pulse implements Runnable{
		@Override
		public void run() {
			pin.setState(PinState.HIGH); 
			//long t1 = System.nanoTime();
			//pin.pulse(2, PinState.HIGH);
			//System.out.println(PinState.HIGH);
			try {
				//Thread.sleep(current/ms, (int)(current%ms));
				Thread.sleep(0, 600000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pin.toggle();
			//long t2 = System.nanoTime();
			//System.out.println(t2-t1); 
			//System.out.println(PinState.LOW);
		}
	}
	
	public void start(){
		timer.scheduleAtFixedRate(new Pulse(), 0, 19, TimeUnit.MILLISECONDS);
		
//		for(long i=left; i<right; i+=1000){
//			current = i;
//			System.out.println(current/ms+"," +(current%ms)+","+ current);
//			try {
//				Thread.sleep(20);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		try{
//			while(true){
//				System.out.println("current:"+current);
//				System.out.println("center:"+center);
//				System.out.println("right:"+right);
//				System.out.println("left:"+left);
//				
//				int c = System.in.read();
//				switch(c){
//				case '-':
//					location--;
//					break;
//				case '=':
//					location++;
//					break;
//				}
//				long full = right - center;
//				current = full / 1000 * location + center; 
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) {
		Pwm2 pwm2 = new Pwm2();
		pwm2.start();
		
	}

}
