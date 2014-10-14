package robin.picar.old;

import robin.picar.CarComponent;
import robin.picar.CarComponentEvent;

public class LightEvent extends CarComponentEvent {

	private static final long serialVersionUID = -6806816264701632738L;
	private LightState state;
	
	public LightEvent(CarComponent source, LightState state) {
		super(source);
		this.state = state;
	}
	
	public LightState getState() {
		return state;
	}
}
