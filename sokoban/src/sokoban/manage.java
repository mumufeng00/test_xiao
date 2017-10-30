package sokoban;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.BufferedImage;

public class manage extends JFrame implements ActionListener,MouseListener,MouseMotionListener
{
	private final int XSIZE = 7,YSIZE = 7;               //图像长宽
	private final int SIZE = 20;                         //方块大小
	private int shortest_path;                           //最短路径
	private Container container;                           
	private JPanel livePane,controlPane;            
	private JButton button[];
    private Square square;
    int man_mink=999;//玩家最短路径
    int box_mink=999;//箱子最短路径
    
    
    //-1障碍 0空地 1箱子 2目的地 3角色
	private int map[][]= {	
							{-1,-1, -1,-1,-1,-1,-1},
							{-1,2, 0,0,0,0,-1},       
   							{-1,0,-1,0,0,0,-1},
   							{-1,0,-1,0,0,0,-1},
   							{-1,0, 1,0,3,0,-1},
   							{-1,0, 0,0,0,0,-1},
   							{-1,-1, -1,-1,-1,-1,-1},
   						 };				  //地图
	private int number = 7,lives[][];
	 
	public manage()
	{

		container = getContentPane();
		container.setLayout( new BorderLayout() );
		livePane = new JPanel( new BorderLayout() );
		livePane.setSize(new Dimension(802,638)); 
		controlPane = new JPanel( new GridLayout( 15,1,0,10 ) );
		controlPane.setSize(new Dimension(38,300));
		button = new JButton[3];
		lives = new int[XSIZE+8][YSIZE+8];     		                                                  
        initButton();                                     //初始化按钮
    	square = new Square();
	    square.addMouseListener(this);
	    square.addMouseMotionListener(this);
	    //初始化地图的颜色   x方向i  y方向k
	    for(int k=0;k<XSIZE;k++)
		    for(int i=0;i<YSIZE;i++)		
	    square.setcolor(k, i, map[k][i]);
	    livePane.add(square);                             //初始化地图
        container.add(livePane,BorderLayout.CENTER );	
        container.add(controlPane,BorderLayout.EAST );	
		setSize(845,638);
		setVisible(true);
		

    }
	//判断箱子是否在墙角的函数 在墙角返回true 不在墙角返回false
	public boolean isincorner(int Xnowbox ,int Ynowbox) {
		boolean flag=false;
		if(map[Xnowbox][Ynowbox+1]==-1)
			if(map[Xnowbox+1][Ynowbox]==-1  || map[Xnowbox-1][Ynowbox]==-1 )
			{
				System.out.println( "A");
			    flag=true;
			}	
			System.out.println( "("+Xnowbox +","+Ynowbox +")\n");
			if(map[Xnowbox][Ynowbox-1]==-1)
				if(map[Xnowbox+1][Ynowbox]==-1  || map[Xnowbox-1][Ynowbox]==-1 )
				{
					System.out.println( "B");
					flag=true;
				}
			

			return flag;
	}
	
