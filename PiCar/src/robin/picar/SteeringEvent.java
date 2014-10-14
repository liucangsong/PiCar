package robin.picar;

public class SteeringEvent extends CarComponentEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5669418183502266572L;
	private int pwm;
	private float direction;

	public SteeringEvent(Steering source,  int pwm, float direction) {
		super(source);
		this.pwm = pwm;
		this.direction = direction;
	}
	public int getPwm() {
		return pwm;
	}
	
	public float getDirection() {
		return direction;
	}
	
	@Override
	public String toString() {
		return "SteeringEvent [pwm=" + pwm + ", position=" + direction + "]";
	}
}
