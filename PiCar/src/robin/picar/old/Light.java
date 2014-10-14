package robin.picar.old;

import java.io.Serializable;

import robin.picar.CarComponent;
import robin.picar.CarComponentListener;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.Pin;

public class Light extends CarComponent implements Serializable{
	private static final long serialVersionUID = 2422166860298109600L;
	
	private Pin pin;
	private PCA9685GpioProvider provider;
	
	public Light(Pin pin, PCA9685GpioProvider provider) {
		this.provider = provider;
		this.pin = pin;
	}
	
	public void addLightListener(LightListener l){
		addCarComponentListener(l);
	}
	
	public void removeLightListener(LightListener l){
		super.removeCarComponentListener(l);
	}
	
	private void fireSwitched(LightState state){
		synchronized (listeners) {
			LightEvent e = new LightEvent(this, state);
			for (CarComponentListener l : listeners) {
				if (l instanceof LightListener) {
					LightListener ll = (LightListener) l;
					ll.switched(e);
				}
			}
		}
	}
	
	public void on(){
		provider.setAlwaysOn(pin);
		fireSwitched(LightState.ON);
	}
	
	public void off(){
		provider.setAlwaysOff(pin);
		fireSwitched(LightState.OFF);
	}
	
}
