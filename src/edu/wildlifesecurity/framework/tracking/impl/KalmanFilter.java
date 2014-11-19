package edu.wildlifesecurity.framework.tracking.impl;
import jama.Matrix;

import java.util.Collections;
import java.util.Vector;

import jkalman.JKalman;

import org.opencv.core.Scalar;

import edu.wildlifesecurity.framework.identification.Classes;


public class KalmanFilter {
	
	
	private JKalman kalman;
	private Integer id;
	private Integer numOfUnseen;
	private Integer numOfSeen;
	private boolean sentCapture;
	Matrix predicted; 
	Scalar colorKalman;
	Vector<Classes> classVec;


		
	public KalmanFilter(Integer id,int x,int y, int height, int width)
	{
		try {
			kalman = new JKalman(6, 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		colorKalman = new Scalar(Math.abs(Math.random()*255),Math.abs(Math.random()*255),Math.abs(Math.random()*255));
		classVec = new Vector<>();
		sentCapture = false;

	}
	
	public boolean isDone(int minSeen, double classRatio)
	{
		if(classVec.isEmpty())
			return false;
		Collections.sort(classVec);
		int maxOcc = 0;
		Classes maxClass;
		for(Classes cl : classVec)
		{
			int tmpMax = Collections.frequency(classVec, cl);
			if(tmpMax > maxOcc)
			{
				maxClass = cl;
				maxOcc = tmpMax;
			}
		}
		System.out.println(numOfSeen + " " + (double) maxOcc/classVec.size());
		if( (numOfSeen >= minSeen) && ((double) maxOcc/classVec.size() > classRatio))
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
	public Scalar getColorKalman()
	{
		return colorKalman;
	}
	public void addClass(Classes classification)
	{
		classVec.add(classification);
	}
	public Vector<Classes> getClassVec()
	{
		return classVec;
	}
	public int getSeen()
	{
		return numOfSeen;
	}
	public boolean isSent()
	{
		return sentCapture;
	}
	public void setSent()
	{
		sentCapture = true;
	}
	
	
}

