import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;


public class MatrixMultiply extends RecursiveTask<double[][]>{

	private int Threshold;
	private int numthread;
	private int location;
	private int matrix_len;
	private int end;
	private double[][] matrix_A;
	private double[][] matrix_B;
	private double[][] output;
	
	public MatrixMultiply(double[][] matrix_A, double[][] matrix_B,int location,  int end, int matrix_len,int Threshold,int numthread,double[][] output){
		this.matrix_A = matrix_A;
		this.matrix_B = matrix_B;
		this.location = location;
		this.Threshold = Threshold;
		this.numthread = numthread;
		this.matrix_len = matrix_len;
		this.end = end;
		this.output = output;
	}
	
	public double[][] Fork_Multiply(){
		
		int temp_loca = location;
		int b_ptr;
		while(temp_loca < end){
			b_ptr = 0;
			for(int i = 0; i< matrix_len; i++){
				for(int j = 0; j< matrix_len; j++){
//					System.out.println("temp_locaA"+matrix_A[temp_loca][i]);
//					System.out.println("temp_locaB"+matrix_B[b_ptr][j]);
					output[temp_loca][j] += matrix_A[temp_loca][i]*matrix_B[b_ptr][j]; 
				}
				b_ptr++;
			}
			temp_loca++;
		}
		return output;
	}
	
	@Override
	protected double[][] compute() {
		List<MatrixMultiply> tasks = new ArrayList<MatrixMultiply>();
		int row_num = (end - location)/numthread;
		if((end - location) <= Threshold){
			System.out.println("fork:"+this.getPool().getActiveThreadCount());
			return Fork_Multiply();		
		}else{
			for(int i = 0 ; i<numthread; i++){
				MatrixMultiply upper_matrix = new MatrixMultiply(matrix_A,matrix_B,(location+row_num*i),
						(location+row_num*(i+1)),matrix_len,Threshold,numthread,output);
				upper_matrix.fork();
				tasks.add(upper_matrix);
			}
			for(int i = 0; i<tasks.size();i++){
				tasks.get(i).join();
			}
			return output;
		}
	}
}
