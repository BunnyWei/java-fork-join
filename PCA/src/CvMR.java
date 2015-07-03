import java.util.concurrent.RecursiveTask;


public class CvMR extends RecursiveTask<double[][]>{

	private double[][] matrix_A;
	private double[][] matrix_mean;
	private double[][] matrix_S;
	private int matrix_col;
	private int matrix_row;
	private int start_col;
	private int end_col;
	private int Threshold;
	
	public CvMR(double[][] matrix_A,double[][] matrix_mean,double[][] matrix_S,int matrix_row,int matrix_col,int start_col,int end_col,int Threshold){
		this.matrix_A = matrix_A;
		this.matrix_mean = matrix_mean;
		this.matrix_S = matrix_S;
		this.matrix_row = matrix_row;
		this.matrix_col = matrix_col;
		this.start_col = start_col;
		this.end_col = end_col;
		this.Threshold = Threshold;
	}
	
	public double[][] computeCvMR(){
		int temp_startrcol = start_col;
		int sum ;
		while(temp_startrcol <end_col){
			sum = 0;
			for(int j = 0; j<matrix_row;j++){
				for(int i = 0 ; i<matrix_row; i++){
					matrix_S[j][j] +=((matrix_A[j][temp_startrcol]-matrix_mean[j][0])*
				   	(matrix_A[j][i]-matrix_mean[j][0]))/matrix_col;
				}
			}
			temp_startrcol++;
		}
		
		return matrix_S;
	}
	@Override
	protected double[][] compute() {
		int middle_col = (end_col - start_col)/2;
		if(middle_col *2 <= Threshold){
			return computeCvMR();
		}else{
			CvMR leftuppercvmr = new CvMR(matrix_A,matrix_mean,matrix_S, matrix_row, matrix_col,
									start_col, (start_col+middle_col), Threshold);
			CvMR leftdowncvmr = new CvMR(matrix_A,matrix_mean,matrix_S, matrix_row, matrix_col,
									(start_col+middle_col+1),end_col,Threshold);
			
			leftuppercvmr.fork();
			leftdowncvmr.fork();
			
			leftuppercvmr.join();
			leftdowncvmr.join();
			return matrix_S;
		}
	}
}
