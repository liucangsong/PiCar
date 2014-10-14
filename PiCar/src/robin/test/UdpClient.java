package robin.test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

public class UdpClient extends IoHandlerAdapter {

	IoConnector connector;
	
	SocketAddress address;
	
	public void start() throws Exception{
		address = new InetSocketAddress("localhost", 3678);
		connector = new NioDatagramConnector();
		connector.setHandler(this);
		connector.getFilterChain().addLast("coder", new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("utf-8"))));
		ConnectFuture future = connector.connect(address);
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		session.write("ÄãºÃ");
	  
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println(message);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
	
	public static void main(String[] args) throws Exception {
		UdpClient client = new UdpClient();
		client.start();
	}
}
