package robin.picar;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class CarComponent implements Serializable{

	private static final long serialVersionUID = -7169638116421812253L;
	
	protected List<CarComponentListener> listeners = new Vector<CarComponentListener>();

	public void addCarComponentListener( CarComponentListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
	}
	
	public void removeCarComponentListener(CarComponentListener l) {
		synchronized (listeners) {
			listeners.remove(l);
		}
	}
	
}
