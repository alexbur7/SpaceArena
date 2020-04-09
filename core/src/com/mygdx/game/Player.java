package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import static com.mygdx.game.ArenaGame.TextureArray_aim_player_2_HASH;
import static com.mygdx.game.ArenaGame.TextureArray_aim_player_4_HASH;
import static com.mygdx.game.ArenaGame.TextureArray_jump_player_2_HASH;
import static com.mygdx.game.ArenaGame.TextureArray_jump_player_4_HASH;
import static com.mygdx.game.ArenaGame.TextureArray_move_player_2_HASH;
import static com.mygdx.game.ArenaGame.TextureArray_move_player_4_HASH;
import static com.mygdx.game.ClientClass.box;
import static com.mygdx.game.Shooting.createEnemyShootingArray;

public class Player extends ActorObj {
    Vector2 lastFrame;
    Vector2 position;

    JumpState jumpState;
    Rectangle rectangle;
    int hp=100;
    boolean killed;
    private Texture txt;
    private Vector2 velocityY;
    private float dt=0;
    private float velocity=150;
    private float JUMP=350;
    private int ID;
    public boolean flip=false;

    private Array<TextureRegion> aim_player_2= new Array<>();
    private Array<TextureRegion> move_player_2= new Array<>();
    private Array<TextureRegion> jump_player_2= new Array<>();

    private Array<TextureRegion> aim_player_4= new Array<>();
    private Array<TextureRegion> move_player_4= new Array<>();
    private Array<TextureRegion> jump_player_4= new Array<>();


    public Array<TextureRegion> getTextureArray_aim_player_2() {return aim_player_2;}

    public Array<TextureRegion> getTextureArray_move_player_2() {
        return move_player_2;
    }

    public Array<TextureRegion> getTextureArray_jump_player_2() {
        return jump_player_2;
    }

    public Array<TextureRegion> getTextureArray_aim_player_4() {
        return aim_player_4;
    }

    public Array<TextureRegion> getTextureArray_move_player_4() {
        return move_player_4;
    }

