package robin.test;

public class Demo {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		int ms = 1000000;
		long l = ~(-1L>>>3);
		System.out.println(Long.toBinaryString( l));
		for(int i=0; i<40; i++){
			long mask = Long.MIN_VALUE>>>i;
			System.out.println("HI");
			Thread.sleep(1000, ms-1);
			System.out.println(Long.toBinaryString( l&mask));
		}
	}

}
