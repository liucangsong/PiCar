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
	
	private static final int LEFT_MAX = 1260; // 1251
	private static final int NEUTRAL = 1448;
	private static final int RIGHT_MAX = 1625; //1645
	
	private Config config;
	
	private ScheduledExecutorService timer;
	
	public Car() {
		forward=1;
		reverse=1;
		config = new Config("car.properties");
		position = NEUTRAL;
		timer = Executors.newScheduledThreadPool(1);
		//定时执行汽车的功能
		timer.schedule(this, 1000/50, TimeUnit.MILLISECONDS);
		
	}
	
	public void forward(){
		forward = 10000;
		reverse = 1;
	}
	
	public void reverse(){
		forward = 1;
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
		hardwares.forward.setPwm(forward);
		hardwares.reverse.setPwm(reverse);
		hardwares.servo.setPwm(position);
		if(lightOn){
			hardwares.leftLight.on();
			hardwares.rightLight.on();
		}else{
			hardwares.leftLight.off();
			hardwares.rightLight.off();
		}
	}
	
	public static void main(String[] args) {
		Car car = new Car();
		Scanner in = new Scanner(System.in);
		while(true){
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
			}else if(s.equals("p")){
				break;
			}
		}
		in.close();
	}

}
