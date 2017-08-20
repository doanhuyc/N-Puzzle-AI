/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package puzzle;

/**
 *
 * @author Huy Cuong Doan
 */
import java.util.*;

public class Astar {
    public Node initialnode;  // nút ban đầu
    public Node goalnode;     // nút đích
    private Node n;            
    private Node tempNode;
    
    private Vector<Node> FRINGE; // tập trạng thái đã sinh ra chưa được xét   
    private Vector<Node> M;      // tập trạng thái con của 1 trạng thái bất kì   
    public Vector<Node> KQ;     // tập trạng thái từ trạng thái hiện tại tới trạng thái đích
    public Vector<Node> VISIT;  // tập trạng thái có f vượt ngưỡng cutOff
    
    private int fmin;          
    private int lowIndex; // vị trí Node có fmin
    private int number;  
    private int cutOff;  
    
    protected String Stop;
    protected long time_solve; //thời gian giải quyết
    protected int total_nodes; // tổng nút trên cây
    protected int count = 0;    // tổng nút đã duyệt
    
    public Astar()
    {
        FRINGE = new Vector<Node>(); 
        M = new Vector<Node>();
        KQ = new Vector<Node>();
        VISIT = new Vector<Node>();        
    }
     public void solveAstar() { //Giải quyết A*
        KQ.clear();
        long startTime = System.currentTimeMillis();
        
        initialnode.f = initialnode.h = initialnode.estimate(goalnode);
        initialnode.g = 0;        
        FRINGE.add(0, initialnode); // cho nút đầu đầu tiên vào FRINGE
        total_nodes = 0;
        count = 0;
        
        while (true)
        {
            if (FRINGE.isEmpty() || !(Main.window.issolu)) //điều kiện dừng
            {
                FRINGE.clear();
                M.clear();
                Stop = "stop";   
                return;
            }
            if (System.currentTimeMillis()- startTime > 180000) //điều kiện dừng
            {
                FRINGE.clear();
                M.clear();
                Stop = " => Khó vãi :D \n";   
                return;
            }
            lowIndex = 0;
            fmin = FRINGE.elementAt(0).f;
            for (int i = 0; i < FRINGE.size(); i++) // tìm nút có f nhỏ nhất trong FRINGE
            {
                number = FRINGE.elementAt(i).f;
                if (number < fmin)
                {                    
                    lowIndex = i;   //vị trí nút có fmin trong FRINGE
                    fmin = number; 
                }
            }

            n = FRINGE.elementAt(lowIndex);  //xét nút n có fmin
            FRINGE.removeElement(n);    //xóa nút đã xét trong FRINGE

            if(n.h == 0)   //if (n.equals(goalnode))   //kiểm tra nút đang xét có phải là đích
            {
                long endTime = System.currentTimeMillis();
                time_solve = endTime - startTime;                 
                total_nodes = count + FRINGE.size();
                AddKQ(n);       //đưa kết quả vào trong KQ
                FRINGE.clear();
                M.clear();
                return;
            }

            M = n.successors(); // sinh tâp trạng thái con của n
            if(n.Parent != null) 
            {
                for(int i = 0; i < M.size(); i++)
                {
                    if(isKT(n.Parent,M.elementAt(i)))  // xóa trạng thái con của n trùng với trạng thái Cha(n)
                        M.remove(i);
                }
            }

            for (int i = 0; i < M.size(); i++) // tính thông số của các trạng thái con
            {
                Node s = M.elementAt(i);
                s.g = n.g + s.cost;
                s.h = s.estimate(goalnode);
                s.f = s.g + s.h;
                
                tempNode = (Node)M.elementAt(i);
                tempNode.Parent = n;  // đặt n là cha các trạng thái con
                
                FRINGE.add(0, M.elementAt(i)); // thêm các trạng thái con vào FRINGE
            }
            count++;  //tăng số nút đã duyệt
        }        
    }

    public void solveIDAstar() { //giải quyết IDA*
        KQ.clear();
        long startTime = System.currentTimeMillis();
        
        initialnode.f = initialnode.h = initialnode.estimate(goalnode);
        initialnode.g = 0;   
        
        FRINGE.add(0, initialnode);
        cutOff = 0;
        total_nodes = 0;
        count = 0;
        while (true)
        {
            while (FRINGE.size() != 0) //quá trình tìm kiếm sâu dần
            {                
                if (FRINGE.isEmpty() || !(Main.window.issolu)) 
                {
                    FRINGE.clear();
                    M.clear();
                    VISIT.clear();
                    Stop = "stop";
                    return;
                }
                if (System.currentTimeMillis()- startTime > 180000) //điều kiện dừng
                {
                    FRINGE.clear();
                    M.clear();
                    Stop = " => Khó vãi :D \n"; 
                    return;
                }
                n = FRINGE.elementAt(0);  //lấy nút đầu tiên trong FRINGE
                if(n.f <= cutOff) count++; //nếu nút n có f ko vượt ngưỡng cutOff thì n là nút đã được xét
                FRINGE.removeElement(n); //xóa nút n
                
                if(n.h == 0)   //if (n.equals(goalnode)) //kiểm tra n là đích chưa
                {
                    long endTime = System.currentTimeMillis();
                    time_solve = endTime - startTime;                    
                    total_nodes = count + FRINGE.size() + VISIT.size();
                    AddKQ(n);
                    FRINGE.clear();
                    VISIT.clear();
                    M.clear();
                    return;
                }
                if (n.f <= cutOff)  //nếu nút n có f ko vượt ngưỡng cutOff thì xét n
                {                   
                    M = n.successors(); // sinh các trạng thái con của n
                    if(n.Parent!=null)
                    {
                        for(int i = 0; i < M.size(); i++)
                        {
                            if(isKT(n.Parent,M.elementAt(i))) // xóa trạng thái con của n trùng với trạng thái Cha(n)
                                M.remove(i);
                        }
                    }
                    for (int i = 0; i < M.size(); i++) 
                    {
                        Node s = M.elementAt(i);
                        s.g = n.g + s.cost;
                        s.h = s.estimate(goalnode);
                        s.f = s.g + s.h;
                        
                        tempNode = (Node)M.elementAt(i);
                        tempNode.Parent = n;                
                    
                        FRINGE.add(0, M.elementAt(i));
                    }
                }
                else  VISIT.add(0, n); // nếu n.f vượt ngưỡng cutOff thì thêm n vào VISIT
                
            }//end while
            
            
            if (VISIT.size() == 0) {
                System.out.println("Failure! Can't solve problem, exiting...");
		return;
            }
            
            fmin = VISIT.elementAt(0).f;
            for (int i = 0; i < VISIT.size(); i++)
            {
                number = VISIT.elementAt(i).f;
                if (number < fmin) fmin = number; //tìm nút có f nhỏ nhất trong VISIT
            }
            cutOff = fmin;// đặt ngưỡng cutOff là fmin
            for (int i = 0; i < VISIT.size(); i++) 
                FRINGE.add(0, VISIT.elementAt(i));
            VISIT.clear();           
        } 
    }     
     
     public boolean isKT(Node n, Node v) //kiểm tra hai nút có trùng nhau không
     {
         if(n.equals(v)) return true;
         return false;
     }

    public void AddKQ(Node n)//đưa kết quả vào KQ
    {
        if(n.Parent!=null)
        {
            AddKQ(n.Parent);
            KQ.add(n);
        }
        else KQ.add(n);
    }

}