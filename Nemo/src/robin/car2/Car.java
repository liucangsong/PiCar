package robin.car2;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoHandlerAdapter;

public class Car extends IoHandlerAdapter implements Runnable {
	
	private Hardwares hardwares;
	
	private int forward;
	private int reverse;
	private int position;
	private boolean lightOn;
	private int speed;
	
	private static final int LEFT_MAX = 1625; // 1251
	private static final int NEUTRAL = 1448;
	private static final int RIGHT_MAX = 1260; //1645
	
	private Config config;
	
	private ScheduledExecutorService timer;
	
	public Car() {
		hardwares = new Hardwares();
		forward=0;
		reverse=0;
		config = new Config("car.properties");
		position = NEUTRAL;
		timer = Executors.newScheduledThreadPool(1);
		//定时执行汽车的功能
		timer.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
		
	}
	
	public void forward(){
		forward = 10000;
		reverse = 0;
	}
	
	public void reverse(){
		forward = 0;
		reverse = 10000;
	}
	
	public void right(){
		position = RIGHT_MAX;
	}
	
	public void left(){
		position = LEFT_MAX;
	}
	
	public void lightOn(){
		this.lightOn = true;
	}
	
	public void lightOff(){
		this.lightOn = false;
	}
	
	
	
	@Override
	//汽车主更新线程
	public void run() {
		System.out.println(this);
		if(forward==0){
			hardwares.gpioPCA9685Provider.setAlwaysOff(hardwares.forwardPin);
		}else{
			hardwares.forward.setPwm(forward);
		}
		if(reverse==0){
			hardwares.gpioPCA9685Provider.setAlwaysOff(hardwares.reversePin);
		}else{
			hardwares.reverse.setPwm(reverse);
		}
		hardwares.servo.setPwm(position);
		if(lightOn){
			hardwares.leftLight.on();
			hardwares.rightLight.on();
		}else{
			hardwares.leftLight.off();
			hardwares.rightLight.off();
		}
	}
	
	@Override
	public String toString() {
		return "Car [forward=" + forward + ", reverse=" + reverse
				+ ", position=" + position + ", lightOn=" + lightOn
				+ ", speed=" + speed + "]";
	}

	public static void main(String[] args) {
		Car car = new Car();
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.print("a s d w p:");
			String s = in.nextLine();
			System.out.println(s);
			if(s.equals("w")){
				car.forward();
			}else if(s.equals("s")){
				car.forward();
			}else if(s.equals("a")){
				car.left();
			}else if(s.equals("d")){
				car.right();
			}else if(s.equals("l")){
				car.lightOn = !car.lightOn;
			}else if(s.equals("p")){
				break;
			}
		}
		in.close();
	}

}
