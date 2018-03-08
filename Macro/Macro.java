import java.io.*;

class Macro
{
	public static void main(String args[]) throws Exception
	{
		MacroProcessor mp= new MacroProcessor();
		mp.scanProgram();
	}
}
class MDT
{
	public int lc;
	public String val;
}

class MNT
{
	String name;
	int kp=0;
	int pp=0;
	int ev=0;
	int mdtp=1;
	int kpdtp=1;
	public int sstp=1;
	
	public int  getParamLen()
	{
		return kp+pp;
	}
	public int getMDTP()
	{
		return mdtp;
	}
	public String  getName()
	{
		return name;
	}
	public int  getppLen()
	{
		return pp;
	}
	public int  getkpLen()
	{
		return kp;
	}
	public int getEVLen()
	{
		return ev;
	}
	public void dispmnt()
	{
		System.out.print("\n\n================================================================");
		System.out.print("\nName\t\tKP#\tPP#\tEV#\tmdtp\tkpdtp\tsstp");
		System.out.print("\n"+name+"\t"+kp+"\t"+pp+"\t"+ev+"\t"+mdtp+"\t"+kpdtp+"\t"+sstp);
		System.out.print("\n================================================================\n");
	}
}

class KPDTAB
{
	static int index;
	String param;
	String value;
	
	public void addKPDTAB(String pm,String val)
	{
		index++;
		param=pm;
		value=val;

	}
	public String getVal()
	{
		return value;
	}
	public void disp()
	{
		System.out.print("\n"+index+"\t"+param+"\t"+value);
	}
}
class SSTAB
{
	int index;
	int address;
	
	public void addSSTAB(int ind,int ad)
	{
		index=ind;
		address=ad;
	}
	public void disp()
	{
		System.out.print("\n"+index+"\t"+address);
	}
	public int getAddress()
	{
		return address;
	}
}

class MacroProcessor
{
	BufferedReader br;
	BufferedWriter bw;
	MNT mnt[]=new MNT[5];
	String PNTAB[]=new String[12];
	String EVNTAB[]=new String[12];
	String SSNTAB[]=new String[12];
	SSTAB seqSym[]=new SSTAB[12];
	KPDTAB kpdtab[]=new KPDTAB[12];
	MDT mdt[]=new MDT[12];
	int SSTAB[]=new int[12];
	String APTAB[]=new String[10];
	String EVTAB[]=new String[10];
	int aptab_ctr=1,evtab_ctr=1,MEC=1,mdtCtr=1;
	int pntab_ctr=0;
	int mCtr=0;
	int MDT_ptr=1;
	int ev_ctr=1;
	int ss_ctr=0,seqSymCtr=0,kpdtab_ctr=0;
	
	/* Necessary Functions */
	int isParam(String param)
	{
		for(int i=0;i<PNTAB.length;i++)
		{
			if(param.equals(PNTAB[i]))
				return i;
		}
		return -1;
	}
	
