import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;


public class StringMatch extends RecursiveTask{
	
	private int OFFSET = 5;
	private int MAX_REC_LEN = 1024;
	private char[] key1 = {'H','e','l','l','o','w','o','r','l','d'};
	private char[] key2 = {'h','o','w','a','r','e','y','o','u'};
	private char[] key3 = {'f','e','r','r','a','r','i'};
	private char[] key4 = {'w','h','o','t','h','e','m','a','n'};
	private char[] final_key1 = new char[key1.length +1];
	private char[] final_key2 = new char[key2.length +1];
	private char[] final_key3 = new char[key3.length +1];
	private char[] final_key4 = new char[key4.length +1];
	private long Threshold;
	private File file;
	private long start;
	private long end;
	private int numthreads;
	public StringMatch(File file, long start, long end, long Threshold,int numthreads){
		this.file = file;
		this.start = start;
		this.end = end;
		this.Threshold = Threshold;
		this.numthreads = numthreads;
	}
	
	public char[] compute_hashes(char[] word, int length, char[] final_word){
		int i;
		for(i = 0; i < length; i++){
			final_word[i] = (char)(word[i] + Integer.toString(OFFSET).charAt(0));
		}
		final_word[i] = 0;
		return final_word;
	}
	
	public void toMatch(){
		compute_hashes(key1,key1.length,final_key1);
		compute_hashes(key2,key2.length,final_key2);
		compute_hashes(key3,key3.length,final_key3);
		compute_hashes(key4,key4.length,final_key4);
		try {
			BufferedReader reader= new BufferedReader(new FileReader(file));
			long index = 0;
			while(index <= (start - 0.01)){
				reader.readLine();
				index++;
			}
			while(index <= end){
				int len = 0;
				String temp_str = reader.readLine();
				char temp_curword[] = new char[temp_str.length()];
				while(len < temp_str.length()){
					temp_curword[len] = temp_str.charAt(len);
					len ++;
				}
				char cur_word_final[] = new char[len +1];
				compute_hashes(temp_curword,len,cur_word_final);
				
				if(isEqual(final_key1,cur_word_final))
					System.out.println("FOUND: WORD IS\n"+chartostring(key1));
				if(isEqual(final_key2,cur_word_final))
					System.out.println("FOUND: WORD IS\n"+chartostring(key2));
				if(isEqual(final_key3,cur_word_final))
					System.out.println("FOUND: WORD IS\n"+chartostring(key3));
				if(isEqual(final_key4,cur_word_final))
					System.out.println("FOUND: WORD IS\n"+chartostring(key4));
				index++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected Object compute() {
		List<StringMatch> tasks = new ArrayList<StringMatch>();
		long middle = (end - start)/numthreads;
		if((end - start) <= Threshold){			
			toMatch();
		}else{
			for(int i = 0; i< numthreads; i++){
				//System.out.println("the number of thread:"+this.getPool().getActiveThreadCount());
				StringMatch leftMatch = new StringMatch(file, start+i*middle, start+(i+1)*middle, Threshold,numthreads);
				leftMatch.fork();
				tasks.add(leftMatch);
			}
			for(int j = 0; j<tasks.size();j++){
				tasks.get(j).join();
			}
		}
		return null;
	}

	//computer the keys encroped value
	public boolean isEqual(char[] a, char[] b){
		if(a.length != b.length)
			return false;
		for(int i = 0; i< a.length ;i++){
			if(a[i] != b[i]){
				return false;
			}
		}
		return true;
	}

	public String chartostring(char[] a){
		String temp_str = "";
		if(a.length <= 0)
			return null;
		for(int i = 0; i < a.length ;i++){
			temp_str += a[i];
		}
		return temp_str;
	}
}
