package backend;

import weka.core.Instances;
import weka.classifiers.Classifier;
import java.io.BufferedReader;
import java.io.FileReader;

public class RunModel {
	
	public RunModel()
	{
		
	}
	
	public static void RunModel(String path){
		//Change path depending on where model is saved
		String rootPath="/D:/"; 
		try{
			Classifier cls = (Classifier) weka.core.SerializationHelper.read(rootPath+"test.model");
		

		//predict instance class values
		Instances originalTrain= new Instances(
                new BufferedReader(
                        new FileReader("/D://test.arff/")));//load or create Instances to predict
		originalTrain.setClassIndex(originalTrain.numAttributes() - 1);
		 cls.buildClassifier(originalTrain);
		//which instance to predict class value
		int s1=0;  

		//perform your prediction
		double value=cls.classifyInstance(originalTrain.instance(s1));
		

		//get the name of the class value
		//String prediction=originalTrain.classAttribute().value((int)value); 

		System.out.println("The predicted value of instance "+
		                    Integer.toString(s1)+
		                    ": "+value);
		}catch (Exception e){
			
			System.out.print(e.getMessage());
		}	
	}
}
