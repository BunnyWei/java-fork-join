import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;


public class MatrixTest {

	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("2 arguments are required: numberthreads  matrix_length");
			System.exit(-1);
		}
		int numthread = Integer.parseInt(args[0]);
		int matrix_len = Integer.parseInt(args[1]);
//		double[][] matrix_A = {{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4}};
//		double[][] matrix_B = {{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4}};
		double[][] matrix_A = new double[matrix_len][matrix_len];
		double[][] matrix_B = new double[matrix_len][matrix_len];
		double[][] output = new double[matrix_len][matrix_len];
		for(int i = 0 ;i < matrix_len;i++){
			for(int j = 0; j<matrix_len;j++){
				matrix_A[i][j] = (double)(1+ Math.random()*100);
			}
		}
		for(int i = 0 ;i < matrix_len;i++){
			for(int j = 0; j<matrix_len;j++){
				matrix_B[i][j] = (double)(1+ Math.random()*100);
			}
		}
		for(int i = 0 ;i < matrix_len;i++){
			for(int j = 0; j<matrix_len;j++){
				output[i][j] = 0;
			}
		}
		long starttime = System.currentTimeMillis();
		int Threshold = matrix_len/numthread +1;
		ForkJoinTask<double[][]> matrixTask = new MatrixMultiply(matrix_A,matrix_B,0,
													matrix_len,matrix_len,Threshold,numthread,output);
		ForkJoinPool fjpool = new ForkJoinPool();
		Future<double[][]> result = fjpool.submit(matrixTask);
		try {
			System.out.println("fork:"+result.get());
			System.out.println("pool size:"+fjpool.getPoolSize());
            starttime = System.currentTimeMillis() - starttime;
            double[][] tempshow = result.get();
            for(int i = 0; i< matrix_len;i++){
                for(int j = 0 ; j<matrix_len;j++){
                    System.out.println(tempshow[i][j]);
            }
            
            }
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		fjpool.shutdown();
		
		System.out.println("the execution time is:"+starttime);
	}

}
