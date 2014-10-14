package robin.picar.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

import robin.picar.CarVO;
import robin.picar.Config;


/**
 * ң���������߼��� ����minaΪͨ�Ű���ʵ��ͨ��
 * 
 * ��ʱ���ͷ�ʽ��
 * ÿ���һ��ʱ�䣬 ��ң���������ݷ��͵���������
 */
public class RemoteController extends Observable implements IoHandler {
	
	private static final int INTERVAL = 1000/50;
	
	private int state = OFF_LINE;
	private static final int OFF_LINE = 0;
	private static final int SEEKING = 1;
	private static final int CAR_FOUNED = 2;
	private static final int CONNECTING = 3;
	private static final int CONNECTED = 4;
	
	private String[] STATE = {"OFF_LINE", "SEEKING", "CAR_FOUND", "CONNECTING", "CONNECTED"};
	
	private IoConnector connector;
	private IoSession session;
	private int discoveryPort;
	private int dataPort;
	private SocketAddress carHeartbeatAddress;	
	private SocketAddress carDataAddress;	
	
	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
	
	private CarVO carState = new CarVO();
	private CarVO stateForSend = new CarVO();
	
	public RemoteController() throws ConfigurationException {
		//heartbeat = new Heartbeat();
		Config config = new Config("car.properties");
		this.dataPort = config.getInt("data.udp.port");
		this.discoveryPort = config.getInt("discovery.udp.port");
	}
	
	public CarVO getStateForSend() {
		return stateForSend;
	}
	
	public String getState(){
		return STATE[state];
	}
	
	public void start(){
		pool.scheduleAtFixedRate(new CommandSender(), 0, INTERVAL, TimeUnit.MILLISECONDS);
		pool.scheduleAtFixedRate(new NetworkConnector(), 0, 200, TimeUnit.MILLISECONDS);
	}
	public class NetworkConnector implements Runnable {
		@Override
		public void run() {
			
			switch (state) {
			case OFF_LINE:
				discovery();
				break;
			case CAR_FOUNED:
				startMina();
				break;
			}			
		}
	}
	public class CommandSender implements Runnable {
		
		@Override
		public void run() {
			//System.out.println(getState());
			if(state==CONNECTED){
				//send(stateForSend);
				session.write(stateForSend);
			}
		}
	}
	private void startMina(){
		try {
			state = CONNECTING;
			System.out.println("�������ݽ����߳�"); 
			if(connector!=null){
				connector.dispose();
			}
			//����Mina �����
			connector = new NioDatagramConnector();
			connector.getFilterChain().addLast("enum", new ProtocolCodecFilter(
					new ObjectSerializationCodecFactory()));
			connector.setHandler(this);
			ConnectFuture future = connector.connect(carDataAddress);
			future.awaitUninterruptibly();
			session = future.getSession();
			state = CONNECTED;
		} catch (Exception e) {
			e.printStackTrace();
			state = CAR_FOUNED;
		}
	}
	
	/** ����Car�ķ���� ����     */
	public String discovery() {
		//heartbeat.discovery();
		DatagramSocket socket = null;
		try{
			state = SEEKING;
			super.notifyObservers("��ʼ����Car...");
			byte[] sendData = HelloCommand.WHERE_R_U.toString().getBytes();
			socket = new DatagramSocket();
			socket.setSoTimeout(3000);
			SocketAddress addr = new InetSocketAddress("255.255.255.255", discoveryPort);
			DatagramPacket data = new DatagramPacket(sendData, sendData.length, addr);
			//System.out.println("��ʼ��ʼ����Car");
			super.notifyObservers("����Ŀ��:"+addr);
			super.notifyObservers("����:"+HelloCommand.WHERE_R_U);
			socket.send(data);
			byte[] receiveData = new byte[50];
			DatagramPacket rcv = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(rcv);
			String msg = new String(receiveData, 0, rcv.getLength());
			super.notifyObservers("�յ���Ϣ:"+msg);
			if (msg.equals(HelloCommand.I_AM_HERE.toString())) {
				carHeartbeatAddress = rcv.getSocketAddress();
				
				System.out.println("�յ�:" + msg + " Car��ַ:"
						+ carHeartbeatAddress);
				carDataAddress = new InetSocketAddress(data.getAddress(),
						dataPort);
				super.notifyObservers("�յ�Car��ַ:"+carDataAddress);
				//startMina();
			}
			
			state = CAR_FOUNED;
			return "���ӳɹ�, ��������:"+carHeartbeatAddress;
		}catch(Exception e){
			e.printStackTrace();
			super.notifyObservers("����ʧ��!"+ e.getMessage());
			state = OFF_LINE;
			return "����ʧ��:" + e.getMessage();
		}finally{
			if(socket!=null) socket.close();
		}
	}
//	
//	public void send(CarVO command) {
//		if (state == CONNECTED) {
//			session.write(command);
//		}
//	}

	public CarVO getCarState() {
		return carState;
	}
	
	public static void main(String[] args) {
		
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		//HelloCommand cmd = (HelloCommand)message;
		System.out.println("�յ�"+message);
		//fireCommand(cmd);
		if (message instanceof CarVO) {
			carState = (CarVO) message;
			super.notifyObservers(state);
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
	

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("������mina�Ự");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("��ʼ��Mina");
		
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Mina ����");
		
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("�Ѿ�������Ϣ"+message);
	}
}
