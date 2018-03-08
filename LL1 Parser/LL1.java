import java.util.Scanner;
import java.io.*;
class LL1
{
	public static void main(String args[]) throws Exception
	{
		Scanner sc=new Scanner(System.in);
		System.out.print("\nEnter an Expression: ");
		String expr=sc.next();
		parser pr=new parser();
		pr.initTable();
		pr.showTable();
		expr=expr+"|";
		pr.Parsing(expr);
	}
	
}

class parser
{
	String tableArr[][]=new String[6][5];
	void initTable() throws Exception
	{
		BufferedReader br=new BufferedReader(new FileReader("LLTable.txt"));
		String token[]=null;
		int i=0;
		String line=br.readLine();
		while(line!=null)
		{
			token=line.split("\t");
			for(int j=0;j<token.length;j++)
			{
				tableArr[i][j]=token[j];
			}
			line=br.readLine();
			i++;
		}
		
	}
	void showTable()
	{
		System.out.print("\n\n================================================\n");
		System.out.print("\t\tParsing Table");
		System.out.print("\n================================================\n\n");
		for(int i=0;i<tableArr.length;i++)
		{
			for(int j=0;j<tableArr[i].length;j++)
			{
				System.out.print(tableArr[i][j]+"\t");
			}
			System.out.print("\n");
		}
	}
	
	void Parsing(String expr)
	{
		int expr_ctr=0,csf_ctr=0,i=0;
		String csf="",data,curr_expr,curr_state;
		curr_expr=expr.charAt(0)+"";
		curr_state=tableArr[1][0];
		if(expr.charAt(expr_ctr)=='a')
		{
			data=getData(curr_state,curr_expr);
			csf=data+csf;
			while(expr_ctr<expr.length() && csf!="")
			{
				System.out.print("\nCSF: "+csf);
				//System.out.print("\nCurrent Expression is: "+expr.charAt(expr_ctr)+"\n");	
				data=getData(csf.charAt(0)+"",expr.charAt(expr_ctr)+"");
				//System.out.println("Data= "+data);
				csf=new StringBuilder(csf).deleteCharAt(0).toString();
				csf=data+csf;
				//System.out.println("\nCSF= "+csf);
				if(data.equals("not found"))
				{
					System.out.print("\nString is invalid");
					return;
				}
				if((csf.charAt(0)+"").equals(expr.charAt(expr_ctr)+""))
				{
					//System.out.print("\nMatch Section\nCSF is--- "+csf);
					csf=new StringBuilder(csf).deleteCharAt(0).toString();
					System.out.print("\n\nCSF is--- "+csf);
					expr_ctr++;
				}
				if(data.equals("e"))
				{
					//System.out.print("\nE found--");
					csf=new StringBuilder(csf).deleteCharAt(0).toString();
					//System.out.print("\nE now csf is: "+csf);
				}
				
				if(csf.equals(""))
				{
					System.out.print("\n\nString is correct");
					return;
				}	
			}
			System.out.print("\nString is invalid");
		}
		else
		{
			System.out.print("\nInvalid Expression");
		}
	}
	String getData(String row,String col)
	{
		int i,j;
		boolean r=false,c=false;
		for(i=1;i<tableArr.length;i++)
		{
			if(tableArr[i][0].equals(row))
			{	
				r=true;
				break;
			}
		}
		for(j=0;j<tableArr[0].length;j++)
		{
			if(tableArr[0][j].equals(col))
			{	
				c=true;
				break;
			}
		}
		if(r && c)
			return tableArr[i][j];
		else
			return "not found";
	}
}