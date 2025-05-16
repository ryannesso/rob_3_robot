import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControlHandler implements KeyListener {
    double speed = 0.0;
    double angle = 0.0;
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            speed += 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            speed -= 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            angle += 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            angle -= 1;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
