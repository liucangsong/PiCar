package robin.picar.old;

import robin.picar.CarComponentListener;

public interface LightListener extends CarComponentListener {
	public void switched(LightEvent e);
}
