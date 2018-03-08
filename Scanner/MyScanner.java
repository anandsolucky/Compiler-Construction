import java.io.*;
import java.util.*;
//import java.lang.*;
public class MyScanner
{
	public static void main(String args[]) throws Exception
	{
		Scanner scn=new Scanner();
		scn.initkeyword();
		scn.initoperator();
		System.out.println("\nKeyword are: ");
		scn.showkeywords();
		System.out.println("\nOperators are: ");
		scn.showoperators();
		System.out.println("_____________________________________________________");
		scn.initstate();
		scn.showstate();
		scn.scancode();
	}
}
class Scanner
{
	String keywords[]=new String[23];
	String operators[]=new String[9];
	int states[][]=new int[9][8];
	String brackets [] = {"[","]","{","}","(",")"};
	BufferedReader br;
	void initkeyword() throws Exception
	{
		br=new BufferedReader(new FileReader("keywords.txt"));
		String liner;
		int i=0;
		liner=br.readLine();
		while(liner!=null)
		{
			keywords[i]=liner;
			liner=br.readLine();
			i++;
		}
		br.close();
	}
	
	void initoperator() throws Exception
	{
		br=new BufferedReader(new FileReader("operators.txt"));
		String liner;
		int i=0;
		liner=br.readLine();
		while(liner!=null)
		{
			operators[i]=liner;
			liner=br.readLine();
			i++;
		}
		br.close();
	}
	
	void initstate() throws Exception
	{
		
		br=new BufferedReader(new FileReader("a_states.txt"));
		String ar_state[]=null;
		String liner;
		int i=0;
		liner=br.readLine();
		
		while(liner!=null)
		{
			ar_state=liner.split("\t");
			for(int j=0;j<ar_state.length;j++)
			{
				states[i][j] = Integer.valueOf(ar_state[j]);
			}
			liner=br.readLine();
			i++;
		}
		br.close();
	}
	
	void showstate() 
	{
		System.out.println("\n\t\t\t   STATE TABLE\n");
		System.out.println("State \tDigit \tChar \t' \t\"");
		System.out.println("------------------------------------------------------------------");
		for(int i=0;i<states.length;i++)
		{
			for(int j=0;j<states[i].length;j++)
			{
				System.out.print(states[i][j]+"\t");
			}
			System.out.println();
		}
	}
	void showkeywords()
	{
		for(int i=0;i<keywords.length;i++)
		{
			System.out.println(keywords[i]);
		}
	}
	
	void showoperators()
	{
		for(int i=0;i<operators.length;i++)
		{
			System.out.println(operators[i]);
		}
	}
	boolean iskeyword(String token)
	{
		for(int i=0;i<keywords.length;i++)
		{
			if(keywords[i].equals(token))
				return true;
		}
		return false;
	}
	
	boolean isoperator(String token)
	{
		for(int i=0;i<operators.length;i++)
		{
			if(operators[i].equals(token))
				return true;
		}
		return false;
	}
	
	boolean isbracket(String token)
	{
		for(int i=0;i<brackets.length;i++)
		{
			if(brackets[i].equals(token))
				return true;
		}
		return false;
	}
	
	int getstate(int cState,char chr)
	{
			int nextstate=-1;
			int col;
			if(Character.isLetter(chr))
				col=2;
			else if(Character.isDigit(chr))
				col=1;
			else if(chr=='"')
				col=4;
			else if(chr=='+' || chr=='-')
				col=7;
			else if(chr=='_')
				col=6;
			else if(chr=='.')
				col=5;
			else if(Character.toString(chr).equals("'"))
				col=3;
			else 
				col=-1;
			if(col!=-1)
			{
				nextstate=states[cState-1][col];
			}
			return nextstate;
	}
	
	void scancode() throws Exception
	{
		
		br=new BufferedReader(new FileReader("input.txt"));
		String liner;
		String tok;
		String tokenarr[]=null;
		liner=br.readLine();
		while(liner!=null)
		{
			
			System.out.println("===============================================================");
			System.out.println("The line is: "+liner);
			System.out.println("===============================================================");
			tokenarr=liner.split(" ");
			for(int i=0;i<tokenarr.length;i++)
			{
				tok=tokenarr[i];
				
				if(iskeyword(tok))
					System.out.println(tok+" is a Keyword");
				else if(isoperator(tok))
					System.out.println(tok+ " is an Operator");
				else if(isbracket(tok))
					System.out.println(tok+ " is a bracket");
				else if(tok.equals(";"))
					System.out.println(tok+ " is end of statement");
				else if(tok.startsWith("."))
					System.out.println(tok+ " is invalid token");
				else
				{
					char cToken[]= tok.toCharArray();
					int cState=1,nState;
					for(int j=0;j<cToken.length;j++)
					{
						nState=getstate(cState,cToken[j]);
						cState=nState;
						if(cState==-1)
						{
						System.out.println("In the break section");	
						break;
						}
					}
					
					if(cState==2)
						System.out.println(tok+" is Integer");
					else if(cState==5)
						System.out.println(tok+" is a Single Character");
					else if(cState==7)
						System.out.println(tok+" is a String");
					else if(cState==8)
						System.out.println(tok+" is an identifier");
					else if(cState==9)
						System.out.println(tok+" is a floating point number");
					else
						System.out.println(tok+" is invalid token");
				}
				
			}
			liner=br.readLine();
		}
		br.close();
	}
	
}