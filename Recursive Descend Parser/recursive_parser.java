import java.util.Scanner;
class tree_node
{
	String data;
	tree_node left;
	tree_node right;
	
	tree_node(String val)
	{
		this.data=val;
		this.left=null;
		this.right=null;
	}
	
	tree_node()
	{
		this.data="";
		this.left=null;
		this.right=null;
	}
	
	tree_node(String val,tree_node left1,tree_node right1)
	{
		this.data=val;
		this.left=left1;
		this.right=right1;
	}
	
	public void display()
	{
		if(left!=null)
			left.display();
		if(right!=null)
			right.display();
		System.out.print(data);
	}
}



public class recursive_parser
{
	public static int nextptr=0;
	public static void main(String args[])
	{
		tree_node a,b;
		Scanner sc=new Scanner(System.in);
		System.out.print("\nEnter an Expression: ");
		String expr=sc.next();
		tree_node root=new tree_node();
		if(expr.charAt(0)=='+'||expr.charAt(0)=='*')
			System.out.print("\nInvalid expression");
		else
		{
			root=proc_E(expr);
			if(root==null||nextptr!=expr.length())
				System.out.println("Invalid expression");
			else
				root.display();
		}
	}
	
	public static tree_node proc_E(String expr)
	{
		tree_node a,b;
		a=proc_T(expr);
		while(nextptr<expr.length() && expr.charAt(nextptr)=='+')
		{
			nextptr++;
			b=proc_T(expr);
			if(b!=null)
				a=new tree_node("+",a,b);
			else 
				return null;
		}
		return a;
		
	}
	
	public static tree_node proc_T(String expr)
	{
		tree_node a,b;
		a=proc_V(expr);
		while(nextptr<expr.length() && expr.charAt(nextptr)=='*')
		{
			nextptr++;
			b=proc_V(expr);
			if(b!=null)
				a=new tree_node("*",a,b);
			else
				return null;
		}
		return a;
	}
	
	public static tree_node proc_V(String expr)
	{
		tree_node a;
		if(nextptr<expr.length() && expr.charAt(nextptr)!='*' && expr.charAt(nextptr)!='+')
		{
			a=new tree_node(expr.charAt(nextptr)+"",null,null);
			nextptr++;
		}
		else
		{
			return null;
		}
		return a;
	}

}