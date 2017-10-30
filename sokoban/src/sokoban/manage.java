package sokoban;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.BufferedImage;

public class manage extends JFrame implements ActionListener,MouseListener,MouseMotionListener
{
	private final int XSIZE = 7,YSIZE = 7;               //ͼ�񳤿�
	private final int SIZE = 20;                         //�����С
	private int shortest_path;                           //���·��
	private Container container;                           
	private JPanel livePane,controlPane;            
	private JButton button[];
    private Square square;
    int man_mink=999;//������·��
    int box_mink=999;//�������·��
    
    
    //-1�ϰ� 0�յ� 1���� 2Ŀ�ĵ� 3��ɫ
	private int map[][]= {	
							{-1,-1, -1,-1,-1,-1,-1},
							{-1,2, 0,0,0,0,-1},       
   							{-1,0,-1,0,0,0,-1},
   							{-1,0,-1,0,0,0,-1},
   							{-1,0, 1,0,3,0,-1},
   							{-1,0, 0,0,0,0,-1},
   							{-1,-1, -1,-1,-1,-1,-1},
   						 };				  //��ͼ
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
        initButton();                                     //��ʼ����ť
    	square = new Square();
	    square.addMouseListener(this);
	    square.addMouseMotionListener(this);
	    //��ʼ����ͼ����ɫ   x����i  y����k
	    for(int k=0;k<XSIZE;k++)
		    for(int i=0;i<YSIZE;i++)		
	    square.setcolor(k, i, map[k][i]);
	    livePane.add(square);                             //��ʼ����ͼ
        container.add(livePane,BorderLayout.CENTER );	
        container.add(controlPane,BorderLayout.EAST );	
		setSize(845,638);
		setVisible(true);
		

    }
	//�ж������Ƿ���ǽ�ǵĺ��� ��ǽ�Ƿ���true ����ǽ�Ƿ���false
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
	
	//������DFS����  �������������Ϸ��������·��
	public int caculateshortpath() {
		int shortpath=0;
		int A=999,B=999,C=999,D=999;
		int Xnowman = 0,Ynowman = 0,Xnowbox = 0,Ynowbox = 0,Xdesty = 0,Ydesty = 0;
		//�ҵ�box,man,destination��λ��
		for(int k=0;k<7;k++)
			for(int j=0;j<7;j++)
				{if(map[k][j]==1)//����
				{
					Xnowbox=k;
					Ynowbox=j;
					System.out.println( "Xnowbox"+Xnowbox+"\n");
					System.out.println( "Ynowbox"+Ynowbox +"\n");
				}
				if(map[k][j]==3)//��
				{
					Xnowman=k;
					Ynowman=j;	
					System.out.println( "Xnowman"+Xnowman+"\n");
					System.out.println( "Ynowman"+Ynowman +"\n");
				}
				if(map[k][j]==2)//Ŀ�ĵ�
				{
					Xdesty=k;
					Ydesty=j;
				}
			
				}
		if(map[Xnowbox][Ynowbox+1]==2 || map[Xnowbox][Ynowbox+1]==0)//���ڿ�յػ���Ŀ�ĵؾͿ��Եݹ���ȥ
			//�ж����ܷ����·�
		A=BoxDFS(Xdesty,Ydesty,Xnowbox,Ynowbox+1,0,1)+manDFS(Xnowbox,Ynowbox-1,Xnowman,Ynowman,0,1);//����
			if(map[Xnowbox][Ynowbox-1]==2 || map[Xnowbox][Ynowbox-1]==0)//���µݹ�
			B=BoxDFS(Xdesty,Ydesty,Xnowbox,Ynowbox-1,0,2)+manDFS(Xnowbox,Ynowbox+1,Xnowman,Ynowman,0,2);//����
			if(map[Xnowbox-1][Ynowbox]==2 || map[Xnowbox-1][Ynowbox]==0)//����ݹ�
			C=BoxDFS(Xdesty,Ydesty,Xnowbox+1,Ynowbox,0,3)+manDFS(Xnowbox+1,Ynowbox,Xnowman,Ynowman,0,4);//����
			if(map[Xnowbox+1][Ynowbox]==2 || map[Xnowbox+1][Ynowbox]==0)//���ҵݹ�
			D=BoxDFS(Xdesty,Ydesty,Xnowbox-1,Ynowbox,0,4)+manDFS(Xnowbox-1,Ynowbox,Xnowman,Ynowman,0,4);//����
		//�ж����Ǹ�����ݹ鲽�����
		shortpath=A;
		if(shortpath>B)
		shortpath=B;
		if(shortpath>C)
		shortpath=C;
		if(shortpath>D)
		shortpath=D;
		return shortpath;
		
	} 
	
	//��һ��DFS����  ����������������·��
	public int caculate_box_shortpath() {	
	int Xnowman = 0,Ynowman = 0,Xnowbox = 0,Ynowbox = 0,Xdesty = 0,Ydesty = 0;
	//�ҵ�box,man,destination��λ��
	for(int k=0;k<7;k++)
		for(int j=0;j<7;j++)
			{if(map[k][j]==1)//����
			{
				Xnowbox=k;
				Ynowbox=j;
				System.out.println( "Xnowbox"+Xnowbox+"\n");
				System.out.println( "Ynowbox"+Ynowbox +"\n");
			}
			if(map[k][j]==3)//��
			{
				Xnowman=k;
				Ynowman=j;	
				System.out.println( "Xnowman"+Xnowman+"\n");
				System.out.println( "Ynowman"+Ynowman +"\n");
			}
			if(map[k][j]==2)//Ŀ�ĵ�
			{
				Xdesty=k;
				Ydesty=j;
			}
		}

	
			//�ж����Ǹ�����ݹ鲽�����
		int shortpath=BoxDFSa(Xdesty,Ydesty,Xnowbox ,Ynowbox,0,0);
	
		return shortpath;
	
} 
	//box����� caculates·������
		public int BoxDFS(int Xd,int Yd,int Xnowbox ,int Ynowbox,int k,int x) {
			//Xd,Yd��ʾҪ����ĵط���Xnowbox��Ynowbox��ʾ���������ĸ�λ�� kΪ���� xΪ�Ƶķ���1���ϣ�2���£�3����4����
			int A,B,C,D;
			int Xnowman = 0, Ynowman = 0;
			A=B=C=D=999;    //��ABCD����ֵ
			System.out.println( "��ʱ�����ӵ���������������ϣ�"+map[Xnowbox][Ynowbox-1]+"�£�"+map[Xnowbox][Ynowbox+1]+"��"+map[Xnowbox-1][Ynowbox]+"�ң�"+map[Xnowbox+1][Ynowbox]);
			//�ж��Ƿ񵽴��յ�		 
			if(Xd== Xnowbox && Yd==Ynowbox)
			{box_mink=k;
			System.out.println( "�����յ�");
			return k;
			}
			System.out.println( "��ǰ������"+k);
			if(k>box_mink)	//�ж��Ƿ񳬹�֮ǰ��С·�����ȣ�������ֱ�ӷ���û��Ҫ�ٽ�����ȥ��
				{System.out.println( "������Сk����");
				return box_mink;
				}
			k++;
			//�ж������Ƿ񵽴�ǽ�ǵ���ǽ�ǾͲ��ý�������Ĳ���ֱ�ӷ���
			if(isincorner(Xnowbox ,Ynowbox)==  true)
			{
			System.out.println( "�ﵽǽ��");
			return 999;
			}
			//�ж������ڵ�λ��
			 switch(x) {
			 case 1:{Xnowman=Xnowbox; Ynowman=Ynowbox-1;};break;
			 case 2:{Xnowman=Xnowbox; Ynowman=Ynowbox+1;};break;
			 case 3:{Xnowman=Xnowbox+1; Ynowman=Ynowbox;};break;
			 case 4:{Xnowman=Xnowbox-1; Ynowman=Ynowbox-1;};break;
			 }
			
			//û�������һ���ݹ�
			if(x!=2)
			{if(  manDFS(Xnowbox,Ynowbox+1,Xnowman,Ynowman,0,0)!=999   &&  (map[Xnowbox][Ynowbox-1]==2 || map[Xnowbox][Ynowbox-1]==0))//���ڿ�յػ���Ŀ�ĵؾͿ��Եݹ���ȥ
				{System.out.println( "������");
				A=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox-1,k,1);
				}
				}
			if(x!=1)
			{if( manDFS(Xnowbox,Ynowbox-1,Xnowman,Ynowman,0,0)!=999   &&  (map[Xnowbox][Ynowbox+1]==2 || map[Xnowbox][Ynowbox+1]==0))
				{System.out.println( "������");
				B=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox+1,k,2);
				}
				}
			if(x!=4)
			{if( manDFS(Xnowbox+1,Ynowbox,Xnowman,Ynowman,0,0)!=999    &&  (map[Xnowbox-1][Ynowbox]==2 || map[Xnowbox-1][Ynowbox]==0))
				{System.out.println( "����");
				C=BoxDFSa(Xd,Yd,Xnowbox-1 ,Ynowbox,k,3);
				}
				}
			if(x!=3)
			{if(manDFS(Xnowbox-1,Ynowbox+1,Xnowman,Ynowman,0,0)!=999   &&  (map[Xnowbox+1][Ynowbox]==2 || map[Xnowbox+1][Ynowbox]==0))
			{	System.out.println( "����");
			D=BoxDFSa(Xd,Yd,Xnowbox+1 ,Ynowbox,k,4);
				}
				}
			//�ж����Ǹ�����ݹ鲽�����
			int ret=A;
			if(ret>B)
			ret=B;
			if(ret>C)
			ret=C;
			if(ret>D)
			ret=D;
			return ret;
			
				
				
			}
			
	//����box����� caculates·���������ÿ������ܷ��� ֻ�ж��¾���
	public int BoxDFSa(int Xd,int Yd,int Xnowbox ,int Ynowbox,int k,int x) {
			//Xd,Yd��ʾҪ����ĵط���Xnowbox��Ynowbox��ʾ���������ĸ�λ�� kΪ���� xΪ�Ƶķ���1���ϣ�2���£�3����4����
			int A,B,C,D;
			A=B=C=D=999;    //��ABCD����ֵ
			System.out.println( "��ʱ�����ӵ���������������ϣ�"+map[Xnowbox][Ynowbox-1]+"�£�"+map[Xnowbox][Ynowbox+1]+"��"+map[Xnowbox-1][Ynowbox]+"�ң�"+map[Xnowbox+1][Ynowbox]);
			//�ж��Ƿ񵽴��յ�		 
			if(Xd== Xnowbox && Yd==Ynowbox)
			{box_mink=k;
			System.out.println( "�����յ�");
			return k;
			}
			System.out.println( "��ǰ������"+k);
			if(k>box_mink)	//�ж��Ƿ񳬹�֮ǰ��С·�����ȣ�������ֱ�ӷ���û��Ҫ�ٽ�����ȥ��
				{System.out.println( "������Сk����");
				return box_mink;
				}
			k++;
			//�ж������Ƿ񵽴�ǽ�ǵ���ǽ�ǾͲ��ý�������Ĳ���ֱ�ӷ���
			if(isincorner(Xnowbox ,Ynowbox)==  true)
			{
			System.out.println( "�ﵽǽ��");
			return 999;
			}
			//û�������һ���ݹ�
			if(x!=2)
			{if( (map[Xnowbox][Ynowbox+1]==0||map[Xnowbox][Ynowbox+1]==1)   &&  (map[Xnowbox][Ynowbox-1]==2 || map[Xnowbox][Ynowbox-1]==0))//���ڿ�յػ���Ŀ�ĵؾͿ��Եݹ���ȥ
			{System.out.println( "������");A=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox-1,k,1);
			}
			}
			if(x!=1)
			{if( (map[Xnowbox][Ynowbox-1]==0||map[Xnowbox][Ynowbox-1]==1)  &&  (map[Xnowbox][Ynowbox+1]==2 || map[Xnowbox][Ynowbox+1]==0))
				{System.out.println( "������");B=BoxDFSa(Xd,Yd,Xnowbox ,Ynowbox+1,k,2);
			}
				}
			if(x!=4)
			{if( (map[Xnowbox+1][Ynowbox]==0||map[Xnowbox+1][Ynowbox]==1)    &&  (map[Xnowbox-1][Ynowbox]==2 || map[Xnowbox-1][Ynowbox]==0))
				{System.out.println( "����");C=BoxDFSa(Xd,Yd,Xnowbox-1 ,Ynowbox,k,3);
			}
				}
			if(x!=3)
			{if( (map[Xnowbox-1][Ynowbox]==0||map[Xnowbox-1][Ynowbox]==1)   &&  (map[Xnowbox+1][Ynowbox]==2 || map[Xnowbox+1][Ynowbox]==0))
			{	System.out.println( "����");D=BoxDFSa(Xd,Yd,Xnowbox+1 ,Ynowbox,k,4);
			}
				}
			//�ж����Ǹ�����ݹ鲽�����
			int ret=A;
			if(ret>B)
			ret=B;
			if(ret>C)
			ret=C;
			if(ret>D)
			ret=D;
			return ret;
			
			
		}
		
	//man�����·������
	public int manDFS(int Xd,int Yd,int Xnowman ,int Ynowman,int k,int x) {
		//Xd,Yd��ʾҪ����ĵط���Xnowman��Ynowman��ʾ���������ĸ�λ��   x��
		
		int A,B,C,D;
		A=B=C=D=999;//��ABCD����ֵ
	
		System.out.println( "������"+k);
		System.out.println( "��С������"+man_mink);
		if(k>man_mink)			//�ж��Ƿ񳬹�֮ǰ��С·�����ȣ�������ֱ�ӷ���û��Ҫ�ٽ�����ȥ��
		return man_mink;
		if(Xd== Xnowman && Yd==Ynowman)
		{man_mink=k;
		return k;
		}
		k++;
		//û�������һ���ݹ�
		if(x!=2)
		{if(map[Xnowman][Ynowman-1]==2 || map[Xnowman][Ynowman-1]==0)//���ϵݹ�  ���ڿ�յػ���Ŀ�ĵؾͿ��Եݹ���ȥ
		{
		A=manDFS(Xd,Yd,Xnowman ,Ynowman-1,k,1);
		}
		}
		if(x!=1)
		if(map[Xnowman][Ynowman+1]==2 || map[Xnowman][Ynowman+1]==0)//���µݹ�
		{
		B=manDFS(Xd,Yd,Xnowman ,Ynowman+1,k,2);
		
		}
		if(x!=4)
		if(map[Xnowman-1][Ynowman]==2 || map[Xnowman-1][Ynowman]==0)//����ݹ�
		{
		C=manDFS(Xd,Yd,Xnowman-1 ,Ynowman,k,3);
		
		}
		if(x!=3)
		if(map[Xnowman+1][Ynowman]==2 || map[Xnowman+1][Ynowman]==0)//���ҵݹ�
		{
		D=manDFS(Xd,Yd,Xnowman+1,Ynowman,k,4);
		
		}//�ж����Ǹ�����ݹ鲽�����
		int ret=A;
		if(ret>B)
		ret=B;
		if(ret>C)
		ret=C;
		if(ret>D)
		ret=D;
		return ret;
	}
	
	//ͼ�����
    public class Square extends JPanel
	{
		private Graphics g;
		private Image frontImage,backImage;
        private Graphics ft,bg;
		
		public Square()
		{
			backImage = new BufferedImage( XSIZE*SIZE, YSIZE*SIZE, 1);
   	        bg = backImage.getGraphics();                  //����ͼ�񻺴�        
   	        frontImage = new BufferedImage( XSIZE*SIZE, YSIZE*SIZE,  1);
   	        ft = frontImage.getGraphics();                 //ǰ��ͼ�񻺴�
   	        initBack();
   	        initGraphic();
		}
		public void paintComponent( Graphics g )
		{
			super.paintComponent(g);
			g.drawImage(frontImage, 0, 0, this);           //��ʾǰ��ͼ��
		}
		
		public void initGraphic()
		{
			ft.drawImage(backImage, 0, 0, this);           //ˢ�±���ͼ��
		}
		
		public void initBack()                             //��ʼ������ͼ��
		{
			bg.setColor(Color.LIGHT_GRAY);
   	        bg.fillRect( 0, 0, XSIZE*SIZE, YSIZE*SIZE );
   	        bg.setColor( Color.WHITE );
            for ( int i = 0; i < XSIZE; i++ )
              for ( int j = 0; j < YSIZE; j++ )
                bg.drawRect(i * SIZE, j * SIZE, SIZE,SIZE);    
        }
		
		public void setcolor( int x,int y,int color )               //����x��yλ��Ϊ��״̬
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
		
		public void setNew()                             //�ػ�
		{
			repaint();
		}
	}//end class Square

