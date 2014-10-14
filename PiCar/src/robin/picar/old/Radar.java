package robin.picar.old;

import robin.picar.CarComponent;
import robin.picar.CarComponentListener;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * 测距雷达
 * 
 * @author Robin
 * 因为 不能进行准确时间取得, 这个功能失败了
 */
public class Radar extends CarComponent  {
	private static final long serialVersionUID = -868171127491485016L;
	private GpioPinDigitalOutput trigger;
	private GpioPinDigitalInput echo;

	public boolean working = true;

	/** 经过雷达测算的距离， 以米为单位 */
	private double distance = FARAWAY;

	public static final double FARAWAY = 300;
	public static final double SOND_SPEED = 340.29;

	/** 雷达脉冲发送以后的等待超时时间 */
	public static final int ECHO_TIMEOUT = 1000;
	/** 发送脉冲的宽度 */
	public static final int PULSE_WIDTH = 10;
	/** 测距间隔时间 */
	public static final int INTERVAL = 1000;

	/** 脉冲已经发生的标记， 只有脉冲已经发送了，得到回波才计算距离， 如果超时， 或者计算完毕 */
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
					// 发送触发，脉冲
					System.out.println("开始发脉冲");
					trigger.setState(PinState.HIGH);
					step = Step.SENDING;
					sleep(PULSE_WIDTH);
					trigger.setState(PinState.LOW);
					step = Step.TRIG_SENT;
					// 记录发送时刻 纳秒数
					//trigTime = System.nanoTime();
					System.out.println("发送完毕,等待回馈");
					// 等待消息回馈
					sleep(ECHO_TIMEOUT);
					System.out.println("回馈超时");
					// 超时处理
					distance = FARAWAY;
					step = Step.IDLE;
					
				} catch (InterruptedException e) {
					System.out.println("正在取消中断");
					while(interrupted()){
						System.out.println("正在取消中断");
					}
					e.printStackTrace();
					//synchronized (Radar.this) {
						if (step == Step.ECHO_REVEIVE) {
							System.out.println("计算距离");
							// 发生中断异常， 就收到了反馈信号，进行距离计算
							// 需要将毫秒换算为秒进行计算， 返回值单位是“米”
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
						System.out.println("正在取消中断");
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

	/** 收到echo消息 */
//	@Override
//	public synchronized void handleGpioPinDigitalStateChangeEvent(
//			GpioPinDigitalStateChangeEvent e) {
//		//e.getEventType();
//		PinEventType t = e.getEventType();
//		if(t==PinEventType.DIGITAL_STATE_CHANGE)
//		
//		if (step == Step.TRIG_SENT) {
//			System.out.println("收到回馈");
//			echoTime = System.nanoTime();
//			step = Step.ECHO_REVEIVE;
//			if (pulseThread != null) {
//				pulseThread.interrupt();
//			}
//		}
//	}
	/** 获取当前探测距离 */
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
//				System.out.println("距离:"+e.getDistance());
//			}
//		});
//		radar.on();
//		System.out.println("开始雷达!");
	}
}
