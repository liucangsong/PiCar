package robin.picar;

import java.util.Map;

import robin.picar.controller.HelloCommand;

public class RadioEvent extends CarComponentEvent {
	private static final long serialVersionUID = -1135571390201284632L;
	private HelloCommand command;
	private CarVO vo;
	
	public RadioEvent(CarComponent source, HelloCommand command) {
		super(source);
		this.command = command;
	}

	public RadioEvent(CarComponent source, CarVO vo) {
		super(source);
		this.vo = vo;
	}

	public HelloCommand getCommand() {
		return command;
	}

	public CarVO getCarVO() {
		return vo;
	}
}
