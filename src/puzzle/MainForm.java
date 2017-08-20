/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainForm.java
 *
 * Created on Oct 24, 2011, 8:18:33 PM
 */
package puzzle;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author Huy Cuong Doan
 */
public class MainForm extends javax.swing.JFrame implements MouseListener, KeyListener, Runnable {

    protected int Size = 3;
    private int Length = 9;
    private State state;
    private JumbledImage Ju;            // Đối tượng khung chứa các ô chữ
    private ViewImage Vi;                //Tạo ảnh gợi ý
    private int[] Value;                //mảng trạng thái trò chơi
    private Astar astar;                // A* & IDA*
    private Sound  sound;                // âm thanh
    protected int count = 0;            // đếm bước di chuyển
    private int times = 0;              //đếm thời gian
    protected int typegame = 0;         //loại game: số , ảnh, ảnh và số
    protected boolean issolu=false;         // biến boolean  issolu = true khi tự động tìm lời giải(sau khi nhấn button Solve)
    protected boolean win = false;          // khi hoàn thành trò chơi win = true
    private boolean  playtime = false;          // khi đếm thời gian playtime =  true
    private Image image;
    private OptionFrame Op;                     // Các lựa chọn
    protected Color ColorEBox = Color.green;      // màu ô trống
    protected Color ColorBoxs = Color.blue;       // màu ô chữ
    protected int typeImage = 0;                  // lựa chọn hình ảnh có sẵn
    protected boolean mute = false;                // mute = true tắt âm thanh
    protected int speed = 1000;                    // chỉnh tốc độ khi tự động chạy
    private MainForm JF;                           // Đối tượng MainForm
    private GameWon Gw;                             // Khung hiển thị khi game won
    private JProgressBar progressBar;               //
    
    public MainForm() {
        initComponents(); 
        progressBar = new JProgressBar();
        progressBar.setLocation(525, 445);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setBorderPainted(true);
        progressBar.setSize(100, 20);
        add(progressBar);
        
        Init1(); 
        addKeyListener(this);
        JF = this;
        this.NewGame(); 
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch(Exception e){
            throw new RuntimeException("Could not set default look and feel");
        }
        setFocusable(true);
    }
    
    public void Init1() //Khởi tạo RadioButton Group
    {
        buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(size1);
        buttonGroup1.add(size2);
        buttonGroup1.add(size3);
        buttonGroup1.add(size4);
        size1.setSelected(true);
        
        buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(number);
        buttonGroup2.add(imageNumber);
        buttonGroup2.add(images);
        number.setSelected(true);
        
        buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(h1);
        buttonGroup3.add(h2);
        buttonGroup3.add(h3);
        h3.setSelected(true);
        
        buttonGroup4 = new ButtonGroup();
        buttonGroup4.add(astarRadio);
        buttonGroup4.add(idastarRadio);
        astarRadio.setSelected(true);        
    }

    public void Init()
    {        
        issolu = false;  
        if(Size == 3) size1.setSelected(true);
        else if(Size == 4) size2.setSelected(true);
        else if(Size == 5) size3.setSelected(true);
        else if(Size == 6) size4.setSelected(true);
        
        if(typegame == 0) number.setSelected(true);
        else if(typegame == 1) imageNumber.setSelected(true);
        else if(typegame == 2) images.setSelected(true);
        
        Value  = new int[Size*Size];
        state = new State(Size);        
        sound = new Sound();   
        astar = new Astar();        
    }
    
    public void NewGame()   //Bắt đầu game mới
    {
        Init();
        Length= Size * Size;
        win = false;                        //Đặt lại biến win
        if(Ju != null) this.remove(Ju);    //Xóa khung chứa ô chữ cũ
        if(Vi != null) this.remove(Vi);     //Xóa khung ảnh gợi ý
        if(Gw != null) 
        {
            Gw = null;
            this.removeMouseListener(this);
        }
        count = 0;
        times = 0;                
        playtime = false;
        time.setText("   00 : 00");     //Đặt lại đồng hồ đếm thời gian
        display.setText("");
        moveTextField.setText("");
        if(Size < 4)        //Sinh ngẫu nhiên trạng thái 3x3. Kích thước > 3x3 => độ phức tạp cao nên trộn hình
        {
            do {
                Value = state.createArrayRandom();//tạo trạng thái xuất phát
            }while (!state.Test(Value));//kiểm tra xem trạng thái có hợp lệ ko
        }
        else Value = state.ArrayTronHinh();//trộn trạng thái đích tới 1 trạng thái bất kì
        InitFileImage();
        Ju = new JumbledImage(image, Size, Value, typegame, ColorEBox, ColorBoxs); //Khởi tạo khung hình
        
        //Đặt vị trí khung hình trên Frame
        if(typegame == 0) Ju.setLocation(380, 20);
        else
        {
            //Đặt tọa độ cân đối cho đẹp mắt
            int x1 = 370 + (430 - Ju.getWidth())/2;
            int y1 = 6 + (430 - Ju.getHeight())/2;
            Ju.setLocation(x1, y1);
        }
        Ju.setSize(Ju.getWidth(), Ju.getHeight()); //Đặt kích thước khung hình
        Ju.addMouseListener(this);      //Lắng nghe sự kiện chuột
        this.add(Ju);                   
        this.repaint();               
        solveButton.setEnabled(true);
        solveButton.setText("Solve");
        JF.requestFocus();        //Có tác dụng khi bắt sự kiện phím
    }  
    
