package ejercicio;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.JsonObject;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Clase para el manejo del GET "/stats"
 * @author galonso
 *
 */
public class StatsHandler implements RequestHandler<Map<String, String>, String> {
	
	static final String mutantsCountJson	= "count_mutant_dna";
	static final String humansCountJson		= "count_human_dna";
	static final String ratioJson			= "ratio";
	
	/**
	 * Obtengo el listado completo de la tabla, en el formato necesario para hacer los cálculos
	 * @return	Objeto con el resultado de los datos de la tabla
	 */
	ScanResult GetAll() {

		ScanRequest scanRequest = new ScanRequest()
				.withTableName(Handler.TableMutants)
				.withProjectionExpression(Handler.IdColumn + ", " + Handler.IsMutantColumn);

		ScanResult result = Handler.client.scan(scanRequest);
		return result;
	}

	/**
	 * Arma un string con el JSON de respuesta esperado por el servicio
	 * @param mutantsCount		Cantidad de mutantes detectados
	 * @param notMutantsCount	Cantidad de no mutantes detectados
	 * @param ratio				Relación mutantes / humanos detectados
	 * @return					String JSON con los valores pasados
	 */
	public String BuildJsonResponse(Integer mutantsCount, Integer notMutantsCount, Double ratio) {

		JsonObject statsResponseJson = new JsonObject();
		statsResponseJson.addProperty(mutantsCountJson, mutantsCount);
		statsResponseJson.addProperty(humansCountJson, mutantsCount + notMutantsCount);
		statsResponseJson.addProperty(ratioJson, (double)Math.round(ratio * 100) / 100);
		
		return statsResponseJson.toString();
	}
	
	@Override
	public String handleRequest(Map<String, String> event, Context context) {

		LambdaLogger logger = context.getLogger();
		logger.log("CONTEXT: " + context.getFunctionName() + "\n");
		
		// Contadores de los registros
		Integer mutantsCount = 0;
		Integer notMutantsCount = 0;
		
		ScanResult results = GetAll();
		// Analizo los resultados devueltos
		for(Map<String, AttributeValue> item : results.getItems()) {
			if(item.get(Handler.IsMutantColumn).getBOOL() == Boolean.TRUE)
				mutantsCount++;
			else
				notMutantsCount++;
		}

		logger.log("\nMutants detected: " + mutantsCount + "\n");
		logger.log("\nNot Mutants detected: " + notMutantsCount + "\n");
		
		Double ratio = 0.0;
		// Si hay mutantes, calculo el ratio
		if(mutantsCount > 0)
			ratio =  (double) mutantsCount / (notMutantsCount + mutantsCount);
		
		// Devuelvo los resultados en el formato requerido		
		return BuildJsonResponse(mutantsCount, notMutantsCount, ratio);
	}
}
