import javax.swing.JFrame;

public class GameFrame extends JFrame{
    
    GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Pong");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); //Takes JFrame and fits it to all components added to frame
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
