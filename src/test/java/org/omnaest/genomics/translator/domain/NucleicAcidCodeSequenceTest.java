/*******************************************************************************
 * Copyright 2021 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
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
package org.omnaest.genomics.translator.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.genomics.translator.domain.NucleicAcidCodeSequence;

/**
 * @see NucleicAcidCodeSequence
 * @author omnaest
 */
public class NucleicAcidCodeSequenceTest
{

	@Test
	public void testToString() throws Exception
	{
		String codes = "GTATAAAGAGGCAGGCTGCGGACTCGGAGCAGCTCGGGGCTGCGCAGCGGGAAGGCTCGCCTAGTCGGTCCGCATCCGTGTCGACCACCTGTCTGGACACCACGAAGATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAGGTGAG";
		String result = NucleicAcidCodeSequence	.valueOf(codes)
												.toString();
		assertEquals(codes, result);
	}

	@Test
	public void testToStringWithActiveInMemoryCompression() throws Exception
	{
		String codes = "GTATAAAGAGGCAGGCTGCGGACTCGGAGCAGCTCGGGGCTGCGCAGCGGGAAGGCTCGCCTAGTCGGTCCGCATCCGTGTCGACCACCTGTCTGGACACCACGAAGATGCCACCCGTTGGGGGCAAAAAGGCCAAGAAGGTGAG";
		String result = NucleicAcidCodeSequence	.valueOf(codes)
												.usingInMemoryCompression()
												.toString();
		assertEquals(codes, result);
	}

}
