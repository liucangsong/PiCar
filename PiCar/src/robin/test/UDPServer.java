package robin.test;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class UDPServer extends IoHandlerAdapter {
	
	int port = 3678;
	
	IoAcceptor acceptor;
	
	public void start() throws Exception{
		acceptor = new NioDatagramAcceptor();
		acceptor.setHandler(this);
		acceptor.getFilterChain().addLast(
				"coder", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("utf-8"))));
		acceptor.bind(new InetSocketAddress(port));
	}
	
	public static void main(String[] args) throws Exception {
		UDPServer server = new UDPServer();
		server.start();
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println(message);
		session.write("收到");
		
		session.write("反馈消息!");
		
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
}