	int isEV(String param)
	{
		for(int i=0;i<EVNTAB.length;i++)
		{
			if(param.equals(EVNTAB[i]))
				return i;
		}
		return -1;
	}

	
	void pass1() throws Exception
	{
		br=new BufferedReader(new FileReader("MacroFile.txt"));
		bw= new BufferedWriter(new FileWriter("MDT.txt"));
		String line;
		String token[];
		
		/* Actual Scanning Starts from here */
		while((line=br.readLine())!=null)
		{
			if(line.equals("MACRO"))
			{
				line=br.readLine();
				token=line.split(" |,");
				mnt[mCtr]=new MNT();
				mnt[mCtr].name=token[0];
				mnt[mCtr].mdtp=MDT_ptr;
				int kp=0,pp=0;
				MDT_ptr=0;
				for(int i=1;i<token.length;i++)
				{
					PNTAB[pntab_ctr]=new String();
					if(token[i].contains("=")) //Keyword Parameter
					{
						kp++;
						mnt[mCtr].kp=kp;
						String tk[];
						tk=token[i].split("=");
						tk[0]=new StringBuilder(tk[0]).deleteCharAt(0).toString();
						kpdtab[kpdtab_ctr]=new KPDTAB();
						kpdtab[kpdtab_ctr].addKPDTAB(tk[0],tk[1]);
						kpdtab_ctr++;
						PNTAB[pntab_ctr]=tk[0];
						pntab_ctr++;
					}
					else
					{
						token[i]=new StringBuilder(token[i]).deleteCharAt(0).toString();
						PNTAB[pntab_ctr]=token[i];
						pntab_ctr++;
						pp++;
					}
				}
				mnt[mCtr].pp=pp;
				
			}
			else
			{
				token=line.split(" |,");
				if(token[0].equals("MEND"))
				{
					bw.write(MDT_ptr+")\tMEND");
				}
				else if(token[0].equals("LCL")) //if LCL stmnt
				{
					
					EVNTAB[ev_ctr]=new String();
					token[1]=new StringBuilder(token[1]).deleteCharAt(0).toString();
					EVNTAB[ev_ctr]=token[1];
					bw.write(MDT_ptr+")\t"+"LCL"+" (E,"+(ev_ctr)+")");
					ev_ctr++;
				}
				
				else if(!(token[0].equals("MEND")) && token[0].equals("AIF")) //For AIF
				{
					bw.write(MDT_ptr+")\t"+"AIF");
					String tk[];
					int last=(token.length)-1;
					for(int z=1;z<last;z++)
					{
						if(token[z].startsWith("("))
						{
							token[z]=new StringBuilder(token[z]).deleteCharAt(0).toString(); //Removed (
							token[z]=new StringBuilder(token[z]).deleteCharAt(0).toString(); //Removed &
							int k;
							for(k=0;k<pntab_ctr;k++)
							{
								if(token[z].equals(PNTAB[k]))
									break;
							}
							if(k<pntab_ctr) //it is a parameter
							{
								bw.write(" ((P,"+(k+1)+")");
							}
							else // it is exp. var
							{
								for(k=1;k<ev_ctr;k++)
								{
								if(token[z].equals(EVNTAB[k]))
									break;
								}
								if(k<ev_ctr)
									bw.write(" ((E,"+(k)+")");
								else
									System.out.print("\n\n*******ERROR*****\nExpansion time variable used but not declared\n");
							}
						}
						if(token[z].endsWith(")"))
						{
							int lp=(token[z].length())-1;
							token[z]=new StringBuilder(token[z]).deleteCharAt(lp).toString(); //Removed )
							token[z]=new StringBuilder(token[z]).deleteCharAt(0).toString(); //Removed &
							int k;
							for(k=0;k<pntab_ctr;k++)
							{
								if(token[z].equals(PNTAB[k]))
									break;
							}
							if(k<pntab_ctr) //it is a parameter
							{
								bw.write("(P,"+(k+1)+")) ");
							}
							else // it is exp. var
							{
								for(k=1;k<ev_ctr;k++)
								{
								if(token[z].equals(EVNTAB[k]))
									break;
								}
								if(k<ev_ctr)
									bw.write("(E,"+(k)+"))");
								else
									System.out.print("\n\n*******ERROR*****\nExpansion time variable used but not declared\n");
							}
						}
						if(token[z].equals("NE"))
							bw.write("NE");
						else if(token[z].equals("GE"))
							bw.write("GE");
						else if(token[z].equals("LE"))
							bw.write("LE");
						else if(token[z].equals("EQ"))
							bw.write("EQ");
						else if(token[z].equals("LT"))
							bw.write("LT");
						else if(token[z].equals("GT"))
							bw.write("GT");
					}
					if(token[last].startsWith("."))
					{
							int x;
							for(x=0;x<ss_ctr;x++)
							{
								if(SSNTAB[x].equals(token[last]))
									break;
							}
							if(x==ss_ctr)
									System.out.print("\n\n*******ERROR*****\nExpansion time variable used but not declared\n");
							else
								bw.write("(S,"+(x+1)+")");
					}
				}
				else if(!(token[0].equals("MEND")) && token[1].equals("SET")) //For SET
				{
					int x;
					for(x=1;x<ev_ctr;x++)  //Searching from expansion time var.
					{
						if(EVNTAB[x].equals(token[0]))
							break;
					}
					if(x>ev_ctr)
						System.out.print("\n\n*******ERROR*****\nExpansion time variable used but not declared\n");
					else
					{
						if(token[2].startsWith("&"))	// for. SET &m+1, note-directly assummed that it will be only &m.
						{
							String tk[];
							bw.write(MDT_ptr+")\t(E,"+(x-1)+") SET (E,"+(x-1)+")"+token[2].charAt(2)+token[2].charAt(3));
						}
						else
						{
							bw.write(MDT_ptr+")\t(E,"+(x-1)+") SET "+token[2]);
						}
					}
					
				}
				/* */
				else  //If model Stmnt
				{
					String liner;
					BufferedReader br2_model;
					br2_model=new BufferedReader(new FileReader("model_stmnt.txt"));
					while((liner=br2_model.readLine())!=null)
					{
						if(token[0].equals(liner)) // if model Statement
						{
							bw.write(MDT_ptr+")\t"+token[0]);
							for(int i=1;i<token.length;i++)
							{
								if(token[i].startsWith("="))
									bw.write(","+token[i]);
								else if(token[i].startsWith("&"))
								{
									int no;
									token[i]=new StringBuilder(token[i]).deleteCharAt(0).toString();
									no=isParam(token[i]);
									if(no==-1) //Trying to check that if it is Expansion time variable
									{
										no=isEV(token[i]);
										if(no==-1)
											System.out.print("\n*******Error*******\n"+token[i]+" Parameter used Which does not exist");
										else
											bw.write(" (E,"+(no)+")");
									}
									else
									{
										bw.write(" (P,"+(no+1)+")");
									}
								}
							}
						}
						else if(!(token[0].equals("MEND")) && token[1].equals(liner))  // First is label then model
						{
							bw.write(MDT_ptr+")\t"+token[1]);
							if(token[0].startsWith(".")) //Sequencing Symbol
							{
								boolean already= false;
								//System.out.print("\n---Seq. Sym found--- ");
									int x=0;
									for(x=0;x<ss_ctr;x++)
									{
										if(SSNTAB[x].equals(token[0]))
										{	
											already=true;
											break;
										}
									}
								
								if(already)
								{
									seqSym[seqSymCtr]=new SSTAB();
									int sc=mnt[mCtr].sstp;
									seqSym[seqSymCtr].addSSTAB((sc+x-1),MDT_ptr);
									seqSymCtr++;
									
								}
								else if(!already)
								{
									
									SSNTAB[ss_ctr]=new String();
									SSNTAB[ss_ctr]=token[0];
									ss_ctr++;
									seqSym[seqSymCtr]=new SSTAB();
									int sc=mnt[mCtr].sstp;
									seqSym[seqSymCtr].addSSTAB((sc+x),MDT_ptr);
									seqSymCtr++;
								}
							 }
								
							String strr="";
							int cntr=0;
													
							for(int i=2;i<token.length;i++)
							{
								if(token[i].startsWith("="))
									bw.write(","+token[i]);
								else if(token[i].startsWith("&"))
								{
									if(token[i].contains("+")||token[i].contains("*"))
									{
										String tk[];
										//System.out.print("\nToken occured of +");
										tk=token[i].split("\\+|\\*");
										for(int j=0;j<tk.length;j++)
										{
											int num;
											
											if(cntr==1)
											{
												bw.write(strr);
												cntr=0;
											}
											if(token[i].contains("+"))
												strr="+";
											else if(token[i].contains("*"))
												strr="*";
											tk[j]=new StringBuilder(tk[j]).deleteCharAt(0).toString();
											num=isParam(tk[j]);
											if(num==-1)
											{
												num=isEV(tk[j]);
												if(num==-1)
													System.out.print("\nOpeartion done but not parameter or expansion variable");
												else
												{
													bw.write("(E,"+(num)+")");
													cntr=1;
												}
											}
											else
											{
												bw.write("(P,"+(num+1)+")");
												cntr=1;
											}
											}
									}
									else
									{
										int no;
										token[i]=new StringBuilder(token[i]).deleteCharAt(0).toString();
										no=isParam(token[i]);
										if(no==-1) //Trying to check that if it is Expansion time variable
										{
											no=isEV(token[i]);
											if(no==-1)
												System.out.print("\n*******Error*******\n"+token[i]+" Parameter used Which does not exist");
											else
												bw.write(" (E,"+(no)+")");
										}
										else
										{
											bw.write(" (P,"+(no+1)+"),");
										}
									}
								}
							}
						}
					}
				}
			}
			bw.newLine();
			MDT_ptr++;
		}
		mnt[mCtr].ev=ev_ctr;
		
		/* Displaying MNT*/
		mnt[mCtr].dispmnt();
		
		/* Displaying EVNTAB  */
		System.out.print("\n-->EVNTAB\n======================");
		for(int i=1;i<ev_ctr;i++)
			System.out.print("\n"+EVNTAB[i]);
		System.out.print("\n======================\n");
		
		/* Displaying SSNTAB */
		System.out.print("\n-->SSNTAB\n======================");
		for(int i=0;i<ss_ctr;i++)
			System.out.print("\n"+SSNTAB[i]);
		System.out.print("\n======================\n");
		
		
		/* Displaying SSTAB*/
		System.out.print("\n-->SSTAB\n======================");
		for(int i=0;i<seqSymCtr;i++)
			seqSym[i].disp();
		System.out.print("\n======================\n");
		
		/* Displaying KPDTAB*/
		System.out.print("\n-->KPDTAB\n======================");
		for(int i=0;i<kpdtab_ctr;i++)
			kpdtab[i].disp();
		System.out.print("\n======================\n");
		
		/* Displaying PNTAB */
		System.out.print("\n-->PNTAB\n======================");
		for(int i=0;i<pntab_ctr;i++)
			System.out.print("\n"+PNTAB[i]);
		System.out.print("\n======================\n");
		
		/* Displaying MDT */
		BufferedReader br_mdt;
		br_mdt=new BufferedReader(new FileReader("MDT.txt"));
		String ln;
		ln=br_mdt.readLine();
		while(ln!=null)
		{
			ln=br_mdt.readLine();
		}
		br_mdt.close();
		bw.close();
	}
	
