import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;


public class StringMatchTest {

	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("2 arguments are required: numberthreads  matrix_length");
			System.exit(-1);
		}
		int numthreads = Integer.parseInt(args[0]);
		File file = new File(args[1]);
		BufferedReader reader = null;
		long start = 0;
		long end;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		long line = 0;
		try {
			while(reader.readLine() != null){
				line++;
			}
			end = line -1;
			long Threshold = line/numthreads +1;
            long starttime = System.currentTimeMillis();
			StringMatch stringmatch = new StringMatch(file,start,end,Threshold,numthreads);
			ForkJoinPool fjpool = new ForkJoinPool();
			fjpool.invoke(stringmatch);
            starttime = System.currentTimeMillis() -starttime;
			fjpool.shutdown();
		}
			catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
