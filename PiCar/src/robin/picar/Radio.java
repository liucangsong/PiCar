package robin.picar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

import robin.picar.controller.HelloCommand;

/** ��Ϣ����װ�ã� ����ң����Ϣ */
public class Radio extends CarComponent implements IoHandler {
	
	private static final long serialVersionUID = -6190902068514826910L;
	/** Mina ����� */
	private IoAcceptor acceptor;
	private IoSession session;
	
	private Car car;
	
	//private GpioLEDComponent led;
	
	//public static final String WHERERU = "Where r u?";
	//public static final String IMHERE = "I'm here!";
	
	private SocketAddress controllerAddress;
	//private SocketAddress controllerHeartbeatAddress;
	private SocketAddress controllerDataAddress;
	public static int discoveryPort ;
	public static int dataPort;
	
	private int state = OFF_LINE;
	private static final int OFF_LINE = 0;
	private static final int WAITING = 2;
	
	//private Configuration config;
	//private Heartbeat heartbeat;
	
	//private List<RadioListener> listeners = new ArrayList<RadioListener>();
	
	private ExecutorService pool = Executors.newCachedThreadPool();
	private ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
 
	class Echo implements Runnable{
		byte[] receiveData = new byte[50];
		DatagramSocket socket;
		byte[] sendData;
		
		public Echo() throws IOException {
			socket = new DatagramSocket(discoveryPort);
			System.out.println("���ڷ��ֵĶ˿�:"+discoveryPort);
			System.out.println("�������ݵĶ˿�:"+dataPort);
			sendData = HelloCommand.I_AM_HERE.toString().getBytes();
		}
		
		@Override
		public void run() {
			while(true){
				try{
					DatagramPacket data = new DatagramPacket(receiveData, receiveData.length);
					//socket = new DatagramSocket(heartbeatPort);
					System.out.println("�ȴ����շ�����Ϣ");
					socket.receive(data);
					String str = new String(receiveData, 0, data.getLength());
					System.out.println("�յ�:"+str);
					if(str.equals(HelloCommand.WHERE_R_U.toString())){
						controllerAddress = data.getSocketAddress();
						System.out.println("�յ� ң������ַ:"+controllerAddress);
						
						
						//SocketAddress address = data.getSocketAddress();
						//controllerHeartbeatAddress = data.getSocketAddress();
						DatagramPacket send = new DatagramPacket(sendData, sendData.length, controllerAddress);
						System.out.println("���ͻظ�:"+HelloCommand.I_AM_HERE.toString());
						socket.send(send);
						//state = CONNECTED;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public Radio() {
	}
	
	public Radio(Car car) {
		this.car = car;
	}
	
//	public Radio(GpioPinDigitalOutput radioLed) {
//		//listeners = new ArrayList<RadioListener>();
//		led = new GpioLEDComponent(radioLed);
//	}
	
//	private void fireCommandReceived(final RemoteCommand command){
////		pool.execute(new Runnable() {
////			public void run() {
////				for (RadioListener l : listeners) {
////					l.commandRevcived(command);
////				}
////			}
////		});
//		RadioEvent e = new RadioEvent(this, command);
//		synchronized (listeners) {
//			for (CarComponentListener l : listeners) {
//				if (l instanceof RadioListener) {
//					RadioListener rl = (RadioListener) l;
//					rl.commandRevcived(e); 
//				}
//			}
//		}
//	}
	
	private void fireCommandReceived(CarVO vo){
		RadioEvent e = new RadioEvent(this, vo);
		synchronized (listeners) {
			for (CarComponentListener l : listeners) {
				if (l instanceof RadioListener) {
					RadioListener rl = (RadioListener) l;
					rl.commandRevcived(e); 
				}
			}
		}
	}
	
	public void addRadioListener(RadioListener l){
		super.addCarComponentListener(l);
	}
	
	public void removeRadioListener(RadioListener l){
		super.removeCarComponentListener(l); 
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println("�յ���Ϣ:"+message); 
		if(message instanceof CarVO){
			CarVO vo = (CarVO) message;
			fireCommandReceived(vo);
			CarVO state;
			if(car == null){
				state = new CarVO();
			}else{
				state = car.getState();
			}
			//System.out.println("������Ϣ:"+state); 
			session.write(state);
			//controllerDataAddress = session.getRemoteAddress();
			this.session = session;
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		System.out.println("����ͨ�Ź���!");
		cause.printStackTrace();
		session.close(true);
		state = OFF_LINE;
	}
	
	private void startMina(){
		if(state == WAITING){
			return;
		}
		try {
			if(acceptor!=null){
				acceptor.dispose();
				session.close(true);
				System.out.println("�رվ�����.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("�������ݽ����߳�"); 
			//����Mina �����
			acceptor = new NioDatagramAcceptor();
			acceptor.getFilterChain().addLast("enum", new ProtocolCodecFilter(
					new ObjectSerializationCodecFactory()));
			acceptor.setHandler(this);
			acceptor.bind(new InetSocketAddress(dataPort));
			state = WAITING;
		} catch (IOException e) {
			e.printStackTrace();
			state = OFF_LINE;
		}
	}
	
	public void start() {

		//heartbeat = new Heartbeat();
		//heartbeat.start();
		try {
			//System.out.println("Radio Start"); 
			//state = READY;
			timer.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					startMina();
				}
			}, 0, 1, TimeUnit.SECONDS);
			pool.execute(new Echo());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		Config config = new Config("car.properties");
		int dataPort = config.getInt("data.udp.port");
		int discoveryPort = config.getInt("discovery.udp.port");
		Radio.dataPort = dataPort;
		Radio.discoveryPort = discoveryPort;
		
		Radio radio = new Radio();
		radio.start();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("�Ѿ�����:"+message); 
	}
}