    public Array<TextureRegion> getTextureArray_jump_player_4() {
        return jump_player_4;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public JumpState getJumpState() {
        return jumpState;
    }
    enum JumpState {
        JUMPING,
        FALLING,
        GROUNDED
    }


    public Player(int x, int y) {
        position=new Vector2();
        jumpState = JumpState.FALLING;
        velocityY=new Vector2();
        lastFrame=new Vector2();
        killed=false;

        txt=new Texture("Aim (1).png");
        setPosition(x,y);
        setWidth(txt.getWidth());
        setHeight(txt.getHeight());
        setOrigin(getWidth()/2,getHeight());
        rectangle = new Rectangle(getX()-30, getY(),getWidth(),getHeight());
    }



    @Override
    public void act(float delta) {
        super.act(delta);
        dt+=delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        try{
        batch.draw(
                animation.getKeyFrame(dt),getX(),getY(),getOriginX(),
                getOriginY(),getWidth(),getHeight(),
                (flip ? -1 : 1)*getScaleX(),getScaleY(),getRotation() );
        //batch.draw(txt,getX(),getY());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void collapse(Player player) {

    }

    @Override
    public void update() {
        if (this.getName().equals("CURRENT_PLAYER")) {
            if (this.getName().equals("ENEMY")) {//удерживает в это же время противника на месте
                position.x = 1000;
                position.y = 50;
                setX(position.x);
                setY(position.y);
            }

            lastFrame.set(position);
            float delta = Gdx.graphics.getDeltaTime();
            velocityY.y -= delta * MainGame.GRAVITY;
            position.mulAdd(velocityY, delta);
            setY(position.y);


            if (position.y - txt.getHeight() / 2 < 0) {
                jumpState = JumpState.GROUNDED;
                position.y = txt.getHeight() / 2;
                velocityY.y = 0;
            }

            if (MainGame.jumped) {
                switch (jumpState) {
                    case GROUNDED:
                        startJump();
                        break;
                    case JUMPING:
                        continueJump();
                        break;
                    case FALLING:
                        break;
                }
            } else endJump();
            //должен быть большой иф, просмотр кого мы будем двигать этим джойстиком
            if (!JoystickLeft.CheckAngleLeft && JoystickLeft.isTouchLeft) {
                flip = true;
                if (this.getID() == 0) useAnim(0.1f, true, move_player_2);
                else useAnim(0.1f, true, move_player_4);
                txt.dispose();
                position.x = getX() - velocity * Gdx.graphics.getDeltaTime();
                setX(position.x);
            } else if (JoystickLeft.isTouchLeft) {
                flip = false;
                if (this.getID() == 0) useAnim(0.1f, true, move_player_2);
                else useAnim(0.1f, true, move_player_4);
                txt.dispose();
                position.x = getX() + velocity * Gdx.graphics.getDeltaTime();
                setX(position.x);
            }
            rectangle.setPosition(getX(), getY());
            if ((!JoystickLeft.isTouchLeft) && (jumpState == JumpState.GROUNDED)) {
                if (this.getID() == 0) useAnim(0.1f, true, aim_player_2);
                else useAnim(0.1f, true, aim_player_4);
                txt.dispose();
            }

            for (Platform pl : ArenaGame.plat) {
                platformReact(pl);
            }
        }
        else {
            System.out.println(this.position.hashCode());
            //System.out.println(box.BpositionPlayer.hashCode());
            this.position= box.BpositionPlayer;
            //ArenaGame.ENEMY.setX(box.BpositionPlayer.x);
            //ArenaGame.ENEMY.setY(box.BpositionPlayer.y);
            this.hp= box.Bhp;

            if (box.BplayerAnimHash==TextureArray_aim_player_2_HASH) this.useAnim(0.1f,true,this.getTextureArray_aim_player_2());
            if (box.BplayerAnimHash==TextureArray_move_player_2_HASH) this.useAnim(0.1f,true,this.getTextureArray_move_player_2());
            if (box.BplayerAnimHash==TextureArray_jump_player_2_HASH) this.useAnim(0.1f,false,this.getTextureArray_jump_player_2());
            if (box.BplayerAnimHash==TextureArray_aim_player_4_HASH) this.useAnim(0.1f,true,this.getTextureArray_aim_player_4());
            if (box.BplayerAnimHash==TextureArray_move_player_4_HASH) this.useAnim(0.1f,true,this.getTextureArray_move_player_4());
            if (box.BplayerAnimHash==TextureArray_jump_player_4_HASH) this.useAnim(0.1f,false,this.getTextureArray_jump_player_4());



            //ArenaGame.ENEMY.anim=box.BplayerAnim;
            //ArenaGame.ENEMY.animation=box.BplayerAnim;
            this.rectangle=box.BrectanglePlayer;

            //ArenaGame.shootingsEnemy=createEnemyShootingArray(box);
        }
    }

    @Override
    public void platformReact(Platform pl) {
        float x1=lastFrame.x;
        float y1=lastFrame.y;
        float x2=lastFrame.x+txt.getWidth();
        float y2=lastFrame.y+txt.getHeight();//координаты концов диагонали персонажа

        float x3=pl.coreX;
        float y3=pl.coreY;
        float x4=pl.coreX+pl.getPlatTexture().getWidth();
        float y4=pl.coreY+pl.getPlatTexture().getHeight();//координаты концов диагонали платформы

        boolean x=((x3 > x1 && x3 < x2) || (x4 > x1 && x4 < x2) || (x1 > x3 && x1 < x4) || (x2 > x3 && x2 < x4));
        boolean y=((y3 > y1 && y3 < y2) || (y4 > y1 && y4 < y2) || (y1 > y3 && y1 < y4) || (y2 > y3 && y2 < y4));

        if (lastFrame.x >= pl.left && lastFrame.x <= pl.right && lastFrame.y >= pl.top && (position.y - pl.top < 0.2f)) {//запрыгивать
            if (velocityY.y <= 0) {
                velocityY.y = 0;
                jumpState = JumpState.GROUNDED;
                position.y = pl.top;
            }
        } else if (lastFrame.x >= pl.left && lastFrame.x <= pl.right && position.y <= pl.top) {//стукаться головой о нижнюю грань
            if (pl.coreY - (position.y + txt.getHeight()) <= 0.2f) {
                velocityY.y = -10;
            }
        }
        else if (x && y) {// посмотреть,что там с игреками,надо подобрать цифру
            if (jumpState!=JumpState.GROUNDED) {
                position.x = lastFrame.x;
                setX(position.x);
            }
        }



        /*else if ((jumpState==JumpState.GROUNDED)&&(pl.left - position.x < 0.2f) && (position.y+10f >= pl.bottom - txt.getHeight() + 50) && (position.y <= pl.top)) {
            position.x = lastFrame.x - 5f;
            setX(position.x);
        }*/
         /*else if ((pl.coreX-lastFrame.x-txt.getWidth()/2<1f) && (lastFrame.y+txt.getHeight()/2-30>=pl.bottom)&& (position.y-txt.getHeight()/2>pl.bottom)){
                if (lastFrame.y>=pl.bottom){
                    position.y=lastFrame.y;
                }
                position.x=lastFrame.x-5f;
                setX(position.x);
            }*/
        rectangle.setPosition(getX(), getY());
    }
    private void endJump() {
        if (jumpState == JumpState.JUMPING) {
            jumpState = JumpState.FALLING;
        }
        MainGame.jumped=false;
    }

    private void startJump(){
        jumpState=JumpState.JUMPING;
        velocityY.y+=JUMP;
        if (this.getID()==0) useAnim(0.1f,false,jump_player_2);
        else useAnim(0.1f,false,jump_player_4);//для второго игрока надо сделать
        continueJump();
    }

    private void continueJump(){
        if (jumpState == JumpState.JUMPING)
            endJump();
    }

    public void dispose(){
        txt.dispose();
    }
}