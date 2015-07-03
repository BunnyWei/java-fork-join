import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;


public class WordCountTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		if(args.length < 2){
			System.out.println("2 arguments are required: numberthreads  file_path");
			System.exit(-1);
		}
		File file = new File(args[1]);
		if(!file.exists()){
			System.out.println("there is not such file");
			return;
		}
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String str = "";
		StringBuilder sb = new StringBuilder();
		str = bf.readLine();
		while(str != null){
			sb.append(str.trim());
			str = bf.readLine();
			if(str == null)
				break;
		}
		bf.close();
		str = sb.toString();		
		StringTokenizer token = new StringTokenizer(str,"\\s || ',' || ''' || '\t' || '\n'");
		MappedByteBuffer inputBuffer = new RandomAccessFile(file,"r").getChannel().
		map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		long length = file.length();
		int numthreads = Integer.parseInt(args[0]);
		int Threshold = (int)(length/numthreads +1);
		ForkJoinTask<Map<String,Integer>> fff = new wordcount(file,0,file.length(),Threshold,numthreads);
		ForkJoinPool fjpool = new ForkJoinPool();
		Future<Map<String,Integer>> result = fjpool.submit(fff);
		//System.out.println("Fork is:"+result.get());
		Map<String,Integer> showresult = result.get();
		Iterator iter = showresult.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			String key = entry.getKey().toString();
			Integer count = (Integer)entry.getValue();
			System.out.println("the string is:  "+key+"   the count is:  "+count);
		}
	}

}
