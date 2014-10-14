package robin.picar.old;

import robin.picar.CarComponentEvent;

public class RadarEvent extends CarComponentEvent {
	private static final long serialVersionUID = -2831289325700447289L;
	private double distance;
	public RadarEvent(Radar source, double distance) {
		super(source);
		this.distance = distance;
	}	
	public double getDistance() {
		return distance;
	}
}