    public void ChangeView()
    {
        if(Ju != null) this.remove(Ju); 
        if(Vi != null) this.remove(Vi);
        if(typegame == 0) number.setSelected(true);
        else if(typegame == 1) imageNumber.setSelected(true);
        else if(typegame == 2) images.setSelected(true);
        InitFileImage();
        Ju = new JumbledImage(image, Size, Value, typegame, ColorEBox, ColorBoxs);
        if(typegame == 0) Ju.setLocation(380, 20);
        else
        {
            int x1 = 370 + (430 - Ju.getWidth())/2;
            int y1 = 6 + (430 - Ju.getHeight())/2;
            Ju.setLocation(x1, y1);
        }
        Ju.setSize(Ju.getWidth(), Ju.getHeight());
        Ju.addMouseListener(this);        
        this.add(Ju);
        this.repaint(); 
        JF.requestFocus();
    }
    
    public void InitFileImage() // lựa chọn hình ảnh
    {
        if(typeImage == 0) 
        {
            try {                
                image = (new ImageIcon(getClass().getResource("/images/aaa.png"))).getImage();
            } catch (Exception e) {}
        }
        else if(typeImage == 1) 
        {
            try {
                image = (new ImageIcon(getClass().getResource("/images/bbb.png"))).getImage();
            } catch (Exception e) {}
        }            
        else if(typeImage == 2)
        {
            try {
                image = (new ImageIcon(getClass().getResource("/images/ccc.png"))).getImage();
            } catch (Exception e) {}
        }
        else if(typeImage == 3)
        {
            try {
                image = (new ImageIcon(getClass().getResource("/images/ddd.png"))).getImage();
            } catch (Exception e) {}
        }
        else if(typeImage == 4)
        {
            try {
                image = (new ImageIcon(getClass().getResource("/images/eee.png"))).getImage();
            } catch (Exception e) {}
        }
        else if(typeImage == 5)
        {
            try {
                image = (new ImageIcon(getClass().getResource("/images/fff.png"))).getImage();
            } catch (Exception e) {}
        }      
        
        if(image != null && typegame > 0)
        {
            Vi = new ViewImage(image);
            Vi.setLocation(520, 475);
            Vi.setSize(Vi.w, Vi.h);
            this.add(Vi);
            this.repaint();
        }
    }
    
    public void SaveGame()
    {
        try{
            FileOutputStream fo = new FileOutputStream("PuzzleSave.DL");
            fo.write(Size);
            fo.write(typegame);           
            fo.write(typeImage);            
            int[] val = Ju.getValue();
            for(int i = 0; i < Length; i++)
                fo.write(val[i]);
            fo.close();
        }catch(IOException e)
        {        
            JOptionPane.showMessageDialog(null,"Unable to write data to your storage device !","Warning", 0);
        }
    }
    
    public void LoadGame()
    {
        try{
            FileInputStream fi  = new FileInputStream("PuzzleSave.DL");
            Size = fi.read();
            typegame = fi.read();
            typeImage = fi.read();
            Length = Size * Size;          
            if(typeImage == 6) typeImage = 0; 
            if(Size == 3) size1.setSelected(true);
            else if(Size == 4) size2.setSelected(true);
            else if(Size == 5) size3.setSelected(true);
            else if(Size == 6) size4.setSelected(true);
        
            if(typegame == 0) number.setSelected(true);
            else if(typegame == 1) imageNumber.setSelected(true);
            else if(typegame == 2) images.setSelected(true);
            if(Ju != null) this.remove(Ju);
            if(Vi != null) this.remove(Vi);
            count = 0;
            Value  = new int[Size*Size];
            display.setText("");
            for(int i = 0; i < Length; i++)
            {
                Value[i] = fi.read();
            }
            fi.close();
            moveTextField.setText("");
            time.setText("   00 : 00");
            times = 0;
            playtime = false;
            Ju = new JumbledImage(image, Size, Value, typegame, ColorEBox, ColorBoxs);
            if(typegame == 0) Ju.setLocation(380, 20);
            else
            {
                int x1 = 370 + (430 - Ju.getWidth())/2;
                int y1 = 6 + (430 - Ju.getHeight())/2;
                Ju.setLocation(x1, y1);
            }
            Ju.setSize(Ju.getWidth(), Ju.getHeight());
            Ju.addMouseListener(this);
            this.add(Ju);
            this.repaint();

            if(image != null && typegame > 0)
            {
                Vi = new ViewImage(image);
                Vi.setLocation(520, 475);
                Vi.setSize(Vi.w, Vi.h);
                this.add(Vi);
                this.repaint();
            }
            solveButton.setEnabled(true);
            solveButton.setText("Solve");
            issolu = false;
        }catch(IOException e)
        {
            JOptionPane.showMessageDialog(null,"Can not read file !","Warning", 0);
        }
        JF.requestFocus();
    }
    
