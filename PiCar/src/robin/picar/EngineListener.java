package robin.picar;

public interface EngineListener extends CarComponentListener{
	public void gearChanged(EngineEvent e);
	public void movementChanged(EngineEvent e);
	public void powerChanged(EngineEvent e);
	public void maxPowerChanged(EngineEvent e);
}
