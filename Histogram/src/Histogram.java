import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;


public class Histogram extends RecursiveTask<int[]> {
	
	private int Threshold;
	private int start;
	private int end;
	private int width;
	//private BufferedImage image;
	private int[] tempcolor = new int[768];
	private int[] tempreturn = new int[768];
	private byte[] pixels;
    private int numthreads;
	
	public Histogram(byte[] pixels ,int start, int end,int Threshold,int numthreads){
		//this.image =image;
		this.start = start;
		this.end = end;
		this.pixels = pixels;
		this.Threshold = Threshold;
        this.numthreads = numthreads;
	}

	@Override
	protected int[] compute() {
		
		//int halfsize = size /2;
		int middle = (end - start) /2;
		if((end - start) <= Threshold){
			return reduce();
		}else{		
			Histogram histone = new Histogram(pixels,start,middle + start,Threshold,numthreads);
			Histogram histtwo = new Histogram(pixels,middle+ start +1,end,Threshold,numthreads);
			histone.fork();
			histtwo.fork();
			int[] tempone = histone.join().clone();
			int[] temptwo = histtwo.join().clone();
			for(int i = 0; i < 768 ; i++){
				//System.out.println("the RGB:"+i);
				tempreturn[i] = tempone[i]+ temptwo[i];
				//System.out.println("the RGB:"+tempreturn[i]);
			}
			return tempreturn;
		}
	}

	public int[] reduce(){
		for(int i = start; i<= end ; i += 3 ){			
			tempcolor[pixels[i] & 0x000000ff] ++;
			if((i+1)< 144000)
			tempcolor[pixels[i+1]& 0x000000ff + 256] ++;
			if((i+2)< 144000)
			tempcolor[pixels[i+2]& 0x000000ff + 512] ++;
		}
		return tempcolor;
	}

	public int[] getTempreturn() {
		return tempreturn;
	}
}
