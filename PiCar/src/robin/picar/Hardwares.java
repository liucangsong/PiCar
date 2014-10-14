package robin.picar;

import java.math.BigDecimal;

import com.pi4j.component.light.impl.GpioDimmableLightComponent;
import com.pi4j.component.power.impl.GpioPowerComponent;
import com.pi4j.component.servo.impl.PCA9685GpioServoProvider;
import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

/**
 * ��ʼ������ȫ����Ӳ��API
 * 
 * @author Robin
 *
 */
public class Hardwares {
	public PCA9685GpioProvider gpioPCA9685Provider;
	public PCA9685GpioServoProvider gpioServoProvider;
	public GpioController gpio;
	// GpioPinDigitalOutput steering;
	// GpioPinDigitalOutput steeringLed;
	// public GpioPinDigitalOutput leftLight;
	// public GpioPinDigitalOutput rightLight;
	public GpioPinDigitalOutput forward;
	public GpioPinDigitalOutput reverse;
	// GpioPinDigitalOutput enginePower;
	// public GpioPinDigitalOutput radioLed;
	public GpioPinDigitalInput speedIn;
	// public GpioPinDigitalInput radarEcho;
	// public GpioPinDigitalOutput radarTrig;

	public GpioPowerComponent engineSwitch;

	public GpioPinPwmOutput servo;

	public GpioPinPwmOutput leftLightPin;
	public BreathingLight leftLight;
	
	public GpioPinPwmOutput rightLightPin;
	public BreathingLight rightLight;
	
	public GpioPinPwmOutput radioLedPin;
	public BreathingLight radioLed;
	
	public GpioPinPwmOutput steeringLedPin;
	public BreathingLight steeringLed;
	
	public GpioPinPwmOutput enginePowerLedPin;
	public BreathingLight enginePowerLed;

	public GpioPinPwmOutput engineLedPin;
	public BreathingLight engineLed;

	public GpioPinPwmOutput workingLedPin;
	public BreathingLight workingLed;

	// public GenericServo servo;

	public Hardwares() {
		gpio = GpioFactory.getInstance();
		gpioPCA9685Provider = createPCA9685Provider();
		// gpioServoProvider = new
		// PCA9685GpioServoProvider(gpioPCA9685Provider);

		engineSwitch = new GpioPowerComponent(gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_23, "�����Դ����"));

		rightLightPin = gpio.provisionPwmOutputPin(gpioPCA9685Provider, PCA9685Pin.PWM_00,
				"�Ҵ��");
		leftLightPin = gpio.provisionPwmOutputPin(gpioPCA9685Provider, PCA9685Pin.PWM_01,
				"����");
		//rightLight = new Light(PCA9685Pin.PWM_00, gpioPCA9685Provider);
		leftLight = new BreathingLight(leftLightPin, 1, 21999);
		rightLight = new BreathingLight(rightLightPin, 1, 21999);
		//leftLight = new Light(PCA9685Pin.PWM_01, gpioPCA9685Provider);
		radioLedPin = gpio.provisionPwmOutputPin(gpioPCA9685Provider,
				PCA9685Pin.PWM_02, "����ͨ��LED");
		radioLed = new BreathingLight(radioLedPin, 1, 10000);
		
		steeringLedPin = gpio.provisionPwmOutputPin(gpioPCA9685Provider,
				PCA9685Pin.PWM_03, "ת��LED");
		steeringLed = new BreathingLight(steeringLedPin, 1, 10000);
		
		enginePowerLedPin = gpio.provisionPwmOutputPin(gpioPCA9685Provider,
				PCA9685Pin.PWM_04, "�����ԴLED");
		enginePowerLed = new BreathingLight(enginePowerLedPin, 1, 10000);
		
		engineLedPin = gpio.provisionPwmOutputPin(gpioPCA9685Provider,
				PCA9685Pin.PWM_05, "���湤��LED");
		engineLed = new BreathingLight(engineLedPin, 1, 10000);
		
		workingLedPin = gpio.provisionPwmOutputPin(gpioPCA9685Provider,
				PCA9685Pin.PWM_06, "�������LED");
		workingLed = new BreathingLight(workingLedPin, 1, 10000);
		
		servo = gpio.provisionPwmOutputPin(gpioPCA9685Provider,
				PCA9685Pin.PWM_07, "ת����");

		// servo = new
		// GenericServo(gpioServoProvider.getServoDriver(PCA9685Pin.PWM_08),
		// "ת����");
		// String left = "49";//pwm = 1251
		// String right = "49";//pwm = 1645
		// servo.setProperty(Servo.PROP_END_POINT_LEFT, left);
		// servo.setProperty(Servo.PROP_END_POINT_RIGHT, right);
		// servo.setProperty(Servo.PROP_SUBTRIM, "-42");
		//
		// steering = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26, "ת��");
		// steeringLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22,
		// "ת���");
		// leftLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, "�����");
		// rightLight = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_26,
		// "�Ҳ���");
		forward = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "ǰ��");
		reverse = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "����");
		// enginePower = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23,
		// "�����Դ����");

		// radioLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27,
		// "����ͨ��LED");

		speedIn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, "�ٶȼ�⴫����");
		// radarEcho = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, "�״��ź�����",
		// PinPullResistance.PULL_UP);
		// radarTrig = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28,
		// "�״��ź����");

		// steering = gpio.provisionPwmOutputPin(gpioPCA9685Provider,
		// PCA9685Pin.PWM_07, "ת����");

	}

	private PCA9685GpioProvider createPCA9685Provider() {
		try {
			BigDecimal frequency = PCA9685GpioProvider.ANALOG_SERVO_FREQUENCY;
			BigDecimal frequencyCorrectionFactor = new BigDecimal("1.0578");
//			����50HZ��ʼ�� ���������.
			I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			return new PCA9685GpioProvider(bus, 0x6f, frequency,
					frequencyCorrectionFactor);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("���Ӷ��������ʧ��!", e);
		}

	}
}
