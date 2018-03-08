import java.util.Scanner;
import java.io.*;
class OperatorPrecedence
{
	public static void main(String args[]) throws Exception
	{
		String expression;
		Scanner sc=new Scanner(System.in);
		System.out.print("\nEnter an Expression: ");
		expression=sc.next();
		expression=expression+"$";
		parser pr=new parser();
		pr.initTable();
		pr.showTable();
		pr.parsingProcess(expression);
		
	}
}

class Node
{
	String value;
	Node left;
	Node right;
	
	Node()
	{
		value="";
		left=null;
		right=null;
	}
	Node(String val,Node lchild,Node rchild)
	{
		value=val;
		left=lchild;
		right=rchild;
	}
	
	void display()
	{
		if(left!=null)
			left.display();
		if(right!=null)
			right.display();
		System.out.print(value);
			
	}
}
class Stack
{
	String operator[];
	Node operand[];
	public static int tos=-1;
	int maxsize;
	Stack(int size)
	{
		maxsize=size;
		operator=new String[maxsize];
		operand=new Node[maxsize];
	}
	void push(String operator,Node operand)
	{
		tos++;
		this.operator[tos]=operator;
		this.operand[tos]=operand;
	}
	void push(Node operand)
	{
		this.operand[tos]=operand;
	}
	void push(String operator)
	{
		tos++;
		this.operator[tos]=operator;
	}
	
	
}
class parser
{
	String table[][]=new String[4][4];
	void initTable() throws Exception
	{
		BufferedReader br=new BufferedReader(new FileReader("op_table.txt"));
		String liner;
		liner=br.readLine();
		String token[];
		int i=0;
		while(liner!=null)
		{
			token=liner.split("\t");
			for(int j=0;j<token.length;j++)
			{
				table[i][j]=token[j];
			}
			i++;
			liner=br.readLine();
		}
	}
	void showTable()
	{
		System.out.print("\n=================================\n");
		System.out.print("\tOperator Table");
		System.out.print("\n=================================\n");
		for(int i=0;i<table.length;i++)
		{
			for(int j=0;j<table[i].length;j++)
			{
				System.out.print(table[i][j]+"\t");
			}
			System.out.print("\n");
		}
	}
	
	String getData(String row,String col)
	{
		int i,j;
		boolean r=false,c=false;
		for(i=1;i<table.length;i++)
		{
			if(table[i][0].equals(row))
			{	r=true;
				break;
			}
		}
		for(j=0;j<table[0].length;j++)
		{
			if(table[0][j].equals(col))
			{	c=true;
				break;
			}
		}
		if(c && r)
			return table[i][j];
		else
			return "not found";
	}
	
	void parsingProcess(String expression)
	{
		Stack stk=new Stack(10);
		int expr_ctr=0;
		int flag=0;
		Node node=new Node((expression.charAt(expr_ctr))+"",null,null);
		stk.push("$",node);
		String operator,operand;
		System.out.print("Top: "+stk.operator[Stack.tos]+" , " );
		node.display();
		String data;
		while(expression.charAt(expr_ctr) != '$')
		{
			expr_ctr++;
			data=getData(stk.operator[Stack.tos],(expression.charAt(expr_ctr)+""));
			System.out.println("Epression: "+expression.charAt(expr_ctr)+", TOS: "+stk.operator[Stack.tos]+", data: "+data+"\nOperand: ");
			stk.operand[Stack.tos].display();
			System.out.print("\n______________________________________________________________\n");
			if(!(data.equals("not found")))
			{
				if(data.equals("<"))
				{
					operator=expression.charAt(expr_ctr)+"";
					operand=expression.charAt(++expr_ctr)+"";
					if(operand.equals("*") || operand.equals("+"))
					{	
						System.out.print("\nInvalid Expression");
						flag=1;
						break;
					}
					else
					{
						Node node1=new Node(operand,null,null);
						stk.push(operator,node1);
					}
				}
				else if(data.equals(">"))
				{
					Node rchild=stk.operand[Stack.tos];
					String operator1=stk.operator[Stack.tos--];
					Node lchild=stk.operand[Stack.tos];
					String operator2=stk.operator[Stack.tos--];
					if(operator1!="*" && operator1!="+" && operator2!="*" && operator2!="+")
					{
						Node node1=new Node(operator1,lchild,rchild);
						stk.push(operator2,node1);
						expr_ctr--;
					}
					else
					{
						System.out.print("\nInvalid Expression");
						flag=1;
						break;
					}	
				}
				else if(data.equals("="))
				{
					if(data.equals("$"))
					{
						System.out.print("\nDone Processing..Valid String");
						break;
					}
				}
			}
			else
			{
				System.out.print("\nInvalid Expression");
				flag=1;
				break;
			}
			
		}
		
		
		if(flag==0)
		{
			Node root=new Node();
			root=stk.operand[Stack.tos];
			System.out.print("\nValid String:\n ");
			root.display();
		}
	}
	
}
/************************************************************************************

E:\Study\Sem3\SS\operator precedence program>java OperatorPrecedence

Enter an Expression: a+a*a+a

=================================
        Operator Table
=================================
op      +       *       $
+       >       <       >
*       >       >       >
$       <       <       =
Top: $ , aEpression: +, TOS: $, data: <
Operand:
a
______________________________________________________________
Epression: *, TOS: +, data: <
Operand:
a
______________________________________________________________
Epression: +, TOS: *, data: >
Operand:
a
______________________________________________________________
Epression: +, TOS: +, data: >
Operand:
aa*
______________________________________________________________
Epression: +, TOS: $, data: <
Operand:
aaa*+
______________________________________________________________
Epression: $, TOS: +, data: >
Operand:
a
______________________________________________________________
Epression: $, TOS: $, data: =
Operand:
aaa*+a+
______________________________________________________________

Valid String:
 aaa*+a+

**************************************************************************************/