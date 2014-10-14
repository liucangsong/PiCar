package robin.test;

import java.util.Scanner;

import robin.picar.Hardwares;

public class TestServo {

	public static void main(String[] args) {
		Hardwares hw=new Hardwares();
		System.out.println("¿ªÊ¼");
		Scanner in = new Scanner(System.in);
		float pos = 0;
		while(true){
			//hw.servo.setPosition(pos);
			System.out.print("pos:");
			pos = in.nextFloat();
		}
	}

}
