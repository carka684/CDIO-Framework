package edu.wildlifesecurity.framework.mediasource;

import java.awt.Image;

import edu.wildlifesecurity.framework.IComponent;

public interface IMediaSource extends IComponent {

	Image getSnapshot();
	
}
