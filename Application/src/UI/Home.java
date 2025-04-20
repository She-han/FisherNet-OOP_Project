
package UI;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;


public class Home extends javax.swing.JFrame {


    public Home() {
        initComponents();
    }
    
        public class Gradient extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // always call super first
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int w = getWidth();
        int h = getHeight();

    Color color1 = new  Color(100, 197, 213); // Deep sea blue
    Color color2 = new Color(0, 105, 148);  // Light aqua

        GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        top = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        left = new javax.swing.JPanel();
        middle = new Gradient();
        add = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        location = new javax.swing.JPanel();
        qr = new javax.swing.JPanel();
        stat = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1920, 1080));

        background.setRequestFocusEnabled(false);
        background.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        top.setBackground(new java.awt.Color(0, 53, 123));
        top.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, java.awt.Color.darkGray, java.awt.Color.white, java.awt.Color.darkGray));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/icons/logo-removebg (1).png"))); // NOI18N
        jLabel2.setMaximumSize(new java.awt.Dimension(937, 266));
        jLabel2.setPreferredSize(new java.awt.Dimension(937, 266));

        javax.swing.GroupLayout topLayout = new javax.swing.GroupLayout(top);
        top.setLayout(topLayout);
        topLayout.setHorizontalGroup(
            topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1635, Short.MAX_VALUE))
        );
        topLayout.setVerticalGroup(
            topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        background.add(top, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 1930, -1));

        left.setBackground(new java.awt.Color(0, 53, 123));
        left.setToolTipText("");
        left.setAlignmentX(0.0F);
        left.setAlignmentY(0.0F);

        javax.swing.GroupLayout leftLayout = new javax.swing.GroupLayout(left);
        left.setLayout(leftLayout);
        leftLayout.setHorizontalGroup(
            leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );
        leftLayout.setVerticalGroup(
            leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );

        background.add(left, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 310, 1000));

        middle.setBackground(new java.awt.Color(51, 153, 255));

        add.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addMouseExited(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UI/icons/add3.png"))); // NOI18N
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout addLayout = new javax.swing.GroupLayout(add);
        add.setLayout(addLayout);
        addLayout.setHorizontalGroup(
            addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addLayout.createSequentialGroup()
                .addContainerGap(239, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
        );
        addLayout.setVerticalGroup(
            addLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jLabel1)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        location.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                locationMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                locationMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                locationMouseExited(evt);
            }
        });

        javax.swing.GroupLayout locationLayout = new javax.swing.GroupLayout(location);
        location.setLayout(locationLayout);
        locationLayout.setHorizontalGroup(
            locationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );
        locationLayout.setVerticalGroup(
            locationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );

        qr.setPreferredSize(new java.awt.Dimension(400, 250));
        qr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                qrMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                qrMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                qrMouseExited(evt);
            }
        });

        javax.swing.GroupLayout qrLayout = new javax.swing.GroupLayout(qr);
        qr.setLayout(qrLayout);
        qrLayout.setHorizontalGroup(
            qrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        qrLayout.setVerticalGroup(
            qrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        stat.setPreferredSize(new java.awt.Dimension(404, 250));
        stat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                statMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                statMouseExited(evt);
            }
        });

        javax.swing.GroupLayout statLayout = new javax.swing.GroupLayout(stat);
        stat.setLayout(statLayout);
        statLayout.setHorizontalGroup(
            statLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );
        statLayout.setVerticalGroup(
            statLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout middleLayout = new javax.swing.GroupLayout(middle);
        middle.setLayout(middleLayout);
        middleLayout.setHorizontalGroup(
            middleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(middleLayout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addGroup(middleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(qr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(83, 83, 83)
                .addGroup(middleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(stat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(location, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(559, Short.MAX_VALUE))
        );
        middleLayout.setVerticalGroup(
            middleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(middleLayout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addGroup(middleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(172, 172, 172)
                .addGroup(middleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(qr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(204, Short.MAX_VALUE))
        );

        background.add(middle, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, 1610, 1000));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1533, 1107));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseEntered
        setColor(add);        // TODO add your handling code here:
    }//GEN-LAST:event_addMouseEntered

    private void addMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseExited
        resetColor(add); // TODO add your handling code here:
    }//GEN-LAST:event_addMouseExited

    private void addMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addMouseClicked
        new NewBoat().show();        // TODO add your handling code here:
    }//GEN-LAST:event_addMouseClicked

    private void locationMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_locationMouseEntered
       setColor(location); // TODO add your handling code here:
    }//GEN-LAST:event_locationMouseEntered

    private void locationMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_locationMouseExited
       resetColor(location); // TODO add your handling code here:
    }//GEN-LAST:event_locationMouseExited

    private void locationMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_locationMouseClicked
         new Locations().show();  // TODO add your handling code here:
    }//GEN-LAST:event_locationMouseClicked

    private void qrMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qrMouseEntered
        setColor(qr);        // TODO add your handling code here:
    }//GEN-LAST:event_qrMouseEntered

    private void qrMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qrMouseExited
        resetColor(qr);// TODO add your handling code here:
    }//GEN-LAST:event_qrMouseExited

    private void qrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_qrMouseClicked
        new ScanUpdate().show();// TODO add your handling code here:
    }//GEN-LAST:event_qrMouseClicked

    private void statMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statMouseEntered
        setColor(stat);        // TODO add your handling code here:
    }//GEN-LAST:event_statMouseEntered

    private void statMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statMouseExited
        resetColor(stat);// TODO add your handling code here:
    }//GEN-LAST:event_statMouseExited
                            
     public void resetColor(JPanel panel)
 {
     panel.setBackground(new java.awt.Color(197, 197, 197));
 }
 
 public void setColor(JPanel panel)
 {
     panel.setBackground(new java.awt.Color(240,240,240));
 }
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel add;
    private javax.swing.JPanel background;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel left;
    private javax.swing.JPanel location;
    private javax.swing.JPanel middle;
    private javax.swing.JPanel qr;
    private javax.swing.JPanel stat;
    private javax.swing.JPanel top;
    // End of variables declaration//GEN-END:variables
}
