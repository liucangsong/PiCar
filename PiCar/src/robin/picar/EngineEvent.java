package robin.picar;

public class EngineEvent extends CarComponentEvent {
	
	private int movement;
	private int gear;
	private int power;
	private int maxPower;

	public EngineEvent(CarComponent source, int movement, int gear, int power, int maxPower) {
		super(source);
		this.movement = movement;
		this.gear = gear;
		this.power = power;
		this.maxPower = maxPower;
	}
	
	public int getMovement() {
		return movement;
	}
 
	public int getGear() {
		return gear;
	}

	public int getPower() {
		return power;
	}
	
	public int getMaxPower() {
		return maxPower;
	}

	@Override
	public String toString() {
		return "EngineEvent [state=" + movement + ", gear=" + gear + ", power="
				+ power + ", maxPower=" + maxPower + "]";
	}

}
