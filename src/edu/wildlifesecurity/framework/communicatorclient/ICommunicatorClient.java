package edu.wildlifesecurity.framework.communicatorclient;

import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.ILogger;

/*
 * The CommunicatorClient component sends and receives messages from the server. It also works as a Logger in a sense as it redirects log messages to the server for storing
 */
public interface ICommunicatorClient extends IComponent, ILogger {

}
