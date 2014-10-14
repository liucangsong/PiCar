package robin.test;

import java.util.Scanner;

import robin.picar.Hardwares;

public class TestPwm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Hardwares hw = new Hardwares();
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.print(" ‰»Î£∫");
			int n = in.nextInt();
			hw.engineLedPin.setPwm(n);
		}

	}

}
