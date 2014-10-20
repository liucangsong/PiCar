package robin.test;

import robin.picar.Hardwares;

public class TestLight {

	public static void main(String[] args) throws InterruptedException {
		Hardwares hw = new Hardwares();
		//hw.engineSwitch.on();
		hw.leftLight.on();
		hw.rightLight.on();
		hw.engineLedPin.setPwm(4096);
		hw.radioLedPin.setPwm(4096);
		hw.steeringLedPin.setPwm(4096);
		hw.workingLedPin.setPwm(4095);
		hw.enginePowerLedPin.setPwm(4095);
		
		Thread.sleep(4000);
		
		hw.leftLight.off();
		hw.rightLight.off();
		hw.engineLedPin.setPwm(1);
		hw.radioLedPin.setPwm(1);
		hw.steeringLedPin.setPwm(1);
		hw.workingLedPin.setPwm(1);
		hw.enginePowerLedPin.setPwm(1);
		
		Thread.sleep(4000);
		
		hw.leftLight.on();
		hw.rightLight.on();
		hw.engineLedPin.setPwm(4095);
		hw.radioLedPin.setPwm(4095);
		hw.steeringLedPin.setPwm(4095);
		hw.workingLedPin.setPwm(4095);
		hw.enginePowerLedPin.setPwm(4095);
		
	}
}
