package org.example;

import java.util.concurrent.Semaphore;

class Main{
    public static void main(String[] args) {
        BankVault bank = new BankVault();
        Semaphore sem = new Semaphore(2);
        System.out.println("Баланс банка = "+ bank.getBalance());
        for(int i=0;i<6;i++){
            new Thread(new Cashier(10+i*10,sem,"Cashir-"+i,bank)).start();
        }

        new Thread(()-> {
            try{
                sem.acquire(2);
                System.out.println("Баланс банка = "+ bank.getBalance());
            }catch(Exception e){}
            finally{
                sem.release(2);
            }
        }).start();
    }
}

class BankVault{
    private     int balance=0;

    public int getBalance(){
        return balance;
    }

    public synchronized void increaseBalance(int dep){
        while(dep>0){
            balance+=10;
            dep-=10;
        }
    }
}

class Cashier implements Runnable{
    int dep;
    Semaphore sem;
    String name;
    BankVault bank;

    Cashier(int dep ,Semaphore sem, String name, BankVault bank){
        this.sem=sem;
        this.name=name;
        this.bank=bank;
        this.dep = dep;
    }

    @Override
    public void run(){
        try {
            sem.acquire();
            System.out.println("кассир " + name + " начал работать");
            System.out.println("Кассир начал класть деньги в сейф");
            bank.increaseBalance(dep);
            Thread.sleep(5000);
        }
        catch(Exception e){}
        finally{
            System.out.println("Кассир закончил " +name+" класть деньги");
            sem.release();
        }
    }
}