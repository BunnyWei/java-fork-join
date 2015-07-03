import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;


public class KMeansTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		if(args.length < 2){
			System.out.println("2 arguments are required: numberthreads -- the number of points");
		}
		boolean bool = false;
		//int count = 5;
		final int demision = 5;
		int group = 10;
		int vector_len = Integer.parseInt(args[1]);
		Vector<Double[]> vec = new Vector<Double[]>(vector_len);
		Vector<Double[]> vector = new Vector<Double[]>(demision);
		Random random = new Random(100);
		for(Double j = 0.0 ;j < vector_len ; j++){
//			vec.add(j,new Double[]{random.nextDouble(),random.nextDouble(),
//					random.nextDouble(),random.nextDouble(),random.nextDouble()});
			vec.add(j.intValue(),new Double[]{j,j+1,j*4,j*2,j*3});
		}
		for(int i = 0; i < group ; i++){
			vector.add(i,vec.elementAt(Math.abs(random.nextInt()%vec.size())));
		}

		int numthreads = Integer.parseInt(args[0]);
		int Threshold = vector_len/numthreads +1;
		List forks = new LinkedList();

		long starttime = System.currentTimeMillis();
		ForkJoinPool fjpool = new ForkJoinPool();
		for(int count = 50; count> 0 ;count--){
			KMeansFork fff = new KMeansFork(vec,vector,demision,group,Threshold,numthreads);
			//forks.add(fff);
			fjpool.invoke(fff);
			vector.clear();
			//System.out.println("fff.size()"+fff.getReturnvector().size());
			for(int i = fff.getReturnvector().size() -1 ;i >= 0 ;i -= (fff.getReturnvector().size()/group)){
				vector.addElement(fff.getReturnvector().elementAt(i));
			}
		}
		fjpool.shutdown();
		starttime = System.currentTimeMillis() - starttime;
		System.out.println("the execution time is:"+starttime);
	}
}
