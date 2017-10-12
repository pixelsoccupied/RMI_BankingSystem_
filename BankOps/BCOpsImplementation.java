package BankOps;

import Clients.CustomerClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static Logs.Log.generateLogFileServer;

public class BCOpsImplementation extends UnicastRemoteObject implements BankingOperations, CustomerOperations, Runnable {

    static private HashMap<Character, ArrayList<CustomerClient>> hashMap = new HashMap<Character, ArrayList<CustomerClient>>();
    static int iD = 1000;
    private HashMap<String, Integer> allBranchCount = new HashMap<String, Integer>();
    //lock object
    Object lock = new Object();


    public BCOpsImplementation() throws Exception {
        super();

    }

    /*

     When a manager invokes this method through a program called ManagerClient, the server associated
     with the indicated branch attempts to create a customer record with the information passed, assigns a unique
     customerID and inserts the customer record at the appropriate location in the hash map maintained at
     the indicated branch.The server returns information to the manager whether the operation was
     successful or not and both the server and the client
     store this information in their logs.
     */
    @Override
    public synchronized boolean createAccountRecord(String firstName, String lastName, String address, String phone, String branch) {

        int noErrorCount = 0;
        System.out.println("Inside createAccountRecord()...");
        //create ID
        String generateID = generateID(branch);
        //create a customer object
        CustomerClient customerClient = new CustomerClient(firstName, lastName, generateID, address, phone);
        customerClient.setBranch(branch);
        //To uppercase
        Character key = lastName.toUpperCase().charAt(0);
        //If key doesnt exist create one, and start the arraylist
        if (hashMap.containsKey(key)) {
            //get the arrayList if the key is present
            ArrayList<CustomerClient> temp = hashMap.get(key);
            temp.add(customerClient);
            hashMap.put(key, temp);
            noErrorCount++;

        } else {
            //Create a temp ArrayList for the value
            ArrayList<CustomerClient> temp = new ArrayList<CustomerClient>();
            temp.add(customerClient);
            //add to the map
            hashMap.put(key, temp);
            noErrorCount++;

        }
        // customerClient.toString();

        //print content of the arraylist for testing
        ArrayList<CustomerClient> temp = hashMap.get(key);
        for (CustomerClient cc : temp
                ) {
            System.out.println(cc.toString());
        }

        if (noErrorCount > 0) {
            //log stuff
            try {
                String msg = "Account created " + customerClient.getAccountNumber();
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        } else
            //log stuff
            try {
                String msg = "Account creation failed";
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;

    }

    /* When invoked by a manager, the server associated with this customer, (determined by the unique customerID)
    searches in the hash map to find the customer record and changes the value of the field identified by
    fieldname to the newValue, if it is found. Upon success or failure it returns a message to the manager and
    the logs are updated with this information. If the new value of the fields such as branch, does not match the type
    it is expecting, it is invalid. For example, if the fieldName is branch and newValue is other than BC, MB, NB, QC,
    the server should return an error. The fields that should be allowed to change are address, phone and branch.
    */

    @Override
    public synchronized boolean editRecord(String customerID, String fieldName, String newValue) {

        //Check if the field
        if (fieldName.equalsIgnoreCase("branch")) {
            if (newValue.equalsIgnoreCase("QC") || newValue.equalsIgnoreCase("BC") ||
                    newValue.equalsIgnoreCase("MB") || newValue.equalsIgnoreCase("NB")) {

                //Lookup customer from the hash map
                CustomerClient customerClient = varifiedEntryLookForUser(customerID);
                if (customerClient != null) {
                    customerClient.setBranch(newValue);
                    //log stuff
                    try {
                        String msg = "Field name change for " + customerID;
                        generateLogFileServer(msg, "Server" , "BCServer");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            } else {
                System.out.println("Invalid branch name. Please select from QC, BC, NB or MB");
                return false;
            }
        }
        //check if the user wants to change phone
        else if (fieldName.equalsIgnoreCase("phone")) {
            CustomerClient customerClient = varifiedEntryLookForUser(customerID);
            if (customerClient != null) {
                customerClient.setPhone(newValue);
                System.out.println("Updated phone number! of Client " + customerClient.getAccountNumber() +
                        " to " + customerClient.getPhone());
                //log stuff
                try {
                    String msg = "Field name change for " + customerID;
                    generateLogFileServer(msg, "Server" , "BCServer");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

        }
        //check if the user wants to change address
        else if (fieldName.equalsIgnoreCase("address")) {
            CustomerClient customerClient = varifiedEntryLookForUser(customerID);
            if (customerClient != null) {
                customerClient.setAddress(newValue);
                //log stuff
                try {
                    String msg = "Field name change for " + customerID;
                    generateLogFileServer(msg, "Server" , "BCServer");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } else {
            System.out.println("Invalid field name. Please select branch, address or phone!");
            return false;
        }

        System.out.println("User with " + customerID + " doesn't exist in the DB");

        return false;
    }


    @Override

    //UDP Client asks for total account holder from all the server
    public synchronized HashMap<String, Integer> getAccountCount() {

        allBranchCount.put("BC", hashArraylistSize());
        Thread t1 = new Thread(this);
        Thread t2 = new Thread(this);
        Thread t3 = new Thread(this);

        //thread names
        t1.setName("QC");
        t2.setName("NB");
        t3.setName("MB");

        t1.start();
        t2.start();
        t3.start();


        try {
            t1.join();
            t2.join();
            t3.join(); }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("--------------------------");
        for (String key : allBranchCount.keySet()) {
            System.out.println(key + " " + allBranchCount.get(key));
        }
        System.out.println("--------------------------");
        //log stuff
        try {
            String msg = "Get account count";
            generateLogFileServer(msg, "Server" , "BCServer");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allBranchCount;
    }

    public int hashArraylistSize() {


        int total = 0;


        // Getting a Set of Key-value pairs
        Set entrySet = hashMap.entrySet();

        // Obtaining an iterator for the entry set
        Iterator it = entrySet.iterator();

        // Iterate through HashMap entries(Key-Value pairs)
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            //System.out.println("Key is: "+ me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = hashMap.get(me.getKey());
            total += temp.size();

        }

        return total;


    }

    @Override
    /* A customer with customerID invokes this method through a CustomerClient. This method increases the balance of
    the customer’s account by the specified amount amt and returns a message indicating the success/failure of the
    operation and the new account balance.*/
    public synchronized boolean deposit(String customerID, double amt) {


        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            total += amt;
            customerClient.setBalance(total);
            System.out.println("Money deposited. Customer " + customerID + " has $" + total + " in the account");

            //log stuff
            try {
                String msg = "Deposit made for " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
           ///log stuff
            try {
                String msg = "Deposit failed " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;
        }
    }


    @Override
    /*A customer with customerID invokes this method through a CustomerClient.
    This method decreases the balance of the customer’s account by specified amount amt if possible and returns a
    message indicating the success/failure of the operation the new account balance.*/
    public synchronized boolean withdraw(String customerID, double amt) {

        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            total -= amt;
            customerClient.setBalance(total);
            System.out.println("Money withdrawn. Customer " + customerID + " has $" + total + " in the account");
            //log stuff
            try {
                String msg = "Withdraw  " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            //log stuff
            try {
                String msg = "Customer doesnt exist " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
   /* A customer with customerID invokes this method through a CustomerClient. This
    method returns the current balance of the customer’s account.*/
    public synchronized double getBalance(String customerID) {

        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            System.out.println("Customer " + customerID + " has $" + total + " in the account");
            //log stuff
            try {
                String msg = "Get balance for " + customerID;
                generateLogFileServer(msg, "Server" , "BCServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return total;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            return 0;
        }


    }


    private String generateID(String branchName) {

        String ID = branchName + "C" + iD;
        System.out.println(ID);
        iD++;
        return ID;

    }

    //find user exists using this
    private CustomerClient varifiedEntryLookForUser(String customerID) {

        // Getting a Set of Key-value pairs
        Set entrySet = hashMap.entrySet();

        // Obtaining an iterator for the entry set
        Iterator it = entrySet.iterator();

        // Iterate through HashMap entries(Key-Value pairs)
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();

            System.out.println("Key is: " + me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = hashMap.get(me.getKey());
            for (CustomerClient cc : temp
                    ) {
                if (cc.getAccountNumber().equalsIgnoreCase(customerID)) {
                    System.out.println("Found user!");
                    return cc;
                }
            }

        }
        System.out.println("Customer does not exist in the DB");

        return null;
    }

    @Override
    public void run() {

        Thread t = Thread.currentThread();
        String name = t.getName();
        System.out.println("About to run thread = " + name);
         /*
        qc->5876
        BC->6876
        MB->7876
        NB->8876

    */


        switch (name) {
            case "MB":
                serverCount(7876, "MB");
                break;
            case "QC":
                serverCount(5876, "QC");
               // System.out.println("--------------sff---------------------");
                break;
            default:
                serverCount(8876, "NB");
               // System.out.println("--------------NB---------------------");

                break;
        }



    }
    private void serverCount(int potNum, String branchName) {


        //int branchPort[] = {6876};


        // 7876, 8876
        //Get other guys's count

        //Become a client to ask for count for every server using for loop

        //System.out.println("Hello");


        try {

            System.out.println("Inside the try!");

            //create socket
            DatagramSocket clientSocket = new DatagramSocket();
            //Get ip address
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "getCount was called!";
            //convert the request to bytes
            sendData = sentence.getBytes();
            //send the stuff

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, potNum);
            clientSocket.send(sendPacket);


            //Receive the stuff
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            //Store it in a variable
            String receivedValue = new String(receivePacket.getData());
            //Print out the data
            System.out.println("FROM SERVER:" + receivedValue.trim());
            //Parse the data

            receivedValue=receivedValue.trim();
            System.out.println(receivedValue);

            synchronized (lock) {
                allBranchCount.put(branchName, Integer.valueOf(receivedValue));
            }

            clientSocket.close();

            System.out.println("End of try");


        } catch (NumberFormatException | IOException ignored) {
        }


    }


}

