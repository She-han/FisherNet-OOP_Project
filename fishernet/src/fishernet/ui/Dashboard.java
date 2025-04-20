/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fishernet.ui;

import javax.swing.*;
 public class Dashboard extends JFrame {
     
     
     
    public Dashboard() {
        setTitle("FisherNet Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JLabel welcome = new JLabel("Welcome to FisherNet System", 
SwingConstants.CENTER);
        add(welcome);
        
        
        
    }
 }
