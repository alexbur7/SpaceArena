package com.mygdx.game;

import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.GdxFIRDatabase;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.distributions.DatabaseDistribution;
import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;
import pl.mk5.gdx.fireapp.functional.Function;

public class FireBaseClass {

    public static void signIn(final String playerEmail, final char[] playerPassword) {
        // Sign in via username/email and password
        GdxFIRAuth.instance()
                .signInWithEmailAndPassword(playerEmail, playerPassword)
                .then(new Consumer<GdxFirebaseUser>() {
                    @Override
                    public void accept(GdxFirebaseUser gdxFirebaseUser) {
                        //if (gdxFirebaseUser.getUserInfo()!=null)
                        successLogin();
                    }
                });
    }


    public static void register(final String playerEmail, final char[] playerPassword){
            GdxFIRAuth.instance()
                    .createUserWithEmailAndPassword(playerEmail, playerPassword).then(new Consumer<GdxFirebaseUser>() {
                        @Override
                        public void accept(GdxFirebaseUser gdxFirebaseUser) {
                            successRegister();
                        }
                    })
                    .fail(new BiConsumer<String, Throwable>() {
                        @Override
                        public void accept(String s, Throwable throwable) {
                                GdxFIRAuth.inst().getCurrentUser().delete().subscribe();
                                System.out.println("ERROR DURING REG");
                        }
                    });
    }

    public static void signOut(){
        GdxFIRAuth.instance().signOut()
                .then(new Consumer<Void>() {
                    @Override
                    public void accept(Void o) {
                        MainGame.authorized=false;
                    }
                });
    }

    private static void successLogin(){
        MainGame.authorized=true;
        System.out.println("LOGGED");
    }

    private static void successRegister(){
        MainGame.registered=true;
        System.out.println("REGISTERED");
    }

    public static void updateKDInDataBase(final int addKills,final int addDeath) {
        GdxFIRDatabase.instance().inReference("/Kills/"+MainGame.playerPassword)
                .transaction(Long.class, new Function<Long, Long>() {
                    @Override
                    public Long apply(Long i) {
                        return i + addKills;
                    }
                }).then( GdxFIRDatabase.inst().inReference("/Death/"+MainGame.playerPassword)
                .transaction(Long.class, new Function<Long, Long>() {
                    @Override
                    public Long apply(Long i) {
                        return i + addDeath;
                    }
                }));
        System.out.println("updated kd");
    }

    public static void addKDInDataBase() {
        GdxFIRAuth.instance().signInWithEmailAndPassword(MainGame.playerLogin,MainGame.playerPassword.toCharArray()).then(
                        GdxFIRDatabase.instance()
                                .inReference("/Death/"+MainGame.playerPassword).setValue(0));
        GdxFIRAuth.instance().signInWithEmailAndPassword(MainGame.playerLogin,MainGame.playerPassword.toCharArray()).then(
                GdxFIRDatabase.instance()
                        .inReference("/Kills/"+MainGame.playerPassword).setValue(0));
        System.out.println("added kd");
    }


}
