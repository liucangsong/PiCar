package robin.picar.old;

import robin.picar.CarComponent;
import robin.picar.CarComponentListener;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * ����״�
 * 
 * @author Robin
 * ��Ϊ ���ܽ���׼ȷʱ��ȡ��, �������ʧ����
 */
public class Radar extends CarComponent  {
	private static final long serialVersionUID = -868171127491485016L;
	private GpioPinDigitalOutput trigger;
	private GpioPinDigitalInput echo;

	public boolean working = true;

	/** �����״����ľ��룬 ����Ϊ��λ */
	private double distance = FARAWAY;

	public static final double FARAWAY = 300;
	public static final double SOND_SPEED = 340.29;

	/** �״����巢���Ժ�ĵȴ���ʱʱ�� */
	public static final int ECHO_TIMEOUT = 1000;
	/** ��������Ŀ�� */
	public static final int PULSE_WIDTH = 10;
	/** �����ʱ�� */
	public static final int INTERVAL = 1000;

	/** �����Ѿ������ı�ǣ� ֻ�������Ѿ������ˣ��õ��ز��ż�����룬 �����ʱ�� ���߼������ */
	private Step step;

	private enum Step {
		IDLE, SENDING, TRIG_SENT, ECHO_REVEIVE
	}

	private Thread pulseThread;

	private long trigTime;
	private long echoTime;

	public Radar(GpioPinDigitalOutput trigger, GpioPinDigitalInput echo) {
		super();
		this.trigger = trigger;
		this.echo = echo;
		step = Step.IDLE;
	}

	public void on() {
		pulseThread = new PulseThread();
		working = true;
		pulseThread.start();
		//echo.addListener(this);
		echo.addListener(new EchoListener());
		step = Step.IDLE;
		
		new Thread(){
			public void run() {
				while(true){
					System.out.println(echo.getState());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	public void off() {
		working = false;
		step = Step.IDLE;
		pulseThread.isInterrupted();
		pulseThread = null;
		//echo.removeListener(this);
	}

	private class PulseThread extends Thread {
		@Override
		public void run() {
			while (working) {
				try {
					// ���ʹ���������
					System.out.println("��ʼ������");
					trigger.setState(PinState.HIGH);
					step = Step.SENDING;
					sleep(PULSE_WIDTH);
					trigger.setState(PinState.LOW);
					step = Step.TRIG_SENT;
					// ��¼����ʱ�� ������
					//trigTime = System.nanoTime();
					System.out.println("�������,�ȴ�����");
					// �ȴ���Ϣ����
					sleep(ECHO_TIMEOUT);
					System.out.println("������ʱ");
					// ��ʱ����
					distance = FARAWAY;
					step = Step.IDLE;
					
				} catch (InterruptedException e) {
					System.out.println("����ȡ���ж�");
					while(interrupted()){
						System.out.println("����ȡ���ж�");
					}
					e.printStackTrace();
					//synchronized (Radar.this) {
						if (step == Step.ECHO_REVEIVE) {
							System.out.println("�������");
							// �����ж��쳣�� ���յ��˷����źţ����о������
							// ��Ҫ�����뻻��Ϊ����м��㣬 ����ֵ��λ�ǡ��ס�
							//trigTime = 
							System.out.println((echoTime - trigTime) / 1000000D / 1000D);
							System.out.println((echoTime - trigTime) );
							
							distance = SOND_SPEED
									* ((echoTime - trigTime) / 1000000D / 1000D) / 2;							
							//distance = SOND_SPEED
							//		* ((echoTime - trigTime) / 1000000D / 1000D) / 2;
							step = Step.IDLE;
							fireDistanceChanged();
							
						}
					//}
				}
				try {
					sleep(INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
					while(interrupted()){
						System.out.println("����ȡ���ж�");
					}
				}
			}
		}
	}
	
	private class EchoListener implements GpioPinListenerDigital {
		//PinState prevState;
		//long startTime;
		//long endTime;
		//public EchoListener() {
			//prevState = echo.getState();
		//}
		public  void handleGpioPinDigitalStateChangeEvent(
				GpioPinDigitalStateChangeEvent e) {
			//System.out.println("echo"+e.getState()); 
			if(step != Step.TRIG_SENT) 
				return;
			if(e.getState().isHigh()){
				trigTime = System.nanoTime();
			}
			if(e.getState().isLow()){
				echoTime = System.nanoTime();
				step = Step.ECHO_REVEIVE;
				pulseThread.interrupt();
			}
			//prevState = echo.getState();
		}
	}

	/** �յ�echo��Ϣ */
//	@Override
//	public synchronized void handleGpioPinDigitalStateChangeEvent(
//			GpioPinDigitalStateChangeEvent e) {
//		//e.getEventType();
//		PinEventType t = e.getEventType();
//		if(t==PinEventType.DIGITAL_STATE_CHANGE)
//		
//		if (step == Step.TRIG_SENT) {
//			System.out.println("�յ�����");
//			echoTime = System.nanoTime();
//			step = Step.ECHO_REVEIVE;
//			if (pulseThread != null) {
//				pulseThread.interrupt();
//			}
//		}
//	}
	/** ��ȡ��ǰ̽����� */
	public double getDistance() {
		return distance;
	}

	public void fireDistanceChanged() {
		RadarEvent e = new RadarEvent(this, distance);
		for (CarComponentListener l : listeners) {
			if (l instanceof RadarListener) {
				RadarListener rl = (RadarListener) l;
				rl.distanceChange(e);
			}
		}
	}
	
	public static void main(String[] args) {
//		Hardwares hardwares = new Hardwares();
//		//Radar radar = new Radar(hardwares.radarTrig, hardwares.radarEcho);
//		radar.addCarComponentListener(new RadarListener() {
//			@Override
//			public void distanceChange(RadarEvent e) {
//				System.out.println("����:"+e.getDistance());
//			}
//		});
//		radar.on();
//		System.out.println("��ʼ�״�!");
	}
}
