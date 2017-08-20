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

public class State {
    static int heuristic = 0;
    public int[] Value;
    private int Size = 3;
    private int Length;
    private int blank;
    private int count = 0;
    public State(int m)
    {
        this.Size=m;
        this.Length = Size*Size;
        this.Value=new int[Length];
        this.blank = 0;
    }

    public State(int[] v, int size)
    {
        Value = v;
        Size=size;
        Length=Size*Size;
    }
    
    public int[] StateArray()
    {
        return Value;
    }


    public void Init()
    {
        for (int i=0; i < Length; i++)
        {
           Value[i]=i;
        }
    }

    public int[] createArrayRandom()
    {
        Init();
        Random rand = new Random();
        int ri;
        for (int i=0; i< Length ; i++) {
            while ((ri = rand.nextInt(Length)) == i);

            int tmp = Value[i];
            Value[i] = Value[ri];
            Value[ri] = tmp;
        }         
        return Value;
    }  
    
    public int[] ArrayTronHinh()
    {      
        int t = 65;        
        Init();
        this.blank=0;
        count = 0;
        int a = 1, b = 0;
        Random rand1 = new Random();
        while(true)
        { 
            switch(a)
            {
                case 1: DOWN();break;
                case 2: RIGHT();break;
                case 3: LEFT();break;
                case 4: UP();break;
            }
            while(true)
            {
                b = rand1.nextInt(4)+1;                
                if((a == 1 && b != 4) || (a == 4 && b != 1) || (a == 2 && b != 3) || (a == 3 && b != 2) ) 
                {
                    a = b;
                    break;
                }
            }            
            if(count == t) break;
        }
//        int[] val = {5,3,1,4,8,15,6,7,10,0,14,2,9,12,13,11};
//        if(Size == 4) Value = val;
        return Value;
    }    
    
    public boolean Test(int[] val) //kiểm tra trạng thái có hợp lệ không
    {
        int row = 0;
        int Count =0;
        int posBlank = 0;
        for(int i = 0; i < Length; i++)
            if(val[i] == 0) 
            {
                posBlank = i;
                break;
            }
        row = posBlank / Size + 1;
        for(int i=0; i < Length; i++)
        {
            int t = val[i];
            if(t > 0 && t < Length)
            {
                for(int j = i + 1; j < Length; j++)
                    if(val[j] < t && val[j] > 0)
                        Count++;
            }
        }

        if( Size % 2 ==1)
        {
            return Count % 2 == 0;
        }
        else
        {
            return Count % 2 == (row+1) % 2;
        }
    }

    public boolean equals(State state) {  // kiểm tra xem 2 trạng thái có trùng nhau ko
        State s = (State)state;
        boolean flag = true;
        for (int i = 0; i < Length; i++)
            if (Value[i] != s.Value[i])
            {
                flag = false;
                break;
            }
        return flag;
    }

    public int findBlank()  // tìm vị trí ô trống
    {
      int i=0;
      for(; i< Length; i++)
          if(Value[i]==0) break;
      return i;
    }

  public Vector<State> successors() // tạo tập trạng thái mới
  {
      Vector<State> s = new Vector<State>();
      int blank = 0;
      for (int i = 0; i < Length; i++)
          if (Value[i] == 0)
          {
              blank = i;
              break;
          }      
      if ((blank / Size) > 0)
      {
          addSuccessor(blank, blank - Size, s, Value); //UP
      }
      if ((blank / Size) < Size-1)
      {
          addSuccessor(blank, blank + Size, s, Value); //DOWN
      }
      if ((blank % Size) > 0)
      {
          addSuccessor(blank, blank - 1, s, Value); //LEFT
      }
      if ((blank % Size) < Size-1)
      {
          addSuccessor(blank, blank + 1, s, Value);//RIGHT
      }
	 return s;
  }


