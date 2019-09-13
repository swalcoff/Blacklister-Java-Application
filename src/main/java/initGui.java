import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;


public class initGui {
    private JFrame f;
    private JPanel p;
    private JButton b2;
    private JButton b3;
    private JLabel lab;
    private JLabel lab1;
    private JLabel lab2;

    private JTextField tex;
    private JList blist;


    public initGui(blacklist b, String username){
        gui2(b, username);
    }

    public void gui2(blacklist b, String username){
        f = new JFrame();
        f.setSize(600, 300);

        f.setLocationRelativeTo(null);
        f.setAlwaysOnTop(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        p = new JPanel();
        b2 = new JButton("Enter");
        b3 = new JButton("Close Window");
        lab = new JLabel("Your blacklist is empty! You must add to" +
                " the blacklist before you proceed.");
        lab2 = new JLabel("Enter the URL title starting with \"http\":");

        tex =  new JTextField(10);

        blist = new JList(b.getList().toArray());

        p.add(lab);
        //p.add(lab1);
        p.add(lab2);

        p.add(tex);
        p.add(b2);
        p.add(b3);
        p.add(blist);
        f.add(p);
        f.setVisible(true);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = tex.getText();
                try{
                    b.add(input);
                    System.out.println(b.toString());
                    main.addToFire(b, username);
                    blist.setListData(b.getList().toArray());
                }catch (java.lang.Exception e1) {
                    System.out.println(e1);
                }
            }
        });
        tex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = tex.getText();
                try{
                    b.add(input);
                    main.addToFire(b, username);
                    blist.setListData(b.getList().toArray());
                }catch (java.lang.Exception e1) {
                    System.out.println(e1);
                }
            }
        });
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                try{
                    main.checker(b, username);
                }catch (java.lang.Exception e2){
                    System.out.println(e2);
                }
            }
        });


    }
}
