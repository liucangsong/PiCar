package robin.test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class TcpClient extends IoHandlerAdapter{
	String host = "localhost";
	int port = 8899;
	
	IoConnector connector;
	
	public void start(){
		connector = new NioSocketConnector();
		connector.setHandler(this);
		connector.getFilterChain().addLast("coder", new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("utf-8"))));
		ConnectFuture future  = connector.connect(new InetSocketAddress(host, port));
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		session.write("ฤ๚บร");
		System.out.println("Sent message");
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("sessionCreated");
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println(message);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		System.out.println(cause);
		cause.printStackTrace();
	}
	
	public static void main(String[] args) {
		TcpClient client = new TcpClient();
		client.start();
	}
}
