package robin.car2;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoHandlerAdapter;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Car extends IoHandlerAdapter implements Runnable {
	
	private Hardwares hardwares;
	
	//private int forward;
	//private int reverse;
	private int position;
	private boolean lightOn;
	private double speed; //m/s
	private int speedPulseCount;
	private static final int ROUND = 50;
	private static final double CIR = 65*Math.PI;
	
	private static final int LEFT_MAX = 1625; // 1251
	private static final int NEUTRAL = 1448;
	private static final int RIGHT_MAX = 1260; //1645

	private int motorPwm;
	
	private Config config;
	
	private ScheduledExecutorService timer;
	
	public static final int INTERVAL = 1000/20; 
	
	public Car() {
		hardwares = new Hardwares();
		//forward=0;
		motorPwm=0;
		config = new Config("car.properties");
		position = NEUTRAL;
		timer = Executors.newScheduledThreadPool(1);
		//定时执行                         汽车的功能
		timer.scheduleAtFixedRate(this, 0, INTERVAL, TimeUnit.MILLISECONDS);
		hardwares.speedIn.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent e) {
				if(e.getState().isHigh()){
					speedPulseCount++;
				}
			}
		});
	}
	
	public void forward(){
		motorPwm = 5000;
	}
	
	public void reverse(){
		motorPwm = 1000;
	}
	
	public void right(){
		position = RIGHT_MAX;
	}
	
	public void left(){
		position = LEFT_MAX;
	}
	
	public void lightSwitch(){
		this.lightOn = ! lightOn;
	}
	/** 主电机控制 */
	private void handlMoto(){
			hardwares.motor.setPwm(motorPwm);
		
	}
	/** 舵机控制器 */
	private void handlServo(){
		hardwares.servo.setPwm(position);
	}
	/** 前灯检查 */
	private void handlLight(){
		if(lightOn){
			hardwares.leftLight.on();
			hardwares.rightLight.on();
		}else{
			hardwares.leftLight.off();
			hardwares.rightLight.off();
		}
	}
	
	private void handlSpeed(){
		double count = speedPulseCount;
		speedPulseCount = 0;		
		speed = count/ROUND * CIR * (1000.0/INTERVAL);
	}
	
	@Override
	//汽车主更新线程
	public void run() {
		//System.out.println(this);
		handlSpeed();
		handlLight();
		handlServo();
		handlMoto();
	}
	
	

	@Override
	public String toString() {
		return "Car [hardwares=" + hardwares + ", position=" + position
				+ ", lightOn=" + lightOn + ", speed=" + speed
				+ ", speedPulseCount=" + speedPulseCount + ", motorPwm="
				+ motorPwm + ", config=" + config + ", timer=" + timer + "]";
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
				car.lightSwitch();
			}else if(s.equals("p")){
				break;
			}
		}
		in.close();
	}

}
