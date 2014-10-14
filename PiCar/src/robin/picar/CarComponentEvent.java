package robin.picar;

import java.io.Serializable;

public class CarComponentEvent implements Serializable{

	private static final long serialVersionUID = 1L;
	private CarComponent source;
	public CarComponentEvent() {
	}
	public CarComponentEvent(CarComponent source) {
		super();
		this.source = source;
	}
	public CarComponent getSource() {
		return source;
	}
	public void setSource(CarComponent source) {
		this.source = source;
	}
	@Override
	public String toString() {
		return "CarComponentEvent [source=" + source + "]";
	}
	
}

