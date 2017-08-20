/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OptionFrame.java
 *
 * Created on Oct 24, 2011, 8:20:13 PM
 */
package puzzle;

import java.awt.Color;
import javax.swing.*;

/**
 *
 * @author Huy Cuong Doan
 */
public class OptionFrame extends javax.swing.JFrame {
    private int Size;

    public OptionFrame() {
        initComponents();
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch(Exception e){
            throw new RuntimeException("Could not set default look and feel");
        }
        buttonGroup1 = new ButtonGroup();
        
        buttonGroup1.add(number);
        buttonGroup1.add(imageNumber);
        buttonGroup1.add(image); 
        Init();   
        Size = size.getSelectedIndex();
    }
    
    public void Init()
    {        
        int kt = Main.window.Size - 3;
        size.setSelectedIndex(kt);
        if(Main.window.mute) sound.setSelected(true);
        Speed.setValue(1000/Main.window.speed);
        int x = Main.window.typegame;
        if(x == 0) 
        {
            number.setSelected(true);
            ColorEmptyBox.setEnabled(true);
            ColorBoxs.setEnabled(true);
            fileImage.setEnabled(false);
        }
        else if(x  == 1)
        {
            imageNumber.setSelected(true);
            ColorEmptyBox.setEnabled(true);
            ColorBoxs.setEnabled(false);
            fileImage.setEnabled(true);
        }
        else 
        {
            image.setSelected(true);
            ColorEmptyBox.setEnabled(true);
            ColorBoxs.setEnabled(false);
            fileImage.setEnabled(true);
        }
        
        Color s1 = Main.window.ColorEBox;
        if(s1 == Color.green) ColorEmptyBox.setSelectedIndex(0);
        else if(s1 == Color.red) ColorEmptyBox.setSelectedIndex(1);
        else if(s1 == Color.orange) ColorEmptyBox.setSelectedIndex(2);
        else ColorEmptyBox.setSelectedIndex(3);

        Color n1 = Main.window.ColorBoxs;
        if(n1 == Color.blue) ColorBoxs.setSelectedIndex(0);
        else if(n1 == Color.magenta) ColorBoxs.setSelectedIndex(1);
        else if(n1 == Color.yellow) ColorBoxs.setSelectedIndex(2);
        else ColorBoxs.setSelectedIndex(3);
        int type = Main.window.typeImage;
        if(type == 6)
        {
            fileImage.addItem("Other");
        }
        fileImage.setSelectedIndex(type);
    }
    
    public void OK()
    { 
        if(sound.isSelected()) Main.window.mute = true;
        else Main.window.mute = false;
        
        Main.window.speed = 1000/Speed.getValue();  
        
        int t = ColorEmptyBox.getSelectedIndex(); 
        if(t == 0) Main.window.ColorEBox = Color.green;
        else if(t == 1) Main.window.ColorEBox = Color.red;
        else if(t == 2) Main.window.ColorEBox = Color.orange;
        else  Main.window.ColorEBox = Color.gray;
        if(number.isSelected())
        {
            int k = ColorBoxs.getSelectedIndex();
            if(k == 0) Main.window.ColorBoxs = Color.blue;
            else if(k == 1) Main.window.ColorBoxs = Color.magenta;
            else if(k == 2) Main.window.ColorBoxs = Color.yellow;
            else Main.window.ColorBoxs = Color.cyan;            
            Main.window.typegame = 0;
        }
        else
        {
            int n = fileImage.getSelectedIndex();
            Main.window.typeImage = n;
            if(imageNumber.isSelected()) Main.window.typegame = 1;
            else Main.window.typegame = 2;
        }
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        size = new javax.swing.JComboBox();
        sound = new javax.swing.JCheckBox();
        number = new javax.swing.JRadioButton();
        imageNumber = new javax.swing.JRadioButton();
        image = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        ColorEmptyBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        ColorBoxs = new javax.swing.JComboBox();
        Images = new javax.swing.JLabel();
        fileImage = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        Speed = new javax.swing.JSlider();
        Ok = new javax.swing.JButton();
        cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gamed Settings");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage((new ImageIcon(getClass().getResource("/images/Puzzle.png"))).getImage());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Size");

        size.setFont(new java.awt.Font("Tahoma", 0, 14));
        size.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "3 x 3", "4 x 4", "5 x 5", "6 x 6" }));

        sound.setText("Mute Sound");

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

        image.setText("Image");
        image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageActionPerformed(evt);
            }
        });

        jLabel2.setText("Color of Empty Box:");

        ColorEmptyBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Red", "Orange", "Gray" }));

        jLabel3.setText("Color of Boxs:");

        ColorBoxs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Magenta", "Yellow", "Cyan" }));

        Images.setText("Images:");

        fileImage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Girl Xinh", "Pig", "Deer", "Bird", "Obama" }));

        jLabel6.setText("Speed");

        Speed.setMaximum(10);
        Speed.setMinimum(1);
        Speed.setPaintTicks(true);

        Ok.setText("OK");
        Ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(Speed, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(number, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(imageNumber)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(image, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Images, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(88, 88, 88)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(fileImage, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(ColorBoxs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(ColorEmptyBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(size, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(66, 66, 66)
                                        .addComponent(sound)))
                                .addGap(45, 45, 45))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(Ok, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(cancel)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(size, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sound))
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(number)
                    .addComponent(image)
                    .addComponent(imageNumber))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ColorEmptyBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ColorBoxs, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Images, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileImage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Speed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Ok, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkActionPerformed
        if(evt.getSource() == Ok)
        {
            int kt = size.getSelectedIndex(); 
            if(kt != Size|| Main.window.win)
            {  
//                if(!Main.window.win || kt != Size)
//                {
//                    int change = JOptionPane.showConfirmDialog(null, " These settings won't apply to the game in progress. \n What do you want to do ?", 
//                            "Changed Gamed Settings", 0, 1, new ImageIcon(getClass().getResource("/images/Puzzle.png")));
//                    if(change == 1)
//                    {
//                        setVisible(false);
//                        return;
//                    }
//                }
                Main.window.Size = kt + 3;
                OK();
                Main.window.NewGame();
                setVisible(false);
            }
            else
            {
                OK();
                Main.window.ChangeView();
                setVisible(false);
            }
        }
    }//GEN-LAST:event_OkActionPerformed

    private void numberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numberActionPerformed
        if(number.isSelected())
        {
            ColorEmptyBox.setEnabled(true);
            ColorBoxs.setEnabled(true);
            fileImage.setEnabled(false);
        }
    }//GEN-LAST:event_numberActionPerformed

    private void imageNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageNumberActionPerformed
        if(imageNumber.isSelected())
        {
            ColorEmptyBox.setEnabled(true);
            ColorBoxs.setEnabled(false);
            fileImage.setEnabled(true);
        }
    }//GEN-LAST:event_imageNumberActionPerformed

    private void imageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageActionPerformed
        if(image.isSelected())
        {
            ColorEmptyBox.setEnabled(true);
            ColorBoxs.setEnabled(false);
            fileImage.setEnabled(true);
        }
    }//GEN-LAST:event_imageActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new OptionFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox ColorBoxs;
    private javax.swing.JComboBox ColorEmptyBox;
    private javax.swing.JLabel Images;
    private javax.swing.JButton Ok;
    private javax.swing.JSlider Speed;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancel;
    private javax.swing.JComboBox fileImage;
    private javax.swing.JRadioButton image;
    private javax.swing.JRadioButton imageNumber;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JRadioButton number;
    private javax.swing.JComboBox size;
    private javax.swing.JCheckBox sound;
    // End of variables declaration//GEN-END:variables
}
