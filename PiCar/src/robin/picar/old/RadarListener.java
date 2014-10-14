package robin.picar.old;

import robin.picar.CarComponentListener;

public interface RadarListener extends CarComponentListener{
	void distanceChange(RadarEvent e);
}
