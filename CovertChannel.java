import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;


public class CovertChannel {
	//method to determind whether or not a string represents an integer number
	public static boolean isInt(String s){
		try{
			Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException nfe){
			return false;
		}

	}
	//main method...will create the objects, parse the input, then send execute command 
	//to the ReferenceMonitor class
	public static void main(String[] args) throws IOException {

		//create security levels
		ReferenceMonitor.SecurityLevel low = ReferenceMonitor.SecurityLevel.createNewSecurityLevel("low");
		ReferenceMonitor.SecurityLevel high = ReferenceMonitor.SecurityLevel.createNewSecurityLevel("high");

		//create 2 secure subjects, from assignment sheet
		SecureSubject Lyle = new SecureSubject("Lyle", low, 0);
		SecureSubject Hal = new SecureSubject("Hal", high, 0);

		//create array list to whole instructions when parsed
		ArrayList<SecureObject> all = new ArrayList<SecureObject>();
		boolean verbose = false;
		int returned;
		String binary = "";
		Random generator = new Random();
		//String test = "Hello World, there is really no reason to keep typing other than to test that this actually works for larger strings";

		//create input vars
		FileInputStream fstream;
		DataInputStream din;
		BufferedReader br;
		String outputFilename;
		File file;
		//checking for verbose tag
		if(args.length == 2){
			fstream = new FileInputStream(args[1]);
			din = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(din));
			outputFilename = args[1] + ".out";
			file = new File(args[1]);
			verbose = true;
		}
		else{
			fstream = new FileInputStream(args[0]);
			din = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(din));
			outputFilename = args[0] + ".out";
			file = new File(args[0]);
		}


		//output setup
		FileWriter outFile = new FileWriter(outputFilename);
		PrintWriter log = new PrintWriter("log");
		FileOutputStream out = new FileOutputStream(outputFilename);
		Stopwatch sw = new Stopwatch();
		sw.start();
		String strLine;
		//checking newlines
		boolean firstLine = true;
		while((strLine = br.readLine()) != null){
			if(firstLine){
				firstLine = false;
			}
			else{
				//was testing under windows, therefore had to watch for the '\r' special
				//out.write('\r');
				out.write('\n');
			}
			//get first line into byte array
			byte[] buf = strLine.getBytes();
			InputStream in = new ByteArrayInputStream(buf);
			//SecureObject hard = new SecureObject("OBJECT", low);
			int c;
			while((c = in.read()) != -1){
				String s = Integer.toString(c, 2);
				//for some reason cuts off leading zero, really just for debugging for myself
				if(s.length() == 6){
					s = "00" + s;
				}
				else{
					s = "0" + s;
				}

				//now we get into the actualy running...as per the handout this covert channel
				//is based on the creation or non-creation of a highlevel object, then a low
				//level subject attempting to write to it.  If I want to transmit a 1, the low
				//level creats, if a 0 viceversa
				for(int n = 0; n < s.length(); n++){
					//create random value for lowbie to try and write
					int r = generator.nextInt();
					if(s.charAt(n) == '1'){
						//System.out.println("ONE!");
						Instruction run = new Instruction("RUN", "HAL");
						Instruction create = new Instruction("CREATE", "HAL", true);
						Instruction read = new Instruction("READ", "Lyle", "CovertObject");
						Instruction write = new Instruction("WRITE", "Lyle", "CovertObject", r);
						Instruction destroy = new Instruction("DESTROY", "Lyle", "CovertObject");
						SecureObject created = ReferenceMonitor.ObjectManager.createNewObject("CovertObject", Lyle.getLevel(), 0);
						ReferenceMonitor.executeRun(run, Hal, verbose, log);
						ReferenceMonitor.executeCreate(created, create, Lyle, all, verbose, log);
						ReferenceMonitor.executeWrite(write, Lyle, created, verbose, log);
						returned = ReferenceMonitor.executeRead(read, Lyle, created, verbose, log);
						ReferenceMonitor.executeDestroy(destroy, Lyle, created, all, verbose, log);
						ReferenceMonitor.executeRun(run, Lyle, verbose, log);
					}
					else{
						//System.out.println("ZERO!");
						Instruction run = new Instruction("RUN", "HAL");
						Instruction create = new Instruction("CREATE", "HAL", true);
						Instruction read = new Instruction("READ", "Lyle", "CovertObject");
						Instruction write = new Instruction("WRITE", "Lyle", "CovertObject", r);
						Instruction destroy = new Instruction("DESTROY", "Lyle", "CovertObject");
						SecureObject created = ReferenceMonitor.ObjectManager.createNewObject("CovertObject", Hal.getLevel(), 0);
						ReferenceMonitor.executeRun(run, Hal, verbose, log);
						ReferenceMonitor.executeCreate(created, create, Hal, all, verbose, log);
						ReferenceMonitor.executeCreate(created, create, Lyle, all, verbose, log);
						ReferenceMonitor.executeWrite(write, Lyle, created, verbose, log);
						returned = ReferenceMonitor.executeRead(read, Lyle, created, verbose, log);
						ReferenceMonitor.executeDestroy(destroy, Lyle, created, all, verbose, log);
						ReferenceMonitor.executeRun(run, Lyle, verbose, log);
					}
					//build a binary string for each char from returned results of the read
					if(returned != 0){
						binary = binary + "1";
					}
					else{
						binary = binary + "0";
					}

				}
				//get decimal version of binary and then output the char associated with it on the ascii table
				int decimal = Integer.parseInt(binary, 2);
				out.write((char)decimal);
				binary = "";
			}
		}
		sw.stop();
		//stop the stopwatch and print out timing results
		System.out.println("Time elapsed = " + sw.time() + " seconds");
		System.out.println("File size = " + file.length() + " bits");
		System.out.println("Bandwidth = " + file.length()/sw.time() + " bits/second");
	}
}

