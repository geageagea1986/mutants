package ejercicio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MutantTest {

	Mutant mutant = new Mutant();

	@Test
	void testEmpty() throws Throwable {
		Throwable t = Assertions.assertThrows(Throwable.class, () -> {
			mutant.isMutant(new String[] { "", "", "", "" });
		});
		assertEquals(Mutant.BadDnaChainException, t.getMessage());
	}
	
	@Test
	void testHuman() throws Throwable {
		String[] dnaHumano = { "ACGT", "CGTA", "AGTC", "TCAG" };
		Assertions.assertEquals(false, mutant.isMutant(dnaHumano));		
	}

	@Test
	void testBadDna() throws Throwable {
		Throwable t = Assertions.assertThrows(Throwable.class, () -> {
			mutant.isMutant(new String[] { "AcAA", "AcAA", "AcAA", "AcAA" });
		});
		assertEquals(Mutant.BadDnaChainException, t.getMessage());
	}

	@Test
	void testOneShorter() throws Throwable {
		Throwable t = Assertions.assertThrows(Throwable.class, () -> {
			mutant.isMutant(new String[] { "AAAA", "AAAA", "AAAA", "AAA" });
		});	
		assertEquals(Mutant.BadDnaChainException, t.getMessage());
	}

	@Test
	void testDoubleVertical() throws Throwable {

		String[] dnaDoubleVertical = { "ATGCAACA", "CAGAGCCA", "TCTTGAGA", "CAAACGCA", "AACCTACA", "TCACGGTA",
				"AACATACA", "TCCCTGCA" };

		Assertions.assertEquals(true, mutant.isMutant(dnaDoubleVertical));
	}

	@Test
	void testDoubleHorizontal() throws Throwable {

		String[] dnaHorizontal = { "AAGAGAAA", "CAGAAGCT", "CACAGCCA", "TTTTTTTT", "AACCTACG", "TCACGGTA", "AACATACA",
				"TCCCTGCA" };

		Assertions.assertEquals(true, mutant.isMutant(dnaHorizontal));
	}

	@Test
	void testDoubleDiagonalUp() throws Throwable {

		String[] dnaDoubleUpDiagonal = { "ATGCAACT", "CACAGCTA", "TGTTGTGA", "CAAATGCT", "AACTTACA", "TCTCGGTA",
				"TTCATACC", "TCCCTGCA" };

		Assertions.assertEquals(true, mutant.isMutant(dnaDoubleUpDiagonal));
	}

	@Test
	void testDoubleDiagonalDown() throws Throwable {
		
		String[] dnaDoubleDownDiagonal = { "TCCCTGCA", "ATCATACC", "TCTCGGTA", "AACTTACA", "CAAATGCT", "TCTTGTGA",
				"CACAGCTA", "ATGAAACT" };

		Assertions.assertEquals(true, mutant.isMutant(dnaDoubleDownDiagonal));
	}

	@Test
	void testNull() throws Throwable {
		Throwable t = Assertions.assertThrows(Throwable.class, () -> {
			mutant.isMutant(null);
		});
		assertEquals(Mutant.BadDnaChainException, t.getMessage());
	}
}
