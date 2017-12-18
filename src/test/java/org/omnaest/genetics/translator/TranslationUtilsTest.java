/*

	Copyright 2017 Danny Kunz

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


*/
package org.omnaest.genetics.translator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.junit.Test;
import org.omnaest.genetics.translator.TranslationUtils;
import org.omnaest.genetics.translator.ComplementaryBasePairUtils.ComplementationType;
import org.omnaest.genetics.translator.TranslationUtils.MultiNucleicAcidCodeSequenceTranslation;
import org.omnaest.genetics.translator.domain.AminoAcidCodeSequence;
import org.omnaest.genetics.translator.domain.NucleicAcidCode;
import org.omnaest.genetics.translator.domain.NucleicAcidCodeSequence;
import org.omnaest.utils.StringUtils;

public class TranslationUtilsTest
{
	@Test
	public void testTransform()
	{
		AminoAcidCodeSequence aminoAcidCodeSequence = AminoAcidCodeSequence.valueOf(TranslationUtils.transform(	0,
																												NucleicAcidCodeSequence.valueOf("ATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAG"))
																									.collect(Collectors.toList()));

		assertEquals(AminoAcidCodeSequence.valueOf("MPPVGGKKAKK"), aminoAcidCodeSequence);
	}

	@Test
	public void testTransformWithFrames()
	{
		{
			NucleicAcidCode[] sourcesOfFirstFrame = TranslationUtils.transform(NucleicAcidCodeSequence.valueOf("ACGTTGCAT"))
																	.skip(1)
																	.findFirst()
																	.get()
																	.getSourcesOfFirstFrame();

			assertArrayEquals(	NucleicAcidCodeSequence	.valueOf("TTG")
														.toArray(),
								sourcesOfFirstFrame);
		}
		{
			NucleicAcidCode[] sourcesOfSecondFrame = TranslationUtils	.transform(NucleicAcidCodeSequence.valueOf("ACGTTGCAT"))
																		.skip(1)
																		.findFirst()
																		.get()
																		.getSourcesOfSecondFrame();

			assertArrayEquals(	NucleicAcidCodeSequence	.valueOf("CGT")
														.toArray(),
								sourcesOfSecondFrame);
		}
	}

	@Test
	public void testTranslateIntNucleicAcidCodeSequence() throws Exception
	{

		assertEquals("MPPVGGKKAKK", TranslationUtils.translate(0, NucleicAcidCodeSequence.valueOf("ATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAG"))
													.asAminoAcidCodeSequence()
													.toString());
		assertEquals("CHPLGAKRPR", TranslationUtils	.translate(1, NucleicAcidCodeSequence.valueOf("ATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAG"))
													.asAminoAcidCodeSequence()
													.toString());
		assertEquals("ATRWGQKGQE", TranslationUtils	.translate(2, NucleicAcidCodeSequence.valueOf("ATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAG"))
													.asAminoAcidCodeSequence()
													.toString());

		//
		assertEquals("SPS", TranslationUtils.translate(0, NucleicAcidCodeSequence.valueOf("TCGCCGTCCGC"))
											.asAminoAcidCodeSequence()
											.toString());
		assertEquals("RRP", TranslationUtils.translate(1, NucleicAcidCodeSequence.valueOf("TCGCCGTCCGC"))
											.asAminoAcidCodeSequence()
											.toString());
		assertEquals("AVR", TranslationUtils.translate(2, NucleicAcidCodeSequence.valueOf("TCGCCGTCCGC"))
											.asAminoAcidCodeSequence()
											.toString());

		//
		assertEquals("RSRAF", TranslationUtils	.translate(0, NucleicAcidCodeSequence.valueOf("CGATCTCGGGCGTTC"))
												.asAminoAcidCodeSequence()
												.toString());
		assertEquals("DLGRS", TranslationUtils	.translate(1, NucleicAcidCodeSequence.valueOf("CGATCTCGGGCGTTCTG"))
												.asAminoAcidCodeSequence()
												.toString());
		assertEquals("ISGVL", TranslationUtils	.translate(2, NucleicAcidCodeSequence.valueOf("CGATCTCGGGCGTTCTG"))
												.asAminoAcidCodeSequence()
												.toString());
	}

