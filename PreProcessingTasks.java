import javafx.util.Pair;

import java.util.*;
import java.util.function.Consumer;
public class PreProcessingTasks {
    static List<Double> customers_index = new ArrayList<>();
    static List<Double> subscriptions_index = new ArrayList<>();
    static List<Double> invoices_index = new ArrayList<>();
    static Map<Double,Integer> planPriceMap = new HashMap<>();
    static Map<Double,Integer> addonPriceMap  = new HashMap<>();
    static Map<Double,Integer> addonRecurringPriceMap  = new HashMap<>();
    static Map<Double,Integer> quantityMap= new HashMap<>();
    static Map<Double,Integer> discountMap=new HashMap<>();
    static Map<Double,Integer> setUpFeeMap=new HashMap<>();
    static Map<Double,Integer> upgradequantityMap=new HashMap<>();
    static Map<Double,Integer> downgradequantityMap=new HashMap<>();
    static List<Double> credit_notes_index = new ArrayList<>();
    static Map<String, Double> taskRankMap = new HashMap<>();
    static Map<Double,String >nameMap = new HashMap<>();
    static Map<Double, Consumer> rankFunctionMap = new HashMap<>();

    public static void executeTasks(List<String> tasks){
        initializeTaskRankMap();
        initializeNameMap();
        initializeRankFunctionMap();
       System.out.println("here");
        for(String task : tasks){
            String[] arguments = task.split(";");
           System.out.println(task);
            String name= arguments[0];//.toLowerCase();
            System.out.println(name);

            Double rank = taskRankMap.get(name);
            if(arguments[1].equals("nil") ){
            	planPriceMap.put(rank, 120000);
            }else {
            	planPriceMap.put(rank,Integer.parseInt(arguments[1]));
            }
            if(arguments[2].equals("nil") ){
            	addonRecurringPriceMap.put(rank, 120);
            }else {
            	addonRecurringPriceMap.put(rank,Integer.parseInt(arguments[2]));
            }
            if(arguments[3].equals("nil") ){
            	addonPriceMap.put(rank, 120);
            }else {
            	addonPriceMap.put(rank,Integer.parseInt(arguments[3]));
            }
            
            if(arguments[4].equals("nil") ){
            	discountMap.put(rank, 80);
            }else {
            	discountMap.put(rank,Integer.parseInt(arguments[4]));
            }
            if(arguments.length>5) {
	            if(arguments[5].equals("nil") ){
	            	setUpFeeMap.put(rank, 100);
	            }else {
	            	setUpFeeMap.put(rank,Integer.parseInt(arguments[5]));
	            }
            }
            rankFunctionMap.get(rank).accept(rank);

        }
    }

    public static void initializeTaskRankMap(){
        taskRankMap.put("CreateCustomer",1.0);
        taskRankMap.put("UpdateCustomer",1.1);
        taskRankMap.put("DeleteCustomer",1.2);


        taskRankMap.put("CreateSubscription",2.0);
        taskRankMap.put("UpgradeSubscription",2.1);
        taskRankMap.put("DowngradeSubscription",2.2);
        taskRankMap.put("DeleteSubscription",2.3);
        taskRankMap.put("pauseSubscription",2.4);
        taskRankMap.put("CancelSubscription",2.5);
        taskRankMap.put("ResumeSubscription",2.6);
        taskRankMap.put("SubscriptionWithSetupFee",2.7);
        taskRankMap.put("DiscountSpecificSubscription",2.8);
        taskRankMap.put("DiscountAllSubscription",2.9);
        taskRankMap.put("MultipleObligations",2.11);
        taskRankMap.put("NormalScenario", 2.12);
        taskRankMap.put("FreeTrailWithSetUpFee",2.13);
        taskRankMap.put("UpgradeQuantity",2.14);
        taskRankMap.put("DowngradeQuantity",2.15);

        taskRankMap.put("CreateInvoice",3.0);
        taskRankMap.put("UnbilledCharges",3.1);
        taskRankMap.put("AddCharges",3.2);
        taskRankMap.put("UpdateInvoice",3.3);
        taskRankMap.put("DeleteInvoice",3.4);
        taskRankMap.put("VoidInvoice",3.5);

        taskRankMap.put("CreateCreditNote",4.0);
        taskRankMap.put("VoidCreditNote",4.1);
        taskRankMap.put("DeleteCreditNote",4.2);
    }
    public static void initializeNameMap() {
    	nameMap.put(1.0,"CreateCustomer");
        nameMap.put(1.1,"UpdateCustomer");
        nameMap.put(1.2,"DeleteCustomer");


        nameMap.put(2.0,"CreateSubscription");
        nameMap.put(2.1,"UpgradeSubscription");
        nameMap.put(2.2,"DowngradeSubscription");
        nameMap.put(2.3,"DeleteSubscription");
        nameMap.put(2.4,"pauseSubscription");
        nameMap.put(2.5,"CancelSubscription");
        nameMap.put(2.6,"ResumeSubscription");
        nameMap.put(2.7,"SubscriptionWithSetupFee");
        nameMap.put(2.8,"DiscountSpecificSubscription");
        nameMap.put(2.9,"DiscountAllSubscription");
        nameMap.put(2.11,"MultipleObligations");
        nameMap.put(2.12,"NormalScenario");
        nameMap.put(2.13,"FreeTrailWithSetUpFee");
        nameMap.put(2.14,"UpgradeQuantity");
        nameMap.put(2.15,"DowngradeQuantity");

        nameMap.put(3.1,"CreateInvoice");
        nameMap.put(3.2,"UnbilledCharges");
        nameMap.put(3.3,"AddCharges");
        nameMap.put(3.4,"UpdateInvoice");
        nameMap.put(3.5,"DeleteInvoice");
        nameMap.put(3.6,"VoidInvoice");

        nameMap.put(4.0,"CreateCreditNote");
        nameMap.put(4.1,"VoidCreditNote");
        nameMap.put(4.2,"DeleteCreditNote");
    }
    public static void initializeRankFunctionMap() {
        rankFunctionMap.put(1.0,createCustomerIndex);
        rankFunctionMap.put(1.1,updateCustomerIndex);
        rankFunctionMap.put(1.2,deleteCustomerIndex);
        rankFunctionMap.put(2.0,createSubscriptionIndex);
        rankFunctionMap.put(2.1,upgradeSubscriptionIndex);
        rankFunctionMap.put(2.2,downgradeSubscriptionIndex);
        rankFunctionMap.put(2.3,deleteSubscriptionIndex);
        rankFunctionMap.put(2.4,pauseSubscriptionIndex);
        rankFunctionMap.put(2.5,cancelSubscriptionIndex);
        rankFunctionMap.put(2.6,resumeSubscriptionIndex);
        rankFunctionMap.put(2.7,subscriptionWithSetupFeeIndex);
        rankFunctionMap.put(2.8,discountSpecificIndex);
        rankFunctionMap.put(2.9,discountAllIndex);
        rankFunctionMap.put(2.11,multipleObligationsIndex);
        rankFunctionMap.put(2.12,normalIndex);
        rankFunctionMap.put(2.13,freetrailwithsetupfee);
        rankFunctionMap.put(2.14,upgradequantity);
        rankFunctionMap.put(2.15,downgradequantity);

        rankFunctionMap.put(3.0,createInvoiceIndex);
        rankFunctionMap.put(3.1,unbilledChargesInvoiceIndex);
        rankFunctionMap.put(3.2,addChargeInvoiceIndex);
        rankFunctionMap.put(3.3,updateInvoiceIndex);
        rankFunctionMap.put(3.4,deleteInvoiceIndex);
        rankFunctionMap.put(3.5,voidInvoiceIndex);
        rankFunctionMap.put(4.0,createCreditNoteIndex);
        rankFunctionMap.put(4.1,voidCreditNoteIndex);
        rankFunctionMap.put(4.2,deleteCreditNoteIndex);
    }


