package backend;

import java.io.File;
import java.io.IOException;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

import java.util.ArrayList;


public class ArffBuilder {
	DBConnect DB = new DBConnect();

	public ArffBuilder() {

	}

	public ArrayList<String> getAttributeList(String tableName) throws Exception {
		ArrayList<String> AttributeName = new ArrayList<String>();
		int NumberofAtts;
		NumberofAtts = DB.checkNumofAtt(tableName);
		for (int i = 0; i < NumberofAtts; i++) {
			AttributeName.add(DB.getCurrentAttName(tableName, i + 1));
			//System.out.println("LOL");
		}
		return AttributeName;
	}

	public void ArffCreate(String tableName, String ARFFName, String Path, ArrayList<Integer> AttributeIndex) throws Exception {
		FastVector atts;
		Instances data;
		double[] vals;
		int i, j, NumberofAttributes, NumberofData, DateNum = 0, DateLock = 0, FilterIndex = 0;
		ArrayList<String> AttributeNameList = getAttributeList(tableName);
		ArrayList<String> FilterAttribute = new ArrayList<String>();

		
		// 1. set up attributes
		atts = new FastVector();
		
		NumberofAttributes = DB.checkNumofAtt(tableName);
		//System.out.println("Number of Attributes: "+ NumberofAttributes);
		for(i = 0; i < NumberofAttributes; i++)
		{
			String NameofAtt = AttributeNameList.get(i);
			if(AttributeIndex.get(i) != 0)
			{
				System.out.println("Before check - Name: "+ NameofAtt);
				if(!(NameofAtt.equals("id")) && !(NameofAtt.equals("date")))
				{
				// - numeric
					atts.addElement(new Attribute(NameofAtt));
					//System.out.println("Attributes Name: "+ NameofAtt);
					FilterAttribute.add(NameofAtt);
					FilterIndex++;
				}
				else if(NameofAtt.equals("date"))
				{
					atts.addElement(new Attribute(NameofAtt, "yyyy-MM-dd"));
					//System.out.println("Date Attributes Name: "+ NameofAtt);
					DateNum = FilterIndex;
					DateLock = 1;
					FilterAttribute.add(NameofAtt);
					FilterIndex++;
				}
			}
		}
		// 2. create Instances object
		data = new Instances("data", atts, 0);

		NumberofData = DB.getNumberofData(tableName);
		
		for(i = 0; i< NumberofData; i++)
		{
			vals = new double[data.numAttributes()];
			for(j = 0; j< data.numAttributes(); j++)
			{
				if(j == DateNum && DateLock == 1)
				{
					vals[j] = data.attribute(j).parseDate(DB.getData(tableName, "id", ""+i+"", ""+FilterAttribute.get(j)+""));
					//System.out.println("Date Vals["+ j + "] :" + vals[j]);
				}
				else
				{		
					//System.out.println("Attribute Name:"+FilterAttribute.get(j)); 
					String valsValue = DB.getData(tableName, "id", ""+i+"", ""+FilterAttribute.get(j)+"");
					//System.out.println("TableName " + tableName + " ID : " + i + " Target " + FilterAttribute.get(j)+ " Attribute Value:"+valsValue); 
					if(DB.getData(tableName, "id", ""+i+"", ""+FilterAttribute.get(j)+"") == null)
					{
						vals[j] = Instance.missingValue();
					}
					else
					{
						vals[j] = Double.parseDouble(valsValue);
					}
					//System.out.println("Vals["+ j + "] :" + vals[j]);
				}
			}
			data.add(new Instance(1.0, vals));
		}
		
		//System.out.println(data);


		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(Path + ARFFName + ".arff"));
		
		saver.writeBatch();
	}
	
	public void UploadARFF(String tableName, String path, String userName) throws Exception
	{
		File inputFile = new File(path);
		ArffLoader atf = new ArffLoader();
		DBConnect DB = new DBConnect();
		try {
			atf.setFile(inputFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Instances instancesTrain = atf.getDataSet();
		instancesTrain.setClassIndex(0);
		
		String InsidedataString = instancesTrain.toString();
		String InsideData[] = InsidedataString.split(" |\n");
		instancesTrain.numInstances();
		ArrayList<String> TableNameList = new ArrayList<String>();
		ArrayList<String> TableTypeList = new ArrayList<String>();
		
		int DataLock = 0, DataCount = 0;
		for(int index = 0; index < InsideData.length; index++)
		{
			if (InsideData[index].indexOf("attribute") != -1)
			{
				TableNameList.add(InsideData[index+1]);
				if(InsideData[index+1].indexOf("date") != -1)
				{
					if(InsideData[index+2].indexOf("{") != -1)
					{
						TableTypeList.add("varchar(100)");
					}
					else
					{
						TableTypeList.add("date");
					}
				}
                                                                        else{
				if(InsideData[index+2].indexOf("{") != -1)
				{
					TableTypeList.add("varchar(100)");
				}
				else if(InsideData[index+2].indexOf("numeric") != -1)
				{
					TableTypeList.add("double(14,8)");
				}
                                                                        }
			}
		}
		DB.createTables(tableName, TableNameList, TableTypeList);
		for(int index = 0; index < InsideData.length; index++)
		{
			if(index > 0)
			{
				if (InsideData[index-1].indexOf("data") != -1)
				{
					DataLock = 1;
				}
			}
			if(DataLock == 1)
			{
				if(InsideData[index] != "\n")
				{
					String lsList[] = InsideData[index].split(",");
					ArrayList<String> TableValueList = new ArrayList<String>();
					TableValueList.add(""+DataCount);
					DataCount++;
					for(int lsindex = 0; lsindex < lsList.length; lsindex++)
					{
						//System.out.println(lsList[lsindex]);
						if(TableTypeList.get(lsindex) == "varchar(100)" )
						{
							TableValueList.add("'" + lsList[lsindex] + "'");
						}
						else
						{
							//System.out.println("���ʵ���� "+lsList[lsindex]);
							if(!(lsList[lsindex].equals("?")))
							{
								TableValueList.add(lsList[lsindex]);
								//System.out.println("����-ʵ���� "+TableValueList.get(lsindex));
							}
							else
							{
								TableValueList.add("NULL");
								//System.out.println("����-ʵ���գ� "+TableValueList.get(lsindex));
							}
						}
					}
					
					DB.insertToTables(tableName, TableNameList, TableValueList);
				}
			}
			//System.out.println(InsideData[index]);
		}
	}	
}