package ejercicio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.BOOL;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Clase para el manejo del POST "/mutants"
 * @author galonso
 *
 */
public class Handler implements RequestHandler<Map<String, ArrayList<String>>, String> {

	// Variables de la base de datos
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();	/**< Cliente de la base de datos */
	static DynamoDB dynamoDB = new DynamoDB(client);								/**< Objeto de la base de datos */

	// Definiciones de nombre de tabla y columnas
	static final String TableMutants = "Mutantes";									/**< Nombre de la tabla donde se guardan los datos */	
	static final String IdColumn = "ID";											/**< Nombre de la columna con la Primary Key */
	static final String IsMutantColumn = "IsMutant";								/**< Nombre de la columna que indica si es mutante o no */

	static final String JsonResponseStatus = "httpStatus";							/**< Nombre del campo que devolverá el código de error HTTP */
	static final String JsonResponseErrorMessage = "errorType";						/**< Nombre del campo que devolverá el tipo de error HTTP en string */
	
	static final int BadRequestCode = 400;											/**< Error de respuesta ante la falta del parámetro dna o que esté corrupto */
	static final String BadRequesStr = "BadRequest";								/**< String de respuesta ante la falta del parámetro dna o que esté corrupto */
	
	static final int HumanDetectedCode = 403;										/**< Error de respuesta al detectar un no mutante */
	static final String HumanDetectedString = "Human detected";						/**< String de respuesta al detectar un no mutante */
	
	private String ResponseErrorCode(int errorCode, String errorMessage) {

		// Genero un json de respuesta genérico
		JsonObject responseJson = new JsonObject();
		responseJson.addProperty(JsonResponseStatus, errorCode);
		responseJson.addProperty(JsonResponseErrorMessage, errorMessage);
		return responseJson.toString();
	}
	
	/** 
	 * Método invocado para el manejo del servicio
	 * @return	String con la respuesta procesada
	 */
	@Override
	public String handleRequest(Map<String, ArrayList<String>> event, Context context) {

		LambdaLogger logger = context.getLogger();
		String response = new String("200 OK");
		
		// log execution details
		logger.log("ENVIRONMENT VARIABLES: " + System.getenv().toString());
		logger.log("CONTEXT: " + context.toString());
		// process event
		// logger.log("EVENT: " + gson.toJson(event));
		logger.log("EVENT TYPE: " + event.getClass().toString());
		
		// Proceso los datos de entrada
		ArrayList<String> dnaReq = event.get("dna");
	
		if (dnaReq == null) {
			logger.log("No me llegó el dna \n");
			response = ResponseErrorCode(BadRequestCode, BadRequesStr);
			throw new RuntimeException(response);
		}
		else {
			logger.log("Dna recibido " + dnaReq.toString() + "\n");
			boolean isMutant = false;
			try {
				
				Mutant mutant = new Mutant();
				String[] dna = new String[dnaReq.size()];
				dna = dnaReq.toArray(dna);
				
				isMutant = mutant.isMutant(dna);
			} catch (Throwable e) {
				logger.log(e.getMessage() + "\n");
				response = ResponseErrorCode(BadRequestCode, e.getMessage());
				throw new RuntimeException(response);
			}

			Table table = dynamoDB.getTable(TableMutants);
			if (isMutant) {
				logger.log("Dna is mutant " + dnaReq.toString() + "\n");
				Item item = new Item().withPrimaryKey(IdColumn, dnaReq.toString()).withBoolean(IsMutantColumn,
						true);
				table.putItem(item);
			} else {
				logger.log("Dna is not mutant" + dnaReq.toString() + "\n");
				Item item = new Item().withPrimaryKey(IdColumn, dnaReq.toString()).withBoolean(IsMutantColumn,
						false);
				table.putItem(item);
				response = ResponseErrorCode(HumanDetectedCode, HumanDetectedString);
				throw new RuntimeException(response);
			}
		}

		return response;
	}
}
