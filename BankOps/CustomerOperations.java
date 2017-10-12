package BankOps;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface CustomerOperations extends Remote {

    boolean deposit (String customerID, double amt)throws RemoteException;
    boolean withdraw (String customerID, double amt)throws RemoteException;
    double getBalance (String customerID)throws RemoteException;

}
