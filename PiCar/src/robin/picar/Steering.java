package robin.picar;

import java.util.Scanner;

import com.pi4j.io.gpio.GpioPinPwmOutput;

public class Steering extends CarComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8232493442320224941L;

	private GpioPinPwmOutput servo;
	
	private static final float LEFT_MAX = 1260; // 1251
	private static final float NEUTRAL = 1448;
	private static final float RIGHT_MAX = 1625; //1645
	
//    int max = 1645;
//    int min = 1251;
    int position;
    
	public Steering(GpioPinPwmOutput servo) {
		this.servo = servo;
	}
	
	public void start(){
		position = (int)NEUTRAL;
		servo.setPwm(position);
	}

	public void neutral(){
		servo.setPwm((int) NEUTRAL);
	} 
	
	public void addSteeringListener(SteeringListener l){
		super.addCarComponentListener(l); 
	}
	
	public void removeSteeringListener(SteeringListener l){
		super.removeCarComponentListener(l); 
	}
	
	/** 
	 *  设置转向位置: -100%(左) ~ 100%(右) 
	 **/
	public void setDirection(float dir){
		int current;
		if (dir>0) {
			current = (int)((RIGHT_MAX-NEUTRAL)/100*dir + NEUTRAL);
			if (current>RIGHT_MAX) {
				current = (int)RIGHT_MAX;
				dir = 100;
			}
		}else{
			current = (int)( NEUTRAL - ((NEUTRAL-LEFT_MAX)/100*(-dir)));
			if(current<LEFT_MAX){
				current = (int) LEFT_MAX;
				dir = -100;
			}
		}
		System.out.println("current:"+current); 
		if(current!=position){
			servo.setPwm(current);
			position = current;
			fireSteeingChanged(current, dir);
		}
	}
	
	private void fireSteeingChanged(int pwm, float direction){
		SteeringEvent e = new SteeringEvent(this, pwm, direction);
		for (CarComponentListener cl : listeners) {
			if (cl instanceof SteeringListener) {
				SteeringListener l = (SteeringListener) cl;
				l.steeringChanged(e); 
			}
		}
	}
	
	public static void main(String[] args) {
		final Hardwares hw = new Hardwares();
		Steering s  = new Steering(hw.servo);
		s.addSteeringListener(new SteeringListener() {

			@Override
			public void steeringChanged(SteeringEvent e) {
				hw.steeringLedPin.setPwm(4095);
				hw.steeringLedPin.setPwm(1);
				
			}
		 
		});
		s.start();
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.print("方向:");
			float f = in.nextFloat();
			s.setDirection(f);
		}
	}
}




