package edu.wildlifesecurity.framework.tracking.impl;
import org.opencv.core.Scalar;

import jama.Matrix;
import jkalman.JKalman;


public class KalmanFilter {
	
	
	private JKalman kalman;
	private Integer id;
	private Integer numOfUnseen;
	Matrix predicted; 
	Scalar color;

		
	public KalmanFilter(Integer id,int x,int y, int height, int width) throws Exception
	{
		kalman = new JKalman(6, 4);
		double[][] tr = 
			   {{1, 0, 0, 0, 1, 0},   
				{0, 1, 0, 0, 0, 1},             
				{0, 0, 1, 0, 0, 0}, 
				{0, 0, 0, 1, 0, 0},
				{0, 0, 0, 0, 1, 0},
				{0, 0, 0, 0, 0, 1}};
		kalman.setTransition_matrix(new Matrix(tr));
		this.id = id;
		numOfUnseen = 0;
		double[][] m = {{x,y,height,width,0,0}};
		Matrix initMat = new Matrix(m);
		kalman.setState_post(initMat.transpose());
		color = new Scalar(Math.abs(Math.random()*255),Math.abs(Math.random()*255),Math.abs(Math.random()*255));
	}
	public void correct(int x, int y,int height, int width)
	{
		double[][] meas = {{x,y,height,width}};
		Matrix measurement = new Matrix(meas).transpose();
		kalman.Correct(measurement);
	}
	public double getError(int x, int y)
	{
		double[][] pos = getPos();
		double error = Math.sqrt(Math.pow(pos[0][0]-x, 2)+Math.pow(pos[1][0]-y, 2));
		return error;
	}
	public double getErrorArea(double area)
	{
		double tmpArea = kalman.getState_pre().get(3, 0)*kalman.getState_pre().get(2, 0);
		double res = tmpArea/area;
		if(res > 1)
			res = 1/res;
		return res;
	}
	public double[] getErrorDim(double height,double width)
	{
		double tmpHeight = height/kalman.getState_pre().get(2, 0);
		double tmpWidth = width/kalman.getState_pre().get(3, 0);
		
		if(tmpHeight > 1)
			tmpHeight = 1/tmpHeight;
		if(tmpWidth > 1)
			tmpWidth  = 1/tmpWidth;
		
		return new double[]{tmpHeight,tmpWidth};
	}

	public void predict()
	{
		predicted = kalman.Predict();	
		kalman.setState_post(predicted);
	}
	public double[][] getPos()
	{
		return this.kalman.getState_pre().getArray();
	}
	public JKalman getKalman()
	{
		return kalman; 
	}
	public Integer getId()
	{
		return id;
	}
	public Integer getNumOfUnseen()
	{
		return numOfUnseen;
	}
	public void addUnseen()
	{
		numOfUnseen++;
	}
	public void seen()
	{
		numOfUnseen = 0;
	}
	public Scalar getColor()
	{
		return color;
	}
	

	
	
}

