import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;


public class HistogramTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
        if(args.length < 2){
            System.out.println("2 arguments are required: numthreads   file_path");
            System.exit(-1);
        }
		File file = new File(args[1]);
		if(!file.exists()){
			System.out.print("there is not such file");
			return;
		}
		try {
			BufferedImage image =ImageIO.read(file);
			System.out.println("width"+image.getWidth());
			System.out.println("width"+image.getHeight());
			
			final byte[] pixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
			int numthreads = Integer.parseInt(args[0]);
			int Threshold = (image.getHeight()*image.getWidth()*3)/numthreads +1;
			long starttime = System.currentTimeMillis();
			ForkJoinTask<int[]> fff =  new Histogram(pixels,0,
			                        image.getHeight()*image.getWidth()*3 -1,Threshold,numthreads);
			ForkJoinPool fjpool = new ForkJoinPool();
            Future<int[]> result = fjpool.submit(fff);
            int[] temp = result.get();
			fjpool.shutdown();
//			fjpool.awaitTermination(15, TimeUnit.MINUTES);
//			System.out.println("Fork is:"+result.get());
			starttime = System.currentTimeMillis() - starttime;
			//int[] temp = fff.getTempreturn().clone();
			for(int i = 0; i<768;i++){
				System.out.println("  "+temp[i]);
			}
			System.out.println("the run time is:"+starttime);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
