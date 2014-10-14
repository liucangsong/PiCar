package robin.picar;

import java.io.Serializable;

public class CarVO implements Serializable, Cloneable{

	private static final long serialVersionUID = -3353565829579546098L;
	
	/**
	 * ���涯�� -30 ~ 100
	 */
	public int enginePower;
	
	/**
	 * ���������ٶȣ������������ٶȣ����ܵ�������ʣ��������Ƶ�������
	 */
	public int maxPower;
	
	/**
	 * �����ٶ� 
	 */
	public int speed;
	/** 
	 * ���� -100 ~ 100 
	 */
	public float direction;
	
	/**
	 * ��λ���������������ٶ� �ǵ�ǰ�ĵ�λ
	 */
	public int gear;
	
	/**
	 * �˶���ʽ�� ǰ�� 1 ͣס0 ����-1
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
	 * ǰ��״̬
	 */
	public int light;
	/**
	 * ���濪��
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
