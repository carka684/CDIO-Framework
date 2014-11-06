package edu.wildlifesecurity.framework.identification.impl;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ImageReader
{

	private static Mat classes;
	private static Map<Integer, Vector<String>> filesMap;
	private static Vector<String> filesVec;
	private static int numOfClasses;
	private static int[] numOfEachClass;

	public ImageReader()
	{
		classes = new Mat();
		filesMap = new HashMap<Integer, Vector<String>>();
		filesVec = new Vector<String>();
		numOfClasses = 0;
		
	}

	public void readImages(String folderTrain)
	{
		filesMap.clear();
		classes.release();
		filesVec.clear();
		
		try {
			filesMap = listFilesForFolder(folderTrain);
			numOfClasses = filesMap.size();
			int[] temp = new int[numOfClasses];
			for(int i = 0; i < filesMap.size(); i++)
			{
				temp[i] = filesMap.get(i).size();
				getClasses().push_back(new Mat(filesMap.get(i).size(),1,CvType.CV_32F, new Scalar(i)));
				for(String str : filesMap.get(i))
				{
					filesVec.add(str);
				}
			}
			numOfEachClass = temp;
		} catch (Exception e) {
			System.out.println("ERROR: Couldn't load images");
			e.printStackTrace();
		}	
	}
	public static Map<Integer, Vector<String>>  listFilesForFolder(String classFolder) {
		File[] folders = new File(classFolder).listFiles();
		int classNum = 0;
		Map<Integer, Vector<String>> map = new HashMap<Integer, Vector<String>>();
		for (File folder : folders) {
			if (folder.isDirectory()) {
				map.put(classNum++,getFiles(folder));
			}
		}
		return map;
	}
	public static Vector<String> getFiles(File folder)
	{		
		Vector<String> filesVec = new Vector<String>();
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				filesVec.add(file.getPath());
			}
		}
		return filesVec;
	}

	public Mat getClasses()
	{
		return classes;
	}
	public Map<Integer, Vector<String>> getMap()
	{
		return filesMap;
	}
	public Vector<String> getFilesVec()
	{
		return filesVec;
	}
	public int getNumOfClasses()
	{
		return numOfClasses;
	}
	public int[] getNumOfEachClass()
	{
		return numOfEachClass;
	}
}