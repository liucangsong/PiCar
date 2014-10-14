package robin.picar;

public interface SteeringListener extends CarComponentListener{
	public void steeringChanged(SteeringEvent e);
}
