package edu.wildlifesecurity.framework.tracking.impl;
import jama.Matrix;

import java.util.Collections;
import java.util.Vector;

import jkalman.JKalman;

import org.opencv.core.Scalar;


public class KalmanFilter {
	
	
	private JKalman kalman;
	private Integer id;
	private Integer numOfUnseen;
	private Integer numOfSeen;
	Matrix predicted; 
	Scalar colorRegion;
	Scalar colorKalman;
	Vector<Scalar> colorVec;
	Vector<Integer> classVec;

		
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
		numOfSeen = 0;
		double[][] m = {{x,y,height,width,0,0}};
		Matrix initMat = new Matrix(m);
		kalman.setState_post(initMat.transpose());
		colorRegion = new Scalar(125,125,125);//new Scalar(Math.abs(Math.random()*255),Math.abs(Math.random()*255),Math.abs(Math.random()*255));
		colorKalman = new Scalar(Math.abs(Math.random()*255),Math.abs(Math.random()*255),Math.abs(Math.random()*255));
		colorVec = new Vector<>();
		colorVec.add(new Scalar(23,17,255));
		colorVec.add(new Scalar(249,83,87));
		colorVec.add(new Scalar(68,227,85));
		classVec = new Vector<>();
	}
	
	public boolean isDone(int minSeen, double classRatio)
	{
		if(classVec.isEmpty())
			return false;
		Collections.sort(classVec);
		int maxOcc = 0;
		int maxClass = -1;
		
		for(int i = classVec.firstElement(); i <= classVec.lastElement(); i++)
		{
			int tmpMax = Collections.frequency(classVec, i);
			if(tmpMax > maxOcc)
			{
				maxClass = i;
				maxOcc = tmpMax;
			}
		}
		if(numOfSeen == minSeen && maxOcc/classVec.size() > classRatio)
			return true;
		
		return false;
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
		numOfSeen++;
	}
	public Scalar getColorRegion()
	{
		return colorRegion;
	}
	public Scalar getColorKalman()
	{
		return colorKalman;
	}
	public void addClass(int classification)
	{
		classVec.add(classification);
		if(classification >= 0)
		colorRegion = colorVec.get(classification);
	}
	public Vector<Integer> getClassVec()
	{
		return classVec;
	}

	
	
}

