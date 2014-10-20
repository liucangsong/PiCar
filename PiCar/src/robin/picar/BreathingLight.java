package robin.picar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pi4j.component.light.impl.GpioDimmableLightComponent;
import com.pi4j.io.gpio.GpioPinPwmOutput;

public class BreathingLight extends GpioDimmableLightComponent {
	
	private GpioPinPwmOutput pin;

	private ScheduledExecutorService timer;
	private boolean active;
	
	public BreathingLight(GpioPinPwmOutput pin, int min, int max) {
		super(pin, min, max);
		this.pin = pin;
	}

	public boolean isActive() {
		return active;
	}
	
	@Override
	public void on() {
		pin.setPwm(getMaxLevel());
	}
	
	@Override
	public void off() {
		pin.setPwm(getMinLevel());
	}
	
	public void setActive(boolean active) {
		this.active = active;
		if(timer==null){
			timer = Executors.newScheduledThreadPool(1);
			timer.scheduleAtFixedRate(new Breathing(), 0, 1, TimeUnit.MILLISECONDS);
		}
	}
	public class Breathing implements Runnable {
		int rate = 950;
		@Override
		public void run() {
			try{
				if(isActive()){
					active = false;
					for(int i=1; i<100; i++){
						int l = (int)(getMaxLevel() / 100f * i);
						//System.out.println(l);
						//setLevel(l);
						pin.setPwm(l);
						Thread.sleep(rate/200);
					}
					for(int i=100; i>0; i--){
						int l = (int)(getMaxLevel() / 100f * i);
						//setLevel(l);
						pin.setPwm(l);
						//System.out.println(l);
						Thread.sleep(rate/200);
					}
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Hardwares hw = new Hardwares();
		//hw.engineSwitch.on();
		while(true){
			hw.steeringLed.setActive(true);
			hw.enginePowerLed.setActive(true);
			//hw.rightLight.setLevel(1000);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
