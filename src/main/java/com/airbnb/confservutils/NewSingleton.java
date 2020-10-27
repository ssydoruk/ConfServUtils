/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.airbnb.confservutils;

/**
 *
 * @author stepan_sydoruk
 */
public class NewSingleton {
    
    private NewSingleton() {
        System.out.println("Constructor");
    }
    
    public static NewSingleton getInstance() {
        return NewSingletonHolder.INSTANCE;
    }
    
    private static class NewSingletonHolder {

        private static final NewSingleton INSTANCE = new NewSingleton();
    }
    
    public static void main(String[] args) {
        System.out.println("-1-");
        NewSingleton.getInstance();
        System.out.println("-2-");
    }
}
