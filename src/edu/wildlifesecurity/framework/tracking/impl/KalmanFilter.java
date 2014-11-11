package edu.wildlifesecurity.framework.tracking.impl;
import jama.Matrix;
import jkalman.JKalman;


public class KalmanFilter {
	
	
	private JKalman kalman;
	private Integer id;
	private Integer numOfUnseen;
	Matrix predicted; 
	private double errorDist;
	
	public KalmanFilter(Integer id,int x,int y) throws Exception
	{
		kalman = new JKalman(4, 2);
		double[][] tr = { {1, 0, 1, 0},   
				{0, 1, 0, 1},             
				{0, 0, 1, 0}, 
				{0, 0, 0, 1} };
		kalman.setTransition_matrix(new Matrix(tr));
		this.id = id;
		numOfUnseen = 0;
		double[][] m = {{x,y,0,0}};
		Matrix measMa = new Matrix(m);
		kalman.setState_post(measMa.transpose());
	}
	public void correct(int x, int y)
	{
		double[][] meas = {{x,y}};
		Matrix measurement = new Matrix(meas).transpose();
		kalman.Correct(measurement);
	}
	public double getError(int x, int y)
	{
		double[][] pos = getPos();
		double error = Math.sqrt(Math.pow(pos[0][0]-x, 2)+Math.pow(pos[1][0]-y, 2));
		return error;
		
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

	
	
}

