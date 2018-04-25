import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.BorderFactory;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;


import java.lang.Object;
import java.util.EventObject;
import java.awt.AWTEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JSlider;
//import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;
import javax.swing.BoxLayout; 
import java.awt.Component;
/**
* This is the main window of the flight game
*/
//public static Map<Integer,String> map = new TreeMap<Integer,String>();

public class MyGameFrame extends JFrame{
  Map<String,String> map = new TreeMap<String,String>();
  //Load the pictures
  Image planeImg = GameUtil.getImage("images/plane1.png");
  Image background = GameUtil.getImage("images/background1.png");
  //Initialize plane, shells, explode and startTime
  PlaneObject plane = new PlaneObject(planeImg,600,600);
  CannonShell[] shells = new CannonShell[20];
  ExplodeObject explode;
  Date startTime = new Date();//start time
  Date endTime;//end time
  int timePeriod; //How long the game last.
  String timestring;
  String n;
  String id;

  //This method is automatically called
  public void paint(Graphics g){
    Color c = g.getColor();
    g.drawImage(background,0,0,null);//draw the background
    plane.drawSelf(g);//draw the plane
    //draw the 50 CannonShells
    for(int j = 0; j<shells.length; j++){
      shells[j].draw(g);
      //Test whether the shell hit the plane,if the result is true, then it hits the plane
      boolean boom = shells[j].getRect().intersects(plane.getRect());
      if(boom){
        plane.live = false;
        explode = new ExplodeObject(plane.x, plane.y);
        /*
        * We need to calculate how long the game last
        * Java automatically use ms as unite, we need to change it to s
        * We also need to change the double to int
        */
        endTime = new Date();
        timePeriod = (int)((endTime.getTime()-startTime.getTime())/1000);
        timestring = String.valueOf(timePeriod);
        explode.draw(g);
      }
      //If the plane died, then only print the time for one time
      if(!plane.live){
        g.setColor(Color.RED);
        Font f = new Font("Times",Font.BOLD,50);
        g.setFont(f);
        g.drawString("Your time is "+timePeriod+"s",(int)plane.x,(int)plane.y);
      }
    }
    g.setColor(c);
  }



  //This class can help us to draw the window repaditly
  class paintThread extends Thread{
    public void run(){
      while(true){
        repaint(); //Re-draw the window
        try{
          //1s=1000ms, the pictures will be re-drawed for 25 times in 1 second
          Thread.sleep(40);
        }catch(InterruptedException e){
          e.printStackTrace();
        }
      }
    }
  }

  //This class can allow user to control the plane via keyboard
  class KeyMonitor extends KeyAdapter{
    public void keyPressed(KeyEvent e){
      plane.addDirection(e);
    }

    public void keyReleased(KeyEvent e){
      plane.removeDirection(e);
    }
  }

//------------------------------------------------------------------------------

