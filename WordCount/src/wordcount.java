import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.RecursiveTask;
import org.w3c.dom.Text;

public class wordcount extends RecursiveTask<Map<String,Integer>>{

	private int Threshold;
	private File file;
	//private Text value;
	//private StringTokenizer token;
	//private String str;
	private long start;
	private long end;
    private int numthreads;
	Map<String,Integer> map = new HashMap<String,Integer>();
	public wordcount(File file,long start, long end, int Threshold,int numthreads){
		this.Threshold = Threshold;
		this.file = file;
		this.start = start;
		this.end = end;
        this.numthreads = numthreads;
	}
	
	public Map<String,Integer> reduce(File file, long start, long end){
		try {
			MappedByteBuffer inputBuffer = new RandomAccessFile(file,"r").getChannel().
			map(FileChannel.MapMode.READ_ONLY, start, end-start);
			byte[] dst =new byte[1024];
			dst[0] = inputBuffer.get(0); 
			for (int offset = 0; offset < inputBuffer.capacity(); offset += 1024) {
				   if (inputBuffer.capacity() - offset >= 1024) {
				    for (int i = 0; i < 1024; i++)
				     dst[i] = inputBuffer.get(offset + i);
				   } else {
				    for (int i = 0; i < inputBuffer.capacity() - offset; i++)
				     dst[i] = inputBuffer.get(offset + i);
				   }
				   int length = (inputBuffer.capacity() % 1024 == 0) ? 1024
				     : inputBuffer.capacity() % 1024;
					String text = new String(dst,0,length);
					StringTokenizer token = new StringTokenizer(text,"\\s || ',' || ''' ");
					while(token.hasMoreTokens()){
						String linecontent = token.nextToken();
						if(!map.containsKey(linecontent)){
							map.put(linecontent, 1);
							System.out.println("the key:"+linecontent);
						}
						else{
							int count_value = map.get(linecontent);
							count_value++;
							map.put(linecontent, count_value);
							System.out.println("the key:"+linecontent);
							System.out.println("the count:"+count_value);
						}
					}
				  }

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return map;
	}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             	
	@Override
	protected Map<String,Integer> compute() {
		
		long worklength = end - start;
		//byte[] textsize = new byte[(long)worklength];
		//List<RecursiveTask<Long>> forks = new LinkedList();
		String str = file.toString();
		StringTokenizer token = new StringTokenizer(str);
		long tokensize = token.countTokens();
		if(worklength <= Threshold){			
			return reduce(file,start,end);
		}
		else{
			long split = worklength /2;
			//System.out.prlongln("the shhhhh is:"+split);
			wordcount left = new wordcount(file,start,split + start,Threshold,numthreads);
			wordcount right = new wordcount(file,split + start +1 ,end,Threshold,numthreads);
			left.fork();
			right.fork();
			Map<String,Integer> temp1 = left.join();
			Map<String,Integer> temp2 = right.join();
			Iterator iter = temp2.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next();
				String key = entry.getKey().toString();
				Integer count = (Integer)entry.getValue();
				if(temp1.get(key) != null){
					temp1.put(key, temp1.get(key) + count);					
				}
				else{
					temp1.put(key, count);
				}
			}
			return temp1;
		}
	}
	
}
