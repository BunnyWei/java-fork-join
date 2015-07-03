import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;


public class PCATest {

	public static void main(String[] args) {
        if(args.length < 3){
            System.out.println("3 arguments are required: numthreads   matrixrowlength   matrixcollength");
            System.exit(-1);
        }
		int matrix_row = Integer.parseInt(args[1]);
		int matrix_col = Integer.parseInt(args[2]);
		int Threshold = matrix_col/Integer.parseInt(args[0]);
		double[][] matrix_A = new double[matrix_row][matrix_col];
		double[][] output = new double[matrix_row][1];
		double[][] matrix_S = new double[matrix_row][matrix_row];
		for(int i = 0 ;i < matrix_col;i++){
			for(int j = 0; j<matrix_row;j++){
				matrix_A[j][i] = (double)(1+ Math.random()*100);
			}
		}
		for(int i = 0; i< matrix_row;i++){
			output[i][0] = 0;
		}
		long starttime = System.currentTimeMillis();
		
		ForkJoinTask<double[][]> pca = new PCA(matrix_A,matrix_row,matrix_col,0,
													matrix_row,output,Threshold);
		ForkJoinPool fjpool = new ForkJoinPool();
		Future<double[][]> result = fjpool.submit(pca);
		try {
			double[][] matrix_mean = result.get();
			System.out.println("hhhh"+matrix_mean.length);
			ForkJoinTask<double[][]> cvmr = new CvMR(matrix_A,matrix_mean,matrix_S, matrix_row, matrix_col,
														0,matrix_col, Threshold);
			Future<double[][]> resultcvmr = fjpool.submit(cvmr);
			System.out.println("fork:"+resultcvmr.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		fjpool.shutdown();
		starttime = System.currentTimeMillis() - starttime;
		System.out.println("the execution time is:"+starttime);
	}

}