	//用两次DFS搜索  计算完成整个游戏所需的最短路径
	public int caculateshortpath() {
		int shortpath=0;
		int A=999,B=999,C=999,D=999;
		int Xnowman = 0,Ynowman = 0,Xnowbox = 0,Ynowbox = 0,Xdesty = 0,Ydesty = 0;
		//找到box,man,destination的位置
		for(int k=0;k<7;k++)
			for(int j=0;j<7;j++)
				{if(map[k][j]==1)//物体
				{
					Xnowbox=k;
					Ynowbox=j;
					System.out.println( "Xnowbox"+Xnowbox+"\n");
					System.out.println( "Ynowbox"+Ynowbox +"\n");
				}
				if(map[k][j]==3)//人
				{
					Xnowman=k;
					Ynowman=j;	
					System.out.println( "Xnowman"+Xnowman+"\n");
					System.out.println( "Ynowman"+Ynowman +"\n");
				}
				if(map[k][j]==2)//目的地
				{
					Xdesty=k;
					Ydesty=j;
				}
			
				}
		if(map[Xnowbox][Ynowbox+1]==2 || map[Xnowbox][Ynowbox+1]==0)//等于块空地或者目的地就可以递归下去
			//判断人能否到他下方
		A=BoxDFS(Xdesty,Ydesty,Xnowbox,Ynowbox+1,0,1)+manDFS(Xnowbox,Ynowbox-1,Xnowman,Ynowman,0,1);//向上
			if(map[Xnowbox][Ynowbox-1]==2 || map[Xnowbox][Ynowbox-1]==0)//向下递归
			B=BoxDFS(Xdesty,Ydesty,Xnowbox,Ynowbox-1,0,2)+manDFS(Xnowbox,Ynowbox+1,Xnowman,Ynowman,0,2);//向下
			if(map[Xnowbox-1][Ynowbox]==2 || map[Xnowbox-1][Ynowbox]==0)//向左递归
			C=BoxDFS(Xdesty,Ydesty,Xnowbox+1,Ynowbox,0,3)+manDFS(Xnowbox+1,Ynowbox,Xnowman,Ynowman,0,4);//向左
			if(map[Xnowbox+1][Ynowbox]==2 || map[Xnowbox+1][Ynowbox]==0)//向右递归
			D=BoxDFS(Xdesty,Ydesty,Xnowbox-1,Ynowbox,0,4)+manDFS(Xnowbox-1,Ynowbox,Xnowman,Ynowman,0,4);//向右
		//判断向那个方向递归步数最短
		shortpath=A;
		if(shortpath>B)
		shortpath=B;
		if(shortpath>C)
		shortpath=C;
		if(shortpath>D)
		shortpath=D;
		return shortpath;
		
	} 
	
