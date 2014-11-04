package edu.wildlifesecurity.framework.actuator;

import edu.wildlifesecurity.framework.IComponent;

public interface IActuator extends IComponent {
		void sendMessage(String IP,String port,String message,String number, String password);
}