	@Test
	public void testMultiTranslate() throws Exception
	{
		MultiNucleicAcidCodeSequenceTranslation translation = TranslationUtils.multiTranslate(NucleicAcidCodeSequence.valueOf("ATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAG"));
		assertEquals("MPPVGGKKAKK", translation	.getForFrame(0)
												.asAminoAcidCodeSequence()
												.toString());
		assertEquals("CHPLGAKRPR", translation	.getForFrame(1)
												.asAminoAcidCodeSequence()
												.toString());
		assertEquals("ATRWGQKGQE", translation	.getForFrame(2)
												.asAminoAcidCodeSequence()
												.toString());
	}

	@Test
	public void testReverseStrand() throws Exception
	{
		NucleicAcidCodeSequence reverseStrand = TranslationUtils.reverseStrand(NucleicAcidCodeSequence.valueOf("GCGATATCGCAAA"), ComplementationType.DNA);
		assertEquals("CGCTATAGCGTTT", reverseStrand.toString());

		NucleicAcidCodeSequence rnaAntisenseStrand = TranslationUtils.translateFromDNAToRNA(reverseStrand);
		assertEquals("CGCUAUAGCGUUU", rnaAntisenseStrand.toString());

		NucleicAcidCodeSequence rnaSenseStrand = TranslationUtils.reverseStrand(rnaAntisenseStrand, ComplementationType.RNA);
		assertEquals("GCGAUAUCGCAAA", rnaSenseStrand.toString());
	}

	@Test
	public void testTranslateReverse() throws Exception
	{
		assertEquals(StringUtils.reverse("FAIS"), TranslationUtils	.translateReverse(0, NucleicAcidCodeSequence.valueOf("GCGATATCGCAAA"))
																	.asAminoAcidCodeSequence()
																	.toString());
		assertEquals(StringUtils.reverse("LRYR"), TranslationUtils	.translateReverse(1, NucleicAcidCodeSequence.valueOf("GCGATATCGCAAA"))
																	.asAminoAcidCodeSequence()
																	.toString());
		assertEquals(StringUtils.reverse("CDI"), TranslationUtils	.translateReverse(2, NucleicAcidCodeSequence.valueOf("GCGATATCGCAAA"))
																	.asAminoAcidCodeSequence()
																	.toString());

	}

	@Test
	public void testTranslation()
	{
		{
			//
			boolean anyMatch = TranslationUtils	.translate("ATGCTCCGTCCCGGCGCGCAGCTGCTGCGGG")
												.allFrames()
												.get()
												.anyMatch(translation -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(	translation	.asAminoAcidCodeSequence()
																																			.toString(),
																																"MLRPGAQLLR"));

			assertTrue(anyMatch);
		}
		{
			//
			String sequence = StringUtils.reverse("ATGCTCCGTCCCGGCGCGCAGCTGCTGCGGG");

			String reverseStrand = TranslationUtils	.reverseStrand(NucleicAcidCodeSequence.valueOf(sequence), ComplementationType.DNA)
													.toString();

			//			System.out.println(reverseStrand);
			//			String reverseTranslation = TranslationUtils.translateReverse(0, NucleicAcidCodeSequence.valueOf(reverseStrand))
			//														.asAminoAcidCodeSequence()
			//														.toString();
			//			System.out.println(reverseTranslation);
			//			System.out.println();

			boolean anyMatch = TranslationUtils	.translate(reverseStrand)
												.allReverseFrames()
												.get()
												.map(translation -> translation	.asAminoAcidCodeSequence()
																				.toString())
												//												.peek(System.out::println)
												.anyMatch(isequence -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(	isequence,
																																org.apache.commons.lang3.StringUtils.reverse("MLRPGAQLLR")));

			assertTrue(anyMatch);
		}
	}

}