  private void addSuccessor(int old_loc, int new_loc, Vector<State> v, int[] old)  // Lưu trữ trạng thái con
  {
      int[] val = (int[])old.clone();
      val[old_loc] = val[new_loc];
      val[new_loc] = 0;
      v.add(0, new State(val, Size));
  }

 
  public int estimate(State goalstate) // lựa chọn hàm heuristic
  {
      int est = 0;
      if (heuristic == 1) est = heuristic1(goalstate);
      else if (heuristic == 2) est = heuristic2(goalstate);
      else if (heuristic == 3) est = heuristic3(goalstate);
      return est;
    }

  //tổng khoảng cách dịch chuyển ngắn nhất để dịch chuyển các ô sai về vị trí đúng của nó: Manhattan
  public int heuristic1(State goalstate) 
  {
      int[] goal = goalstate.Value;
      int distance = 0;
      for (int i = 0; i < Length; i++)
      {
          int c = Value[i];
          int v = 0;
          for (int j = 0; j < Length; j++)
              if (c == goal[j])
              {
                  v = j;
                  break;
              }
          if (c != 0)
          {
              int xd = Math.abs((i % Size) - (v % Size));
              int yd = Math.abs((i / Size) - (v / Size));
              distance += xd + yd;
          }
      }

      return distance;
  } 

   //hàm h2 = heuristic1 + chỉ số phạt cặp ô hàng xóm với nhau đang nằm ngược vị trí của nhau
  public int heuristic2(State goalstate)
  {
      int[] goal = goalstate.Value;
      int distance = 0;
      int a = 0;
      for (int i = 0; i < Length; i++)
      {
          int c = Value[i];
          int v = 0;
          for (int j = 0; j < Length; j++)
              if (c == goal[j])
              {
                  v = j;
                  break;
              }
          if (c != 0)
          {
              int xd = Math.abs((i % Size) - (v % Size));
              int yd = Math.abs((i / Size) - (v / Size));
              distance += xd + yd;
          }
          //Tính chỉ số phạt
          if((i != 0) && (i % Size != Size-1) && (Value[i] == i+1) && (Value[i+1] == i)) a += 2;
          if((i != 0) && (i < Length - Size) && (Value[i] == i+Size)&& (Value[i+Size] == i)) a += 2;
      }

      return distance + a;
  }
  
  public int heuristic3(State goalstate)    //Hàm ước lượng đề xuất: thống kê nhiều trạng thái thấy hàm này tốt hơn 2 hàm kia
  {
      int[] goal = goalstate.Value;
      int distance = 0;
      int a = 0;
      for (int i = 0; i < Length; i++)
      {
          int c = Value[i];
          int v = 0;
          for (int j = 0; j < Length; j++)
              if (c == goal[j])
              {
                  v = j;
                  break;
              }
          if (c != 0)
          {
              int xd = (i % Size) - (v % Size);
              int yd = (i / Size) - (v / Size);
              distance += xd*xd + yd*yd;
          }
          //Tính chỉ số phạt
          if((i != 0) && (i % Size != Size-1) && (Value[i] == i+1) && (Value[i+1] == i)) a += 2;
          if((i != 0) && (i < Length - Size) && (Value[i] == i+Size)&& (Value[i+Size] == i)) a += 2;
      }      
      distance = distance - (int)(0.15*distance) + a;
      return distance;
  }    
   
    public void DOWN()
    {
        int temp;
            if(blank < Length-Size)
            {
                temp = Value[blank];
                Value[blank] = Value[blank+Size];
                Value[blank+Size] = temp;
                blank=blank+Size;
                count++;
            }
            else return;
    }

    public void UP()
    {
        int temp;
            if(blank >= Size)
            {
                temp = Value[blank];
                Value[blank] = Value[blank-Size];
                Value[blank-Size] = temp;
                blank=blank-Size;
                count++;
            }
            else return;
    }

    public void RIGHT()
    {
        int temp;
            if(blank % Size !=Size-1)
            {
                temp = Value[blank];
                Value[blank] = Value[blank+1];
                Value[blank+1] = temp;
                blank=blank+1;
                count++;
            }
            else return;
    }

    public void LEFT()
    {
        int temp;
            if(blank % Size != 0 )
            {
                temp = Value[blank];
                Value[blank] = Value[blank-1];
                Value[blank-1] = temp;
                blank=blank-1;
                count++;
            }
            else return;
    } 
  
}
  
  