	void pass2() throws Exception
	{
		int mnameLine=0;
		fillEVTAB();
		fillAPTAB();
	}
	
	void fillAPTAB() throws Exception
	{
		br=new BufferedReader(new FileReader("input.txt"));
		String line;
		String token[];
		String macro_name=mnt[mCtr].getName();
		int pp=mnt[mCtr].getppLen();
		int kp=mnt[mCtr].getkpLen();
		boolean found=false;
		int lcounter=0;
		while((line=br.readLine())!=null)
		{
			//System.out.print("\n--Line: "+line);
			if(line.contains(macro_name))
			{
				lcounter++;
				found=true;
			}
			if(found)
			{
				token=line.split(" |,");
				for(int i=1;i<(pp+1);i++)
				{
					APTAB[i]=new String();
					APTAB[i]=token[i];
				}
				int j=0;
				for(int i=(pp+1);i<=token.length;i++)
				{
					 String val= kpdtab[j].getVal();
					 APTAB[i]=new String();
					 APTAB[i]=val;
					 j++;
				}
				break;
			}
		}
		
		br.close();
		br=new BufferedReader(new FileReader("MDT.txt"));
		BufferedReader br2=new BufferedReader(new FileReader("model_stmnt.txt"));
		String tk[];
		BufferedWriter bw=new BufferedWriter(new FileWriter("expansion.txt"));
		boolean found2=false;
		boolean jump=false;
		int go=0;
		while((line=br.readLine())!=null)
		{
			tk=line.split("\t");
			if(tk[0].length()>0)
			{
				String ch=tk[0].replace(tk[0].substring(tk[0].length()-1),"");
				if(!found2 && ch.equals(String.valueOf(mnt[mCtr].getMDTP())))
				{	
					found2=true;
				}
				if(found2)
				{
					if(jump)
					{
						br=new BufferedReader(new FileReader("MDT.txt"));
						String tt[];
						while(line!=null)
						{
							tt=line.split("\t");
							if(tt.length>1)
							{
								int pos=tt[0].length()-1;
								ch=tt[0];
								ch=ch.replace(ch.substring(pos),"");
								if(ch.equals(String.valueOf(go)))
								{
									jump=false;
									break;
								}
							}
							line=br.readLine();
						}
					}
					if(!jump)
					{
						token=tk[1].split(" ");
						if(token.length>1)
						{
							if(token[1].equals("SET"))
							{
								int eno,len,epos=0,val=0;
								if(token[0].contains("E"))
								{
									len=token[0].length()-2;
									eno=Integer.parseInt(token[0].charAt(len)+"");
									epos=eno;
									if(token[2].length()==1) //if its like (E,1) set 5
									{
										EVTAB[epos]=token[2];
									}	
									else 
									{
										String t[];
										t=token[2].split("\\+|\\-");
										if(t[0].contains("E"))
										{
											len=token[0].length()-2;
											eno=Integer.parseInt(token[0].charAt(len)+"");
											val+=Integer.parseInt(EVTAB[eno]);
										}
										else
											val+=Integer.parseInt(t[0]);
										if(token[2].contains("+"))
										{	
											val+=Integer.parseInt(t[1]);
										}
										else if(token[2].contains("-"))
											val-=Integer.parseInt(t[1]);
										EVTAB[epos]=String.valueOf(val);
									}
								}
							}
							else if(token[0].equals("LCL"))
							{
								continue;
							}
							else if(token[0].equals("AIF"))
							{
								/* Expression solving */
								String condition="";
								if(token[1].contains("NE"))
									condition="NE";
								else if(token[1].contains("EQ"))
									condition="EQ";
								else if(token[1].contains("LT"))
									condition="LT";
								else if(token[1].contains("GT"))
									condition="GT";
								else if(token[1].contains("LE"))
									condition="LE";
								else if(token[1].contains("GE"))
									condition="GE";
								String t[]=token[1].split(condition);
								String val1="",val2="";
								int v1=0,v2=0;
								int len=0,no=0;
								boolean ans=false;
								int val3=0;
								for(int i=0;i<t.length;i++)
								{
									if(t[i].contains("P"))
									{
										if((t[i].charAt(1)+"").equals("("))
											len=t[i].length()-2;
										else 
											len=t[i].length()-3;
										no=Integer.parseInt(t[i].charAt(len)+"");
										if(i==0)
											val1=APTAB[no];
										else 
											val2=APTAB[no];
									}
									else if(t[i].contains("E"))
									{
										if((t[i].charAt(1)+"").equals("("))
											len=t[i].length()-2;
										else 
											len=t[i].length()-3;
										no=Integer.parseInt(t[i].charAt(len)+"");
										if(i==0)
											val1=EVTAB[no];
										else 
											val2=EVTAB[no];
									}
								}
								v1=Integer.parseInt(val1);
								v2=Integer.parseInt(val2);
								if(condition.equals("GT"))
								{
									if(Integer.parseInt(val1)>Integer.parseInt(val2))
										ans=true;
									else
										ans=false;
								}
								else if(condition.equals("LT"))
								{
									if(v1<v2)
										ans=true;
									else
										ans=false;
								}
								else if(condition.equals("LE"))
								{
									if(v1<=v2)
										ans=true;
									else
										ans=false;
								}
								else if(condition.equals("GE"))
								{
									if(v1>=v2)
										ans=true;
									else
										ans=false;
								}
								else if(condition.equals("EQ"))
								{
									if(v1==v2)
										ans=true;
									else
										ans=false;
								}
								else if(condition.equals("NE"))
								{
									if(v1==v2)
										ans=false;
									else
										ans=true;
								}
								
								/* Part for identifying and jumping to seq symbol */
								if(ans)
								{
									jump=true;
									len=token[2].length()-2;
									no=token[2].charAt(len);
									go=(seqSym[seqSymCtr-1].getAddress())-1;
								}
								else
								{
									jump=false;
									continue;
								}
							}
							else{
							String ln2;
							br2=new BufferedReader(new FileReader("model_stmnt.txt"));
							while((ln2=br2.readLine())!=null)
							{
								if(token[0].equals(ln2))
								{
									bw.write("+\t"+token[0]);
									tk=token[1].split("\\),");
									for(int k=0;k<tk.length;k++)
									{
										if(tk[k].startsWith("="))
										{ 	
											bw.write(","+tk[k]);
										}
										else if(tk[k].contains("P") && (tk[k].contains("+") || tk[k].contains("-")))  //Assumming first operand can only be register
										{
											String str1="",str2="";
											int no;
											boolean plus=false;
											boolean minus=false;
											String t[]=tk[k].split("");;
											if(tk[k].contains("+"))
											{
												t=tk[k].split("\\+");
												plus=true;
											}
											else if(tk[k].contains("-"))
											{
												t=tk[k].split("\\-");
												minus=true;
											}
												int len=t[0].length()-2;
												if(t[0].contains("P"))
												{
													no=Integer.parseInt(t[0].charAt(len)+"");
													str1=APTAB[no];
												}
												else if(t[0].contains("E"))
												{
													no=Integer.parseInt(t[0].charAt(len)+"");
													str1=EVTAB[no];
												}
												if(t[1].contains("P"))
												{
													no=Integer.parseInt(t[1].charAt(len)+"");
													str2=APTAB[no];
												}
												else if(t[1].contains("E"))
												{
													no=Integer.parseInt(t[1].charAt(len)+"");
													str2=EVTAB[no];
												}
												if(plus)
													bw.write(","+str1+"+"+str2);
												else if(minus)
													bw.write(","+str1+"-"+str2);
										}
										else 
										{
											int len=tk[k].length()-1;
											int no=Integer.parseInt(tk[k].charAt(3)+"");
											bw.write("\t"+APTAB[no]);
										}
									}
									bw.newLine();
									break;
								}
							}
						}
					}
				}
				}
			}
		}
		bw.close();
		bw=new BufferedWriter(new FileWriter("finalFile.txt"));
		br2=new BufferedReader(new FileReader("input.txt"));
		BufferedReader brexp=new BufferedReader(new FileReader("expansion.txt"));
		String liner;
		while((line=br2.readLine())!=null)
		{
			if(!(line.contains(mnt[mCtr].getName())))
			{
				bw.write(line);
				bw.newLine();
			}
			else
			{
				while((liner=brexp.readLine())!=null)
				{
					bw.write(liner);
					bw.newLine();
				}
			}
		}
		bw.close();
		
		MEC=mnt[mCtr].getMDTP();
		System.out.print("\n-->APTAB\n======================");
		for(int i=1;i<=(kp+pp);i++)
			System.out.print("\n"+APTAB[i]);
		System.out.print("\n======================\n");
		
		System.out.print("\n-->EVTAB\n======================");
		for(int i=1;i<ev_ctr;i++)
			System.out.print("\n"+EVTAB[i]);
		System.out.print("\n======================\n");
		
		br.close();
		
		
		
		
	}
	
	void showOldFile() throws Exception
		{
			String line;
			System.out.print("\n-->Old Input File\n======================");
			br=new BufferedReader(new FileReader("input.txt"));
			while((line=br.readLine())!=null)
			{
				System.out.print("\n"+line);
			}
			System.out.print("\n==============================\n");
		}
		
	void showNewFile() throws Exception
		{
			String line;
			System.out.print("\n-->Input File\n==============================");
			br=new BufferedReader(new FileReader("finalFile.txt"));
			while((line=br.readLine())!=null)
			{
				System.out.print("\n"+line);
			}
			System.out.print("\n==============================\n");
		}
	void fillEVTAB()
	{
		for(int i=1;i<=ev_ctr;i++)
		{
			EVTAB[i]=new String();
			EVTAB[i]="-";
		}
	}

	void scanProgram() throws Exception
	{
		pass1();
		pass2();
		showOldFile();
		showNewFile();
	}
}