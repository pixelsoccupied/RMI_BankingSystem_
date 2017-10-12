package Clients;

import BankOps.CustomerOperations;
import Logs.Log;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.Scanner;

public class CustomerClient {
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String address;
    private String phone;
    private double balance;
    private String branch;






    public CustomerClient(String firstName, String lastName, String accountNumber, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.address = address;
        this.phone = phone;
        this.balance = 0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }



    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "CustomerClient{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public static void main(String[] args) throws Exception {

       startClientOperations();

    }



    private static void startClientOperations() throws Exception {

        Scanner kb = new Scanner(System.in);


        String customerID;
        boolean lock = false;


        while (!lock) {

            System.out.println("Hi, please enter your ID to access the appropriate serve");
            customerID = kb.next();
            System.out.println("You've entered " + customerID);
            if (customerID.charAt(2) == 'C' || customerID.charAt(2) == 'c' ) {
                init_Sever(customerID);
            }
            else
                System.out.println("Invalid entry, must be a customer!");


        }


    }

    private static void init_Sever(String customerID) throws Exception {

        String serverName = customerID.substring(0,2);
        serverName = serverName.toLowerCase();

        if(Objects.equals(serverName, "qc")){
            Registry registry = LocateRegistry.getRegistry(5876);
            CustomerOperations qc = (CustomerOperations)registry.lookup("QC");
            System.out.println("QC server selected");
            readyForOperation(qc,customerID );


        }
        else if(Objects.equals(serverName, "bc")){
            Registry registry = LocateRegistry.getRegistry(6876);
            CustomerOperations bc = (CustomerOperations)registry.lookup("BC");
            System.out.println("BC server selected");
            readyForOperation(bc, customerID);

        }
        else if(Objects.equals(serverName, "nb")){
            Registry registry = LocateRegistry.getRegistry(8876);
            CustomerOperations nb = (CustomerOperations)registry.lookup("NB");
            System.out.println("NB server selected");
            readyForOperation(nb, customerID);


        }
        else if(Objects.equals(serverName, "mb")){
            Registry registry = LocateRegistry.getRegistry(7876);
            CustomerOperations mb = (CustomerOperations)registry.lookup("MB");
            System.out.println("MB server selected");
            readyForOperation(mb, customerID);


        }
        else {
            System.out.println("Invalid server");
            startClientOperations();
        }






    }

    private static void readyForOperation(CustomerOperations server, String customerID) throws Exception {

        int optionSelected = 0;
        String customerServer = customerID.substring(0, 2);
        Scanner kb = new Scanner(System.in);


        do {
            System.out.println("Hi " + customerID + " what would you like to do today?");
            System.out.println("\n1.Deposit" + " \n2.Withraw \n3.Get balance. \n4.Exit");
            optionSelected = kb.nextInt();

            switch (optionSelected) {

                case 1:
                    System.out.println("You've selected Deposit. Enter amount to deposit!");
                    double dep = kb.nextDouble();
                    kb.nextLine();


                    boolean deposit = server.deposit(customerID, dep);

                    if (deposit) {
                        System.out.println("Successful deposit");
                        double getDepBalance = server.getBalance(customerID);
                        System.out.println("New balance $" + getDepBalance);
                        Log.generateLogFileClient("Deposit ", "Customer", customerID);
                    } else {
                        System.out.println("Problem with deposit!");
                        Log.generateLogFileClient("Deposit failed ", "Customer", customerID);
                    }
                    System.out.println("--------------------------");

                    break;

                case 2:
                    System.out.println("You've selected withdraw! Enter amount to withdraw!");

                    double withdrw = kb.nextDouble();
                    kb.nextLine();


                    boolean withdraw = server.withdraw(customerID, withdrw);

                    if (withdraw) {
                        System.out.println("Successful Withdraw!");
                        double getDepBalance = server.getBalance(customerID);
                        System.out.println("New balance $" + getDepBalance);
                        Log.generateLogFileClient("Withdraw ", "Customer", customerID);

                    } else
                        System.out.println("Problem with withdraw!");
                    System.out.println("--------------------------");
                    Log.generateLogFileClient("Withdraw failed  ", "Customer", customerID);


                    break;
                case 3:

                    double checkBalance = server.getBalance(customerID);
                    System.out.println("Current balance $" + checkBalance);
                    Log.generateLogFileClient("Checked balance ", "Customer", customerID);

                    System.out.println("--------------------------");
                    break;
                case 4:
                    System.out.println("Logging out");
                    System.out.println("Bye! " + customerID);
                    System.out.println("--------------------------");
                    startClientOperations();
                    break;


            }


        } while (true);


    }


}
