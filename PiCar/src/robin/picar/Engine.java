package robin.picar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class Engine extends CarComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -424275754144967846L;
	private GpioPinDigitalOutput forwardPin;
	private GpioPinDigitalOutput reversePin;
	
	/** pwm ���ʱ����, 20���� */
	public static final int INTERVAL = 20;
	public static final int GEAR_SWITCH_INTERVAL = 100;
	

	/** ��λ*/
	private int gear;
	
	//public static final int GEAR_R = -1;
	public static final int GEAR_P = 0;
	public static final int GEAR_1 = 1;
	public static final int GEAR_2 = 2;
	public static final int GEAR_3 = 3;
	public static final int GEAR_4 = 4;
	
	/** ��������İٷֱ�  ��ֵ��: -30~0~100֮��, 0��ʾ�������, -25����, 100 �Ե�ǰ��λ������ٶ�ǰ�� */
	private int currentPower ;
	
	/** �����������İٷֱ�, ���Ե�������, ������������������ 0~maxPower֮�����ֵ*/
	private int maxPower;
	
	public int movement;
	public static final int FORWARD = 1;
	public static final int PARK = 0;
	public static final int REVERSE = -1;
	public static final int AUTO = 2;
 
	private ScheduledExecutorService timer = Executors.newScheduledThreadPool(2);
		
	/** 1������� 1000000���� */
	private int ms = 1000000;
	
	public Engine(GpioPinDigitalOutput forwardPin, GpioPinDigitalOutput reversePin) {
		this.forwardPin = forwardPin;
		this.reversePin = reversePin;
	}
	
	
	public void addEngineListener(EngineListener l){
		super.addCarComponentListener(l);
	}	
	public void removeEngineListener(EngineListener l){
		super.removeCarComponentListener(l);
	}
	
	public int getPower(){
		return this.currentPower;
	}
	
	private void firePowerChanged(){
		synchronized (listeners) {
			EngineEvent e = new EngineEvent(this, movement, gear, currentPower, maxPower);
			for (CarComponentListener l : listeners) {
				if (l instanceof EngineListener) {
					EngineListener el = (EngineListener) l;
					el.powerChanged(e);
				}
			}
		}
	}
	
	private void fireMovementChanged(){
		synchronized (listeners) {
			EngineEvent e = new EngineEvent(this, movement, gear, currentPower, maxPower);
			for (CarComponentListener l : listeners) {
				if (l instanceof EngineListener) {
					EngineListener el = (EngineListener) l;
					el.movementChanged(e);
				}
			}
		}
	}
	
	private void fireGearChanged(){
		synchronized (listeners) {
			EngineEvent e = new EngineEvent(this, movement, gear, currentPower, maxPower);
			for (CarComponentListener l : listeners) {
				if (l instanceof EngineListener) {
					EngineListener el = (EngineListener) l;
					el.gearChanged(e);
				}
			}
		}
	}
	
	private void fireMaxPowerChanged(){
		synchronized (listeners) {
			EngineEvent e = new EngineEvent(this, movement, gear, currentPower, maxPower);
			for (CarComponentListener l : listeners) {
				if (l instanceof EngineListener) {
					EngineListener el = (EngineListener) l;
					el.maxPowerChanged(e);
				}
			}
		}
	}
	
	public void setMaxPower(int maxPower) {
		this.maxPower = maxPower;
		fireMaxPowerChanged();
	}
	
	public int getMaxPower() {
		return maxPower;
	}
	
	public synchronized void forward(){
		int old = movement;
		movement = FORWARD;
		if(old!=movement){
			fireMovementChanged();
		}
	}	
	
	public synchronized void reverse(){
		int old = movement;
		movement = REVERSE;
		if(old!=movement){
			fireMovementChanged();
		}
	}
	
	public synchronized void stop(){
		int old = movement;
		movement = PARK;
		if(old!=movement){
			fireMovementChanged();
		}
	}	
	
	public synchronized void auto(){	
		int old = movement;
		movement = AUTO;
		if(old!=movement){
			fireMovementChanged();
		}
		old = gear;
		gear = GEAR_4;
		if(old!=gear){
			fireGearChanged();
		}
	}
	/** �Զ�ģʽ, �����趨������ʵĴ�С, ������������ ����ܹ��ʵ����� */
	public  void setPower(int power) {
		if(power>100){
			power = 100;
		}
		if(power<-30){
			power = -30;
		}
		this.currentPower = power;
		firePowerChanged();
	}
	
	public void plusGaer(){
		gear++;
		if(gear>GEAR_4){
			gear = GEAR_4 ;
		}
		fireGearChanged();
	}
	public void subtractGaer(){
		gear--;
		if(gear<GEAR_P){
			gear = GEAR_P;
		}
		fireGearChanged();
	}
	
	public int getGaer() {
		return gear;
	}
	public void setGear(int gear) {
		this.gear = gear;
		fireGearChanged();
	}
