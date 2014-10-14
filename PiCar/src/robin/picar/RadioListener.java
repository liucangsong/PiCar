package robin.picar;

import robin.picar.controller.HelloCommand;

public interface RadioListener extends CarComponentListener {
	void commandRevcived(RadioEvent e);
	//void stateRevcived(RadioEvent e);
}
