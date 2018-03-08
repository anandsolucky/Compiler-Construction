import java.io.*;
import java.util.*;
class MyException extends Exception
{
	int id;
	String name;
	MyException(int id,String name)
	{
		this.id=id;
		this.name=name;
	}
	public void showException()
	{
		System.out.print("\n\n*****  Exception Occured  ******\n\nException code: "+id+" , Exception: "+name+"\n");
	}
}

class Symbol
{
	static int index=1;
	String name;
	public int address;
	
	Symbol(String name)
	{
		index++;
		this.name=name;
		address=-1;
	}
}
class Litt
{
	static int index=1;
	String name;
	public int address;
	
	Litt(String name)
	{
		index++;
		this.name=name;
		address=-1;
	}
}
class Mnemonic
{
	String opcode;
	String name;
	String type;
	String args;
	
	public void fillMnm(String line)
	{
		String token[];
		token=line.split("\t");
		for(int i=0;i<token.length;i++)
		{
			opcode=token[0];
			name=token[1];
			type=token[2];
			args=token[3];
		}
	}	
}

class Assembler
{
	
	public static void main(String args[]) throws Exception
	{
		Processing pr=new Processing();
		pr.passOne();
		pr.passTwo();
	}
	
	
}

class Processing
{
	Mnemonic mnm[]=new Mnemonic[14];
	Symbol sym[]=new Symbol[10];
	Litt litt[]=new Litt[10];
	BufferedReader br;
	BufferedWriter bw;
	int location[]=new int[100];
	int lcounter=0;
	int pool[]=new int[10];
	int poolCtr=0;
	int littCtr=0,symCtr=0,lc=0;
	int startAddress=0;
	void passOne() throws Exception
	{
		fillMnemonics();
		showMnemonics();
		bw=new BufferedWriter(new FileWriter("intermediate.txt"));
		br=new BufferedReader(new FileReader("input.txt"));
		pool[poolCtr]=1;
		String line=br.readLine();
		String token[];
		try{
		if(!(line.contains("START")))
		{
			throw new MyException(101,"Start not Found at begining");
		}
		else
		{
			System.out.print("\n\n========  INPUT FILE  ========\n");
			while(line!=null && !(line.equals("STOP")))
			{
				token=line.split(" |,");
				for(int j=0;j<mnm.length;j++)
				{
					if(token[0].equals(mnm[j].name))
					{
						if((token.length-1)==Integer.parseInt(mnm[j].args))
						{
							if(token[0].equals("START"))
							{
								startAddress=Integer.parseInt(token[1]);
								lc=Integer.parseInt(token[1]);
								location[lcounter++]=lc;
								bw.write("("+mnm[j].type+","+mnm[j].opcode+") (C,"+token[1]+") ");
								break;
							}
							bw.write("("+mnm[j].type+","+mnm[j].opcode+") ");
							for(int i=1;i<token.length;i++)
							{
								if(token[i].equals("AREG")||token[i].equals("BREG")||token[i].equals("CREG")||token[i].equals("DREG"))
								{
									if(token[i].equals("AREG"))
										bw.write("(R,01) ");
									else if(token[i].equals("BREG"))
										bw.write("(R,02) ");
									else if(token[i].equals("CREG"))
										bw.write("(R,03) ");
									else if(token[i].equals("DREG"))
										bw.write("(R,04) ");
								}
								else if(token[i].startsWith("="))
								{
									litt[littCtr]=new Litt(token[i]);
									littCtr++;
									bw.write("(L,"+(littCtr)+") ");
								}
								else
								{
									int pos=alreadySymbol(token[i]);
									if(pos!=-1)
									{
										bw.write("(S,"+(pos+1)+") ");
									}
									else
									{
										sym[symCtr]=new Symbol(token[i]);
										symCtr++;
										bw.write("(S,"+(Symbol.index-1)+") ");
									}
								}
							}
						}
						else
						{
							throw new MyException(102,"Invalid Argument while scanning "+mnm[j].name);
							
						}
					}
					else if(token[0].equals("LTORG"))
					{
						for(int i=pool[poolCtr];i<=littCtr;i++)
						{
							litt[i-1].address=lc;
							lc++;
							
						}
						poolCtr++;
						pool[poolCtr]=littCtr+1;
						bw.write("(AD,05) ");
						break;
					}
					else if(token[0].equals("END"))
					{
						bw.write("(AD,08) ");
						break;
					}
					else if(token[1].equals("DC")||token[1].equals("DS"))
					{
						int p=alreadySymbol(token[0]);
						if(token[1].equals("DS"))
						{
							if(p!=-1)
							{
								sym[p].address=lc;
								lc=lc+((Integer.parseInt(token[2]))-1);
								bw.write("(S,"+(p+1)+") (DL,11) (C,"+token[2]+") ");
								
							}
							else
							{
								sym[symCtr]=new Symbol(token[0]);
								sym[symCtr].address=lc;
								bw.write("(S,"+(symCtr+1)+") (DL,10) (C,"+token[2]+") ");
								lc=lc+((Integer.parseInt(token[2]))-1);
								symCtr++;
							}
							location[lcounter++]=lc;
						}
						else if(token[1].equals("DC"))
						{
							if(p!=-1)
							{
								sym[p].address=lc;
								bw.write("(S,"+(p+1)+") (DL,11) (C,"+token[2]+") ");
							}
							else
							{
								sym[symCtr]=new Symbol(token[0]);
								sym[symCtr].address=lc;
								bw.write("(S,"+(symCtr+1)+") (DL,11) (C,"+token[2]+") ");
								symCtr++;
							}
							
						}
						break;
					}
					else if(token.length>1 && token[1].equals(mnm[j].name))
					{
						if((token.length-1)==Integer.parseInt(mnm[j].args))
						{
							bw.write("("+mnm[j].type+","+mnm[j].opcode+") ");
							for(int i=1;i<token.length;i++)
							{
								if(token[i].equals("AREG")||token[i].equals("BREG")||token[i].equals("CREG")||token[i].equals("DREG"))
								{
									if(token[i].equals("AREG"))
										bw.write("(R,01) ");
									else if(token[i].equals("BREG"))
										bw.write("(R,02) ");
									else if(token[i].equals("CREG"))
										bw.write("(R,03) ");
									else if(token[i].equals("DREG"))
										bw.write("(R,04) ");
								}
								else if(token[i].startsWith("="))
								{
									litt[littCtr]=new Litt(token[i]);
									littCtr++;
									bw.write("(L,"+(littCtr)+") ");
								}
								
								else
								{
									int pos=alreadySymbol(token[i]);
									if(pos!=-1)
									{
										bw.write("(S,"+(pos+1)+") ");
									}
									else
									{
										sym[symCtr]=new Symbol(token[i]);
										symCtr++;
										bw.write("(S,"+(Symbol.index-1)+") ");
									}
								}
							}
						}
						else
						{
							throw new MyException(102,"Invalid Argument while scanning "+mnm[j].name);
						}
					}
				}
				bw.newLine();
				System.out.print("\n"+line);
				line=br.readLine();
				lc++;
				location[lcounter++]=lc;
			}
		}
		br.close();
		bw.close();
		writeFileSym();
		writeFileLitt();
		writeFilePool();
	}
	catch(MyException e)
	{
		e.showException();
	}
	}
	void passTwo() throws Exception
	{
		br=new BufferedReader(new FileReader("intermediate.txt"));
		bw=new BufferedWriter(new FileWriter("MachineCode.txt"));
		int ctr=startAddress;
		String line;
		String token[],tk[];
		
		while((line=br.readLine())!=null)
		{
			token=line.split(" ");
			if(line.contains("AD"))
			{
				bw.newLine();
				ctr++;
				continue;
			}
			else if(line.contains("DL"))
			{
				int size;
				token[2]=new StringBuilder(token[2]).deleteCharAt(0).toString();
				token[2]=new StringBuilder(token[2]).deleteCharAt(0).toString();
				token[2]=new StringBuilder(token[2]).deleteCharAt(0).toString();
				size=(token[2].length())-1;
				token[2]=new StringBuilder(token[2]).deleteCharAt(size).toString();
				bw.write(ctr+")");
				ctr=ctr+Integer.parseInt(token[2]);
				bw.newLine();
				continue;
			}
			else
			{
				for(int i=0;i<token.length;i++)
				{
					tk=token[i].split(",");
					for(int j=0;j<tk.length;j++)
					{
						if(tk[j].startsWith("("))
							tk[j]=new StringBuffer(tk[j]).deleteCharAt(0).toString();
						else if(tk[j].endsWith(")"))
						{
							int pos=tk[j].length()-1;
							tk[j]=new StringBuffer(tk[j]).deleteCharAt(pos).toString();
						}
						if(j==1)
						{
							if(tk[j-1].equals("IS")||tk[j-1].equals("DL"))
							{
								bw.write(ctr+")\t+"+tk[j]);
								
							}
							else if(tk[j-1].equals("C"))
							{
								ctr=ctr+Integer.parseInt(tk[j]);
							}
							else if(tk[j-1].equals("S") && i==0)
								continue;
							else if(tk[j-1].equals("R"))
							{
								bw.write("\t"+tk[j]);
								
							}
							else if(tk[j-1].equals("S"))
							{
								int add=sym[(Integer.parseInt(tk[j]))-1].address;
								bw.write("\t"+add);
								
							}
							else if(tk[j-1].equals("L"))
							{
								int add=litt[(Integer.parseInt(tk[j]))-1].address;
								bw.write("\t"+add);
								
							}
							
						}
					}
				}
				ctr++;
				bw.newLine();
				
			}
			
		}
		br.close();
		bw.close();
	}
	