    private void Disable()
    {
        if(Ju != null)
        {
            Ju.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        Panel1 = new javax.swing.JPanel();
        heuristic = new javax.swing.JPanel();
        h2 = new javax.swing.JRadioButton();
        h1 = new javax.swing.JRadioButton();
        h3 = new javax.swing.JRadioButton();
        typesolve = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        astarRadio = new javax.swing.JRadioButton();
        idastarRadio = new javax.swing.JRadioButton();
        sizePanel = new javax.swing.JPanel();
        size2 = new javax.swing.JRadioButton();
        size3 = new javax.swing.JRadioButton();
        size1 = new javax.swing.JRadioButton();
        size4 = new javax.swing.JRadioButton();
        typegamePanel = new javax.swing.JPanel();
        images = new javax.swing.JRadioButton();
        number = new javax.swing.JRadioButton();
        imageNumber = new javax.swing.JRadioButton();
        newGameButton = new javax.swing.JButton();
        solveButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        time = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        moveTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        display = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        newGame = new javax.swing.JMenu();
        newGameMenu = new javax.swing.JMenuItem();
        saveGameMenu = new javax.swing.JMenuItem();
        loadGameMenu = new javax.swing.JMenuItem();
        exitGameMenu = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        addImageMenu = new javax.swing.JMenuItem();
        optionMenu = new javax.swing.JMenu();
        option = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        viewHelpMenu = new javax.swing.JMenuItem();
        aboutMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("N-Puzzle Game");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage((new ImageIcon(getClass().getResource("/images/Puzzle.png"))).getImage());

        Panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        heuristic.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Heuristic"));

        h2.setText("heuristic2");

        h1.setText("heuristic1");

        h3.setText("heuristic3");

        javax.swing.GroupLayout heuristicLayout = new javax.swing.GroupLayout(heuristic);
        heuristic.setLayout(heuristicLayout);
        heuristicLayout.setHorizontalGroup(
            heuristicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(heuristicLayout.createSequentialGroup()
                .addGroup(heuristicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(h1)
                    .addComponent(h2)
                    .addComponent(h3, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                .addContainerGap())
        );
        heuristicLayout.setVerticalGroup(
            heuristicLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(heuristicLayout.createSequentialGroup()
                .addComponent(h1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(h2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(h3)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        typesolve.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Type Solve"));

        jRadioButton4.setText("IDAstar");

        astarRadio.setText("Astar");

        idastarRadio.setText("IDAstar");

        javax.swing.GroupLayout typesolveLayout = new javax.swing.GroupLayout(typesolve);
        typesolve.setLayout(typesolveLayout);
        typesolveLayout.setHorizontalGroup(
            typesolveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(typesolveLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(typesolveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(typesolveLayout.createSequentialGroup()
                        .addComponent(jRadioButton4, 0, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idastarRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(astarRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        typesolveLayout.setVerticalGroup(
            typesolveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, typesolveLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(astarRadio)
                .addGap(18, 18, 18)
                .addGroup(typesolveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, typesolveLayout.createSequentialGroup()
                        .addComponent(idastarRadio)
                        .addGap(22, 22, 22))))
        );

        sizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Board size"));

        size2.setText("4x4");
        size2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size2ActionPerformed(evt);
            }
        });

        size3.setText("5x5");
        size3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size3ActionPerformed(evt);
            }
        });

        size1.setText("3x3");
        size1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size1ActionPerformed(evt);
            }
        });

        size4.setText("6x6");
        size4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                size4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sizePanelLayout = new javax.swing.GroupLayout(sizePanel);
        sizePanel.setLayout(sizePanelLayout);
        sizePanelLayout.setHorizontalGroup(
            sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(size1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(size2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(size4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(size3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        sizePanelLayout.setVerticalGroup(
            sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sizePanelLayout.createSequentialGroup()
                .addComponent(size1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(size2)
                .addGap(3, 3, 3)
                .addComponent(size3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(size4)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        typegamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Type game"));

        images.setText("Image");
        images.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imagesActionPerformed(evt);
            }
        });

        number.setText("Number");
        number.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numberActionPerformed(evt);
            }
        });

        imageNumber.setText("Image-Number");
        imageNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageNumberActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout typegamePanelLayout = new javax.swing.GroupLayout(typegamePanel);
        typegamePanel.setLayout(typegamePanelLayout);
        typegamePanelLayout.setHorizontalGroup(
            typegamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(number, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(imageNumber)
            .addComponent(images, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        typegamePanelLayout.setVerticalGroup(
            typegamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(typegamePanelLayout.createSequentialGroup()
                .addComponent(number)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(imageNumber)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(images)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        newGameButton.setText("New Game");
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });

        solveButton.setText("Solve");
        solveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solveButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Time");

        time.setEditable(false);
        time.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Move");

        moveTextField.setEditable(false);
        moveTextField.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout Panel1Layout = new javax.swing.GroupLayout(Panel1);
        Panel1.setLayout(Panel1Layout);
        Panel1Layout.setHorizontalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addComponent(sizePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(typegamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addComponent(heuristic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(typesolve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(solveButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newGameButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(moveTextField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(time, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        Panel1Layout.setVerticalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(typegamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sizePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(typesolve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(heuristic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(newGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(solveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(moveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        display.setColumns(20);
        display.setEditable(false);
        display.setFont(new java.awt.Font("Tahoma", 0, 13));
        display.setRows(5);
        display.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(display);

        newGame.setText("File");
        newGame.setFont(new java.awt.Font("Tahoma", 0, 12));

        newGameMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newGameMenu.setFont(new java.awt.Font("Tahoma", 0, 12));
        newGameMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        newGameMenu.setText("New Game");
        newGameMenu.setPreferredSize(new java.awt.Dimension(177, 22));
        newGameMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                newGameMenunewGameMousePressed(evt);
            }
        });
        newGame.add(newGameMenu);

        saveGameMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveGameMenu.setFont(new java.awt.Font("Tahoma", 0, 12));
        saveGameMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        saveGameMenu.setText("Save Game");
        saveGameMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                saveGameMenusaveGameMousePressed(evt);
            }
        });
        newGame.add(saveGameMenu);

        loadGameMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        loadGameMenu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        loadGameMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/load.png"))); // NOI18N
        loadGameMenu.setText("Load Game");
        loadGameMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                loadGameMenuLoadGameMousePressed(evt);
            }
        });
        newGame.add(loadGameMenu);

        exitGameMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitGameMenu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        exitGameMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit.png"))); // NOI18N
        exitGameMenu.setText("Exit");
        exitGameMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                exitGameMenuexitMousePressed(evt);
            }
        });
        newGame.add(exitGameMenu);

        jMenuBar1.add(newGame);

        jMenu2.setText("Edit");
        jMenu2.setFont(new java.awt.Font("Tahoma", 0, 12));

        addImageMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        addImageMenu.setText("Add Image");
        addImageMenu.setRequestFocusEnabled(false);
        addImageMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                addImageMenuaddImageMousePressed(evt);
            }
        });
        jMenu2.add(addImageMenu);

        jMenuBar1.add(jMenu2);

        optionMenu.setText("Tool");
        optionMenu.setFont(new java.awt.Font("Tahoma", 0, 12));

        option.setFont(new java.awt.Font("Tahoma", 0, 12));
        option.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/settings.png"))); // NOI18N
        option.setText("Options");
        option.setRequestFocusEnabled(false);
        option.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                optionMousePresses(evt);
            }
        });
        optionMenu.add(option);

        jMenuBar1.add(optionMenu);

        helpMenu.setText("Help");
        helpMenu.setFont(new java.awt.Font("Tahoma", 0, 12));
        helpMenu.setRequestFocusEnabled(false);

        viewHelpMenu.setFont(new java.awt.Font("Tahoma", 0, 12));
        viewHelpMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/help.png"))); // NOI18N
        viewHelpMenu.setText("View Help");
        viewHelpMenu.setRequestFocusEnabled(false);
        viewHelpMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                helpMenuMousePressed(evt);
            }
        });
        helpMenu.add(viewHelpMenu);

