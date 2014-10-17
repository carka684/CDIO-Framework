package edu.wildlifesecurity.framework.identification.impl;

import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.identification.IClassificationResult;

public class ClassificationResult implements IClassificationResult {

	private Classes resultClass;
	
	public ClassificationResult(Classes resultClass) {
		this.resultClass = resultClass;
	}
	
	@Override
	public Classes getResultingClass() {
		return resultClass;
	}

}
