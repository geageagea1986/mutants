package ejercicio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MutantTest {

	@Test
	void test() {

		Mutant mutant = new Mutant();
		String[] dnaDoubleVertical = {
				"ATGCAACA",
				"CAGAGCCA",
				"TCTTGAGA",
				"CAAACGCA",
				"AACCTACA",
				"TCACGGTA",
				"AACATACA",
				"TCCCTGCA"
				};
		String[] dnaHorizontalVertical = {
				"AAGAGAAA",
				"CAGAAGCT",
				"CACAGCCA",
				"TTTTTTTT",
				"AACCTACG",
				"TCACGGTA",
				"AACATACA",
				"TCCCTGCA"
				};
		String[] dnaDoubleUpDiagonal = {
				"ATGCAACT",
				"CACAGCTA",
				"TGTTGTGA",
				"CAAATGCT",
				"AACTTACA",
				"TCTCGGTA",
				"TTCATACC",
				"TCCCTGCA"
				};
		String[] dnaDoubleDownDiagonal = {
				"TCCCTGCA",
				"ATCATACC",
				"TCTCGGTA",
				"AACTTACA",
				"CAAATGCT",
				"TCTTGTGA",
				"CACAGCTA",
				"ATGAAACT"
				};
		
		try {
			assertEquals(true, mutant.isMutant(dnaHorizontalVertical));
			assertEquals(true, mutant.isMutant(dnaDoubleVertical));
			assertEquals(true, mutant.isMutant(dnaDoubleUpDiagonal));
			assertEquals(true, mutant.isMutant(dnaDoubleDownDiagonal));
			assertEquals(false, mutant.isMutant(null));
		} catch (Throwable e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		try {
			assertEquals(false, mutant.isMutant(new String[] {"", "", ""}));
		} catch (Throwable e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		try {
			assertEquals(true, mutant.isMutant(new String[] {"AAAA", "AAAA", "AAAA", "AAAA"}));
			assertEquals(false, mutant.isMutant(new String[] {"AcAA", "AcAA", "AcAA", "AcAA"}));
		} catch (Throwable e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		try {
			
			String[] dnaHumano = {
					"ACGT",
					"CGTA",
					"AGTC",
					"TCAG"
			};
			assertEquals(false, mutant.isMutant(dnaHumano));
			assertEquals(false, mutant.isMutant(new String[] {"AAAA", "AAAA", "AAAA", "AAA"}));
		} catch (Throwable e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
}
