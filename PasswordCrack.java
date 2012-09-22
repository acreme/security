import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PasswordCrack {
	public static final List<String> augdic = new ArrayList<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	//gets the necessary fields from in input line
	public static List<String> entry(String inputLine){
		List<String> entry = new ArrayList<String>();
		char delim = ':';
		int count = 0;
		String pass = inputLine.substring(inputLine.indexOf(delim)+1, inputLine.indexOf(delim)+14);
		String salt = pass.substring(0, 2);
		String epswd = pass.substring(2,pass.length());
		String chopper = inputLine.substring(inputLine.indexOf(delim) + 14, inputLine.length());
		while(count < 3){
			chopper = chopper.substring(chopper.indexOf(delim)+1, chopper.length());
			count++;
		}
		String fullname = chopper.substring(0, chopper.indexOf(delim));
		char space = ' ';
		String fname = fullname.substring(0, fullname.indexOf(space));
		String lname = fullname.substring(fullname.indexOf(space)+1, fullname.length());
		
		entry.add(pass);
		entry.add(salt);
		entry.add(epswd);
		entry.add(fname);
		entry.add(lname);
		
		return entry;		
	}
	//reverses a string
	public static String reverse(String x){
		int length = x.length();
		StringBuffer ret = new StringBuffer(length);

		for (int i = length - 1; i >= 0; i--){
			ret.append(x.charAt(i));
		}
		return ret.toString();
	}
	//alternates caps on even elements of strings
	public static String alternate(String x){
		String temp = "";
		for(int i = 0; i < x.length(); i++){
			if(i%2 == 0){
				temp = temp + (Character.toUpperCase(x.charAt(i)));
			}
			else{
				temp = temp + x.charAt(i);
			}
		}
		return temp;
	}
	//alternates caps on odd elements of string
	public static String alternate2(String x){
		String temp = "";
		for(int i = 0; i < x.length(); i++){
			if(i%2 != 0){
				temp = temp + (Character.toUpperCase(x.charAt(i)));
			}
			else{
				temp = temp + x.charAt(i);
			}
		}
		return temp;
	}
	//mangle method, uses the mangles given in project assignment
	public static String mangle(String x, int i, int n){
		String temp = "";
		temp = x;
		char first;
		String p;
		switch (i) {
		case 0:
			temp = temp.toUpperCase();
			break;
		case 1:
			temp = temp.toLowerCase();
			break;
		case 2:
			temp = temp.substring(1, temp.length());
			break;
		case 3:
			temp = temp.substring(0, temp.length()-1);
			break;
		case 4:
			temp = reverse(temp);
			break;
		case 5:
			temp = temp + temp;
			break;
		case 6:
			temp = temp + reverse(temp);
			break;
		case 7:
			temp = reverse(temp) + temp;
			break;
		case 8:
			temp = alternate(temp);
			break;
		case 9:
			temp = alternate2(temp);
			break;
		case 10:
			first = temp.charAt(0);
			first = Character.toUpperCase(first);
			p = temp.substring(1, temp.length());
			temp = first + p;
			break;
		case 11:
			temp = temp.toLowerCase();
			first = temp.charAt(0);
			p = temp.substring(1, temp.length());
			p = p.toUpperCase();
			temp = first + p;
			break;
		case 12:
			temp = (char)n + temp;
			break;
		case 13:
			temp = temp + (char)n;
			break;
		default:
			break;
		}

		return temp;
	}
	
	public static String check(List<String> user, List<String> dictionary){
		String pass = user.get(0);
		String salt = user.get(1);
		String epswd = user.get(2);
		String toTest = "";
		String mangled = "";
		//first pass, just check dictionary
		for(int i = 0; i < dictionary.size(); i++){
			toTest = jcrypt.crypt(salt, dictionary.get(i));
			String x = dictionary.get(i);
			if(toTest.equalsIgnoreCase(pass)){
				return(x);
			}
			//otherwise start mangling!!
			else{
				for(int j = 0; j < 14; j++){
					if(j == 12 || j == 13){
						for(int n = 33; n < 127; n++){
							mangled = mangle(x, j, n);
							augdic.add(mangled);
							toTest = jcrypt.crypt(salt, mangled);
							if(toTest.equalsIgnoreCase(pass)){
								return mangled;
							}

						}
					}
					else{
						mangled = mangle(x, j, 0);
						augdic.add(mangled);
						toTest = jcrypt.crypt(salt, mangled);
						if(toTest.equalsIgnoreCase(pass)){
							return mangled;
						}
					}
				}
				//mangle dictionary.get(i)
			}
		}
		//first round of mangling doesn't return pword, return this;
		return null;
	}
	
	public static String checkMore(List<String> user, List<String> dictionary){
		String pass = user.get(0);
		String salt = user.get(1);
		String epswd = user.get(2);
		String toTest = "";
		String mangled = "";
		//first pass, just check dictionary
		for(int i = 0; i < dictionary.size(); i++){
			toTest = jcrypt.crypt(salt, dictionary.get(i));
			String x = dictionary.get(i);
			if(toTest.equalsIgnoreCase(pass)){
				return(x);
			}
			//otherwise start mangling!!
			else{
				for(int j = 0; j < 14; j++){
					if(j == 12 || j == 13){
						for(int n = 33; n < 127; n++){
							mangled = mangle(x, j, n);
				
							toTest = jcrypt.crypt(salt, mangled);
							if(toTest.equalsIgnoreCase(pass)){
								return mangled;
							}

						}
					}
					else{
						mangled = mangle(x, j, 0);
					
						toTest = jcrypt.crypt(salt, mangled);
						if(toTest.equalsIgnoreCase(pass)){
							return mangled;
						}
					}
				}
				//mangle dictionary.get(i)
			}
		}
		//first round of mangling doesn't return pword, return this;
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		String iLine;
		String dLine;
		//setting up inputs
		String dictionary = args[0];
		String inputFile = args[1];
		
		List<String> aLine = new ArrayList<String>();
		//dictionary list
		List<String> dic = new ArrayList<String>();
		//put dictionary into an arraylist
		BufferedReader d = new BufferedReader(new FileReader(dictionary));
		while((dLine = d.readLine()) != null){
			dic.add(dLine);
		}
//		stopwatch used for timing, removed for final submission
//		Stopwatch sw = new Stopwatch();
		//get a line for a user
		BufferedReader i = new BufferedReader(new FileReader(inputFile));
//		sw.start();
		while((iLine = i.readLine()) != null){
			aLine = entry(iLine);
			dic.add(aLine.get(3));
			dic.add(aLine.get(4));
			String result = check(aLine, dic);
			if(result == null){
				System.out.println(checkMore(aLine, augdic));
			}
			else{
				System.out.println(result);
			}			
		}
//		sw.stop();
//		System.out.println("Time elapsed = " + sw.time() + " seconds");
	}
}

