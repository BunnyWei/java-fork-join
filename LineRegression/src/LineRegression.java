import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.RecursiveTask;


public class LineRegression extends RecursiveTask<Map<String,Long>>{
	private long Threshold;
	private File file;
	private long start;
	private long end;
	
	public LineRegression(File file, long start, long end, long Threshold){
		this.Threshold = Threshold;
		this.file = file;
		this.start = start;
		this.end = end;
	}
	
	public Map<String,Long> calcuSepe(){
		long tempstart = 0;
		HashMap<String,Long> map = new HashMap<String,Long>();
		//System.out.println("start!:"+start);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			while(tempstart <= (start - 0.01)){
				reader.readLine();
				tempstart++;
			}
			//System.out.println("tempstart!:"+tempstart);
			map.put("KEY_SXX", 0L);
			map.put("KEY_SYY", 0L);
			map.put("KEY_SXY", 0L);
			map.put("KEY_SX",  0L);
			map.put("KEY_SY",  0L);
			//System.out.println("end!:"+end);
			while(tempstart <= end ){
				String str = reader.readLine();
				//System.out.println("tempstart!:::::::::"+tempstart);
				StringTokenizer token = new StringTokenizer(str);
				while(token.hasMoreTokens()){
					//XX,YY,XY,X,Y
					long tempX = Long.valueOf(token.nextToken()).longValue();
					long tempY = Long.valueOf(token.nextToken()).longValue();
					long l = map.get("KEY_SXX") + (tempX * tempX);
					map.put("KEY_SXX", l);
					l = map.get("KEY_SYY") + (tempY * tempY);
					map.put("KEY_SYY", l);
					l = map.get("KEY_SXY") + (tempX * tempY);
					map.put("KEY_SXY", l);
					l = map.get("KEY_SX") + tempX;
					map.put("KEY_SX", l);
					l = map.get("KEY_SY") + tempY;
					map.put("KEY_SY", l);
				}
				tempstart++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	protected Map<String,Long> compute() {		
		if((end - start) <= Threshold){
			return calcuSepe();
		}else{
			long middle = (end - start)/2;
			LineRegression left = new LineRegression(file, start, (start + middle), Threshold);
			LineRegression right = new LineRegression(file, (start + middle +1), end, Threshold);
			left.fork();
			right.fork();
			Map<String,Long> leftMap = left.join();
			Map<String,Long> rightMap = right.join();
			Iterator iter = leftMap.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next();
				String key = entry.getKey().toString();
				Long count = (Long)entry.getValue();
				if(rightMap.get(key) != null){
					leftMap.put(key, rightMap.get(key) + count);					
				}
				else{
					leftMap.put(key, count);
				}
			}
			return leftMap;
		}
	}
}
