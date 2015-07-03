
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.RecursiveTask;


public class KMeansFork extends RecursiveTask<HashMap<Double[],Vector<Double[]>>>{
	
	private int Threshold;
	private final int demision;
	private int group;
    private int numthreads;
	private Vector<Double[]> vec;
	private Vector<Double[]> vector;
	private Vector<Double[]> returnvector = new Vector<Double[]>();
	public KMeansFork(Vector<Double[]> vec,Vector<Double[]> vector,int demision, int group,int Threshold,int numthreads){
		this.demision = demision;
		this.group = group;
		this.vec = vec;
		this.vector = vector;
		this.Threshold = Threshold;
        this.numthreads = numthreads;
	}
	
	public HashMap<Double[],Vector<Double[]>> Iteration(Vector<Double[]> vec){
		Double distance = 0.0;
		int[] ClusterNumber = new int[vec.size()];
		//vec.addElement(new Double[]{1.0,2.0,2.0,3.0,4.0,5.0});
		for(int i = vec.size() -1 ; i >= 0 ; i--){
			Double min = Double.MAX_VALUE;
			for(int j = vector.size() -1 ; j >= 0 ; j--){
				distance = sumDistance(vec.elementAt(i),vec.elementAt(j));
				if(distance < min){
					min = distance;
					ClusterNumber[i] = j;
					//System.out.println("the distance:"+distance);
				}
			}                                                                                                                   
		}
		//System.out.println("the size of ClusterNumber:"+ClusterNumber.length);
		return computerCluster(ClusterNumber,vec,vector);
		//ComputerAver(ClusterNumber);
	}
	public HashMap<Double[],Vector<Double[]>> computerCluster(int[] clu,Vector<Double[]> temprec,Vector<Double[]> tempvector){
		HashMap<Double[],Vector<Double[]>> map = new HashMap<Double[],Vector<Double[]>>();
		for(int i = clu.length -1 ; i >= 0 ; i--){
			if(!map.containsKey(temprec.elementAt(clu[i]))){
				Vector<Double[]> clusterVec = new Vector<Double[]>();
				clusterVec.add(temprec.elementAt(i));
				map.put(temprec.elementAt(clu[i]), clusterVec);
			}
			else{
				
				map.get(temprec.elementAt(clu[i])).add(temprec.elementAt(clu[i]));
			}
		}
		return map;
	}
	
	public Double sumDistance(Double[] x, Double[] y){
		Double sum = 0.0;
		for(int i = demision -1; i >= 0; i--){
			sum += (x[i] - y[i]) *(x[i] - y[i]);
		}
		return sum;
	}
	public Double[] average(Double[] x, Double[] y){
		Double[] aver = new Double[demision];
		for(int i = demision -1; i >= 0; i--){
			aver[i] = (x[i] + y[i]);
		}
		return aver;
	}
	
	public HashMap<Double[],Vector<Double[]>> ComputerAver(HashMap<Double[],Vector<Double[]>> avermap){	
		
		Iterator averiter = avermap.entrySet().iterator();
		HashMap<Double[],Vector<Double[]>> computerMap = new HashMap<Double[],Vector<Double[]>>();
		int count = 0;
		while(averiter.hasNext()){
			Map.Entry entry = (Map.Entry) averiter.next();
			Double[] key = (Double[])entry.getKey();
			Vector<Double[]> itervec = (Vector<Double[]>)entry.getValue();
			Double[] averagemeans = new Double[demision];
			for(int i = demision -1; i >= 0; i--){
				int aversize = itervec.size() -1;
				Double sum = 0.0;
				while(aversize >= 0){
					sum += itervec.elementAt(aversize)[i];
					aversize--;
				}
				 averagemeans[i] = sum / itervec.size();				
			}
			returnvector.add(count++, averagemeans);
			computerMap.put(averagemeans, itervec);
		}
		return computerMap;
	}

	//this function is right;
	@Override
	protected HashMap<Double[],Vector<Double[]>> compute() {
		int hel = vec.size();
		if(hel < Threshold){
			return Iteration(vec);
		}
		else{
			int veclength = vec.size();
			int middle = veclength /2;
			Vector<Double[]> vectorone = new Vector<Double[]>(middle);
			for(int i =0 ; i< middle; i++){
				vectorone.add(i,vec.elementAt(i));
			}
			Vector<Double[]> vectortwo = new Vector<Double[]>(veclength - middle);
			for(int i =middle ; i< vec.size(); i++){
				vectortwo.add((i-middle),vec.elementAt(i));
			}
			KMeansFork kmeansone = new KMeansFork(vectorone,vector,demision,group,Threshold,numthreads);
			KMeansFork kmeanstwo = new KMeansFork(vectortwo,vector,demision,group,Threshold,numthreads);
			kmeansone.fork();
			kmeanstwo.fork();
			HashMap<Double[],Vector<Double[]>> leftmap = kmeansone.join();
			HashMap<Double[],Vector<Double[]>> rightmap = kmeanstwo.join();
			Iterator iter = leftmap.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next();
				Double[] key = (Double[])entry.getKey();
				Vector<Double[]> itervec = (Vector<Double[]>)entry.getValue();
				if(rightmap.get(key) != null){
					//System.out.println("hello");
					Vector<Double[]> tempvecone = rightmap.get(key);
					int num = tempvecone.size();
					for(int i = 0; i < itervec.size();i++){
						tempvecone.addElement(itervec.elementAt(i+num));
					}
					rightmap.put(key, tempvecone);				
				}
				else{
					//System.out.println("hfsdugfiord");
					rightmap.put(key, itervec);
				}
			}
			return ComputerAver(rightmap);
			//System.out.println("hhhhhhhhhh");
//			if(rightmap.size() > group){
//				/************************/
//				int num = rightmap.size()  - group;
//				while(num > 0){
//					Iterator iterone = leftmap.entrySet().iterator();
//					Map.Entry entry = (Map.Entry) iterone.next();
//					Double[] key = (Double[])entry.getKey();
//					Vector<Double[]> itervec = (Vector<Double[]>)entry.getValue();	
//					rightmap.remove(key);
//					Map.Entry entryone = (Map.Entry) iterone.next();
//					Double[] keyone = (Double[])entry.getKey();
//					num--;
//					System.out.println("num"+num);
//				}
//			}

		}
	}

	public Vector<Double[]> getReturnvector() {
		return returnvector;
	}
}
