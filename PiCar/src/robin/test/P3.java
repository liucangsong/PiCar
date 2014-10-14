package robin.test;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class P3 {

	ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
	
	int ms = 1000000;
	
	int dir;
	int LEFT = 0;
	int RIGHT = 1;
	
	long left =  1500000; //min
	long right = 500000; //max

	long current = left;
	
	GpioController gpio;
	GpioPinDigitalOutput pin ;
	
	public P3() {
		gpio = GpioFactory.getInstance();
		pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		pin.setState(PinState.LOW);
	}
	
	int times = 0;
	
	public void moveLeft(){
		current = left;
		times = 7;
		dir = LEFT;
	}

	public void moveRight(){
		current = right;
		times = 9;
		dir = RIGHT;
	}
	
	class Pulse implements Runnable{
		@Override
		public void run() {
			if(times<=0){
				pin.setState(PinState.LOW);
				return;
			}
			times--;
			if(times == 0 ){
				current = dir==RIGHT ? ms*2 : ms/2;
			}
			pin.setState(PinState.HIGH); 
			//long t1 = System.nanoTime();
			//pin.pulse(2, PinState.HIGH);
			//System.out.println(PinState.HIGH);
			try {
				Thread.sleep(current/ms, (int)(current%ms));
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
		timer.scheduleAtFixedRate(new Pulse(), 0, 10, TimeUnit.MILLISECONDS);
		
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
		try{
			while(true){
				System.out.println("current:"+current);
				System.out.println("right:"+right);
				System.out.println("left:"+left);
				
				int c = System.in.read();
				switch(c){
				case '-':
					moveLeft();
					break;
				case '=':
					moveRight();
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		P3 p = new P3();
		p.start();
		
	}


}
