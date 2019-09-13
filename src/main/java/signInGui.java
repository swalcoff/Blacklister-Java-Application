import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class signInGui {
    private JFrame f;
    private JPanel p;
    private JButton b2;
    private JButton b3;
    private JLabel lab;
    private JLabel lab1;
    private JLabel lab2;
    private String username;

    private JTextField tex;


    public String signInGui(){
        return gui3();
    }

    public String gui3(){
        f = new JFrame();
        f.setSize(600, 300);

        f.setLocationRelativeTo(null);
        f.setAlwaysOnTop(true);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        p = new JPanel();
        b2 = new JButton("Sign in");
        lab = new JLabel("Please enter the email associated with your account");
        lab2 = new JLabel("If you haven't signed up in the mobile app, please do so first");

        tex =  new JTextField(10);


        p.add(lab);
        //p.add(lab1);
        p.add(lab2);

        p.add(tex);
        p.add(b2);

        f.add(p);
        f.setVisible(true);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = tex.getText();

            }
        });
        tex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = tex.getText();
            }
        });

        return username;
    }
}
