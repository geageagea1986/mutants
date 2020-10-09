package ejercicio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@TestMethodOrder(OrderAnnotation.class)
public class LambdaFunctionHandlerTest {

    private static Map<String, ArrayList<String>> input;
    private static Handler handler = new Handler();
    private static StatsHandler stats = new StatsHandler();
    
    private static Integer mutantsDetected;
    private static Integer notMutantsDetected;
    private static Double ratio;

    @BeforeAll
    public static void setUp() {
        handler.modifyEndpointRegion("http://localhost:8000", "us-west-2");
        createMutantTable();
        
        mutantsDetected = 0;
        notMutantsDetected = 0;
        ratio = 0.0;
    }
    
    /**
     * Crea el objeto para mandarle al método que sirve el POST /mutants
     * @param dna
     */
    public void createInput(ArrayList<String> dna) {
        input = new HashMap<String, ArrayList<String>>();
        input.put("dna", dna);
    }

    /**
     * Crea un contexto nuevo para pasar a los métodos que sirven a los servicios POST/mutant y GET/stats
     * @param testName		Nombre del método de test que invoca
     * @return				Objeto creado con el nombre del método
     */
    private Context createContext(String testName) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(testName);
        return ctx;
    }
    
    /**
     * Crea la tabla de mutantes en la base de datos si es que no existe
     */
    private static void createMutantTable() {
    	TableCollection<ListTablesResult> tables = Handler.dynamoDB.listTables();
    	
    	// Verifico que no esté creada
    	for(Table table : tables) {
    		if(table.getTableName().equals(Handler.TableMutants))
    			return;
    	}
    	
		Table testTable = Handler.dynamoDB.createTable(
    			Handler.TableMutants, 
    			Arrays.asList(new KeySchemaElement("ID", KeyType.HASH)), 
    			Arrays.asList(new AttributeDefinition("ID", ScalarAttributeType.S)),
    			new ProvisionedThroughput(10L, 10L));
		try {
			testTable.waitForActive();
		} catch(Exception e) {
			System.err.println("Error creating table " + e.getMessage());
		}
    }

    @Test
    @Order(1)    
    public void testMutant() throws IOException {
        Context ctx = createContext("Mutant DNA");
        
        createInput(
        		new ArrayList<String>(){
        			{
        				add("ATGCGA");
        				add("CAGTGC");
        				add("TTATGT");
        				add("AGAAGG");
        				add("CCCCTA");
        				add("TCACTG");
        			}
        		});
        
        String output = handler.handleRequest(input, ctx);
        Assert.assertEquals(Handler.ResponseOk, output);
        mutantsDetected++;
    }
    
    @Test
    @Order(2)    
    public void testNotMutant() {
        Context ctx = createContext("Not Mutant DNA");
        
        createInput(
        		new ArrayList<String>(){
        			{
        				add("ACGT");
        				add("GTCA");
        				add("ACGT");
        				add("GTAC");
        			}
        		});
        
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {handler.handleRequest(input, ctx);});
        assertEquals(handler.ResponseErrorCode(Handler.HumanDetectedCode, Handler.HumanDetectedString), exception.getMessage()); 
        notMutantsDetected++;
    }
    
    @Test
    @Order(3)    
    public void testMutantBigDna() {
        Context ctx = createContext("Mutant Big DNA");
        
        createInput(
        		new ArrayList<String>(){
        			{
        				add("ACGTACGT");
        				add("GTCATCAT");
        				add("ACGTTCCT");
        				add("GGGGAAAT");
        				add("ACGTACGT");
        				add("GTCATCAT");
        				add("ACGTTCCT");
        				add("GGGGAAAT");
        			}
        		});

        String output = handler.handleRequest(input, ctx);
        Assert.assertEquals(Handler.ResponseOk, output);
        mutantsDetected++;
    }
    
    @Test
    @Order(4)    
    public void testInputNull() {
        Context ctx = createContext("Mutant Input Null");
        
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {handler.handleRequest(null, ctx);});
        assertEquals(handler.ResponseErrorCode(Handler.BadRequestCode, Handler.BadRequesStr), exception.getMessage()); 
    }
    
    @Test
    @Order(5)    
    public void testDnaNull() {
        Context ctx = createContext("Input Dna Null");
        
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {handler.handleRequest(new HashMap<String, ArrayList<String>>(), ctx);});
        assertEquals(handler.ResponseErrorCode(Handler.BadRequestCode, Handler.BadRequesStr), exception.getMessage()); 
    }
    
    @Test
    @Order(6)    
    public void testBadDnaChain() {

        Context ctx = createContext("Input Bad Dna");
        
        createInput(
        		new ArrayList<String>(){
        			{
        				add("ACGTACGT");
        				add("GTCATCAT");
        				add("ACGTTCCT");
        				add("GGGGAAT");
        				add("ACGTACGT");
        				add("GTCATCAT");
        				add("ACGTTCCT");
        				add("GGGGAAAT");
        			}
        		});
        
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {handler.handleRequest(input, ctx);});
        assertEquals(handler.ResponseErrorCode(Handler.BadRequestCode, Mutant.BadDnaChainException), exception.getMessage()); 
    }
    
    @Test
    @Order(7)    
    public void testStats() {

        Context ctx = createContext("Stats");
        String statsResponse = stats.handleRequest(null, ctx);
        
        if(mutantsDetected > 0)
        	ratio = (double) mutantsDetected / (mutantsDetected + notMutantsDetected);
        assertEquals(stats.BuildJsonResponse(mutantsDetected, notMutantsDetected, ratio), statsResponse);
    }
    
    @AfterAll
    public static void tearDown() throws InterruptedException {
    	Table mutantsTable = Handler.dynamoDB.getTable(Handler.TableMutants);
    	mutantsTable.delete();
    	mutantsTable.waitForDelete();
    }
}
