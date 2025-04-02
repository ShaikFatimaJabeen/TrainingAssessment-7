import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class BankAccount {
    private int balance;
    private final int accountId;
    private final Lock lock = new ReentrantLock();

    public BankAccount(int accountId, int balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getBalance() {
        return balance;
    }

    public boolean withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public boolean tryLock() {
        return lock.tryLock(); 
    }

    public void unlock() {
        lock.unlock();
    }
}


class Transaction implements Runnable {
    private final BankAccount fromAccount;
    private final BankAccount toAccount;
    private final int amount;

    public Transaction(BankAccount fromAccount, BankAccount toAccount, int amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    @Override
    public void run() {
        while (true) {
            if (fromAccount.tryLock()) {
                try {
                    if (toAccount.tryLock()) {
                        try {
                            if (fromAccount.withdraw(amount)) {
                                toAccount.deposit(amount);
                                System.out.println(Thread.currentThread().getName() + 
                                    "  Transferred $" + amount + 
                                    " from Account " + fromAccount.getAccountId() + 
                                    " to Account " + toAccount.getAccountId());
                            } else {
                                System.out.println(Thread.currentThread().getName() + 
                                    "  Insufficient funds in Account " + fromAccount.getAccountId());
                            }
                            return; 
                        } finally {
                            toAccount.unlock();
                        }
                    }
                } finally {
                    fromAccount.unlock();
                }
            }
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


public class BankTransactionSystem {
    public static void main(String[] args) {
        BankAccount account1 = new BankAccount(101, 1000);
        BankAccount account2 = new BankAccount(102, 2000);

        // Simulating multiple transactions using threads
        Thread t1 = new Thread(new Transaction(account1, account2, 300), "Thread-1");
        Thread t2 = new Thread(new Transaction(account2, account1, 500), "Thread-2");
        Thread t3 = new Thread(new Transaction(account1, account2, 700), "Thread-3");

        t1.start();
        t2.start();
        t3.start();
    }
}