    public static Consumer<Double> createCustomerIndex = ( rank) ->{
        customers_index.add(rank);
    };
    
    public static Consumer<Double> updateCustomerIndex = ( rank) ->{
        createCustomerIndex.accept(rank);
    };

    public static Consumer<Double> deleteCustomerIndex = ( rank)-> {
        createCustomerIndex.accept(rank);
    };


    public  static Consumer<Double> createSubscriptionIndex = (rank) ->{
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };

    public  static Consumer<Double> upgradeSubscriptionIndex = (rank) ->{
        createSubscriptionIndex.accept(rank);
        PostProcessingTasks.rankFunctionMap.put(rank,PostProcessingTasks.updateSubscription);
    };

    public  static Consumer<Double> downgradeSubscriptionIndex = (rank) ->{
        createSubscriptionIndex.accept(rank);
        PostProcessingTasks.rankFunctionMap.put(rank,PostProcessingTasks.updateSubscription);
    };

    public  static Consumer<Double> pauseSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> deleteSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> cancelSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
        PostProcessingTasks.rankFunctionMap.put(rank,PostProcessingTasks.cancelSubscriptions);
    };

    public  static Consumer<Double> resumeSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> subscriptionWithSetupFeeIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> discountSpecificIndex = ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };

    public  static Consumer<Double> discountAllIndex = ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };

    public  static Consumer<Double> multipleObligationsIndex = ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };
     
    public  static Consumer<Double> normalIndex = ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };
    
    public  static Consumer<Double> freetrailwithsetupfee= ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };
    
    public  static Consumer<Double> upgradequantity= ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };
    
    public  static Consumer<Double> downgradequantity= ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };
    public  static Consumer<Double> createInvoiceIndex = ( rank) -> {
        invoices_index.add(rank);
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> unbilledChargesInvoiceIndex = (Double rank) -> {
        createSubscriptionIndex.accept(rank);
        PostProcessingTasks.rankFunctionMap.put(rank,PostProcessingTasks.createUnBilledChargesForSubscriptions);
    };

    public  static Consumer<Double> addChargeInvoiceIndex = (Double rank)-> {
        createSubscriptionIndex.accept(rank);
        PostProcessingTasks.rankFunctionMap.put(rank,PostProcessingTasks.addCharges);
    };

    public  static Consumer<Double> updateInvoiceIndex = ( rank) ->{
        createInvoiceIndex.accept(rank);
    };

    public  static Consumer<Double> deleteInvoiceIndex = (Double rank)->{
        createInvoiceIndex.accept(rank);
    };

    public  static Consumer<Double> voidInvoiceIndex = (Double rank) -> {
        createInvoiceIndex.accept(rank);
    };

    public  static Consumer<Double> createCreditNoteIndex = (Double rank) ->{
        credit_notes_index.add(rank);
    };

    public  static Consumer<Double> voidCreditNoteIndex = (Double rank) ->{
        createCreditNoteIndex.accept(rank);
    };

    public  static Consumer<Double> deleteCreditNoteIndex = (Double rank) -> {
        createCreditNoteIndex.accept(rank);
    };

}
