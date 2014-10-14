package robin.picar.old;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import robin.picar.CarComponent;

import com.pi4j.component.light.impl.GpioLEDComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Steering extends CarComponent{
	
	/** 脉冲信号定时器 */
	private ScheduledExecutorService timer;
	/** 1毫秒等于 1000000纳秒 */
	private int ms = 1000000;
	/** 当前方向 */
	private int direction;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private String[] DIR = {"LEFT", "RIGHT"};
	
	/** 向左方向运动的脉冲长度， 1.5毫秒， 1500000纳秒 */
	private int left =  1500000; //min
	/** 向左运动的脉冲时间长短， 0.5毫秒 500000纳秒 */
	private int right = 500000; //max
	
	/** 当前的正在使用的脉冲时长, 取值来自 left 或者 right */
	private int current = left;
	
	/** 舵机操作脉冲发送数量 */
	private int times = 0;
	
	/** 舵机转向脉冲发送时间间隔 10 毫秒 */
	private int interval = 10;
	
	//private GpioController gpio;
	/** 当前舵机连接的管脚, 需要利用构造器传入 */
	private GpioPinDigitalOutput pin ;
	
	/** 转向控制显示灯 */
	private GpioLEDComponent led;
	
	public Steering(GpioPinDigitalOutput pin) {
		this(pin, null);
	}
	/**
	 * 创建方向盘
	 * @param pin 方向舵机控制连接的 GPIO pin 
	 * @param led 显示转向信号灯 GPIO pin 
	 */
	public  Steering(GpioPinDigitalOutput pin, GpioPinDigitalOutput led) {
		//gpio = GpioFactory.getInstance();
		//pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		this.pin = pin;
		pin.setState(PinState.LOW);
		if(led!=null){
			this.led = new GpioLEDComponent(led);
			this.led.off();
		}
		start();
	}
	
	/** 启动转向舵机脉冲发送任务定时器 */
	private void start(){
		timer = Executors.newScheduledThreadPool(1);
		timer.scheduleAtFixedRate(new PulseTask(), 0, interval, TimeUnit.MILLISECONDS);

	}
	
	public synchronized void turnLeft(){
		if(times>0){
			return;
		}
		current = left;
		times = 7;
		direction = LEFT;
	}

	public synchronized void turnRight(){
		if(times>0){
			return;
		}
		current = right;
		times = 9;
		direction = RIGHT;
	}
	/** 转向脉冲发送任务, 每个时间间隔以后执行一次发送任务 */
	private class PulseTask implements Runnable{
		@Override
		public void run() {
			if(times<=0){
				pin.setState(PinState.LOW);
				return;
			}
			times--;
			if(times == 0 ){
				current = direction==RIGHT ? ms*2 : ms/2;
			}
			pin.setState(PinState.HIGH); 
			if(led!=null){
				led.blink(300);
			}
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

	@Override
	public String toString() {
		return DIR[direction]+",current:"+current;
	}
	
	public static void main(String[] args) {
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
		Steering p = new Steering(pin);
		try{
			ex:
			while(true){
				System.out.println(p);
				System.out.print("enter:[-][=][0]:");
				int c = System.in.read();
				switch(c){
				case '-':
					p.turnLeft();
					break;
				case '=':
					p.turnRight();
					break;
				case '0':
					p.turnRight();
					break ex;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