  /**
  * Initialize the window
  */
  public void launchFrame(){

    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    JPanel rightbar = new JPanel();
    rightbar.setLayout(new GridLayout(0,1));
    JMenuBar mb = new JMenuBar();
    JMenu op = new JMenu("Options");
    mb.add(op);
    JMenuItem setName = new JMenuItem("check name");
    op.add(setName);
    JMenuItem searchName = new JMenuItem("search name");
    op.add(searchName);
    JTextField namestartTF = new JTextField("Tim");
    JButton start = new JButton("Start");
    JButton reStart = new JButton("Re-Start");
    JLabel label = new JLabel("Enter your name and your score here!");
    JTextField nameTF = new JTextField("Tim");
    JTextField scoreTF = new JTextField("100");
    JLabel namea = new JLabel("Name: ");
    JPanel name1 = new JPanel();
    JButton save = new JButton("save");
    content.add(save, BorderLayout.PAGE_END);
    name1.setLayout(new GridLayout(0,2));
    name1.add(namea);
    name1.add(namestartTF);
    JPanel name2 = new JPanel();
    name2.setLayout(new GridLayout(0,2));
    JLabel name2a = new JLabel("Name: ");
    name2.add(name2a);
    name2.add(nameTF);
    JPanel score = new JPanel();
    score.setLayout(new GridLayout(0,2));
    JLabel scorea = new JLabel("Score:  ");
    JLabel explain = new JLabel("<html><body>To get TXT,<br>enter name and id in options<br>and click save before click start<br>or restart and play the game<body></html>");
    name2.add(scorea);
    name2.add(scoreTF);
    rightbar.add(name1);
    rightbar.add(start);rightbar.add(reStart);
    rightbar.add(label);
    rightbar.add(name2);rightbar.add(score);
    rightbar.add(explain);
    content.add(rightbar,BorderLayout.CENTER);
    start.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        new paintThread().start(); //Start the thread of redrawing the pictures
      }
    });

    save.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        writeMapToFile(map, "playerInfo.txt");
        printDictionary(map);
      }
    });

      ActionListener clickSound = new ActionListener() {
              @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == start) {
                        try {
                            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./good.wav").getAbsoluteFile());
                            Clip clip = AudioSystem.getClip();
                            clip.open(audioInputStream);
                            clip.start();
                        }catch(Exception x) { x.printStackTrace(); }
                    } else if (e.getSource() == reStart) {
                      try {
                            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./bad.wav").getAbsoluteFile());
                            Clip clip = AudioSystem.getClip();
                            clip.open(audioInputStream);
                            clip.start();
                      } catch(Exception x) { x.printStackTrace(); }
                    }
                  }
                };
                start.addActionListener(clickSound);

    setName.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        JFrame f = new JFrame();
        n = JOptionPane.showInputDialog(f, "Enter name");
        id = JOptionPane.showInputDialog(f, "Enter ID");
        System.out.println("name and id entered.");
        map.put(n,id);
      }
    });

    searchName.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        JFrame f = new JFrame();
        String a = JOptionPane.showInputDialog(f, "Enter the player name you want to search id of");
        String b = map.get(a);
        JOptionPane.showMessageDialog(f,"<html><h1>"+b+"</h1></html>");
      }
    });

    reStart.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent event){
        shells = new CannonShell[20];

        MyGameFrame f = new MyGameFrame();
        f.launchFrame();

        for(int i = 0; i<shells.length; i++){
          shells[i]=new CannonShell();
        }
        String n = namestartTF.getText();
        System.out.println("the survival time for player " + n + " is " + timePeriod);

      }
    });

    JFrame window = new JFrame("Controls");
    window.setJMenuBar(mb);
    window.setContentPane(content);
    window.setSize(300,700);
    window.setLocation(1300,50);
    window.setVisible(true);

    this.setTitle("Flight Game Demo");
    this.setSize(Constant.GAME_WIDTH+50,Constant.GAME_HEIGHT);
    this.setLocation(50,50);
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    this.addKeyListener(new KeyMonitor()); //Start the KeyAdapter

    shells = new CannonShell[20];

    for(int i = 0; i<shells.length; i++){
      shells[i]=new CannonShell();
    }

  }
  public static void writeMapToFile(Map<String,String>d,String filename){
    try {
      PrintWriter writer = new PrintWriter(filename, "UTF-8");
      Set<String> keys = d.keySet();
      for(String n: keys){
        writer.println(n+"|"+d.get(n));
      }
      writer.close();
    } catch (Exception e){
      System.out.println("Problem writing to file: "+e);
    }
  }

  public static void printDictionary(Map<String,String>d){
    System.out.println("\n\n gradebook:\n");
    Set<String> keys = d.keySet();
    for(String n: keys){
      System.out.println("The id for "+n+" is "+d.get(n));
    }
  }

  public static Map<String,String> readMapFromFile(String filename){
      Map<String,String> d = new TreeMap<String,String>();
      try{
        File file = new File(filename);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()){
          String line = scanner.nextLine();
          int delimiter = line.indexOf("|");
          String key = line.substring(0,delimiter);
          String value = line.substring(delimiter+1);
          d.put(key,value);
        }
        scanner.close();
      } catch (FileNotFoundException e){
        System.out.println("Problem reading map from file "+e);
      }
      return d;
  }

  public static void main(String[] args) {
    MyGameFrame f = new MyGameFrame();
    f.launchFrame();
}

  //Double buffering tech to solve CannonShell flashing problem
  private Image offScreenImage = null;
  public void update(Graphics g){
    if(offScreenImage == null){
      offScreenImage = this.createImage(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);
    }
    Graphics gOff = offScreenImage.getGraphics();
    paint(gOff);
    g.drawImage(offScreenImage,0,0,null);
  }
}
