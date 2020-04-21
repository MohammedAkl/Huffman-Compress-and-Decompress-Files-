package huffman;

import java.util.PriorityQueue; 
import java.util.Scanner; 
import java.util.Comparator;
import java.util.HashMap;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class HuffmanNode { 
	int data; 
	byte c; 

	HuffmanNode left; 
	HuffmanNode right; 
	public HuffmanNode(byte byteValue) {
		this.c=byteValue;
		
	}
	public HuffmanNode(HuffmanNode leftChild, HuffmanNode rightChild) {	
		this.left=leftChild;
		this.right=rightChild;
	}
	public HuffmanNode() {	
	}
} 


class MyComparator implements Comparator<HuffmanNode> { 
	public int compare(HuffmanNode x, HuffmanNode y) 
	{ 

		return x.data - y.data; 
	} 
} 

public class Huffman { 
	static Scanner s = new Scanner(System.in);
	static String header="" ;
	static String CompFile = "";
	static String DeCompFile = "";
	static String OutDeCompFile = "";
	static String compfile="" ;
	static HashMap<Byte, Integer> inputMap = new HashMap<Byte, Integer>();
	static HashMap<Byte, String > outputMap = new HashMap<Byte, String>();
	static HashMap<String, Byte > DecompMap = new HashMap<String, Byte>();
	static String reader;
	static String DecompB;
	static byte[] body =null;
	static byte[] bodyDe =null;



	public static HuffmanNode HuffmanF() {
		

		int n = inputMap.size(); 
		PriorityQueue<HuffmanNode> q 
			= new PriorityQueue<HuffmanNode>(n, new MyComparator()); 

		for (Byte x : inputMap.keySet()) {
			HuffmanNode hn = new HuffmanNode(); 
			hn.c = x; 
			hn.data = inputMap.get(x); 
			hn.left = null; 
			hn.right = null; 
			q.add(hn); 
		} 
		HuffmanNode root = null; 
		while (q.size() > 1) { 

			HuffmanNode x = q.peek(); 
			q.poll(); 
			HuffmanNode y = q.peek(); 
			q.poll(); 
			HuffmanNode f = new HuffmanNode(); 
			f.data = x.data + y.data; 
			f.c = '-'; 
			f.left = x; 
			f.right = y; 
			root = f; 
			q.add(f); 
		} 
		printCode(root, "");
		return root;
	}
	public static void printCode(HuffmanNode root, String s) 
	{ 
		if (root.left 
				== null
			&& root.right 
				== null) { 
			String binCode =Integer.toBinaryString(root.c);
			System.out.println((char)root.c + ":"+ binCode+":" + s);
			outputMap.put(root.c, s);

			return; 
		} 
		printCode(root.left, s + "0"); 
		printCode(root.right, s + "1"); 
	} 

	public static void ReadFromInput() throws IOException {
			try {
				body=Files.readAllBytes(Paths.get(CompFile));
			} catch(IOException e){
				e.printStackTrace();
			}
			for (int lenB=0 ; lenB<body.length;lenB++) { 
				if(inputMap.containsKey(body[lenB])==false) {
					inputMap.put(body[lenB], 1);
				}
					else {
						inputMap.replace(body[lenB],inputMap.get(body[lenB])+1);
					}
						
			}

		
		
	}
	public static void EncodeNode(HuffmanNode root)
	{
	    if (root.left==null & root.right==null )
	    {
	    	//StringBuilder sb = new StringBuilder("");
	       header=header+"1";
	        
	        String temp=Integer.toBinaryString(root.c);
	        while(temp.length()<7) {
	        	temp="0"+temp;
	       }
	        header=header+temp;
	    }
	    else
	    {
	        header=header+"0";
	        EncodeNode(root.left);
	        EncodeNode(root.right);
	    }
	}
	
