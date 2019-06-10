import com.chargebee.ListResult;
import com.chargebee.models.Invoice;
import com.chargebee.models.Subscription;
import com.chargebee.models.Invoice.*;

import java.io.FileOutputStream;
import java.util.*;
import java.util.function.Consumer;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.sql.Timestamp;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import javafx.util.*;
public class PostProcessingTasks {
    static Map<Double,Consumer<Double>> rankFunctionMap = new HashMap<>();
    static Map<String,String> subscriptionTaskMap= new HashMap<String,String>();
    public static void writeToFile() throws Exception{
       // List<String> subscriptionIds = new ArrayList(ProcessingTasks.subscriptionMap.values());
        for(String task : ProcessingTasks.tasks){
        	String[] arguments = task.split(";");
        	String name=arguments[0];
            Double rank = PreProcessingTasks.taskRankMap.get(name);
           subscriptionTaskMap.put(ProcessingTasks.subscriptionMap.get(rank),name);
        }
        final String FILE_NAME = "/Users/cb-ananthi/Documents/fin.csv";
        HSSFWorkbook workbook = new HSSFWorkbook();
        
        HSSFSheet sheet = workbook.createSheet("Subscription Details");
        sheet.setDefaultRowHeight((short)600);
        sheet.setDefaultColumnWidth((short)20);
        int rowNum = 0;
        int total_amount = 0;
        System.out.println(subscriptionTaskMap);
        System.out.println(subscriptionTaskMap);
        for (String id : subscriptionTaskMap.keySet()) {
        	if(id!=null) {
        	System.out.println("----"+id);
        	String task = subscriptionTaskMap.get(id);
            total_amount = 0;
            rowNum+=2;
            Row row = sheet.createRow(rowNum);
            
            
           // rowNum=rowNum+3;
            
            Cell cell = row.createCell(0);
            cell.setCellValue("USE CASE: ");
            cell = row.createCell(1);
            System.out.println(subscriptionTaskMap.get(id));
            cell.setCellValue(subscriptionTaskMap.get(id));
            rowNum++;
            row = sheet.createRow(rowNum);

            cell = row.createCell(0);
            cell.setCellValue("SUBSCRIPTION ID:");
            cell = row.createCell(1);
            cell.setCellValue(id);
            rowNum++;

            row = sheet.createRow(rowNum);

            cell = row.createCell(0);
            cell.setCellValue("LINE ITEM NAME");
            cell = row.createCell(1);
            cell.setCellValue("SUB PRICE");
            cell = row.createCell(2);
            cell.setCellValue("START DATE");
            cell = row.createCell(3);
            cell.setCellValue("END DATE");
            cell = row.createCell(4);
            cell.setCellValue("QTY");
            rowNum++;
            ListResult invoiceResult = Invoice.list().subscriptionId().is(id).request();
            for (ListResult.Entry entry : invoiceResult) {
                Invoice invoice = entry.invoice();
                invoice.subscriptionId();
                List<LineItem> lineItems = entry.invoice().lineItems();
                for (LineItem item : lineItems) {
                    List<Object> values = new ArrayList();
                    values.add(item.entityType()+"_"+item.entityId());
                    total_amount+=item.amount();
                    values.add(item.amount());
                    values.add(item.dateFrom());
                    values.add(item.dateTo());
                    values.add(item.quantity());
                    int colNum = 0;
                    row = sheet.createRow(rowNum++);
                    for (Object field : values) {
                        cell = row.createCell(colNum++);
                        if (field instanceof String) {
                            cell.setCellValue((String) field);

                        } else if (field instanceof Integer) {
                            cell.setCellValue((Integer) field);

                        } else if (field instanceof Timestamp) {
                            cell.setCellValue(((Timestamp) field).toGMTString());
                        }
                    }
                    List<Object> values1 = new ArrayList();
                    if(item.discountAmount()!=0) {
                        values1.add("Discount_"+item.entityType()+"_"+item.entityId());
                        total_amount-=item.discountAmount();
                        values1.add(item.discountAmount());
                        values1.add(item.dateFrom());
                        values1.add(item.dateTo());
                        values1.add(item.quantity());
                        colNum = 0;
                        row = sheet.createRow(rowNum++);
                        for (Object field : values1) {
                            cell = row.createCell(colNum++);
                            if (field instanceof String) {
                                cell.setCellValue((String) field);

                            } else if (field instanceof Integer) {
                                cell.setCellValue((Integer) field);

                            } else if (field instanceof Timestamp) {
                                cell.setCellValue(((Timestamp) field).toGMTString());
                            }
                        }
                    }
                }
            }
            row = sheet.createRow(rowNum++);
            cell = row.createCell(1);
            cell.setCellValue("Total - "+total_amount);
        }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void executeTasks() throws Exception{
        for(Map.Entry<Double, Consumer<Double>> entry : rankFunctionMap.entrySet()){
            entry.getValue().accept(entry.getKey());
        }
        writeToFile();
    }

    public static Consumer<Double> updateSubscription = (rank)-> {
        try {
            String planId;
            if (rank == 2.1) {
                planId = ProcessingTasks.createPlan.apply(PreProcessingTasks.planPriceMap.get(rank)*3/2);
            } else {//rank=2.2
                planId = ProcessingTasks.createPlan.apply(PreProcessingTasks.planPriceMap.get(rank)/2);
            }
            String subscriptionId = ProcessingTasks.subscriptionMap.get(rank);
            Subscription.update(subscriptionId)
                    .planId(planId)
                    .endOfTerm(false)
                    .request().subscription();
        }
        catch(Exception e){
            throw new RuntimeException(e.getCause());
        }
    };
    public static Consumer<Double> upgradequantity = (rank)-> {
        try {
        	String subscriptionId = ProcessingTasks.subscriptionMap.get(rank);
        	int quanity=PreProcessingTasks.upgradequantityMap.get(rank);
            Subscription.update(subscriptionId)
           
                    .planQuantity(25)
                    
                    .request().subscription();
        }
        catch(Exception e){
            throw new RuntimeException(e.getCause());
        }
    }; 

    public static Consumer<Double> downgradequantity = (rank)-> {
        try {
        	String subscriptionId = ProcessingTasks.subscriptionMap.get(rank);
        	int quanity=PreProcessingTasks.downgradequantityMap.get(rank);
            Subscription.update(subscriptionId)
                    .planQuantity(5)
                    .request().subscription();
        }
        catch(Exception e){
            throw new RuntimeException(e.getCause());
        }
    }; 

    public static Consumer<Double> createUnBilledChargesForSubscriptions = (rank) -> {
        try {
            String subscriptionId = ProcessingTasks.subscriptionMap.get(rank);
            Subscription.addChargeAtTermEnd(subscriptionId).amount(1000).description("Service Charge").request().subscription();
        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }

    };

    public static Consumer<Double> cancelSubscriptions = (rank) -> {
        try{
            String subscriptionId = ProcessingTasks.subscriptionMap.get(rank);
            Subscription.cancel(subscriptionId)
                    .endOfTerm(false)
                    .request().subscription();
        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }

    };

    public static Consumer<Double> addCharges = (rank) ->{
        try{
            String subscriptionId = ProcessingTasks.subscriptionMap.get(rank);
            Invoice.charge()
                    .subscriptionId(subscriptionId)
                    .amount(1000)
                    .description("Support Charge")
                    .request().invoice();
        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    };
}
