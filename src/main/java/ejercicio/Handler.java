package ejercicio;

import java.util.ArrayList;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
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
	
	static final String ResponseOk = "200 OK";										/**< String de respuesta en caso de que se detecte un mutante */
	
	/**
	 * Método para generar la respuesta en caso de algún error o detección de humano
	 * @param errorCode			Código http devuelto
	 * @param errorMessage		Mensaje a enviar
	 * @return					String del json armado { httpStatus: $errorCode, errorType: $errorMessage }
	 */
	public String ResponseErrorCode(int errorCode, String errorMessage) {

		// Genero un json de respuesta genérico
		JsonObject responseJson = new JsonObject();
		responseJson.addProperty(JsonResponseStatus, errorCode);
		responseJson.addProperty(JsonResponseErrorMessage, errorMessage);
		return responseJson.toString();
	}
	
	public void modifyEndpointRegion(String endpoint, String region) {

    	client = AmazonDynamoDBClientBuilder.standard()
    	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
    	            .build();

        dynamoDB = new DynamoDB(client);
	}
	
	/** 
	 * Método invocado para el manejo del servicio
	 * @return	String con la respuesta procesada
	 */
	@Override
	public String handleRequest(Map<String, ArrayList<String>> event, Context context) {

		LambdaLogger logger = context.getLogger();
		logger.log("CONTEXT: " + context.getFunctionName() + "\n");
		String response = ResponseOk;
		
		if(event == null) {
			logger.log("No me llegó data\n");
			response = ResponseErrorCode(BadRequestCode, BadRequesStr);
			throw new RuntimeException(response);
		}
		
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

				// Obtengo la tabla de mutantes
				Table table = dynamoDB.getTable(TableMutants);
				if (isMutant) {
					// Agrego un nuevo item de mutante
					logger.log("Dna is mutant " + dnaReq.toString() + "\n");
					Item item = new Item().withPrimaryKey(IdColumn, dnaReq.toString()).withBoolean(IsMutantColumn,
							true);
					table.putItem(item);
				} else {
					// Agrego un nuevo item de no mutante
					logger.log("Dna is not mutant" + dnaReq.toString() + "\n");
					Item item = new Item().withPrimaryKey(IdColumn, dnaReq.toString()).withBoolean(IsMutantColumn,
							false);
					table.putItem(item);
					
					// Lanzo la excepción para que el API Gateway reconozca el error
					response = ResponseErrorCode(HumanDetectedCode, HumanDetectedString);
					throw new RuntimeException(response);
				}
			}
			// Excepción si es un humano
			catch (RuntimeException re) {
				// Lanzo nuevamente la excepción así el API Gateway lo devuelve como error
				throw re;
			}
			// Excepción si hubo un error en el parseo de la cadena
			catch (Throwable e) {
				logger.log(e.getMessage() + "\n");
				response = ResponseErrorCode(BadRequestCode, e.getMessage());

				// Lanzo nuevamente la excepción así el API Gateway lo devuelve como error
				throw new RuntimeException(response);
			} 
		}

		return response;
	}
}