        aboutMenu.setFont(new java.awt.Font("Tahoma", 0, 12));
        aboutMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/about.png"))); // NOI18N
        aboutMenu.setText("About us");
        aboutMenu.setRequestFocusEnabled(false);
        aboutMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                aboutMenuaboutMousePressed(evt);
            }
        });
        helpMenu.add(aboutMenu);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(477, 477, 477))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void newGameMenunewGameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newGameMenunewGameMousePressed
        this.NewGame();
}//GEN-LAST:event_newGameMenunewGameMousePressed

private void saveGameMenusaveGameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveGameMenusaveGameMousePressed
        this.SaveGame();
}//GEN-LAST:event_saveGameMenusaveGameMousePressed

private void loadGameMenuLoadGameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loadGameMenuLoadGameMousePressed
        if(!issolu)
            this.LoadGame();
}//GEN-LAST:event_loadGameMenuLoadGameMousePressed

private void addImageMenuaddImageMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addImageMenuaddImageMousePressed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.jpg; *.png; *.gif) Images", "jpg", "gif", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            if(issolu) {
                int change = JOptionPane.showConfirmDialog(null, " These settings won't apply to the game in progress."
                        + " \n What do you want to do ?",
                        "Changed Gamed Settings", 0, 1, new ImageIcon(getClass().getResource("/images/Puzzle.png")));
                if(change == 1) return;
            }
            typegame = 2;
            File imagefile = chooser.getSelectedFile();
            String s = imagefile.getPath();
            image = (new ImageIcon(s)).getImage();
            typeImage = 6;
            NewGame();
        }
}//GEN-LAST:event_addImageMenuaddImageMousePressed