//Instruction class and helper methods
class Instruction{
	public String op;
	public String sub;
	public String obj;
	public int val = 0;
	//int to determine op... 1 = read, 2 = write, 3 = create, 4 = destroy, 5 = run;
	public int toggle = 0;
	//boolean value to let us know if the instruction is a read or not
	public boolean isR;
	public boolean isC;
	//default constructor
	public Instruction(){
		
	}
	public Instruction(String run, String subject){
		op = run;
		sub = subject;
		toggle = 5;
	}
	//constructor for a create, sets boolean value to true if create, false if destroy
	public Instruction(String create, String subject, boolean cflag){

		op = create;
		sub = subject;
		val = 0;
		if(create.equalsIgnoreCase("create")){
			toggle = 3;
		}
		else{
			toggle = 4;
		}
	}
	//constructor for a read, sets boolean value to true
	public Instruction(String read, String subject, String object){
		op = read;
		sub = subject;
		obj = object;
		isR = true;
		toggle = 1;
	}
	//constructor for a write, sets boolean value to false
	public Instruction(String write, String subject, String object, int value){
		op = write;
		sub = subject;
		obj = object;
		val = value;
		isR = false;
		toggle = 2;
	}
	//get the current instructions value
	public int getVal(){
		return val;
	}
	public int getToggle(){
		return toggle;
	}
	//get the current instructions subject
	public String getSub(){
		return sub;
	}
	//get the current instructions object
	public String getObj(){
		return obj;
	}
	//return whether it is a read or write instruction
	public boolean isRead(){
		return this.isR;
	}
	public boolean isCreate(){
		return this.isC;
	}
	//print method for instruction class
	public void print(){
		if(op.equalsIgnoreCase("read")){
			System.out.println(sub + " " + op + "s " + obj);
		}
		else{
			System.out.println(sub + " " + op + "s value " + val + " to " + obj);
		}
	}
}
//BadInstruction class and helper methods
class BadInstruction{
	public String error;
	//default constructor for BadInstruction, since only the output
	//saying there was a bad instruction is required, thats all it does...
	public BadInstruction(){
		error = "Bad Instruction";
	}
	//print method for bad instruction
	public void print(){
		System.out.println("Bad Instruction");
	}
}
//SecureSubject class and helper methods
class SecureSubject{
	public String n;
	public ReferenceMonitor.SecurityLevel l;
	public int v = 0;
	//default constructor
	public SecureSubject(){
		
	}
	//create a new securesubject, if value isn't passed, value stays at 0
	public SecureSubject(String name, ReferenceMonitor.SecurityLevel level, int value){
		n = name;
		l = level;
		v = value;
	}
	//access current object value
	public int getVal(){
		return this.v;
	}
	//access current object name
	public String getName(){
		return this.n;
	}
	//set current object value
	public void setVal(int x){
		this.v = x;
	}
	//ping referencemonitor to get current level
	public ReferenceMonitor.SecurityLevel getLevel(){
		return this.l;
	}
	//notused
	//not used in strong tranq
	public void updateLevel(int x){
		this.v = x;
	}
}
//SecureObject class and helper methods
class SecureObject{
	public int v = 0;
	public String n;
	public ReferenceMonitor.SecurityLevel l;
	//default constructor
	public SecureObject(){
		
	}
	//constructor for a read object
	public SecureObject(String name, ReferenceMonitor.SecurityLevel level){
		v = 0;
		n = name;
		l = level;
	}
	//constructor for a write object
	public SecureObject(String name, ReferenceMonitor.SecurityLevel level, int value){
		v = value;
		n = name;
		l = level;
	}