	void writeFileSym() throws Exception
	{
		bw=new BufferedWriter(new FileWriter("SymbolTable.txt"));
		for(int i=0;i<Symbol.index-1;i++)
		{
			bw.write((i+1)+"\t"+sym[i].name+"\t"+sym[i].address);
			bw.newLine();
		}
		bw.close();
	}
	
	void writeFileLitt() throws Exception
	{
		bw=new BufferedWriter(new FileWriter("LitteralTable.txt"));
		for(int i=0;i<Litt.index-1;i++)
		{
			bw.write((i+1)+"\t"+litt[i].name+"\t"+litt[i].address);
			bw.newLine();
		}
		bw.close();
	}
	
	void writeFilePool() throws Exception
	{
		bw=new BufferedWriter(new FileWriter("PoolTable.txt"));
		for(int i=0;i<=poolCtr;i++)
		{
			bw.write(pool[i]+"");
			bw.newLine();
		}
		bw.close();
	}
	
	
	int  alreadySymbol(String s)
	{
		int pos=0;
		for(int i=0;i<symCtr;i++)
		{
			if(s.equals(sym[i].name))
				return pos;
		}
		return -1;
	}
	
	void fillMnemonics() throws Exception
	{
		br=new BufferedReader(new FileReader("Mnemonics.txt"));
		String line=br.readLine();
		String token[];
		int i=0;
		while(line!=null)
		{
			token=line.split("\t");
			mnm[i]=new Mnemonic();
			mnm[i].fillMnm(line);
			i++;
			line=br.readLine();
		}
		br.close();
	}
	void showMnemonics()
	{
		System.out.print("\n=========  Mnemonics  =========\n");
		for(int i=0;i<mnm.length;i++)
		{
			System.out.print("\n"+mnm[i].opcode+"\t"+mnm[i].name+"\t"+mnm[i].type+"\t"+mnm[i].args);
		}
	}
}