private void optionMousePresses(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionMousePresses
        Op = new OptionFrame();
        Op.setLocation(800, 50);
        Op.setVisible(true);
        Op.setResizable(false);
}//GEN-LAST:event_optionMousePresses

private void helpMenuMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpMenuMousePressed
        JOptionPane.showMessageDialog(null," N-Puzzle Game \n + Use your mouse or press DOWN, UP LEFT RIGHT from your keyboard."
                + "\n + Reposition a image to: blank,1,2,3,.... "
                + "\n + Press Solve to let the AI doing its job.\n + Press Stop to stop the process"
                + "\n + Thank you and enjoy!", "Help", 1, new ImageIcon(getClass().getResource("/images/Puzzle.png")));
}//GEN-LAST:event_helpMenuMousePressed

private void aboutMenuaboutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutMenuaboutMousePressed
        JOptionPane.showMessageDialog(null," N-Puzzle Game \n CopyRight \n    Huy Cuong Doan "
                + "\n    Email: hcuong.doan AT gmail.com"
                + "\n    University of Montreal"
                + "\n Version 1.0", " CopyRight", 1, new ImageIcon(getClass().getResource("/images/Puzzle.png")));
}//GEN-LAST:event_aboutMenuaboutMousePressed

private void solveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solveButtonActionPerformed
        if(issolu)
        {
            issolu = false;
            solveButton.setText("Solve");
            return;
        }
        playtime = false;
        times = 0;
        time.setText("   00 : 00");
        moveTextField.setText("");
        int[] initarray = Ju.getValue(); //lấy mảng trạng thái hiện tại
        int[] goalarray = new int[Length]; // mảng trạng thái đích
        for(int i=0; i < Length; i++)
            goalarray[i] = i;
        
        State initsta = new State(initarray,Size); //Trạng thái bắt đầu      
        State goalsta = new State(goalarray,Size); //Trạng thái đích
        
        astar.initialnode = new Node(initsta, 0);
        astar.goalnode= new Node(goalsta, 0);

        //System.out.println(initsta.heuristic4(goalsta));
        //System.out.println(initsta.heuristic4(goalsta));
        
        //Lựa chọn các hàm ước lượng
        if(h1.isSelected()) State.heuristic = 1;
        else if(h2.isSelected()) State.heuristic = 2;
        else if(h3.isSelected()) State.heuristic = 3;

        progressBar.setVisible(true);
        progressBar.setString("Please wait ...");
        progressBar.setIndeterminate(true);
        issolu = true; // báo cho luồng thực hiện chạy thuật toán
        solveButton.setText("Stop");
        JF.requestFocus();
}//GEN-LAST:event_solveButtonActionPerformed

private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameButtonActionPerformed
    this.NewGame();
    JF.requestFocus();
}//GEN-LAST:event_newGameButtonActionPerformed

private void size1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_size1ActionPerformed
    if(!issolu && size1.isSelected()) 
    {
        Size = 3;
        this.NewGame();
    }
}//GEN-LAST:event_size1ActionPerformed

private void size2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_size2ActionPerformed
    if(!issolu && size2.isSelected()) 
    {
        Size = 4;
        this.NewGame();
    }
}//GEN-LAST:event_size2ActionPerformed