	public static void compress() {
		StringBuilder bodyS =new StringBuilder("");
		for(int lenB2= 0 ; lenB2< body.length;lenB2++) {
			bodyS=bodyS.append(outputMap.get(body[lenB2]));
		}
		System.out.println("Header is  "+header);
		//System.out.println("Body is  "+bodyS);

		compfile=header+bodyS;
		//System.out.println("Number of bits of input Before Adding padding : "+compfile.length());
		int numOfzeros=8-( (compfile.length()+3) % 8);
			if(numOfzeros==8)
				numOfzeros=0;
		//System.out.println( "num of zeros = "+ numOfzeros );
		String numOfzerosb =Integer.toBinaryString(numOfzeros);
		while(numOfzerosb.length()<3) {
			numOfzerosb='0'+numOfzerosb;
		}
		compfile=numOfzerosb+compfile;
		//System.out.println("Number of bits of After Adding padding : "+compfile.length());
		for(int numZ = 0 ;numZ<numOfzeros;numZ++) {
			compfile=compfile+"0";
		}
		//System.out.println(  "Number of bits of After Adding zeros : "+compfile.length());
		//System.out.println("bits of output file is  "+compfile);

	}
	public static void writeToCompressfile() throws IOException {
	byte[] fileArr = new byte[compfile.length()/8];
	FileOutputStream writerOut = new FileOutputStream(DeCompFile);
	String s1;
	int s2=0;
	Integer sTemp;
	for(int sFile=0;sFile<compfile.length();sFile=sFile+8) {
		s1=compfile.substring(sFile, sFile+8);
		sTemp = Integer.parseUnsignedInt(s1, 2);
		fileArr[s2]=sTemp.byteValue();
		s2++;

	}
	//write in  compreesed file
	for(int s3=0 ;s3<fileArr.length;s3++) {
		writerOut.write(fileArr[s3]);

		}
	writerOut.close();
	}
	public static Long SizeOfCompFile() {
		File cfile = new File(CompFile);
		if (!cfile.exists() || !cfile.isFile()) 
			System.out.println("Error in SizeOfCompFile");
		Long sizeOfComp =cfile.length();
		return sizeOfComp;
	}
	public static Long SizeOfDeCompFile() {
		File decfile = new File(DeCompFile);
		if (!decfile.exists() || !decfile.isFile())
			System.out.println("Error in SizeOfDeCompFile");
		Long sizeOfDeComp =decfile.length();
		return sizeOfDeComp;
	}
	public static void PrintRatios(Long sizeOfComp ,Long sizeOfDeComp) {
		System.out.println("Size Of Compressed file "+sizeOfComp+" Bytes");
		System.out.println("Size Of Decompressed file " +sizeOfDeComp+" Bytes");
		float CompRatio= sizeOfComp.floatValue() / sizeOfDeComp.floatValue() *100;
	    System.out.printf("Compression Ratio %.2f %% %n"  , CompRatio  );

	}
	public static void PrintTime(long startTime) {
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Execution Time : " +elapsedTime +" ms ");
		
	}
	

	public static int getDecimal(int binary){  
	    int decimal = 0;  
	    int n = 0;  
	    while(true){  
	      if(binary == 0){  
	        break;  
	      } else {  
	          int temp = binary%10;  
	          decimal += temp*Math.pow(2, n);  
	          binary = binary/10;  
	          n++;  
	       }  
	    }  
	    return decimal;  
	}
	
	
	public static void ReadFromDecomp() throws IOException {
		try {
			bodyDe=Files.readAllBytes(Paths.get(DeCompFile));
		} catch(IOException e){
			e.printStackTrace();	
		}
	    System.out.println( "1 ");

		StringBuilder outputDe= new StringBuilder("");
		for (int lenB=0 ; lenB<bodyDe.length;lenB++) { 

		outputDe =outputDe.append(Integer.toBinaryString((bodyDe[lenB] & 0xFF) + 0x100).substring(1));
		}
	    //System.out.println( "Output from compressed "+ outputDe );
	    System.out.println( "2 ");

		String padding = (String) outputDe.subSequence(0,3);
	    System.out.println( "Padding "+ padding );
	    reader =(String) outputDe.substring(3, header.length()+3);
	   // System.out.println( "Reader :  "+ reader );
	    // padding 
	    System.out.println( "3 ");
	   int Binaryint = Integer.parseInt(padding);
	   int paddecimal = getDecimal(Binaryint);
	   System.out.println("padding in decimal =  " +  paddecimal );
	   DecompB= (String)outputDe.substring(header.length()+3,outputDe.length()-paddecimal);
	   //System.out.println("Body in decompress =  " +  DecompB );


	   
	}
		

