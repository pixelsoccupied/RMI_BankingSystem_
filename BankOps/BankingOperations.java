package BankOps;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface BankingOperations extends Remote{


    boolean createAccountRecord(String firstName,String lastName, String address, String phone,String branch) throws RemoteException;
    boolean editRecord (String customerID, String fieldName, String newValue) throws RemoteException;
    HashMap<String, Integer> getAccountCount() throws RemoteException;

    //Customer specific methods

    boolean deposit (String customerID, double amt)throws RemoteException;
    boolean withdraw (String customerID, double amt)throws RemoteException;
    double getBalance (String customerID)throws RemoteException;










}
