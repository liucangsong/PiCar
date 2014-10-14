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
	
	/** �����źŶ�ʱ�� */
	private ScheduledExecutorService timer;
	/** 1������� 1000000���� */
	private int ms = 1000000;
	/** ��ǰ���� */
	private int direction;
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	
	private String[] DIR = {"LEFT", "RIGHT"};
	
	/** �������˶������峤�ȣ� 1.5���룬 1500000���� */
	private int left =  1500000; //min
	/** �����˶�������ʱ�䳤�̣� 0.5���� 500000���� */
	private int right = 500000; //max
	
	/** ��ǰ������ʹ�õ�����ʱ��, ȡֵ���� left ���� right */
	private int current = left;
	
	/** ����������巢������ */
	private int times = 0;
	
	/** ���ת�����巢��ʱ���� 10 ���� */
	private int interval = 10;
	
	//private GpioController gpio;
	/** ��ǰ������ӵĹܽ�, ��Ҫ���ù��������� */
	private GpioPinDigitalOutput pin ;
	
	/** ת�������ʾ�� */
	private GpioLEDComponent led;
	
	public Steering(GpioPinDigitalOutput pin) {
		this(pin, null);
	}
	/**
	 * ����������
	 * @param pin �������������ӵ� GPIO pin 
	 * @param led ��ʾת���źŵ� GPIO pin 
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
	
	/** ����ת�������巢������ʱ�� */
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
	/** ת�����巢������, ÿ��ʱ�����Ժ�ִ��һ�η������� */
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