		public static HuffmanNode ReadNode()
		{ 
		    if (reader.charAt(0) == '1')
		    {			    	
		    	reader=reader.substring(1);
		    	while(reader.length()<7) {
		    		reader="0"+reader;
		    	}
		    
		    	Integer Temp;		    	
		    	Temp = Integer.parseUnsignedInt(reader.substring(0,7),2);
		
		    	reader=reader.substring(7);
			 //  System.out.println( "Temp.byteValue() :  "+ Temp.byteValue());
		        return new HuffmanNode(Temp.byteValue());
		    }
		    else
		    {
		    	reader=reader.substring(1);
		    	HuffmanNode leftChild = ReadNode();
		    	HuffmanNode rightChild = ReadNode();
		        return new HuffmanNode(leftChild, rightChild);
		    }
		     
		}
		
		
		public static void printDecompTree(HuffmanNode root, String s) 
		{ 

			if (root.left 
					== null
				&& root.right 
					== null) { 

				System.out.println((char)root.c + ":" + s);
				DecompMap.put(s, root.c);
				return; 
			} 
			printDecompTree(root.left, s + "0"); 
			printDecompTree(root.right, s + "1"); 
		} 

		public static StringBuilder GetDecompress() {
			 StringBuilder out=new StringBuilder("");
			 Byte s1;
			 char s2;
			 String s;
		 	s=Character.toString(DecompB.charAt(0));
		    //System.out.println( "1 ");
			 	for(int lenD=1 ; lenD< DecompB.length();lenD++){
					if(DecompMap.containsKey(s)) {
						s1=DecompMap.get(s);
						int s3=s1;
						s2=(char)s3;
						out=out.append(s2);
						s="";
						}
			 	s=s+Character.toString(DecompB.charAt(lenD));
			 	// for last iteration
			 	if (lenD==DecompB.length()-1 && DecompMap.containsKey(s) ) {
			 		s1=DecompMap.get(s);
					int s3=s1;
					s2=(char)s3;
					out=out.append(s2);
					s="";		
			 		}
			 	}
			 //	System.out.println(out);
			 	return out;
			 
			
		}
		public static void WriteDecompress(StringBuilder out) throws IOException {
			PrintWriter writerDe = new PrintWriter(OutDeCompFile);
			writerDe.print(out);
			writerDe.close();
			
		}
		public static int Choice() {
			int x ;
			System.out.print("Choice 1 for Compreess --- 2 for Decompress ");
	    	x = s.nextInt();
	    	System.out.println("You entered " + x);
	    	return x;
		}
		public static String ChoiceFile(int x1) {
			String x ;
			if(x1==1) {
			System.out.print("Choice File To Be Compressed ");
	    	x = s.next();
	    	//System.out.println("You Want Compress " + x);
	    	return x;
	    	}
			else if(x1==2)
			{
				System.out.print("Choice File Compress To " );
		    	x = s.next();
		    	//System.out.println("You Want Compress to " + x);
		    	return x;
		    }
			else if(x1==3)
			{
				System.out.print("Choice file to Decompress " + DeCompFile+" to");
		    	x = s.next();
		    	System.out.println("You Want Deompress " + x);
		    	return x;
		    }
			return null;
		}
	// main function 
	public static void main(String[] args) throws IOException 
	{ 
    	CompFile=ChoiceFile(1);
    	DeCompFile=ChoiceFile(2);
    	OutDeCompFile=ChoiceFile(3);
    	int x;
    	x=Choice();
    	long startTime = System.currentTimeMillis();
    	ReadFromInput();
    	HuffmanNode root = HuffmanF();
    	EncodeNode(root);
    	
    	// COMPRESS
		if (x==1) {
	    System.out.println("Compressing ...");
		Long sizeOfComp= SizeOfCompFile();
		compress();
		writeToCompressfile();
		Long sizeOfDeComp =SizeOfDeCompFile();
		PrintRatios(sizeOfComp,sizeOfDeComp);
		PrintTime(startTime);
		}
		else if (x==2) {
		// DECOMPRESS
		System.out.println("Decompressing ...");
		ReadFromDecomp();
		HuffmanNode DecompRoot= ReadNode();
		printDecompTree(DecompRoot,"");
		StringBuilder out =GetDecompress();
		WriteDecompress(out);
		PrintTime(startTime);
		}
		//WRONG CHOICE
    	else {
	    System.out.println("You entered " + x + " Wrong Choice !1");

    	}
    	

		

	
			
	} 
} 
