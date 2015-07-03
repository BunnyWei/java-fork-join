import java.util.concurrent.RecursiveTask;


public class PCA extends RecursiveTask<double[][]>{

	//mean vector 
	private int Threshold;
	private int start;
	private int end;
	private int matrix_row;
	private int matrix_col;
	private double[][] matrix_A;
	private double[][] mean_matrix;
	
	public PCA(double[][] matrix_A, int matrix_row,int matrix_col, int start, int end, double[][] mean_matrix,int Threshold){
		this.matrix_A = matrix_A;
		this.mean_matrix = mean_matrix;
		this.matrix_row = matrix_row;
		this.matrix_col = matrix_col;
		this.start = start;
		this.end = end;
		this.Threshold = Threshold;
	}
	
	public void CompuMeanVector(){
		int temp_start = start;
		while(temp_start < end){
			long sum = 0;
			for(int j = 0; j < matrix_col; j++){
				sum += matrix_A[temp_start][j];
			}
			mean_matrix[temp_start][0] = sum/matrix_col;
			temp_start++;
		}
	}
	
	@Override
	protected double[][] compute() {
		int middle = (end-start)/2;
		if((end-start) <= Threshold){
			CompuMeanVector();
		}else{
			PCA left  = new PCA(matrix_A,matrix_row,matrix_col,start,(start+middle),mean_matrix,Threshold);
			PCA right = new PCA(matrix_A,matrix_row,matrix_col,(start+middle+1),end,mean_matrix,Threshold);
			left.fork();
			right.fork();
			left.join();
			right.join();
		}
		return mean_matrix;
	}

}
