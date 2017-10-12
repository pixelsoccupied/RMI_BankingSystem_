package Clients;

import BankOps.BankingOperations;
import Logs.Log;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class MangerClient{

    private String managerID;
    private String branch;
    private static int iDCount = 1000;

    public MangerClient(String branch) {

        this.branch = branch.toUpperCase();
        iDCount++;
        this.managerID = branch + "M" + iDCount;
    }



    public static void main(String[] args) throws Exception {


        //populate();
        startManagerOperations();


    }










    private static void populate() throws RemoteException, NotBoundException {


        /*

        qc->5876
        BC->6876
        MB->7876
        NB->8876

        */


        Registry registry = LocateRegistry.getRegistry(5876);

        //Stub
        BankingOperations bankingOperations = (BankingOperations)registry.lookup("QC");


        boolean good = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"QC");
        boolean good2 = bankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"QC");
        boolean good3 = bankingOperations.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"QC");

        System.out.println(good);

        boolean wa = bankingOperations.editRecord("QCC1000", "Phone", "999" );


        //


        Registry registry2 = LocateRegistry.getRegistry(6876);
        BankingOperations bankingOperations2 = (BankingOperations)registry2.lookup("BC");

        boolean asd2 = bankingOperations2.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"BC");
        boolean asd222 = bankingOperations2.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"BC");
        boolean asd322 = bankingOperations2.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"BC");
        boolean qwfq22 = bankingOperations2.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"BC");



        Registry nb = LocateRegistry.getRegistry(8876);
        BankingOperations nbbankingOperations = (BankingOperations)nb.lookup("NB");

        boolean asdds = nbbankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"NB");
        boolean asd2ds = nbbankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"NB");
        boolean asd3ds = nbbankingOperations.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"NB");
        boolean qwfqds = nbbankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"NB");
        boolean asd4ds = nbbankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"NB");





        Registry mb = LocateRegistry.getRegistry(7876);
        BankingOperations mbbankingOperations2 = (BankingOperations)mb.lookup("MB");

        boolean asddas = mbbankingOperations2.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"MB");
        boolean asd3aa = mbbankingOperations2.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"MB");
        boolean qwfqaa = mbbankingOperations2.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"MB");
        boolean asd4aa = mbbankingOperations2.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"MB");
        boolean asd5aa = mbbankingOperations2.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"MB");


        bankingOperations.getAccountCount();
        bankingOperations2.getAccountCount();
        nbbankingOperations.getAccountCount();
        mbbankingOperations2.getAccountCount();


        bankingOperations2.deposit("BCC1000", 1000 );

    }

    private static void startManagerOperations() throws RemoteException, NotBoundException {

        Scanner kb = new Scanner(System.in);


        String managerID = null;
        boolean lock = false;


            while (!lock) {

                System.out.println("Hi, please enter your ID to access the appropriate serve");
                managerID = kb.next();
                System.out.println("You've entered " + managerID);
                if (managerID.charAt(2) == 'M' || managerID.charAt(2) == 'm' ) {
                    init_Sever(managerID);
                }
                else
                    System.out.println("Invalid entry, must be a manager");
                    lock = false;

            }


    }

    private static void init_Sever(String managerID) throws RemoteException, NotBoundException {

        String serverName = managerID.substring(0,2);
        serverName = serverName.toLowerCase();

        if(Objects.equals(serverName, "qc")){
            Registry registry = LocateRegistry.getRegistry(5876);
            BankingOperations qc = (BankingOperations)registry.lookup("QC");
            System.out.println("QC server selected");
            readyForOperation(qc,managerID );


        }
        else if(Objects.equals(serverName, "bc")){
            Registry registry = LocateRegistry.getRegistry(6876);
            BankingOperations bc = (BankingOperations)registry.lookup("BC");
            System.out.println("BC server selected");
            readyForOperation(bc, managerID);

        }
        else if(Objects.equals(serverName, "nb")){
            Registry registry = LocateRegistry.getRegistry(8876);
            BankingOperations nb = (BankingOperations)registry.lookup("NB");
            System.out.println("NB server selected");
            readyForOperation(nb, managerID);


        }
        else if(Objects.equals(serverName, "mb")){
            Registry registry = LocateRegistry.getRegistry(7876);
            BankingOperations mb = (BankingOperations)registry.lookup("MB");
            System.out.println("MB server selected");
            readyForOperation(mb, managerID);


        }
        else {
            System.out.println("Invalid server");
            startManagerOperations();
        }






    }

    private static void readyForOperation(BankingOperations server, String managerID) throws RemoteException, NotBoundException {

        int optionSelected = 0;
        String managerServer = managerID.substring(0,2);
        Scanner kb = new Scanner(System.in);


        while(optionSelected != 7){
            System.out.println("Hi " + managerID + " what would you like to do today?");
            System.out.println("\n1.Create new account.\n2.Edit record.\n3.Get an account count.\n4.Deposit" +
                    " \n5.Withraw \n6.Get balance. \n7.Exit");
            optionSelected = kb.nextInt();

             switch (optionSelected){

                 case 1:
                     System.out.println("You've selected create new account!Enter - Fname, lname, address, phone, branch");
                     String fName = kb.next();
                     String lName = kb.next();
                     String address = kb.next();
                     String phone = kb.next();
                     String branch = kb.next();
                     kb.nextLine();


                     if(Objects.equals(managerServer, branch)){
                         boolean accCreate = server.createAccountRecord(fName, lName, address, phone, branch);
                         if(accCreate){
                             System.out.println("Account created!");
                             try {
                                 Log.generateLogFileClient("Account created ", "Manager", managerID);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }

                         }
                         else{
                             System.out.println("Account was not created. Please try again");
                             try {
                                 Log.generateLogFileClient("Account creation failed ", "Manager", managerID);
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }

                         }
                     }
                     else {
                         System.out.println("Must be the same branch as the manager using the server!");
                         try {
                             Log.generateLogFileClient("Account creation failed ", "Manager", managerID);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }

                     }

                     System.out.println("--------------------------");

                     break;


                 case 2:
                     System.out.println("You've selected edit record. Enter customerID, field and new value!");
                     String custID = kb.next();
                     String field = kb.next();
                     String newValue = kb.next();
                     kb.nextLine();


                     boolean editAcc = server.editRecord(custID, field, newValue);

                     if(editAcc){
                         System.out.println("Account edited!");
                         try {
                             Log.generateLogFileClient("Account edited ", "Manager", managerID);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                     else{
                         System.out.println("Problem with editing account!");
                         try {
                         Log.generateLogFileClient("Account created ", "Manager", managerID);
                         } catch (IOException e) {
                         e.printStackTrace();
                         }
                     }
                     System.out.println("--------------------------");

                     break;


                 case 3:
                     System.out.println("Printing the current");
                     HashMap<String, Integer> clientCount = server.getAccountCount();
                     System.out.println("--------------------------");
                     for (String key : clientCount.keySet()) {
                         System.out.println(key + " " + clientCount.get(key));
                     }

                     try {
                         Log.generateLogFileClient("Get account count ", "Manager", managerID);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                     System.out.println("--------------------------");

                     break;
                 case 4:
                     System.out.println("You've selected Deposit. Enter customerID, and amount to deposit!");
                     String custIDDep = kb.next();
                     double dep = kb.nextDouble();
                     kb.nextLine();


                     boolean deposit = server.deposit(custIDDep, dep);

                     if(deposit){
                         System.out.println("Successful deposit");
                         double getDepBalance = server.getBalance(custIDDep);
                         System.out.println("New balance $" + getDepBalance);
                         try {
                             Log.generateLogFileClient("Deposit successful ", "Manager", managerID);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                     else {
                         System.out.println("Problem with deposit!");
                         try {
                             Log.generateLogFileClient("Deposit failed ", "Manager", managerID);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                     System.out.println("--------------------------");

                     break;

                 case 5:
                     System.out.println("You've selected withdraw! Enter customerID, and amount to withdraw!");

                     String custIDWith = kb.next();
                     double withdrw = kb.nextDouble();
                     kb.nextLine();




                     boolean withdraw = server.withdraw(custIDWith, withdrw);

                     if(withdraw){
                         System.out.println("Successful Withdraw!");
                         double getDepBalance = server.getBalance(custIDWith);
                         System.out.println("New balance $" + getDepBalance);
                         try {
                             Log.generateLogFileClient("Withdraw success ", "Manager", managerID);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                     else {
                         System.out.println("Problem with withdraw!");
                         try {
                             Log.generateLogFileClient("Withdraw failed ", "Manager", managerID);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                     System.out.println("--------------------------");

                     break;
                 case 6:
                     System.out.println("Enter the customer to see balance");
                     String custBal = kb.next();
                     kb.nextLine();
                     double checkBalance = server.getBalance(custBal);
                     System.out.println("Current balance $" + checkBalance);
                     try {
                         Log.generateLogFileClient("Balance check ", "Manager", managerID);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }

                     System.out.println("--------------------------");
                     break;
                 case 7:
                     System.out.println("Logging out");
                     System.out.println("Bye! " + managerID);
                     System.out.println("--------------------------");
                     startManagerOperations();
                     break;


             }






        }



    }


}
