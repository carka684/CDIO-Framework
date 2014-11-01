package edu.wildlifesecurity.framework;

import edu.wildlifesecurity.framework.repository.IRepository;

/*
 *  Represents an interface to the system. The interface enables configuration of the system and reading system log.
 *  
 *  TODO: Potential additional features:
 *   - Get live images from trap devices
 *   - Get positions of trap devices
 */
public interface ISystemInterface {
	
	void link(IRepository repository);

}
