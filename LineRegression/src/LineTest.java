import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;


public class LineTest {

	public static void main(String[] args) {
		try {
            if(args.length < 2){
                System.out.println("2 arguments are required: numthreads   file_path");
                System.exit(-1);
            }
			
			long start = 0;
			long end;
			File newfile = new File(args[1]);
			BufferedReader reader = null;
			reader = new BufferedReader(new FileReader(newfile));
			long line = 0;
			while(reader.readLine() != null){
				line++;
			}
			end = line -1;
            long Threshold = line/Integer.parseInt(args[0]);
			reader.close();
			reader = new BufferedReader(new FileReader(newfile));
            long starttime = System.currentTimeMillis();
			ForkJoinTask<Map<String,Long>> lineregression = new LineRegression(newfile,start,end,Threshold);
			ForkJoinPool fjpool = new ForkJoinPool();
			Future<Map<String,Long>> result = fjpool.submit(lineregression);
			try {
				Map<String,Long> tempResult = result.get();
                starttime = System.currentTimeMillis()-starttime;
				long SXY = tempResult.get("KEY_SXY");
				long SYY = tempResult.get("KEY_SYY");
				long SXX = tempResult.get("KEY_SXX");
				long SY = tempResult.get("KEY_SY");
				long SX = tempResult.get("KEY_SX");
				double b = (double)(SXY - SX * SY / line) / (SXX - SX * SX /line);
				double a = (double)(SY / line - b * SX/line);
				double xBar = (double) SX /(line * line);
				double yBar = (double) SY /(line * line);
				double r2 = (double)(SXY - SX * SY / line)* (SXY - SX * SY / line)/ ((SXX - SX * SX/line) *
						            (SYY - SY * SY/line));
				System.out.println("Fork is:"+result.get());
				System.out.println("b is:"+b);
				System.out.println("a is:"+a);
				System.out.println("xBar is:"+xBar);
				System.out.println("yBar is:"+yBar);
				System.out.println("r2 is:"+r2);
                System.out.println("the run time:"+starttime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

}