	//用一次DFS搜索  人推箱子所需的最短路径
	public int caculate_box_shortpath() {	
	int Xnowman = 0,Ynowman = 0,Xnowbox = 0,Ynowbox = 0,Xdesty = 0,Ydesty = 0;
	//找到box,man,destination的位置
	for(int k=0;k<7;k++)
		for(int j=0;j<7;j++)
			{if(map[k][j]==1)//物体
			{
				Xnowbox=k;
				Ynowbox=j;
				System.out.println( "Xnowbox"+Xnowbox+"\n");
				System.out.println( "Ynowbox"+Ynowbox +"\n");
			}
			if(map[k][j]==3)//人
			{
				Xnowman=k;
				Ynowman=j;	
				System.out.println( "Xnowman"+Xnowman+"\n");
				System.out.println( "Ynowman"+Ynowman +"\n");
			}
			if(map[k][j]==2)//目的地
			{
				Xdesty=k;
				Ydesty=j;
			}
		}

	
			//判断向那个方向递归步数最短
		int shortpath=BoxDFSa(Xdesty,Ydesty,Xnowbox ,Ynowbox,0,0);
	
		return shortpath;
	
} 
	//box的最短 caculates路径搜索
		public int BoxDFS(int Xd,int Yd,int Xnowbox ,int Ynowbox,int k,int x) {
			//Xd,Yd表示要到达的地方，Xnowbox，Ynowbox表示现在人在哪个位置 k为步数 x为推的方向1向上，2向下，3向左，4向右
			int A,B,C,D;
			int Xnowman = 0, Ynowman = 0;
			A=B=C=D=999;    //给ABCD赋初值
			System.out.println( "该时刻箱子的上下左右情况，上："+map[Xnowbox][Ynowbox-1]+"下："+map[Xnowbox][Ynowbox+1]+"左："+map[Xnowbox-1][Ynowbox]+"右："+map[Xnowbox+1][Ynowbox]);
			//判断是否到达终点		 
			if(Xd== Xnowbox && Yd==Ynowbox)
			{box_mink=k;
			System.out.println( "到达终点");
			return k;
			}
			System.out.println( "当前步数："+k);
			if(k>box_mink)	//判断是否超过之前最小路径长度，超过就直接返回没必要再进行下去了
				{System.out.println( "超过最小k返回");
				return box_mink;
				}
			k++;
			//判断箱子是否到达墙角到达墙角就不用进行下面的操作直接返回
			if(isincorner(Xnowbox ,Ynowbox)==  true)
			{
			System.out.println( "达到墙角");
			return 999;
			}
			//判断人现在的位置
			 switch(x) {
			 case 1:{Xnowman=Xnowbox; Ynowman=Ynowbox-1;};break;
			 case 2:{Xnowman=Xnowbox; Ynowman=Ynowbox+1;};break;
			 case 3:{Xnowman=Xnowbox+1; Ynowman=Ynowbox;};break;
			 case 4:{Xnowman=Xnowbox-1; Ynowman=Ynowbox-1;};break;
			 }
			
			//没到达则进一步递归
			if(x!=2)
			{if(  manDFS(Xnowbox,Ynowbox+1,Xnowman,Ynowman,0,0)!=999   &&  (map[Xnowbox][Ynowbox-1]==2 || map[Xnowbox][Ynowbox-1]==0))//等于块空地或者目的地就可以递归下去
				{System.out.println( "向上走");
				A=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox-1,k,1);
				}
				}
			if(x!=1)
			{if( manDFS(Xnowbox,Ynowbox-1,Xnowman,Ynowman,0,0)!=999   &&  (map[Xnowbox][Ynowbox+1]==2 || map[Xnowbox][Ynowbox+1]==0))
				{System.out.println( "向下走");
				B=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox+1,k,2);
				}
				}
			if(x!=4)
			{if( manDFS(Xnowbox+1,Ynowbox,Xnowman,Ynowman,0,0)!=999    &&  (map[Xnowbox-1][Ynowbox]==2 || map[Xnowbox-1][Ynowbox]==0))
				{System.out.println( "向左");
				C=BoxDFSa(Xd,Yd,Xnowbox-1 ,Ynowbox,k,3);
				}
				}
			if(x!=3)
			{if(manDFS(Xnowbox-1,Ynowbox+1,Xnowman,Ynowman,0,0)!=999   &&  (map[Xnowbox+1][Ynowbox]==2 || map[Xnowbox+1][Ynowbox]==0))
			{	System.out.println( "向右");
			D=BoxDFSa(Xd,Yd,Xnowbox+1 ,Ynowbox,k,4);
				}
				}
			//判断向那个方向递归步数最短
			int ret=A;
			if(ret>B)
			ret=B;
			if(ret>C)
			ret=C;
			if(ret>D)
			ret=D;
			return ret;
			
				
				
			}
			
	//人推box的最短 caculates路径搜索不用考虑人能否推 只判断下就行
	public int BoxDFSa(int Xd,int Yd,int Xnowbox ,int Ynowbox,int k,int x) {
			//Xd,Yd表示要到达的地方，Xnowbox，Ynowbox表示现在人在哪个位置 k为步数 x为推的方向1向上，2向下，3向左，4向右
			int A,B,C,D;
			A=B=C=D=999;    //给ABCD赋初值
			System.out.println( "该时刻箱子的上下左右情况，上："+map[Xnowbox][Ynowbox-1]+"下："+map[Xnowbox][Ynowbox+1]+"左："+map[Xnowbox-1][Ynowbox]+"右："+map[Xnowbox+1][Ynowbox]);
			//判断是否到达终点		 
			if(Xd== Xnowbox && Yd==Ynowbox)
			{box_mink=k;
			System.out.println( "到达终点");
			return k;
			}
			System.out.println( "当前步数："+k);
			if(k>box_mink)	//判断是否超过之前最小路径长度，超过就直接返回没必要再进行下去了
				{System.out.println( "超过最小k返回");
				return box_mink;
				}
			k++;
			//判断箱子是否到达墙角到达墙角就不用进行下面的操作直接返回
			if(isincorner(Xnowbox ,Ynowbox)==  true)
			{
			System.out.println( "达到墙角");
			return 999;
			}
			//没到达则进一步递归
			if(x!=2)
			{if( (map[Xnowbox][Ynowbox+1]==0||map[Xnowbox][Ynowbox+1]==1)   &&  (map[Xnowbox][Ynowbox-1]==2 || map[Xnowbox][Ynowbox-1]==0))//等于块空地或者目的地就可以递归下去
			{System.out.println( "向上走");A=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox-1,k,1);
			}
			}
			if(x!=1)
			{if( (map[Xnowbox][Ynowbox-1]==0||map[Xnowbox][Ynowbox-1]==1)  &&  (map[Xnowbox][Ynowbox+1]==2 || map[Xnowbox][Ynowbox+1]==0))
				{System.out.println( "向下走");B=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox+1,k,2);
			}
				}
			if(x!=4)
			{if( (map[Xnowbox+1][Ynowbox]==0||map[Xnowbox+1][Ynowbox]==1)    &&  (map[Xnowbox-1][Ynowbox]==2 || map[Xnowbox-1][Ynowbox]==0))
				{System.out.println( "向左");C=BoxDFSa(Xd,Yd,Xnowbox-1 ,Ynowbox,k,3);
			}
				}
			if(x!=3)
			{if( (map[Xnowbox-1][Ynowbox]==0||map[Xnowbox-1][Ynowbox]==1)   &&  (map[Xnowbox+1][Ynowbox]==2 || map[Xnowbox+1][Ynowbox]==0))
			{	System.out.println( "向右");D=BoxDFSa(Xd,Yd,Xnowbox+1 ,Ynowbox,k,4);
			}
				}
			//判断向那个方向递归步数最短
			int ret=A;
			if(ret>B)
			ret=B;
			if(ret>C)
			ret=C;
			if(ret>D)
			ret=D;
			return ret;
			
			
		}
		
	//man的最短路径搜索
	public int manDFS(int Xd,int Yd,int Xnowman ,int Ynowman,int k,int x) {
		//Xd,Yd表示要到达的地方，Xnowman，Ynowman表示现在人在哪个位置   x是
		
		int A,B,C,D;
		A=B=C=D=999;//给ABCD赋初值
	
		System.out.println( "步数："+k);
		System.out.println( "最小步数："+man_mink);
		if(k>man_mink)			//判断是否超过之前最小路径长度，超过就直接返回没必要再进行下去了
		return man_mink;
		if(Xd== Xnowman && Yd==Ynowman)
		{man_mink=k;
		return k;
		}
		k++;
		//没到达则进一步递归
		if(x!=2)
		{if(map[Xnowman][Ynowman-1]==2 || map[Xnowman][Ynowman-1]==0)//向上递归  等于块空地或者目的地就可以递归下去
		{
		A=manDFS(Xd,Yd,Xnowman ,Ynowman-1,k,1);
		}
		}
		if(x!=1)
		if(map[Xnowman][Ynowman+1]==2 || map[Xnowman][Ynowman+1]==0)//向下递归
		{
		B=manDFS(Xd,Yd,Xnowman ,Ynowman+1,k,2);
		
		}
		if(x!=4)
		if(map[Xnowman-1][Ynowman]==2 || map[Xnowman-1][Ynowman]==0)//向左递归
		{
		C=manDFS(Xd,Yd,Xnowman-1 ,Ynowman,k,3);
		
		}
		if(x!=3)
		if(map[Xnowman+1][Ynowman]==2 || map[Xnowman+1][Ynowman]==0)//向右递归
		{
		D=manDFS(Xd,Yd,Xnowman+1,Ynowman,k,4);
		
		}//判断向那个方向递归步数最短
		int ret=A;
		if(ret>B)
		ret=B;
		if(ret>C)
		ret=C;
		if(ret>D)
		ret=D;
		return ret;
	}
	
	//图类添加
    public class Square extends JPanel
	{
		private Graphics g;
		private Image frontImage,backImage;
        private Graphics ft,bg;
		
		public Square()
		{
			backImage = new BufferedImage( XSIZE*SIZE, YSIZE*SIZE, 1);
   	        bg = backImage.getGraphics();                  //背景图像缓存        
   	        frontImage = new BufferedImage( XSIZE*SIZE, YSIZE*SIZE,  1);
   	        ft = frontImage.getGraphics();                 //前景图像缓存
   	        initBack();
   	        initGraphic();
		}
		public void paintComponent( Graphics g )
		{
			super.paintComponent(g);
			g.drawImage(frontImage, 0, 0, this);           //显示前景图像
		}
		
		public void initGraphic()
		{
			ft.drawImage(backImage, 0, 0, this);           //刷新背景图像
		}
		
		public void initBack()                             //初始化背景图像
		{
			bg.setColor(Color.LIGHT_GRAY);
   	        bg.fillRect( 0, 0, XSIZE*SIZE, YSIZE*SIZE );
   	        bg.setColor( Color.WHITE );
            for ( int i = 0; i < XSIZE; i++ )
              for ( int j = 0; j < YSIZE; j++ )
                bg.drawRect(i * SIZE, j * SIZE, SIZE,SIZE);    
        }
		
		public void setcolor( int x,int y,int color )               //设置x、y位置为活状态
		{
			if(color==-1)
		{
			ft.setColor( Color.BLACK );
		}else 	if(color==0) {
			ft.setColor( Color.WHITE );
		}
		else 	if(color==1) {
			ft.setColor( Color.BLUE);
		}
		else 	if(color==2) {
			ft.setColor( Color.RED);
		}
		else 	if(color==3) {
			ft.setColor( Color.YELLOW);
		}
			ft.fillRect( x * SIZE+1, y * SIZE+1, SIZE-1,SIZE-1 );  
		}
		
		public void setNew()                             //重画
		{
			repaint();
		}
	}//end class Square

