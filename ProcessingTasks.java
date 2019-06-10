import java.util.*;
import com.chargebee.Environment;
import com.chargebee.*;
import com.chargebee.models.*;
import com.chargebee.models.enums.*;
import com.chargebee.models.Addon.*;
import com.chargebee.models.Plan.TrialPeriodUnit;
import com.chargebee.models.Subscription.BillingPeriodUnit;

import javafx.util.Pair;

import java.util.function.*;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ProcessingTasks {
    static Map<Double, String> customerMap = new HashMap<Double, String>();
    static Map<Double, String> subscriptionMap = new HashMap<Double, String>();
    static Map<Double, String> invoiceMap = new HashMap<Double, String>();
    static List<String> tasks = new ArrayList<>();
    List<Pair<String,String>> subscritptionTaskList = new ArrayList<Pair<String,String>>();

    public  static  void InitializeTasks() throws  Exception{
        File file = new File("/Users/cb-ananthi/Documents/sub/file1.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null)
        {
            tasks.add(st);
        }
    }


    public static void executeTasks(){
        createCustomers.accept(0.0);
        createSubscriptions.accept(0);
        createInvoices.accept(0);
    }
    public static void main(String args[]) throws  Exception{
        Environment.configure("mannar-test", "test___dev__8cdKENYtV2evzPYlV3TvRwDLXPvXhKUBL");
        InitializeTasks();
        PreProcessingTasks.executeTasks(tasks);
        executeTasks();
        PostProcessingTasks.executeTasks();
    }
    public static Consumer<Double> createCustomers = (arguments) ->{
        try {
            for(Double rank : PreProcessingTasks.customers_index) {
                Result result = Customer.create()
                        .firstName("Test_User_"  + System.currentTimeMillis())
                        .email("Test_User_"  + System.currentTimeMillis()+"@test.com")
                        
                    	.cardNumber("4012888888881881")
                    	.cardExpiryMonth(10)
                    	.cardExpiryYear(2022)
                    	.cardCvv("999")
                        .request();
                
                Customer customer = result.customer();
                customerMap.put(rank, customer.id());
            }
        }
        catch(Exception e) {
        	System.out.println(e);
            throw new RuntimeException(e.getCause()) ;
        }
    };

    public static BiFunction<Integer,Integer,String> createPlanWithSetUpFeeAndFreeTrail = (price,setUpFee) -> {
        Plan plan;
        try{
            plan = Plan.create()
                        .id("Saas3")
                        .name("SaaS3")
                        .currencyCode("USD")
                        .price(price)
                        .setupCost(setUpFee)
                        .trialPeriodUnit(TrialPeriodUnit.DAY)
                        .periodUnit(com.chargebee.models.Plan.PeriodUnit.YEAR)
                    	.trialPeriod(30)
                        .request().plan();

        }
        catch (Exception e){
        	System.out.println(e);
        	return "SaaS3";
        }
        return  plan.id();
    };
    public static Function<Integer,String> createPlanWithTrail = (price) -> {
        Plan plan;
        try{
            plan = Plan.create()
                        .id("Test_plan_" + System.currentTimeMillis())
                        .name("Test_plan_" + System.currentTimeMillis())
                        .currencyCode("USD")
                        .price(price)
                        .trialPeriodUnit(TrialPeriodUnit.DAY)
                    	.trialPeriod(30)
                        .request().plan();

        }
        catch (Exception e2){
            throw new RuntimeException(e2.getCause());
        }
        return  plan.id();
    };
    public static Function<Integer,String> createPlan = (price) -> {
        Plan plan;
        try{
            plan = Plan.create()
                    .id("SaaS")
                    .name("SaaS")
                    .currencyCode("USD")
                    .price(price)
                    .periodUnit(com.chargebee.models.Plan.PeriodUnit.YEAR)
                    .request().plan();
        }
        catch (Exception e1){
        		return "SaaS";            
        }
        return  plan.id();
    };
	
    public static Function<Integer,String> createRecurringAddon = (price) -> {
    	Addon addon;
        try{
        	 addon = Addon.create()
            .id("SaaS2")
            .name("SaaS2")
            .invoiceName("Test Recurring addon pack")
            .chargeType(ChargeType.RECURRING)
            .period(1)
            .periodUnit(PeriodUnit.YEAR)
            .price(price)
            .currencyCode("USD")
            .pricingModel(PricingModel.PER_UNIT)
            .request().addon();
         	System.out.println(" recurring created");

        }
        catch (Exception e1){
        	System.out.println(e1);
        	return "SaaS2";
        }
        return  addon.id();
    };
    public static Function<Integer,String> createNonRecurringAddon = (price) -> {
    	Addon addon;
        try{

        	addon = Addon.create()
                    .id("Training")
                    .name("Traning")
                    .invoiceName("Test non Recurring addon pack")
                    .chargeType(ChargeType.NON_RECURRING)
                    .price(price)
                    .currencyCode("USD")
                    
                    .pricingModel(PricingModel.FLAT_FEE)
                    .request().addon();
        	System.out.println("non recurring created");
        }
        catch (Exception e1){
        	System.out.println(e1);
        	return "Training";
        }
        return  addon.id();
    };
    public static Function<Integer,String> createCoupon = (price) -> {
    	 Coupon coupon;
        try{
        	coupon = Coupon.create()
            .id("Discount")
            .name("Discount")
 
        	.discountType(Coupon.DiscountType.FIXED_AMOUNT)
        	.discountAmount(500)
        	.applyOn(Coupon.ApplyOn.INVOICE_AMOUNT)
        	.durationType(Coupon.DurationType.FOREVER)
        	.currencyCode("USD")
        	.request()          
        	.coupon();

        }
        catch (Exception e1){
            return "Discount";
        }
        return  coupon.id();
    };
    
    
    public static Consumer<Integer> createSubscriptions = (arguments) ->{
        try {
            for(Double rank : PreProcessingTasks.subscriptions_index) {
                String planID = null;
                Customer customer = Customer.retrieve(customerMap.get(rank)).request().customer();
                Coupon.PlanConstraint planConstraint;
                Coupon.AddonConstraint addonConstraint;
                if(rank==2.8 || rank==2.9) {
                    if (rank == 2.8) {
                        planConstraint = Coupon.PlanConstraint.ALL;
                        addonConstraint = Coupon.AddonConstraint.NONE;
                    } else {//rank=2.9
                        planConstraint = Coupon.PlanConstraint.ALL;
                        addonConstraint = Coupon.AddonConstraint.ALL;
                    }
                    Addon addon = Addon.create()
                            .id("Test_Addon_" + System.currentTimeMillis())
                            .name("Test_Addon_" + System.currentTimeMillis())
                            .invoiceName("Test Recurring addon pack")
                            .chargeType(ChargeType.RECURRING)
                            .period(1)
                            .periodUnit(PeriodUnit.MONTH)
                            .price(PreProcessingTasks.addonPriceMap.get(rank))
                            .currencyCode("USD")
                            .pricingModel(PricingModel.FLAT_FEE)
                            .request().addon();
                    String planId = createPlan.apply(PreProcessingTasks.planPriceMap.get(rank));
                    Coupon coupon = Coupon.create()
                            .id("Test_Coupon_" + System.currentTimeMillis())
                            .name("Test_Offer_" + System.currentTimeMillis())
                            .discountType(Coupon.DiscountType.FIXED_AMOUNT)
                            .applyOn(Coupon.ApplyOn.EACH_SPECIFIED_ITEM)
                            .planConstraint(planConstraint)
                            .addonConstraint(addonConstraint)
                            .status(Coupon.Status.ACTIVE)
                            .discountAmount(2000)
                            .currencyCode("USD")
                            .durationType(Coupon.DurationType.FOREVER)
                            .request().coupon();
                    Result result = Subscription.createForCustomer(customer.id())
                            .id("Subscription_"+System.currentTimeMillis())
                            .planId(planId)
                            .addonId(0,addon.id())
                            .couponIds(coupon.id())
                            .autoCollection(AutoCollection.OFF)
                            .request();
                    Subscription subscription = result.subscription();
                    subscriptionMap.put(rank,subscription.id());
                    continue;
                }
                else if(rank == 2.11){
                    Addon addon = Addon.create()
                            .id("Test_Addon_" + System.currentTimeMillis())
                            .name("Test_Addon_" + System.currentTimeMillis())
                            .invoiceName("Test Recurring addon pack")
                            .chargeType(ChargeType.RECURRING)
                            .period(1)
                            .periodUnit(PeriodUnit.MONTH)
                            .price(PreProcessingTasks.addonPriceMap.get(rank))
                            .currencyCode("USD")
                            .pricingModel(PricingModel.PER_UNIT)
                            .request().addon();
                    String planId = createPlan.apply(PreProcessingTasks.planPriceMap.get(rank));
                    Result result = Subscription.createForCustomer(customer.id())
                            .id(PreProcessingTasks.nameMap.get(rank))
                            .planId(planId)
                            .addonId(0,addon.id())
                            .autoCollection(AutoCollection.OFF)
                            .request();
                    Subscription subscription = result.subscription();
                    subscriptionMap.put(rank,subscription.id());
                    continue;
                }
                else if(rank==2.12) {
                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                	Date date = sdf.parse("2018-10-01T00:00:00");
                	System.out.println("Test "+ date);
                    String planId=createPlan.apply(PreProcessingTasks.planPriceMap.get(rank));
                    
                    String addon1=createRecurringAddon.apply(PreProcessingTasks.addonPriceMap.get(rank));
                 String addon2=createNonRecurringAddon.apply(PreProcessingTasks.addonRecurringPriceMap.get(rank));
                 

                	String discount=createCoupon.apply(PreProcessingTasks.discountMap.get(rank));
                	Result result = Subscription.createForCustomer(customer.id())
                            .id(PreProcessingTasks.nameMap.get(rank))
                            .planId(planId)
                            .addonId(0,addon1)
                            .addonQuantity(0,10)
                            .addonId(1, addon2)
                            .startDate(new Timestamp(date.getTime()))
                            .coupon(discount)
                            .invoiceImmediately(true)
                          //  .billingAlignmentMode(BillingAlignmentMode.DELAYED)

                            .autoCollection(AutoCollection.OFF)
                            .request();
                

                
                	
                    Subscription subscription = result.subscription();
                    subscriptionMap.put(rank,subscription.id());
                	

                	continue;
                }
                else if(rank==2.13) {
                	SimpleDateFormat sdf = new SimpleDateFormat("ddMMYYYY");
                	Date date = sdf.parse("01012019");

                	 String planId=createPlanWithSetUpFeeAndFreeTrail.apply(PreProcessingTasks.planPriceMap.get(rank),PreProcessingTasks.setUpFeeMap.get(rank));  

                	 String addon1=createRecurringAddon.apply(PreProcessingTasks.addonPriceMap.get(rank));
                 	String discount=createCoupon.apply(PreProcessingTasks.discountMap.get(rank));

                     Result result = Subscription.createForCustomer(customer.id())
                             .id(PreProcessingTasks.nameMap.get(rank))
                             .planId(planId)
                            .startDate(new Timestamp(date.getTime()))
                             .addonId(0,addon1)
                             .addonQuantity(0, 10)
                             .coupon(discount)
                             .invoiceImmediately(true)
                             .request();

                     Subscription subscription = result.subscription();
                     subscriptionMap.put(rank,subscription.id());
                 	continue;
                }
                else if(rank==2.14 ||rank==2.15) {
                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                	Date date = sdf.parse("2018-10-01T00:00:00");
                	
                	 String planId=createPlan.apply(PreProcessingTasks.planPriceMap.get(rank));  
                     String addon1=createRecurringAddon.apply(PreProcessingTasks.addonPriceMap.get(rank));
                     String addon2=createNonRecurringAddon.apply(PreProcessingTasks.addonRecurringPriceMap.get(rank));

                     String discount=createCoupon.apply(PreProcessingTasks.discountMap.get(rank));
                    Result result = Subscription.createForCustomer(customer.id())
                            .id(PreProcessingTasks.nameMap.get(rank))
                            .planId(planId)
                            .addonId(0,addon1)
                            .addonId(1, addon2)
                            .startDate(new Timestamp(date.getTime()))
                            .addonQuantity(0, 10)
                            
                            .coupon(discount)
                            .autoCollection(AutoCollection.OFF)
                            .request();
                    
                 
                    
                    
                    Subscription subscription = result.subscription();
                    subscriptionMap.put(rank,subscription.id());
                  
                	continue;
                }
                else if(rank==2.5) {
                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                	Date date = sdf.parse("2018-10-01T00:00:00");
                	
                	 String planId=createPlan.apply(PreProcessingTasks.planPriceMap.get(rank));  
                     String addon1=createRecurringAddon.apply(PreProcessingTasks.addonPriceMap.get(rank));
                     String addon2=createNonRecurringAddon.apply(PreProcessingTasks.addonRecurringPriceMap.get(rank));

                     String discount=createCoupon.apply(PreProcessingTasks.discountMap.get(rank));
                    Result result = Subscription.createForCustomer(customer.id())
                            .id(PreProcessingTasks.nameMap.get(rank))
                            .planId(planId)
                            .addonId(0,addon1)
                            .addonId(1, addon2)
                            .addonQuantity(0, 10)
                            .startDate(new Timestamp(date.getTime()))
                            .coupon(discount)
                            .autoCollection(AutoCollection.OFF)
                            .request();
                    
                    Subscription subscription = result.subscription();
                    subscriptionMap.put(rank,subscription.id());
                  
                	continue;
                }
                else if(rank == 2.7){
                	planID = createPlan.apply(PreProcessingTasks.planPriceMap.get(rank));
                }
                
                else {
                    planID = createPlan.apply(PreProcessingTasks.planPriceMap.get(rank));
                }
                Result result = Subscription.createForCustomer(customer.id())
                        .id(PreProcessingTasks.nameMap.get(rank))
                        .planId(planID)
                     //   .invoiceImmediately(false)
                        .request();
                Subscription subscription = result.subscription();
                subscriptionMap.put(rank,subscription.id());
            }
        }
        catch(Exception e) {
        	System.out.println(e);
            throw new RuntimeException(e.getMessage()) ;
        }
    };
    public static Consumer createInvoices = (arguments) ->{
        try {
            for (Double rank : PreProcessingTasks.invoices_index) {
                Subscription subscription = Subscription.retrieve(subscriptionMap.get(rank)).request().subscription();
                Result result = Invoice.charge()
                        .subscriptionId(subscription.id())
                        .amount(2000)
                        .description("Maintainance")
                        
                        .request();
                Invoice invoice = result.invoice();
                invoiceMap.put(rank,invoice.id());
            }
        }
            catch(Exception e) {
                throw new RuntimeException(e.getCause()) ;
            }
    };
}
