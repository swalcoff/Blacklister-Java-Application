

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;


public class myGui extends QuickstartSample {
    private JFrame f;
    private JPanel p;
    private JButton b1;
    private JButton b2;
    private JLabel lab;
    private JLabel lab1;
    private JLabel lab2;
    private JLabel lab3;
    private JTextField tex;
    private JList blist;

    //blacklist gBlack;

    public myGui(blacklist b){
        gui(b);
    }

    public void gui(blacklist b){
        f = new JFrame();
        f.setSize(400, 300);

        f.setLocationRelativeTo(null);
        f.setAlwaysOnTop(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        p = new JPanel();
        b1 = new JButton("Close Browser");
        b2 = new JButton("Enter");
        lab = new JLabel("Warning: you are getting distracted.");
        //lab1 = new JLabel("Here is the blacklist: " + b.toString());
        lab2 = new JLabel("Add to the blacklist below:");
//        lab3 = new JLabel(".com");
        tex =  new JTextField(10);

        blist = new JList(b.getList().toArray());

        p.add(b1);
        p.add(lab);
        //p.add(lab1);
        p.add(lab2);
        p.add(tex);
        p.add(b2);
        p.add(blist);
//        p.add(lab3);
        f.add(p);
        f.setVisible(true);
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //closeScreen();
                //closeBrowser();
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                try{
                    checker(b);
                } catch (java.lang.Exception e1){
                    System.err.println(e1);
                }
            }
        });
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = tex.getText();
                try{
                        b.add(input);
                        addToFire(b);
                        blist.setListData(b.getList().toArray());
                }catch (java.lang.Exception e1) {}
            }
        });
        tex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = tex.getText();
                try{
                        b.add(input);
                        addToFire(b);
                        blist.setListData(b.getList().toArray());
                }catch (java.lang.Exception e1) {}
            }
        });


    }
}
