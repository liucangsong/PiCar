package robin.picar.controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import robin.picar.CarVO;
import robin.picar.Engine;


public class ControllerFrame extends JFrame implements Observer, ChangeListener {
	
	RemoteController controller;
	
	JPanel root;
	JPanel videoPane;
	JPanel controlPane;
	JPanel carStatePane;
	
	//发动机最大输出
	JSlider maxPowerSlider;
	JSlider maxPowerBar;
	
	//引擎动力
	JSlider enginePowerBar;
	
	//当前方向
	JSlider directionSlider;
	JSlider directionBar;
	
	//档位
	JSlider gearSlider;
	JSlider gearBar;
	
	//移动方向
	JSlider movementSlider;
	JSlider movementBar;
	
	public ControllerFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("遥控器");
		setAlwaysOnTop(true);
		setSize(800, 400);
		setLocationRelativeTo(null);
		init();
	}
	
	public void setController(RemoteController controller) {
		this.controller = controller;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void init() {
		root = new JPanel(new BorderLayout());
		videoPane = new JPanel(new BorderLayout());
		videoPane.setBorder(new TitledBorder("视频")); 
		controlPane = new JPanel(new GridLayout(5, 1, 6, 6));
		carStatePane = new JPanel(new GridLayout(6, 1, 6, 6));
		
		//controlPane.setBorder( new TitledBorder("控制"));
		
		root.add(videoPane, BorderLayout.CENTER);
		root.add(controlPane, BorderLayout.WEST);
		root.add(carStatePane, BorderLayout.EAST);
				
		//发动机最大输出
		maxPowerSlider = new JSlider(30, 100, 50);
		maxPowerSlider.setBorder(new TitledBorder( "最大输出"));
		maxPowerSlider.setLabelTable(maxPowerSlider.createStandardLabels(10));
		maxPowerSlider.setPaintLabels(true);
		controlPane.add(maxPowerSlider);
		maxPowerSlider.addChangeListener(this);
	
		maxPowerBar = new JSlider(30, 100);
		maxPowerBar.setBorder(new TitledBorder("汽车最大输出"));
		maxPowerBar.setLabelTable(maxPowerBar.createStandardLabels(10));
		maxPowerBar.setPaintLabels(true);
		maxPowerBar.setValue(50);
		carStatePane.add(maxPowerBar);
		
		
		//引擎动力
		enginePowerBar = new JSlider(-30, 100);
		enginePowerBar.setLabelTable(enginePowerBar.createStandardLabels(20, -20));
		enginePowerBar.setPaintLabels(true);
		enginePowerBar.setBorder(new TitledBorder("汽车当前输出"));
		enginePowerBar.setValue(0);
		carStatePane.add(enginePowerBar);
		
		//当前方向
		Hashtable table = new Hashtable();
		table.put(-100, new JLabel("左"));
		table.put(100, new JLabel("右"));
		table.put(0, new JLabel("中"));
		
		directionSlider = new JSlider(-100, 100, 0);
		directionSlider.setBorder(new TitledBorder("方向"));
//		directionSlider.setMinorTickSpacing(100);
//		directionSlider.setPaintTicks(true);
		directionSlider.setLabelTable(table);
		directionSlider.setPaintLabels(true);
		controlPane.add(directionSlider);
		directionSlider.addChangeListener(this);
		
		
		directionBar = new JSlider(-100, 100);
		directionBar.setBorder(new TitledBorder("汽车方向"));
		directionBar.setLabelTable(table);
		directionBar.setPaintLabels(true);
		directionBar.setFocusable(false);
		carStatePane.add(directionBar);
		
		
		//档位
		gearSlider = new JSlider(Engine.GEAR_P, Engine.GEAR_4);
		gearSlider.setBorder(new TitledBorder("档位"));
		gearSlider.setLabelTable(gearSlider.createStandardLabels(1));
		gearSlider.setPaintLabels(true);
		controlPane.add(gearSlider);
		gearSlider.addChangeListener(this);
		
		gearBar = new  JSlider(Engine.GEAR_P, Engine.GEAR_4);
		gearBar.setBorder(new TitledBorder("汽车档位"));
		gearBar.setLabelTable(gearBar.createStandardLabels(1));
		gearBar.setPaintLabels(true);
		carStatePane.add(gearBar);
		
		//移动方向
		Hashtable labels = new Hashtable();
		labels.put(-1, new JLabel("后退"));
		labels.put(0, new JLabel("停"));
		labels.put(1, new JLabel("前进"));
		
		movementSlider = new JSlider(-1, 1, 0);
		movementSlider.setBorder(new TitledBorder("运动方向"));
		movementSlider.setLabelTable(labels);
		movementSlider.setPaintLabels(true);
		controlPane.add(movementSlider);
		movementSlider.addChangeListener(this);
		
		movementBar = new JSlider(-1, 1, 0);
		movementBar.setBorder(new TitledBorder("汽车运动方向"));
		movementBar.setLabelTable(labels);
		movementBar.setPaintLabels(true);
		carStatePane.add(movementBar);
		
		this.add(root);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		int val = ((JSlider)source).getValue();
		System.out.println(val);
		if(source==maxPowerSlider){
			if(controller!=null){
				controller.getStateForSend().maxPower = val;
			}
		}
		if(source==this.directionSlider){
			if(controller!=null){
				controller.getStateForSend().direction = val;
			}
		}
		if(source==this.movementSlider){
			if(controller!=null){
				controller.getStateForSend().movement = val;
			}
		}
		if(source==this.gearSlider){
			if(controller!=null){
				controller.getStateForSend().gear = val;
			}
		}
		//System.out.println("改变:"+controller.getStateForSend());
		
	}
	
	
	@Override
	public void update(Observable o, Object obj) {
		System.out.println(obj); 
		//CarVO state = (CarVO) carStateVo;
		if(obj instanceof CarVO){
			//收到汽车状态
			CarVO state = (CarVO) obj;
			this.directionBar.setValue((int)(state.direction));
			this.enginePowerBar.setValue(state.enginePower);
			this.gearBar.setValue(state.gear);
			this.maxPowerBar.setValue(state.maxPower);
			this.movementBar.setValue(state.movement);
		}else if(obj instanceof String){
			//收到文本提示消息
			System.out.println(obj); 
		}
	}
	
	public void action(){
//		root.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				System.out.println(e.getKeyChar());
//				switch (e.getKeyCode()) {
//				case KeyEvent.VK_UP:
//					
//					break;
//				case KeyEvent.VK_DOWN:
////					controller.send(HelloCommand.REVESE);
////					textArea.append(HelloCommand.REVESE+"\n");
////					break;
////				case KeyEvent.VK_LEFT:
////					controller.send(HelloCommand.TURN_LEFT);
////					textArea.append(HelloCommand.TURN_LEFT+"\n");
////					break;
////				case KeyEvent.VK_RIGHT:
////					controller.send(HelloCommand.TURN_RIGHT);
////					textArea.append(HelloCommand.TURN_RIGHT+"\n");
////					break;
////				case KeyEvent.VK_A:
////					controller.send(HelloCommand.PLUS_GAER);
////					textArea.append(HelloCommand.PLUS_GAER+"\n");
////					break;
////				case KeyEvent.VK_S:
////					controller.send(HelloCommand.SUBTRACT_GARE);
////					textArea.append(HelloCommand.SUBTRACT_GARE+"\n");
////					break;
////				case KeyEvent.VK_Q:
////					controller.send(HelloCommand.STOP);
////					textArea.append(HelloCommand.STOP+"\n");
////					break;
////				case KeyEvent.VK_SPACE:
////					try {
////						System.out.println("开始发现");
////						textArea.append("discovery\n");
////						controller.discovery(new Monitor() {
////							public void show(String msg) {
////								textArea.append(msg+"\n");
////							}
////						});
////					} catch (Exception e1) {
////						e1.printStackTrace();
////					}
////					break;
////				}
//			}
//		});
//		root.requestFocus();
//		root.setFocusable(true); 
	}
	
	public static void main(String[] args)  {
		RemoteController controller = new RemoteController();
		ControllerFrame frame  = new ControllerFrame();
		frame.controller = controller;
		controller.addObserver(frame);
		controller.start();
		frame.setVisible(true);
		frame.action();
	}

}






