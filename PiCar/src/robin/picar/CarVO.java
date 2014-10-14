package robin.picar;

import java.io.Serializable;

public class CarVO implements Serializable, Cloneable{

	private static final long serialVersionUID = -3353565829579546098L;
	
	/**
	 * 引擎动力 -30 ~ 100
	 */
	public int enginePower;
	
	/**
	 * 引擎的最大速度，是引擎的最大速度，是总的输出功率，用于限制电机的输出
	 */
	public int maxPower;
	
	/**
	 * 汽车速度 
	 */
	public int speed;
	/** 
	 * 方向 -100 ~ 100 
	 */
	public float direction;
	
	/**
	 * 档位，代表引擎的最高速度 是当前的档位
	 */
	public int gear;
	
	/**
	 * 运动方式： 前进 1 停住0 后退-1
	 */
	public int movement;
	
	public static final int FORWARD = 1;
	public static final int PARK = 0;
	public static final int REVERSE = -1;
	
	/**
	 * 
	 */
	public final static int ON = 1;
	public final static int OFF = 1;
	/**
	 * 前灯状态
	 */
	public int light;
	/**
	 * 引擎开关
	 */
	public int engineSwitch;
	
	public CarVO() {
	}
	
	public CarVO copy() {
		return (CarVO)this.clone();
	}
	
	@Override
	protected Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "enginePower=" + enginePower + "\n maxPower=" + maxPower
				+ "\n speed=" + speed + "\n direction=" + direction + "\n gear="
				+ gear + "\n movement=" + movement + "\n light=" + light
				+ "\n engine=" + engineSwitch ;
	}
	
	
}