//	/** ���㵱ǰ������� ��: 0.6 ��ʾ���60% */
//	private float getPowerOutput(){
//		//ǰ��������
//		float gaerCount = 4;
//		//��ǰ��λ������������
//		float fullPower = maxPower /gaerCount * gaer /100f ;
//		//���㵱ǰ��ʵ���������
//		float power = fullPower/100*this.currentPower;
//		return power;
//	}
	/**
	 * ��ǰ�����ʱ��, ��20����Ϊ Ƶ�ʵ�λ
	 * @return ���� 
	 */
	private int getPulseDuration(){
		//ǰ��������
		float gaerCount = 4;
		//��ǰ��λ������������ �ٷֱ�
		float fullPower = maxPower /gaerCount * gear /100f ;
		//���㵱ǰ��ʵ��������� �ٷֱ�
		float realPower = fullPower/100*this.currentPower;
		//����ʵ�ʹ��ʰٷֱ� ���㵱ǰ����ʱ��
		int ns = (int)(INTERVAL * ms * realPower);
		return ns;
	}
	
	public void start(){
		forwardPin.setState(PinState.LOW);
		reversePin.setState(PinState.LOW);
		timer.scheduleAtFixedRate(new Pulse(), 0, INTERVAL, TimeUnit.MILLISECONDS);
		timer.scheduleAtFixedRate(new GaerSwitcher(), 0, GEAR_SWITCH_INTERVAL, TimeUnit.MILLISECONDS);
	}
	/** ƽ�������л���, ÿ�����ӻ��߼���10%�Ĺ������, �����һֱ��סǰ��, ���Զ����䵽ͣ��λ�� */
	private class GaerSwitcher implements Runnable{
		@Override
		public void run() {
			synchronized (Engine.this) {
				int old = currentPower;
				if(movement==AUTO){
					return;
				}
				currentPower = currentPower/10*10;
				switch (movement) {
				case FORWARD:
					if(currentPower<=100){
						currentPower+=10;
					}
					movement = PARK;
					break;
				case PARK:
					if(currentPower>0){
						currentPower-=10;
					}else if(currentPower<0){
						currentPower+=10;
					}
					break;
				case REVERSE:
					if(currentPower>=-30){
						currentPower-=10;
					}
					movement = PARK;
					break;
				}
				if(old!=currentPower){
					firePowerChanged();
				}
			}
		}
	}
	/**  ��綯�����������ź� */
	private class Pulse implements Runnable {
		@Override
		public void run() {
			//forwardPin.setState(PinState.LOW);
			//reversePin.setState(PinState.LOW);
			if(currentPower==0){
				return;
			}
			int duration = getPulseDuration();
			//int duration = ms * 10 + ms/2;
			//����������>0 �Ϳ���ǰ��pin ������ƺ����ź�, ͬʱֻ�ܿ���һ��pin�ź�, ����һ��Ϊ�͵�ƽ
			GpioPinDigitalOutput pin = reversePin;
			if(duration>0){
				pin = forwardPin;
			}
			pin.setState(PinState.HIGH);
			duration = Math.abs( duration );
			//�����뻻��Ϊ����
			int ms = duration/Engine.this.ms;
			int ns = duration%Engine.this.ms;
			try{
				Thread.sleep(ms, ns);
			}catch(Exception e){
				e.printStackTrace();
			}
			pin.setState(PinState.LOW);
		}
	}
	public static void main(String[] args) throws Exception {
		//���Է���, �����������湤�����
		//Configuration config = new PropertiesConfiguration("car.properties");
		System.out.println("��ȡ�������ļ�");
		int maxPower = 30;// config.getInt("motor.max.power");
		//Engine.maxPower = maxPower;
		final Hardwares hw = new Hardwares();
		
		hw.engineSwitch.on();
		
		//pins.enginePower.setState(PinState.HIGH); 
//		//
//		
//		final Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				pins.forward.setState(PinState.HIGH);
//				try {
//					Thread.sleep(5); 
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				pins.forward.setState(PinState.LOW);
//
//			}
//		}, 0, 20);  
//		
//		Thread.sleep(10*1000);
//	
//		pins.enginePower.setState(PinState.LOW); 
		
		Engine engine = new Engine(hw.forward, hw.reverse);
		engine.maxPower = maxPower;
		//engine.auto();
	 
		engine.start();
		System.out.println("��ȡ������");
		//Scanner in = new Scanner(System.in);
//		while(true){
//			System.out.print("power");
//			int power = in.nextInt();
//			engine.setPower(power);
//		}
		
		while(true){
			System.out.print("+ - a s p");
			int c = System.in.read();
			switch (c) {
			case '+':
				engine.plusGaer();
				break;
			case '-':
				engine.subtractGaer();
				break;
			case 'a':
				engine.forward();
				break;
			case 's':
				engine.reverse();
				break;
			case 'p':
				engine.stop();
			}
		}
	}
}
