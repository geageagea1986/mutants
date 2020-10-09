package ejercicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Mutant {
	
	final int LENGTH_CHAR_MATCH 	= 4;	/**< Tamaño de la secuencia para saber si es mutante */
	final int MAX_MATCHES 			= 2;	/**< Cantidad de coincidencias para la detección de un mutante */
	
	static public String BadDnaChainException = "Bad dna chain";		/**< String de respuesta en caso de error de parseo de la cadena dna */
	
	/**
	 * Método para la búsqueda de coincidencias anteriores en horizontal
	 * @param indexChar				Índice de la columna a verificar
	 * @param indexDna				Índice de la fila a verificar
	 * @param horizontalMatches		Lista de las coincidencias anteriores
	 * @return						Verdadero en caso de pertenecer a una coincidencia previa
	 */
	boolean alredyHorizontalMatch(int indexChar, int indexDna, HashMap<Integer, ArrayList<Integer>> horizontalMatches)
	{
		ArrayList<Integer> rowList = horizontalMatches.get(indexDna);
		if(rowList != null) {
			for(Integer column: rowList)
			{
				if(indexChar < column + LENGTH_CHAR_MATCH)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Método recursivo para buscar coincidencias horizontales
	 * @param dna					Matriz originial
	 * @param indexDna				Índice de la fila origen
	 * @param indexChar				Índice de la columna origen
	 * @param length				Largo detectado
	 * @param horizontalMatches		Lista de todas las detecciones hasta el momento de este estilo
	 * @return						Verdadero en caso de ser una cadena correcta y no detectada previamente
	 */
	boolean findHorizontal(String[] dna, int indexDna, int indexChar, int length, HashMap<Integer, ArrayList<Integer>> horizontalMatches)
	{
		// Verifico no haber llegado al final
		if(indexDna < dna.length && indexChar + length < dna[indexDna].length())
		{
			if(dna[indexDna].charAt(indexChar) == dna[indexDna].charAt(indexChar + length))
			{
				if(!alredyHorizontalMatch(indexChar + (length - 1), indexDna, horizontalMatches))
				{
					length++;
					if(length == LENGTH_CHAR_MATCH)
					{
						return true;
					}
					else 
						return findHorizontal(dna, indexDna, indexChar, length, horizontalMatches);
				}
			}
		}
		return false;
	}

	/**
	 * Método para la búsqueda de coincidencias anteriores en vertical
	 * @param indexChar				Índice de la columna a verificar
	 * @param indexDna				Índice de la fila a verificar
	 * @param verticalMatches		Lista de las coincidencias anteriores
	 * @return						Verdadero en caso de pertenecer a una coincidencia previa
	 */
	boolean alredyVerticalMatch(int indexChar, int indexDna, HashMap<Integer, ArrayList<Integer>> verticalMatches)
	{
		ArrayList<Integer> columnList = verticalMatches.get(indexChar);
		if(columnList != null)
		{
			for(Integer row: columnList) {
				if(indexDna < row + LENGTH_CHAR_MATCH) {
					return true;
				}
			}
		}
		return false;		
	}
	
	/**
	 * Método recursivo para buscar coincidencias verticales
	 * @param dna					Matriz originial
	 * @param indexDna				Índice de la fila origen
	 * @param indexChar				Índice de la columna origen
	 * @param length				Largo detectado
	 * @param verticalMatches		Lista de todas las detecciones hasta el momento de este estilo
	 * @return						Verdadero en caso de ser una cadena correcta y no detectada previamente
	 */
	boolean findVertical(String[] dna, int indexDna, int indexChar, int length, HashMap<Integer, ArrayList<Integer>> verticalMatches)
	{
		// Verifico no haber llegado al final
		if(indexDna + length < dna.length && indexChar < dna[indexDna].length())
		{
			if(dna[indexDna].charAt(indexChar) == dna[indexDna + length].charAt(indexChar))
			{
				if(!alredyVerticalMatch(indexChar, indexDna + (length - 1), verticalMatches))
				{
					length++;
					if(length == LENGTH_CHAR_MATCH)
					{
						return true;
					}
					else 
						return findVertical(dna, indexDna, indexChar, length, verticalMatches);
				}
			}
		}
		return false;
	}

	/**
	 * Método para la búsqueda de coincidencias anteriores en diagonal descendente
	 * @param indexChar				Índice de la columna a verificar
	 * @param indexDna				Índice de la fila a verificar
	 * @param diagonalDownMatches	Lista de las coincidencias anteriores
	 * @return						Verdadero en caso de pertenecer a una coincidencia previa
	 */
	boolean alredyDiagonalDownMatch(int indexChar, int indexDna, HashMap<Integer, ArrayList<Integer>> diagonalDownMatches)
	{
		ArrayList<Integer> diagonalList = diagonalDownMatches.get(indexDna - indexChar);
		if(diagonalList != null) {
			for(Integer diagonal: diagonalList) {
				if(indexDna < diagonal + LENGTH_CHAR_MATCH)
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Método recursivo para buscar coincidencias en diagonal descendente
	 * @param dna					Matriz originial
	 * @param indexDna				Índice de la fila origen
	 * @param indexChar				Índice de la columna origen
	 * @param length				Largo detectado
	 * @param diagonalDownMatches	Lista de todas las detecciones hasta el momento de este estilo
	 * @return						Verdadero en caso de ser una diagonal correcta y no detectada previamente
	 */
	boolean findDiagonalDown(String[] dna, int indexDna, int indexChar, int length, HashMap<Integer, ArrayList<Integer>> diagonalDownMatches)
	{
		if(indexDna + length < dna.length && 
				indexChar + length < dna[indexDna].length()) {

			if(dna[indexDna].charAt(indexChar) == dna[indexDna + length].charAt(indexChar + length))
			{
				if(!alredyDiagonalDownMatch(indexChar + (length - 1), indexDna + (length - 1), diagonalDownMatches))
				{
					length++;
					if(length == LENGTH_CHAR_MATCH)
					{
						return true;
					}
					else 
						return findDiagonalDown(dna, indexDna, indexChar, length, diagonalDownMatches);
				}
				
			}
			
		}
		return false;
	}

	/**
	 * Método para la búsqueda de coincidencias anteriores en diagonal ascendente
	 * @param indexChar				Índice de la columna a verificar
	 * @param indexDna				Índice de la fila a verificar
	 * @param diagonalUpMatches		Lista de las coincidencias anteriores
	 * @return						Verdadero en caso de pertenecer a una coincidencia previa
	 */
	boolean alredyDiagonalUpMatch(int indexChar, int indexDna, HashMap<Integer, ArrayList<Integer>> diagonalUpMatches)
	{
		ArrayList<Integer> diagonalList = diagonalUpMatches.get(indexDna + indexChar);
		if(diagonalList != null) {
			for(Integer diagonal: diagonalList) {
				if(indexDna - diagonal < LENGTH_CHAR_MATCH)
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Método recursivo para buscar coincidencias en diagonal ascendente
	 * @param dna					Matriz originial
	 * @param indexDna				Índice de la fila origen
	 * @param indexChar				Índice de la columna origen
	 * @param length				Largo detectado
	 * @param diagonalUpMatches		Lista de todas las detecciones hasta el momento de este estilo
	 * @return						Verdadero en caso de ser una diagonal correcta y no detectada previamente
	 */
	boolean findDiagonalUp(String[] dna, int indexDna, int indexChar, int length, HashMap<Integer, ArrayList<Integer>> diagonalUpMatches)
	{
		if(indexDna - length >= 0 && 
				indexChar + length < dna[indexDna].length()) {

			if(dna[indexDna].charAt(indexChar) == dna[indexDna - length].charAt(indexChar + length))
			{
				if(length > 1 || !alredyDiagonalUpMatch(indexChar + (length - 1), indexDna - (length - 1), diagonalUpMatches))
				{
					length++;
					if(length == LENGTH_CHAR_MATCH)
					{
						return true;
					}
					else 
						return findDiagonalUp(dna, indexDna, indexChar, length, diagonalUpMatches);
				}
				
			}
			
		}
		return false;
	}
	
	/**
	 * Verifica si la matriz dna es una cadena de dna mutante o no
	 * @param dna			matriz de NxN que contiene la cadena a controlar
	 * @return				Verdadero en caso que la cadena a controlar sea de un mutante, falso si es la de un humano
	 * @throws Throwable	En caso de que la cadena a controlar no sea una cadena dna correcta
	 */
	boolean isMutant(String[] dna) throws Throwable { // Ejemplo Java
		
		// Verifico que no sea null y que el tamaño verifique la condición del ejercicio
		if(dna == null || dna.length < LENGTH_CHAR_MATCH)
			throw new Throwable(BadDnaChainException);
		
		final int mMatrix = dna.length;
		
		// Verifico primero la integridad de todos los datos
		for(String cadena : dna)
		{			
			// Verifico que la dimensión sea la correcta
			if(cadena.length() != mMatrix)
				throw new Throwable(BadDnaChainException);
			
			// Verifico que la cadena contenga sólo ATCG
			if(!Pattern.matches("^[ATCG]*$", cadena))
			{
				throw new Throwable(BadDnaChainException);
			}
		}
		
		System.out.println("Verificando cadena:");			
		for(String cadena: dna)
		{
			System.out.println(cadena);			
		}

		int matches = 0;
		
		HashMap<Integer, ArrayList<Integer>> horizontalMatches 		= new HashMap<>();
		HashMap<Integer, ArrayList<Integer>> verticalMatches 		= new HashMap<>();
		HashMap<Integer, ArrayList<Integer>> diagonalUpMatches 		= new HashMap<>();
		HashMap<Integer, ArrayList<Integer>> diagonalDownMatches 	= new HashMap<>();
		
		// Busco cadenas de ADN que sean mutantes
		for(int indexDna = 0; indexDna < mMatrix; indexDna++)
		{
			for(int indexChar = 0; indexChar < dna[0].length(); indexChar++)
			{
				if(findHorizontal(dna, indexDna, indexChar, 1, horizontalMatches))
				{
					if(horizontalMatches.get(indexDna) == null)
						horizontalMatches.put(indexDna, new ArrayList<Integer>());
					horizontalMatches.get(indexDna).add(indexChar);
					matches++;
				}
				if(findVertical(dna, indexDna, indexChar, 1, verticalMatches))
				{
					if(verticalMatches.get(indexChar) == null)
						verticalMatches.put(indexChar, new ArrayList<Integer>());
					verticalMatches.get(indexChar).add(indexDna);
					matches++;
				}
				if(findDiagonalDown(dna, indexDna, indexChar, 1, diagonalDownMatches))
				{
					if(diagonalDownMatches.get(indexDna - indexChar) == null)
						diagonalDownMatches.put(indexDna - indexChar, new ArrayList<Integer>());
					diagonalDownMatches.get(indexDna - indexChar).add(indexDna);
					matches++;
				}
				if(findDiagonalUp(dna, indexDna, indexChar, 1, diagonalUpMatches))
				{
					if(diagonalUpMatches.get(indexDna + indexChar) == null)
						diagonalUpMatches.put(indexDna + indexChar, new ArrayList<Integer>());
					diagonalUpMatches.get(indexDna + indexChar).add(indexDna);
					matches++;
				}
				
				// Si encuentro al menos 2 coincidencias, salgo
				if(matches >= MAX_MATCHES)
				{
					return true;
				}
			}
		}
		
		// En caso de no encontrar al menos 2 coincidencias
		return false;
	}

}
