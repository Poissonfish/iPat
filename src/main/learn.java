package main;

public class learn{
	static int c=0;
	public static void main(String[] args){
		B b1 = new B();
		b1.test();
		System.out.println(c);
	}
}

class B{
	 void test(){
		learn.c= learn.c+3;
	}
}