private void size3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_size3ActionPerformed
    if(!issolu && size3.isSelected()) 
    {
        Size = 5;
        this.NewGame();
    }
}//GEN-LAST:event_size3ActionPerformed

private void size4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_size4ActionPerformed
    if(!issolu && size4.isSelected()) 
    {
        Size = 6;
        this.NewGame();
    }
}//GEN-LAST:event_size4ActionPerformed

private void numberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numberActionPerformed
    if(!issolu && number.isSelected()) 
    {
        typegame = 0;
        this.ChangeView();
    }
}//GEN-LAST:event_numberActionPerformed

private void imageNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageNumberActionPerformed
    if(!issolu && imageNumber.isSelected()) 
    {
        typegame = 1;
        this.ChangeView();
    }
}//GEN-LAST:event_imageNumberActionPerformed

private void imagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imagesActionPerformed
    if(!issolu && images.isSelected()) 
    {
        typegame = 2;
        this.ChangeView();
    }
}//GEN-LAST:event_imagesActionPerformed

    private void exitGameMenuexitMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitGameMenuexitMousePressed
        this.ExitGame();
    }//GEN-LAST:event_exitGameMenuexitMousePressed

public void solution(){
        playtime = true;
        Thread t1 = new Thread()
        {
            public synchronized void start()
            {
                super.start();
            }
            public void run()
            {
                CountTime();
                super.run();
            }
        };
        t1.start(); //Bắt đầu luồng đếm thời gian
        if(astarRadio.isSelected()) // lựa chọn A*
        {
            display.append("+ Astar Algorithm\n");   
            display.append(" Heuristic " + State.heuristic + "\n");
            astar.solveAstar();
        }
        else if(idastarRadio.isSelected()) // lựa chon IDA*
        {
            display.append("+ IDAstar Algorithm\n");
            display.append(" Heuristic " + State.heuristic + "\n");
            astar.solveIDAstar();
        }  
        playtime = false; //Ngừng đếm thời gian
        if(astar.Stop != null || !issolu)
        {
            issolu = false;
            if(astar.Stop != "stop") display.append(astar.Stop);
            display.append(" Done!\n\n");
            solveButton.setText("Solve");
            astar.Stop = null;            
            return;
        }

        /////
//        int[] goalarray = new int[Length];
//        for(int i=0; i < Length; i++)
//            goalarray[i] = i;
//        State goalsta = new State(goalarray,Size);
//        astar.goalnode= new Node(goalsta, 0);
//        for(int i = 0; i < astar.KQ.size(); i++)
//        {
//            System.out.println("h" + State.heuristic + " = " + astar.KQ.elementAt(i).state.estimate(goalsta));
//        }
//
//        /////
        
        display.append(" Nodes already evaluated: " + astar.count+"\n");
        display.append(" Nodes in a tree: " + astar.total_nodes+"\n");
        display.append(" Move: " + (astar.KQ.size() - 1) +"\n");
        display.append(" Time: " + astar.time_solve+"ms\n\n");
        
        int numStates=astar.KQ.size();
        int k=numStates-1;
        
        if(!mute) sound.play(getClass().getResource("/Music/win1.au"));
        int auto = JOptionPane.showConfirmDialog(null, " + Solution found in " + k +" steps."
                + "\n + The time needed to find it is " + astar.time_solve + "ms"
                + "\n + Do you want to auto Solve ?", 
                "Autorun", 0, 1, new ImageIcon(getClass().getResource("/images/Puzzle.png")));
        if(auto == 1) //auto = 1 ko tự động chạy
        {
            issolu = false;
            solveButton.setText("Solve");
            return;
        }

        //Lấy các trạng thái kết quả trong KQ để tự động chạy
        for(int i = 0; i < numStates - 1; i++)
        {
            if(!issolu) 
            {
                if(Length == Size*Size) solveButton.setEnabled(true);
                return;
            }
            int j=0, m=0;
            j= astar.KQ.elementAt(i).Blank();       //Vị trí ô trống của trạng thái i
            m = astar.KQ.elementAt(i+1).Blank();     //Vị trí ô trống của trạng thái i+1       
            if(j == m + 1)        
            {
                Ju.LEFT();
                this.repaint();
            }
            else if(j == m  - 1)
            {
                Ju.RIGHT();
                this.repaint();
            }
            else if(j  == m  + Size)
            {
                Ju.UP();
                this.repaint();
            }
            else if(j == m - Size)
            {
                Ju.DOWN();
                this.repaint();
            }
            count++; //Tăng số bước di chuyển
            if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
            moveTextField.setText(" " + count + "/" + k);
            if(Ju.checkWin())       //Kiểm tra xem đã là trạng thái đích chưa
            {
                win = true;
                if(!mute) sound.play(getClass().getResource("/Music/win1.au"));                
                this.Disable();
                this.repaint();
                int t = JOptionPane.showConfirmDialog(null,"       Game finished \n   You want to play again ? ",
                        "N-Puzzle",0 , 1 , new ImageIcon(getClass().getResource("/images/finish.png")));              
                if(t == 0)
                {
                    this.NewGame();
                    return;
                }
                else
                {
                    solveButton.setText("Slove");
                    solveButton.setEnabled(false);
                    issolu = false;
                    return;
                }
            }
            try {
                Thread.sleep(speed);
            }catch (InterruptedException ex) {}
        }        
        issolu = false;
}

    public void GameWon() //Khi hiện thị khung GameWon sẽ không sử dụng được MainFrame nữa
    {
        Gw = new GameWon();
        Gw.setLocation(400, 280);
        Gw.setVisible(true);
        Gw.setResizable(false);
        this.addMouseListener(this);
        this.setEnabled(false);
    }
    
    public void ExitGame()
    {        
        System.exit(0);        
    }
    
    public int getsize()
    {
        return Size;
    }
    
    public int getlength()
    {
        return Length;
    }    
    
    public void CountTime()
    {
        int munites = 0;
        int seconds = 0;
        for(;;)
        {
            if(win || !playtime)
            {
                playtime = false;
                times =  0;
                break;
            }   
            if(times < 60)
            {
                seconds = times;
                munites = 0;
            }
            else
            {
                munites = times / 60;
                seconds = times % 60;
            }
            if(munites < 10)
            {
                if(seconds < 10) time.setText("   0" + munites +" : 0" + seconds);
                else time.setText("   0" + munites +" : " + seconds);
            }
            else
            {
                if(seconds < 10) time.setText("   " + munites +" : 0" + seconds);
                else time.setText("   " + munites +" : " + seconds);
            }
            times++;                    
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {};                     
        }
    }

    public void start(){
        Thread t=new Thread(this);
        t.start();
    }
    

    public void run() {         
        while(true)
        {  
            if(Gw != null && Gw.isClosed) //Khi tắt Frame Game Won thì trả lại trạng thái ban đầu cho MainForm
            {  
                this.setVisible(true);
                this.setEnabled(true);
                this.removeMouseListener(this);
                Gw = null;
            }
            if(issolu)
            {                
                DisableRadio();
                solution();
                EnableRadio();
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
            }
            else if(playtime) CountTime();
            else if(mute) sound.stop();
            else
            {
                if(playtime || issolu) break;
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){};
            }
        }
    }
    
    public void DisableRadio()
    {
        size1.setEnabled(false);
        size2.setEnabled(false);
        size3.setEnabled(false);
        size4.setEnabled(false);
        
        number.setEnabled(false);
        imageNumber.setEnabled(false);
        images.setEnabled(false);
        
        h1.setEnabled(false);
        h2.setEnabled(false);
        h3.setEnabled(false);
        
        astarRadio.setEnabled(false);
        idastarRadio.setEnabled(false);
        
    }
    
    public void EnableRadio()
    {
        size1.setEnabled(true);
        size2.setEnabled(true);
        size3.setEnabled(true);
        size4.setEnabled(true);
        
        h1.setEnabled(true);
        h2.setEnabled(true);
        h3.setEnabled(true);
        
        number.setEnabled(true);
        imageNumber.setEnabled(true);
        images.setEnabled(true);
        
        astarRadio.setEnabled(true);
        idastarRadio.setEnabled(true);        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel1;
    private javax.swing.JMenuItem aboutMenu;
    private javax.swing.JMenuItem addImageMenu;
    private javax.swing.JRadioButton astarRadio;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JTextArea display;
    private javax.swing.JMenuItem exitGameMenu;
    private javax.swing.JRadioButton h1;
    private javax.swing.JRadioButton h2;
    private javax.swing.JRadioButton h3;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPanel heuristic;
    private javax.swing.JRadioButton idastarRadio;
    private javax.swing.JRadioButton imageNumber;
    private javax.swing.JRadioButton images;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem loadGameMenu;
    private javax.swing.JTextField moveTextField;
    private javax.swing.JMenu newGame;
    private javax.swing.JButton newGameButton;
    private javax.swing.JMenuItem newGameMenu;
    private javax.swing.JRadioButton number;
    private javax.swing.JMenuItem option;
    private javax.swing.JMenu optionMenu;
    private javax.swing.JMenuItem saveGameMenu;
    private javax.swing.JRadioButton size1;
    private javax.swing.JRadioButton size2;
    private javax.swing.JRadioButton size3;
    private javax.swing.JRadioButton size4;
    private javax.swing.JPanel sizePanel;
    private javax.swing.JButton solveButton;
    private javax.swing.JTextField time;
    private javax.swing.JPanel typegamePanel;
    private javax.swing.JPanel typesolve;
    private javax.swing.JMenuItem viewHelpMenu;
    // End of variables declaration//GEN-END:variables

    public void mouseClicked(MouseEvent me) {}

    public void mousePressed(MouseEvent me) {
         if(Gw != null)
        {            
            Gw.setVisible(true);
            if(!mute) Toolkit.getDefaultToolkit().beep();
            return;
        }
        if(issolu || !Ju.isEnabled()) return;
        playtime = true;
        //Gốc tọa độ paint hình (4; 4): Hàm g.translate(4, 4);
        int x = (me.getX() - 4) / Ju.getCw(); //(x; y) tọa độ cột và hàng của ô
        int y = (me.getY() - 4) / Ju.getCh();
        int pos = y * Size + x; //Vị trí ô: từ 0 tới 2^n - 1
        if(Ju.checkWin())
        {  
            win = true;
            if(!mute) sound.play(getClass().getResource("/Music/win.au"));
            this.Disable();
            solveButton.setEnabled(false);
            this.GameWon();
            return;
        }
        else
        {
            if(Ju.blank >= Size && Ju.blank == pos + Size) // di chuyển ô trống lên trên
            {
                Ju.UP();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(Ju.blank < Length - Size && Ju.blank == pos - Size) // di chuyển ô trống xuống dưới
            {
                Ju.DOWN();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(Ju.blank % Size != 0 && Ju.blank == pos + 1)  // // di chuyển ô trống sang trái
            {
                Ju.LEFT();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(Ju.blank % Size != Size - 1 && Ju.blank == pos - 1)
            {
                Ju.RIGHT();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au")); // di chuyển ô trống sang phải
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(!mute) Toolkit.getDefaultToolkit().beep();
             
            if(Ju.checkWin())
            {
                win = true; 
                if(!mute) sound.play(getClass().getResource("/Music/win.au"));
                this.Disable();
                solveButton.setEnabled(false);
                this.GameWon();
                return;
            }
        }
    }

    public void mouseReleased(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
    public void keyTyped(KeyEvent ke) {}   
    public void keyReleased(KeyEvent ke) {}

    public void keyPressed(KeyEvent ke) {
        if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) //Esc dừng tìm kiếm kết quả
        {
            issolu = false;
            return;
        }
        if(ke.getKeyCode() == KeyEvent.VK_N)        //Ctrl + N: New game
            if(ke.isControlDown()) 
            {
                NewGame();
                return;
            }
        if(ke.getKeyCode() == KeyEvent.VK_S)        //Ctrl + S: Save game
            if(ke.isControlDown()) 
            {
                SaveGame();
                return;
            }
        if(ke.getKeyCode() == KeyEvent.VK_L && !issolu)   //Ctrl + L: Load game
            if(ke.isControlDown()) 
            {
                LoadGame();
                return;
            }
        if(ke.getKeyCode() == KeyEvent.VK_F4 && !issolu)  //Alt + F4: Thoát game
            if(ke.isAltDown()) 
            {
                this.ExitGame();
            }
        if(issolu || !Ju.isEnabled()) return;    
        
        if(Ju.checkWin())
        {  
            win = true;
            if(!mute) sound.play(getClass().getResource("/Music/win.au"));
            JF.requestFocus();       
            this.Disable();
            solveButton.setEnabled(false);
            this.GameWon();
            return;
        }
        else
        {
            if(ke.getKeyCode() == KeyEvent.VK_DOWN && Ju.blank >= Size) 
            {
                playtime = true;
                Ju.UP();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(ke.getKeyCode() == KeyEvent.VK_UP && Ju.blank < Length - Size) 
            {
                playtime = true;
                Ju.DOWN();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(ke.getKeyCode() == KeyEvent.VK_RIGHT && Ju.blank % Size != 0)
            {
                playtime = true;
                Ju.LEFT();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
                count++;
                moveTextField.setText("   " + count);
                this.repaint();
            }
            else if(ke.getKeyCode() == KeyEvent.VK_LEFT && Ju.blank % Size != Size - 1)
            {
                playtime = true;
                Ju.RIGHT();
                if(!mute) sound.play(getClass().getResource("/Music/slide.au"));
                count++;
                moveTextField.setText("   " + count);  
                this.repaint();
            }            
            if(Ju.checkWin())
            {
                win = true; 
                if(!mute) sound.play(getClass().getResource("/Music/win.au"));
                JF.requestFocus();                
                this.Disable();
                solveButton.setEnabled(false);
                this.GameWon();
                return;
            }
        }
    }
   
}