//******************************��ʼ����ť**************************************
    public void initButton()
    {
    	String option[] = {"��ʼ��Ϸ","����·��"," ���¿�ʼ "};
    	controlPane.add( new JLabel("     �������") );
    	for( int i=0; i<button.length; i++ )
    	{
    		button[ i ] = new JButton( option[ i ] );
    		button[ i ].addActionListener( this );
    		controlPane.add( button[i] );
    	}
    }

    

	

//******************************�����Ӧ�¼�************************************    
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

 //*****************************��ť��Ӧ�¼�*************************************    
    public void actionPerformed( ActionEvent event )
    {
    	if( event.getSource() == button[0] )	//��ʼ��Ϸ
    	{
    		
    	}                
    	else if( event.getSource() == button[1])//����·��
    	{//shortest_path= manDFS(3,3,4,4,0,0);
    	shortest_path=caculateshortpath() ;
    		//shortest_path= caculate_box_shortpath();
    		   JOptionPane.showMessageDialog( null,
    		    	   "����ʹ�ò���Ϊ"+shortest_path ,"����ʹ�ò���",JOptionPane.PLAIN_MESSAGE );
    	}
    	else if( event.getSource() == button[2] )//���¿�ʼ��Ϸ
    	{
    		
    	}
    	
    }


    //******************************main����****************************************		
	public static void main(String[] args){
		manage application = new manage();
		application.setResizable( false );//windows can not resizable
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}	
}