	//get current value of object
	public int getVal(){
		return this.v;
	}
	//get current name of object
	public String getName(){
		return this.n;
	}
	//set the current value of object
	public void setVal(int x){
		this.v = x;
	}
	//access referencemonitor to get the current level of object
	public ReferenceMonitor.SecurityLevel getLevel(){
		return this.l;
	}
	//notused
	public void updateLevel(int x){
		this.v = x;
	}
}
//ReferenceMonitor class and helper methods inc. ObjectManager class and the SecurityLevel class
class ReferenceMonitor{
	Instruction instr;
	static Integer counter = 0;
	//create the holder for all instructions...
	//default constructor
	public ReferenceMonitor(){
		
	}
	//giving it an actual instruction
	public ReferenceMonitor(Instruction i){
		instr = i;
	}
	//object manager class and helpers including methods to create secure objects...creates
	//a read or write based on whether or not it is given a value
	static class ObjectManager{
		public String name;
		public SecurityLevel level;
		public SecureObject obj;
		public ObjectManager(){
			
		}
		//get the value of the object
		public int returnObjectVal(SecureObject s){
			return s.getVal();
		}
		//create new object, read object
		public static SecureObject createNewObject(String n, SecurityLevel l){
			SecureObject obj = new SecureObject(n, l);
			return obj;
		}
		//create new object, write object
		public static SecureObject createNewObject(String n, SecurityLevel l, int val){
			SecureObject obj = new SecureObject(n, l, val);
			return obj;
		}
		
	}
	//executes instruction, fed our instruction object and some need-to-know variables
	public static void execute(String name, Instruction i, SecureSubject sub, SecureObject obj, ArrayList<SecureObject> all, boolean verbose, PrintWriter out){
		if(i.getToggle() == 1){
			executeRead(i, sub, obj, verbose, out);
		}
		else if(i.getToggle() == 2){
			executeWrite(i, sub, obj, verbose, out);
		}
		else if(i.getToggle() == 3){
			executeCreate(obj, i, sub, all, verbose, out);			
		}
		else if(i.getToggle() == 4){
			executeDestroy(i, sub, obj, all, verbose, out);
		}
		else if(i.getToggle() == 5){
			executeRun(i, sub, verbose, out);
		}
		else{
			System.out.println("SYSTEM FAILURE");
		}
	}
	
