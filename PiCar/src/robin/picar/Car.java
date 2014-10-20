package robin.picar;

import org.apache.logging.log4j.core.config.ConfigurationException;

import com.pi4j.component.power.PowerListener;
import com.pi4j.component.power.PowerState;
import com.pi4j.component.power.PowerStateChangeEvent;

/** 汽车 */
public class Car implements 
	RadioListener, EngineListener, SteeringListener {
	
	private Config config;

	private Engine engine;
	
	private Steering steering;
	
	private Radio radio;
	
	
	/** 管脚定义 */
	private Hardwares hw;
	
	private CarVO state;
	
	/** 状态key, 启动动力 , 值是一个百分数 */
	public static final String POWER = "POWER";
		
	public Car()  {
		config = new Config("car.properties");
		int dataPort = config.getInt("data.udp.port");
		int discoveryPort = config.getInt("discovery.udp.port");
		//int maxPower = config.getInt("motor.max.power");
		
		Radio.dataPort = dataPort;
		Radio.discoveryPort = discoveryPort;
		
		hw = new Hardwares();
		
		hw.workingLed.on();
		
		state = new CarVO();
		
		steering = new Steering(hw.servo);
		engine = new Engine(hw.forward, hw.reverse);
		radio = new Radio(this);
		Radio.discoveryPort = discoveryPort;
		Radio.dataPort = dataPort;
	}
	
	public void ready(){
		steering.addSteeringListener(this);
		engine.addEngineListener(this);
		radio.addRadioListener(this);
		
//		hw.engineSwitch.addListener(new PowerListener() {
//			@Override
//			public void onStateChange(PowerStateChangeEvent event) {
//				if(event.getNewState() == PowerState.ON){
//					hw.enginePowerLed.on();
//				}else{
//					hw.enginePowerLed.off();
//				}
//			}
//		});
		
		//hw.engineSwitch.on();//打开电源板开关

		state.enginePower=CarVO.ON;
		
		engine.setMaxPower(config.getInt("motor.max.power"));

		steering.start();
		steering.neutral();

	}
	
	 
	/**
	 * 处理从遥控器收到的命令
	 */
	@Override
	public void commandRevcived(RadioEvent e) {
		hw.radioLed.setActive(true);
		CarVO cmd = e.getCarVO();
		if(cmd.direction != state.direction){
			steering.setDirection(cmd.direction);
		}
//		if(cmd.engineSwitch!=state.engineSwitch){
//			state.engineSwitch = cmd.engineSwitch;
//			if(cmd.engineSwitch==CarVO.OFF){
//				hw.engineSwitch.off();
//			}else{
//				hw.engineSwitch.on();
//			}
//		}
		if(cmd.light != state.light){
			state.light = cmd.light;
			if(cmd.light==CarVO.OFF){
				hw.leftLight.off();
				hw.rightLight.off();
			}else{
				hw.leftLight.on();
				hw.rightLight.on();
			}
		}
		if(cmd.maxPower != state.maxPower){
			engine.setMaxPower(cmd.maxPower);
		}
		if(cmd.movement!=state.movement){
			//state.movement = cmd.movement;
			switch(cmd.movement){
			case CarVO.FORWARD:
				engine.forward();
				break;
			case CarVO.REVERSE:
				engine.reverse();
				break;
			case CarVO.PARK:
				engine.stop();
				break;
			}
		}
		if(cmd.gear != state.gear){
			engine.setGear(cmd.gear);
		}
	}
	
	public CarVO getState(){
		//this.state.enginePower = engine.getPower();
		//this.state.movement = engine.getPower()>0 ?
		return this.state.copy();
	}
	
	@Override
	public void gearChanged(EngineEvent e) {
		hw.engineLed.setActive(true);
		this.state.gear=e.getGear();
	}
	@Override
	public void powerChanged(EngineEvent e) {
		hw.engineLed.setActive(true);
		this.state.enginePower = e.getPower();
	}
	@Override
	public void movementChanged(EngineEvent e) { 
		hw.engineLed.setActive(true);
		this.state.movement = e.getMovement();
	}
	@Override
	public void steeringChanged(SteeringEvent e) {
		hw.steeringLed.setActive(true);
		state.direction = e.getDirection();
	}

	@Override
	public void maxPowerChanged(EngineEvent e) {
		hw.engineLed.setActive(true);
		state.maxPower = e.getMaxPower();
	}	
	
	public void go(){
		engine.start();
		radio.start();
	}
	
	public static void main(String[] args) throws ConfigurationException {
		Car car = new Car();
		car.ready();
		car.go();
	}
}