//******************************初始化按钮**************************************
    public void initButton()
    {
    	String option[] = {"开始游戏","计算路径"," 重新开始 "};
    	controlPane.add( new JLabel("     控制面板") );
    	for( int i=0; i<button.length; i++ )
    	{
    		button[ i ] = new JButton( option[ i ] );
    		button[ i ].addActionListener( this );
    		controlPane.add( button[i] );
    	}
    }

    

	

//******************************鼠标响应事件************************************    
    public void mouseClicked( MouseEvent event )
    {}
    public void mousePressed( MouseEvent event )
    {}
    public void mouseReleased( MouseEvent event )
    {}
    public void mouseEntered( MouseEvent event )
    {}
    public void mouseExited( MouseEvent event )
    {}
    public void mouseDragged( MouseEvent event )
    {}
    public void mouseMoved( MouseEvent event )
    {}

 //*****************************按钮响应事件*************************************    
    public void actionPerformed( ActionEvent event )
    {
    	if( event.getSource() == button[0] )	//开始游戏
    	{
    		
    	}                
    	else if( event.getSource() == button[1])//计算路径
    	{//shortest_path= manDFS(3,3,4,4,0,0);
    	shortest_path=caculateshortpath() ;
    		//shortest_path= caculate_box_shortpath();
    		   JOptionPane.showMessageDialog( null,
    		    	   "最少使用步数为"+shortest_path ,"最少使用步数",JOptionPane.PLAIN_MESSAGE );
    	}
    	else if( event.getSource() == button[2] )//重新开始游戏
    	{
    		
    	}
    	
    }


    //******************************main方法****************************************		
	public static void main(String[] args){
		manage application = new manage();
		application.setResizable( false );//windows can not resizable
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}	
}

