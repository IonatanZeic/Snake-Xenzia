
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int  UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static  final int DELAY = 70;
    static final int SCORE_PER_APPLE = 1;
    static final int  OUT_OF_PANEL = (SCREEN_WIDTH*3);
    private static final int SCORE_PER_SPECIAL = 4 ;
    private static final int SPECIALAPPLE_TIME = 2 ;

    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    int bodyParts = 6;
    int applesEaten=0;
    int score=0;
    int appleX;
    int appleY;

    boolean verify = false;
    boolean gamePause = false;

    boolean cantPause = false;

    int cantPauseTime =0;

    int specialX = OUT_OF_PANEL;
    int specialY = OUT_OF_PANEL;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    TimerSpecial timerSpecial;
    private boolean isSpecialApple = false;

    GamePanel(){
        random  = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }


    public void startGame(){
        newApple();

        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
        //timerStop = false;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if (running  ) {
            if(!gamePause)
                drawGame(g);
            else
                gamePausedDraw(g);
        }

        else {
            gameOver(g);
        }

    }



    public void drawGame(Graphics g){
            for (int i = 0; i<SCREEN_HEIGHT/UNIT_SIZE; i++){
                g.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0,i*UNIT_SIZE,SCREEN_WIDTH,i*UNIT_SIZE );
            }
            g.setColor(Color.blue);
            g.fillOval(appleX,appleY,UNIT_SIZE, UNIT_SIZE);

            if(applesEaten % 5 == 0 && applesEaten!=0){

                g.setColor(Color.cyan);
                g.fillOval(specialX,specialY,UNIT_SIZE*3, UNIT_SIZE*3);
            }
                 CantPause(g);


            for(int i= 0; i < bodyParts; i++){
                if(i == 0){
                    g.setColor(Color.red);
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
                else {
                    // g.setColor(new Color(45,180,0));
                    g.setColor(Color.green);
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
            }
            g.setColor(Color.green);
            g.setFont(new Font("Ink Free",Font.BOLD,45));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score:"+score,(SCREEN_WIDTH - metrics.stringWidth("Score:"+score))/2 , g.getFont().getSize());



        }
public void CantPause(Graphics g){
    if( cantPauseTime > 0){

        g.setColor(Color.white);
        g.setFont(new Font("URW Gothic L",Font.BOLD,UNIT_SIZE));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("You can not pause when a special apple appears",(SCREEN_WIDTH - metrics1.stringWidth("You can not pause when a special apple appears"))/2 , SCREEN_HEIGHT-UNIT_SIZE);
        cantPause = false;
        cantPauseTime--;
    }
}
    public void newApple () {
        appleX = random.nextInt(((SCREEN_WIDTH/UNIT_SIZE)-1))*UNIT_SIZE;
        appleY = random.nextInt(((SCREEN_HEIGHT/UNIT_SIZE)-1))*UNIT_SIZE;
    }
    public void newSpecialApple () {
        TimerStart();
        isSpecialApple = true;
            specialX = random.nextInt(((SCREEN_WIDTH / UNIT_SIZE) - 3)) * UNIT_SIZE;
            specialY = random.nextInt( ((SCREEN_HEIGHT / UNIT_SIZE) - 3)) * UNIT_SIZE;


    }
    private void TimerStart(){
        timerSpecial = new TimerSpecial(SPECIALAPPLE_TIME);

    }
    private void checkSpecialTimer() {
        //we check if the apple appeared -->
        //if the timer is off --> the special apple disappearsj
        // create new apple-
            if(isSpecialApple && !gamePause) {
                if (!timerSpecial.isTimerOn()) {
                    specialY = OUT_OF_PANEL;
                    specialX = OUT_OF_PANEL;
                    isSpecialApple = false;
                    newApple();
                }
            }


    }
    public void move(){
        for(int i= bodyParts;i>0;i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }
    public void checkApple(){

            if(((x[0] == appleX) && y[0] == appleY) ){
                bodyParts++;
                applesEaten+= SCORE_PER_APPLE;
                score += SCORE_PER_APPLE;
                if(applesEaten % 5 != 0 ){
                    newApple();
                    verify = false;
                }
                else if(applesEaten != 0 )  {
                    newSpecialApple();
                    appleY = 1000;
                    appleX = 1000;
                    verify = true;
                }
            }
            if( ((x[0] == specialX || x[0] == specialX+25 || x[0] == specialX+50) && (y[0]== specialY ||  y[0]== specialY + 25 || y[0] == specialY+50)) && verify){

                newApple();
                bodyParts++;
                applesEaten = 0;
                score += SCORE_PER_SPECIAL;
                verify = false;
                isSpecialApple = false;

            }



    }
    public void checkCollisions() {
        //checks if head collides with body
        for(int i = bodyParts;i>0;i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //check if head touches left border
        if(x[0] < 0) {
            running = false;
        }
        //check if head touches right border
        if(x[0] > SCREEN_WIDTH-UNIT_SIZE) {
            running = false;
        }
        //check if head touches top border
        if(y[0] < 0) {
            running = false;
        }
        //check if head touches bottom border
        if(y[0] > SCREEN_HEIGHT-UNIT_SIZE) {
            running = false;
        }
        if(!running)
            timer.stop();


    }
    public void gameOver(Graphics g){
        //game over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free",Font.BOLD,75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over",(SCREEN_WIDTH - metrics1.stringWidth("Game Over"))/2 , SCREEN_HEIGHT/2);

        g.setColor(Color.green);
        g.setFont(new Font("Ink Free",Font.BOLD,45));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score:"+score,(SCREEN_WIDTH - metrics2.stringWidth("Score:"+score))/2 , g.getFont().getSize());
    }








    public void gamePausedDraw(Graphics g){
        //game Pause text
        g.setColor(Color.blue);
        g.setFont(new Font("Ink Free",Font.BOLD,55));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Is Paused",(SCREEN_WIDTH - metrics1.stringWidth("Game Is Paused"))/2   , SCREEN_HEIGHT-(SCREEN_HEIGHT*2/3));
        g.drawString("Press R to resume",(SCREEN_WIDTH - metrics1.stringWidth("Press R to resume"))/2 , ((SCREEN_HEIGHT)/2));

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(running && !gamePause) {
            move();
            checkApple();
            checkCollisions();
            checkSpecialTimer();
        }


        repaint();
    }



    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';
                    }
                }
                case KeyEvent.VK_SPACE -> {

                    if(!gamePause && !isSpecialApple){
                        gamePause = true;
                    }
                    else if( !gamePause){
                        cantPauseTime = 10;

                    }


                }
                case KeyEvent.VK_R -> {

                    if(gamePause){
                        gamePause = false;
                    }


                }
            }
        }
    }
}