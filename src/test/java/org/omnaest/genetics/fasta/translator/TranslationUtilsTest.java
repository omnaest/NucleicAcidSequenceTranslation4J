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
package org.omnaest.genetics.fasta.translator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;
import org.omnaest.genetics.fasta.domain.AminoAcidCodeSequence;
import org.omnaest.genetics.fasta.domain.NucleicAcidCodeSequence;

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
		AminoAcidCodeSequence aminoAcidCodeSequence = TranslationUtils	.translate(0, NucleicAcidCodeSequence.valueOf("ATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAG"))
																		.asAminoAcidCodeSequence();

		assertEquals(AminoAcidCodeSequence.valueOf("MPPVGGKKAKK"), aminoAcidCodeSequence);
	}
}
