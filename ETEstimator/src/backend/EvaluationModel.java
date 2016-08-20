package backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.trees.M5P;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;



public class EvaluationModel {

	double MaxR, MaxRMSE;
	int ID = 1;
	DBConnect DB = new DBConnect();
	StringBuffer Predictions1;
	weka.core.Range attsToOutput = null;

	// SMO Parameters
	double LValue = 0.0010;
	double CValue = 1.0;
	double PValue = 1.0E-12;
	int NValue = 0;
	int VValue = -1;
	int WValue = 1;
	int SCValue = 250007;
	double EValue = 1.0;
	double TValue = 0.001;
	double OValue = 1.0;
	double SValue = 1.0;
	String K = "-K";

	public EvaluationModel() {

	}

	public void evaluation(String models, String ArffName, String ResultTable, String path) throws Exception {
		if (models == "M5P") {
			double mNI = 1.0;
			String M = "-M";
			String Reg = "-R";

			M5P model = new M5P();
			ID = DB.getNumberofData(ResultTable)+1;
			for (double i = 0; i <= 5; i++) {
				String[] options = { M, "" + mNI + "", Reg };
				double RSquare;
				double RMSE;
				File inputFile = new File(path);
				ArffLoader atf = new ArffLoader();
				try {
					atf.setFile(inputFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Instances testData = atf.getDataSet();
				testData.setClassIndex(testData.numAttributes() - 1);
				
				// System.out.println(testData);
				model.setOptions(options);
				model.buildClassifier(testData);
				Evaluation eval = new Evaluation(testData);
				Predictions1 = new StringBuffer();
				eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
				// System.out.println(eval.toSummaryString("\nResult", false));
				// System.out.println(eval.toMatrixString());
				// System.out.println(eval.toClassDetailsString());

				RMSE = eval.rootMeanSquaredError();
				RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();

				double NSEValue = calResult(Predictions1);
				DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
						"" + ID + "", "'" + models + "'", "'" + M + " " + mNI + " " + Reg + "'", "" + RMSE + "",
						"" + RSquare + "", "" + NSEValue + "", "'" + ArffName + "'");
				ID = ID + 1;
				mNI = mNI + 1;
				//System.out.println("RMSE" + RMSE);
				//System.out.println("RSquare" + RSquare);
			}

			mNI = 1.0;

			for (double i = 0; i <= 5; i++) {
				String[] options = { M, "" + mNI + "" };
				File inputFile = new File(path);
				ArffLoader atf = new ArffLoader();
				try {
					atf.setFile(inputFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Instances testData = atf.getDataSet();
				testData.setClassIndex(testData.numAttributes() - 1);
				
				// System.out.println(testData);
				model.setOptions(options);
				model.buildClassifier(testData);
				Evaluation eval = new Evaluation(testData);
				Predictions1 = new StringBuffer();
				eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
				// System.out.println(eval.toSummaryString("\nResult", false));
				// System.out.println(eval.toMatrixString());
				// System.out.println(eval.toClassDetailsString());

				double RMSE = eval.rootMeanSquaredError();
				double RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();

				double NSEValue = calResult(Predictions1);
				DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
						"" + ID + "", "'" + models + "'", "'" + M + " " + mNI + "'", "" + RMSE + "", "" + RSquare + "",
						"" + NSEValue + "", "'" + ArffName + "'");
				ID = ID + 1;
				mNI = mNI + 1;

				//System.out.println("RMSE" + RMSE);
			}
		} else if (models == "SMOregPoly") {
			ID = DB.getNumberofData(ResultTable)+1;
			SMOreg model = new SMOreg();
			String[] options = { "-C", "" + CValue + "", "-L", "" + LValue + "", "-P", "" + PValue + "", "-N",
					"" + NValue + "", "weka.classifiers.functions.supportVector.RegSMOImproved", "-V", "" + VValue 
					+ "", "-W", "" + WValue + "", "-T", ""+ TValue +"", "-K",
					"weka.classifiers.functions.supportVector.PolyKernel", "-C", "" + SCValue + "", "-E",
					"" + EValue + "" };
			double RSquare;
			double RMSE;
			File inputFile = new File(path);
			ArffLoader atf = new ArffLoader();
			try {
				atf.setFile(inputFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Instances testData = atf.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			String optionString = options[0];
			for(int Ots = 1; Ots<options.length; Ots++)
			{
				optionString = optionString + " " + options[Ots];
			}
			// System.out.println(testData);
			model.setOptions(options);
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());

			RMSE = eval.rootMeanSquaredError();
			RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();

			double NSEValue = calResult(Predictions1);
			DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
					"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
					"" + NSEValue + "", "'" + ArffName + "'");
			ID = ID + 1;

			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
		} else if (models == "SMOregPuk") {
			ID = DB.getNumberofData(ResultTable)+1;
			SMOreg model = new SMOreg();
			String[] options = { "-C", "" + CValue + "", "-L", "" + LValue + "", "-P", "" + PValue + "", "-N",
					"" + NValue + "", "weka.classifiers.functions.supportVector.RegSMOImproved", "-V", "" + VValue 
					+ "", "-W", "" + WValue + "", "-T", ""+ TValue +"", "-K",
					"weka.classifiers.functions.supportVector.Puk", "-C", "" + SCValue + "", "-O", ""+ OValue + "", "-S",
					"" + SValue + "" };
			double RSquare;
			double RMSE;
			File inputFile = new File(path);
			ArffLoader atf = new ArffLoader();
			try {
				atf.setFile(inputFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Instances testData = atf.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			String optionString = options[0];
			for(int Ots = 1; Ots<options.length; Ots++)
			{
				optionString = optionString + " " + options[Ots];
			}
			// System.out.println(testData);
			model.setOptions(options);
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());

			RMSE = eval.rootMeanSquaredError();
			RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();

			double NSEValue = calResult(Predictions1);
			DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
					"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
					"" + NSEValue + "", "'" + ArffName + "'");
			ID = ID + 1;

			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
		} else if (models == "SMOregRBF") {
			ID = DB.getNumberofData(ResultTable)+1;
			SMOreg model = new SMOreg();
			double GValue = 0.01;
			DecimalFormat dcmFmt = new DecimalFormat("0.00");
			
			for(GValue = 0.01; GValue <= 1.00; GValue = GValue + 0.01)
			{
				
				String[] options = { "-C", "" + CValue + "", "-N", "" + NValue + "", "-I", "weka.classifiers.functions.supportVector.RegSMOImproved", 
						"-L", "" + LValue + "", "-W", "" + WValue + "", "-P", "" + PValue + "", "-T", ""+ TValue +"", "-V"/*, "" + VValue + ""*/, 
						"-K", "weka.classifiers.functions.supportVector.RBFKernel", "-C", "" + SCValue + "", "-G",
						"" + Double.parseDouble(dcmFmt.format(GValue)) + "" };
				
				double RSquare;
				double RMSE;
				File inputFile = new File(path);
				ArffLoader atf = new ArffLoader();
				try {
					atf.setFile(inputFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Instances testData = atf.getDataSet();
				testData.setClassIndex(testData.numAttributes() - 1);
				String optionString = options[0];
				for(int Ots = 1; Ots<options.length; Ots++)
				{
					optionString = optionString + " " + options[Ots];
				}
				// System.out.println(testData);
				model.setOptions(options);
				String[] kop = {"-C", "" + SCValue + "", "-G", "" + Double.parseDouble(dcmFmt.format(GValue)) + "" };
				model.setKernel(Kernel.forName("weka.classifiers.functions.supportVector.RBFKernel", kop));
				String[] op = model.getOptions();
				//for(int Ots = 1; Ots<op.length; Ots++)
				//{
				//	System.out.println(op[Ots]);
				//}
				model.buildClassifier(testData);
				Evaluation eval = new Evaluation(testData);
				Predictions1 = new StringBuffer();
				eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
				
				// System.out.println(eval.toSummaryString("\nResult", false));
				// System.out.println(eval.toMatrixString());
				// System.out.println(eval.toClassDetailsString());
	
				RMSE = eval.rootMeanSquaredError();
				RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
	
				double NSEValue = calResult(Predictions1);
				DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
						"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
						"" + NSEValue + "", "'" + ArffName + "'");
				ID = ID + 1;
	
				//System.out.println("RMSE" + RMSE);
				//System.out.println("RSquare" + RSquare);
			}
		} else if (models == "LR") {
			ID = DB.getNumberofData(ResultTable)+1;
			LinearRegression model = new LinearRegression();
			String[] options = {"-S" , "0", "-R", "1.0E-8"};
			double RSquare;
			double RMSE;
			File inputFile = new File(path);
			ArffLoader atf = new ArffLoader();
			try {
				atf.setFile(inputFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Instances testData = atf.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			String optionString = options[0];
			for(int Ots = 1; Ots<options.length; Ots++)
			{
				optionString = optionString + " " + options[Ots];
			}
			// System.out.println(testData);
			model.setOptions(options);
			
			String[] op = model.getOptions();
			//for(int Ots = 1; Ots<op.length; Ots++)
			//{
			//	System.out.println(op[Ots]);
			//}
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			RMSE = eval.rootMeanSquaredError();
			RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			double NSEValue = calResult(Predictions1);
			DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
					"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
					"" + NSEValue + "", "'" + ArffName + "'");
				ID = ID + 1;
	
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
		} else if (models == "MultiPerceptron") {
			ID = DB.getNumberofData(ResultTable)+1;
			MultilayerPerceptron model = new MultilayerPerceptron();
			String[] options = {"-L" , "0.3", "-M", "0.2", "-N", "500", "-V", "0", "-S", "0", "-E", "20", "-H", "a"};
			double RSquare;
			double RMSE;
			File inputFile = new File(path);
			ArffLoader atf = new ArffLoader();
			try {
				atf.setFile(inputFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Instances testData = atf.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			String optionString = options[0];
			for(int Ots = 1; Ots<options.length; Ots++)
			{
				optionString = optionString + " " + options[Ots];
			}
			// System.out.println(testData);
			model.setOptions(options);
			
			String[] op = model.getOptions();
			//for(int Ots = 1; Ots<op.length; Ots++)
			//{
			//	System.out.println(op[Ots]);
			//}
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			RMSE = eval.rootMeanSquaredError();
			RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			double NSEValue = calResult(Predictions1);
			DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
					"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
					"" + NSEValue + "", "'" + ArffName + "'");
				ID = ID + 1;
	
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
		} else if (models == "GPPoly") {
			ID = DB.getNumberofData(ResultTable)+1;
			GaussianProcesses model = new GaussianProcesses();
			String[] options = {"-L" , "1.0", "-N", "0", "-K", "weka.classifiers.functions.supportVector.PolyKernel", "-C", "250007", "-E", "1.0"};
			double RSquare;
			double RMSE;
			File inputFile = new File(path);
			ArffLoader atf = new ArffLoader();
			try {
				atf.setFile(inputFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Instances testData = atf.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			String optionString = options[0];
			for(int Ots = 1; Ots<options.length; Ots++)
			{
				optionString = optionString + " " + options[Ots];
			}
			// System.out.println(testData);
			model.setOptions(options);
			
			String[] op = model.getOptions();
			//for(int Ots = 1; Ots<op.length; Ots++)
			//{
			//	System.out.println(op[Ots]);
			//}
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			RMSE = eval.rootMeanSquaredError();
			RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			double NSEValue = calResult(Predictions1);
			DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
					"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
					"" + NSEValue + "", "'" + ArffName + "'");
				ID = ID + 1;
	
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
		} else if (models == "GPPuk") {
			ID = DB.getNumberofData(ResultTable)+1;
			GaussianProcesses model = new GaussianProcesses();
			String[] options = {"-L" , "1.0", "-N", "0", "-K", "weka.classifiers.functions.supportVector.Puk", "-C", "250007", "-O", "1.0", "-S", "1.0"};
			double RSquare;
			double RMSE;
			File inputFile = new File(path);
			ArffLoader atf = new ArffLoader();
			try {
				atf.setFile(inputFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Instances testData = atf.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			String optionString = options[0];
			for(int Ots = 1; Ots<options.length; Ots++)
			{
				optionString = optionString + " " + options[Ots];
			}
			// System.out.println(testData);
			model.setOptions(options);
			
			String[] op = model.getOptions();
			//for(int Ots = 1; Ots<op.length; Ots++)
			//{
			//	System.out.println(op[Ots]);
			//}
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			RMSE = eval.rootMeanSquaredError();
			RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			double NSEValue = calResult(Predictions1);
			DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
					"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
					"" + NSEValue + "", "'" + ArffName + "'");
				ID = ID + 1;
	
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
		} else if (models == "GPRBF") {
			ID = DB.getNumberofData(ResultTable)+1;
			GaussianProcesses model = new GaussianProcesses();
			double GValue = 0.01;
			DecimalFormat dcmFmt = new DecimalFormat("0.00");
			
			for(GValue = 0.01; GValue <= 1.00; GValue = GValue + 0.01)
			{
				String[] options = {"-L" , "1.0", "-N", "0", "-K", "weka.classifiers.functions.supportVector.RBFKernel", "-C", "250007", "-G", dcmFmt.format(GValue)};
				double RSquare;
				double RMSE;
				File inputFile = new File(path);
				ArffLoader atf = new ArffLoader();
				try {
					atf.setFile(inputFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Instances testData = atf.getDataSet();
				testData.setClassIndex(testData.numAttributes() - 1);
				String optionString = options[0];
				for(int Ots = 1; Ots<options.length; Ots++)
				{
					optionString = optionString + " " + options[Ots];
				}
				// System.out.println(testData);
				model.setOptions(options);
				String[] kop = {"-C", "250007", "-G", dcmFmt.format(GValue)};
				model.setKernel(Kernel.forName("weka.classifiers.functions.supportVector.RBFKernel", kop));
				//System.out.println("Kernel GP " + model.getKernel());
				String[] op = model.getOptions();
				//for(int Ots = 1; Ots<op.length; Ots++)
				//{
				//	System.out.println(op[Ots]);
				//}
				model.buildClassifier(testData);
				Evaluation eval = new Evaluation(testData);
				Predictions1 = new StringBuffer();
				eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
				
				// System.out.println(eval.toSummaryString("\nResult", false));
				// System.out.println(eval.toMatrixString());
				// System.out.println(eval.toClassDetailsString());
				RMSE = eval.rootMeanSquaredError();
				RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
				double NSEValue = calResult(Predictions1);
				DB.insertResultDB("testResultcreate", "ID", "Model", "ModelSetting", "RMSE", "R", "NSE", "NameofArff",
						"" + ID + "", "'" + models + "'", "'" + optionString + "'", "" + RMSE + "", "" + RSquare + "",
						"" + NSEValue + "", "'" + ArffName + "'");
					ID = ID + 1;
				}
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
		}
	} 

	public double calResult(StringBuffer Sbuffer) {

		String pres = Sbuffer.toString();
		char s1[] = pres.toCharArray();
		double NSEValue;
		String actuals = "";
		String predicteds = "";
		String errors = "";
		int index = 0;
		int lockIndex = 0;
		
		ArrayList<Double> ActualsA = new ArrayList<Double>();
		ArrayList<Double> PredictedsA = new ArrayList<Double>();
		ArrayList<Double> ErrorsA = new ArrayList<Double>();
		int Aindex = 0, Pindex = 0, Eindex = 0;
		// 39 \n,
		for (int nums = 0; nums < pres.length(); nums++) {
			if (nums <= 44) {
				actuals = "";
				predicteds = "";
				errors = "";
				index = 0;
				lockIndex = 0;
			}

			else if (nums > 44) {

				if (s1[nums] != ' ' && s1[nums] != '\n') {
					if (index == 0) {
						actuals = "";
						predicteds = "";
						errors = "";
						// System.out.println("index0 +" + nums);
						lockIndex = 0;
						// Inst#
						// do nothing
					} else if (index == 1) {
						actuals = actuals + s1[nums] + "";
						/*
						 * System.out.println(nums);
						 * System.out.println("actuals");
						 * System.out.println(actuals);
						 */
						lockIndex = 0;

					} else if (index == 2) {
						predicteds = predicteds + s1[nums] + "";
						/*
						 * System.out.println("predicteds");
						 * System.out.println(predicteds);
						 */
						lockIndex = 0;

					} else if (index == 3) {
						errors = errors + s1[nums] + "";
						/*
						 * System.out.println("errors");
						 * System.out.println(errors); System.out.println(
						 * "errorprintf " + nums);
						 */
						lockIndex = 0;

					}
				} else if (s1[nums] == ' ' && lockIndex == 0) {
					// System.out.println("inside");
					// System.out.println("lockprintf " + nums);
					if (index == 0) {
						index++;
					} else if (index == 1) {
						ActualsA.add(Double.parseDouble(actuals));
						// System.out.println(ActualsA.get(Aindex));
						Aindex++;
						index++;
					} else if (index == 2) {
						PredictedsA.add(Double.parseDouble(predicteds));
						// System.out.println(PredictedsA.get(Pindex));
						Pindex++;
						index++;
					} else if (index == 3) {
						ErrorsA.add(Double.parseDouble(errors));
						// System.out.println(ErrorsA.get(Eindex));
						Eindex++;
						index = 0;
					}
					lockIndex = 1;
				}
			}
		}
		double sumActual = 0, sumPredict = 0, sumSquaredError = 0, meanActual = 0, fmValue = 0;
		for (int sumindex = 0; sumindex < Eindex; sumindex++) {
			sumActual = sumActual + ActualsA.get(sumindex);
			sumPredict = sumPredict + PredictedsA.get(sumindex);
			sumSquaredError = sumSquaredError + (ErrorsA.get(sumindex) * ErrorsA.get(sumindex));
		}
		meanActual = sumActual / Aindex;
		for (int sumindex = 0; sumindex < Aindex; sumindex++) {
			fmValue += (ActualsA.get(sumindex) - meanActual) * (ActualsA.get(sumindex) - meanActual);
		}
		NSEValue = 1 - (sumSquaredError / fmValue);
		return NSEValue;
	}
	
	public void ModelGeneration(String Method, String[] options, String path) throws Exception
	{
		//double RSquare;
		//double RMSE;

		File inputFile = new File(path);
		ArffLoader atf = new ArffLoader();
		try {
			atf.setFile(inputFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Instances testData = atf.getDataSet();
		testData.setClassIndex(testData.numAttributes() - 1);
		// System.out.println(testData);
		if(Method.equals("M5P"))
		{
			M5P model = new M5P();
			model.setOptions(options);
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			//RMSE = eval.rootMeanSquaredError();
			//RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			//double NSEValue = calResult(Predictions1);
			
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\test.model"));
			oos.writeObject(model);
			oos.flush();
			oos.close();
		}
		else if(Method.equals("SMOregPoly") || Method.equals("SMOregPuk") || Method.equals("SMOregRBF"))
		{
			SMOreg model = new SMOreg();
			model.setOptions(options);
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			//RMSE = eval.rootMeanSquaredError();
			//RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			//double NSEValue = calResult(Predictions1);
			
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\test.model"));
			oos.writeObject(model);
			oos.flush();
			oos.close();
		}
		else if(Method.equals("LR"))
		{
			LinearRegression model = new LinearRegression();
			model.setOptions(options);
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			//RMSE = eval.rootMeanSquaredError();
			//RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			//double NSEValue = calResult(Predictions1);
			
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\test.model"));
			oos.writeObject(model);
			oos.flush();
			oos.close();
		}
		else if(Method.equals("GPPoly") || Method.equals("GPPuk") || Method.equals("GPRBF"))
		{
			GaussianProcesses model = new GaussianProcesses();	
			model.setOptions(options);
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			//RMSE = eval.rootMeanSquaredError();
			//RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			//double NSEValue = calResult(Predictions1);
			
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\test.model"));
			oos.writeObject(model);
			oos.flush();
			oos.close();
		}
		else if(Method.equals("MultiPerceptron"))
		{
			MultilayerPerceptron model = new MultilayerPerceptron();
			model.setOptions(options);
			model.buildClassifier(testData);
			Evaluation eval = new Evaluation(testData);
			Predictions1 = new StringBuffer();
			eval.crossValidateModel(model, testData, 10, new Random(1), Predictions1, attsToOutput, true);
			// System.out.println(eval.toSummaryString("\nResult", false));
			// System.out.println(eval.toMatrixString());
			// System.out.println(eval.toClassDetailsString());
			//RMSE = eval.rootMeanSquaredError();
			//RSquare = eval.correlationCoefficient() * eval.correlationCoefficient();
			//double NSEValue = calResult(Predictions1);
			
			//System.out.println("RMSE" + RMSE);
			//System.out.println("RSquare" + RSquare);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("D:\\test.model"));
			oos.writeObject(model);
			oos.flush();
			oos.close();
		}
		
	}
	
}