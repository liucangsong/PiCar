package robin.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class TCPServer implements IoHandler {
	
	int port = 8899;
	IoAcceptor acceptor;
	public void start() throws IOException{
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("coder", new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("utf-8"))));
		acceptor.setHandler(this);
		acceptor.bind(new InetSocketAddress(port));
	}
	
	public static void main(String[] args) throws IOException {
		TCPServer server = new TCPServer();
		server.start();
		System.out.println("Over");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("sessionCreated");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpened");
		
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("sessionClosed");
		
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		System.out.println("sessionIdle");
		
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		System.out.println("exceptionCaught");
		
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println("messageReceived:"+message);
		session.write("OK");
		
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("messageSent");
		
	}

	
}