	//executes a create instruction
	public static void executeCreate(SecureObject obj, Instruction i, SecureSubject sub, ArrayList<SecureObject> all, boolean verbose, PrintWriter out){
		//if verbose output to file
		if(all.contains(obj)){
			if(verbose){
				out.println(i.op + " " + sub.getName() + " " + obj.getName());
			}
		}
		else{
			if(verbose){
				out.println(i.op + " " + sub.getName() + " " + obj.getName());
			}
			all.add(obj);
		}
	}
	
	
	//executes a destroy instruction
	public static void executeDestroy(Instruction i, SecureSubject sub, SecureObject obj, ArrayList<SecureObject> all, boolean verbose, PrintWriter out){
		//if verbose, output to file
		if(obj.getLevel().wallowed(sub.getLevel().getLevel())){
			if(verbose){
				out.println(i.op  + " " + sub.getName() + " " + obj.getName());
			}
			all.remove(obj);
		}
		else{
			if(verbose){
				out.println(i.op  + " " + sub.getName() + " " + obj.getName());
			}
		}
	}
	//executes a run instruction
	public static void executeRun(Instruction i, SecureSubject sub, boolean verbose, PrintWriter out){
		//if verbose output to file
		if(sub.getName().equalsIgnoreCase("Lyle")){	
			if(verbose){
				out.println(i.op + " " + sub.getName());
			}
		}
		else{
			if(verbose){
				out.println(i.op + " " + sub.getName());
			}
		}
	}
	//executes a read instruction
	public static int executeRead(Instruction i, SecureSubject sub, SecureObject obj, boolean verbose, PrintWriter out){
		//if verbose output to file
		if(obj.getLevel().rallowed(sub.getLevel().getLevel())){
			if(verbose){
				out.println(i.op + " " + sub.getName() + " " + obj.getName());
			}
			return obj.getVal();
		}
		else{
			if(verbose){
				out.println(i.op + " " + sub.getName() + " " + obj.getName());
			}
			return 0;
		}

	}

	//executes a write instruction, first checks to see if it is allowed by the program constraints
	public static void executeWrite(Instruction i, SecureSubject sub, SecureObject obj, boolean verbose, PrintWriter out){
		//if verbose output to file
		if(obj.getLevel().wallowed(sub.getLevel().getLevel())){
			if(verbose){
				out.println(i.op + " " + sub.getName() + " " + obj.getName() + " " + i.getVal());
			}
			obj.v = i.getVal();
		}
		else{
			if(verbose){
				out.println(i.op + " " + sub.getName() + " " + obj.getName() + " " + i.getVal());
			}
		}
	}
	//prints the current states/references
	public static void printState(SecureSubject sub, SecureObject obj){
		System.out.println("\t" + obj.getName() + " has the value: " + sub.getVal());
		System.out.println("\t" + sub.getName() + " has recently read: " + sub.getVal());
	}
	static class SecurityLevel{
		public int level;
		//default constructor
		public SecurityLevel(){
			
		}
		//non default constructor, assigned integer values for security levels, high being 9, low being 1..can add in arbitrary 
		//amount of levels depending on system design
		public SecurityLevel(String s){
			if(s.equalsIgnoreCase("low")){
				level = 1;
			}
			else if(s.equalsIgnoreCase("high")){
				level = 9;
			}
			else{
				level = 0;
			}
		}
		//create a new security level object
		public static SecurityLevel createNewSecurityLevel(String n){
			SecurityLevel level = new SecurityLevel(n);
			return level;
		}
		//get security level
		public int getLevel(){
			return level;
		}
		//get system low level
		public int getLow(){
			level = 1;
			return level;		
		}
		//get system high level
		public int getHigh(){
			level = 9;
			return level;
		}
		//is a write allowed?  send in users level and check on the securitylevel
		//only allowed tow write up or same level - as explained in BLP, * and 
		//whatnot from notes
		public boolean wallowed(int x){
			if(x <= this.level){
				return true;
			}
			else{
				return false;
			}
		}
		//is a read allowed?  send in users level and check on the securitylevel
		//only allowed to read down or same level - as outlined in class notes
		public boolean rallowed(int x){
			if (x >= this.level){
				return true;
			}
			else{
				return false;
			}
		}
	}
}
