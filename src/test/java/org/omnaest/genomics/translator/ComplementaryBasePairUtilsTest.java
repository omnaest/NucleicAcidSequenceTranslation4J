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
package org.omnaest.genomics.translator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.genomics.translator.ComplementaryBasePairUtils;
import org.omnaest.genomics.translator.ComplementaryBasePairUtils.ComplementationType;
import org.omnaest.genomics.translator.domain.NucleicAcidCode;

public class ComplementaryBasePairUtilsTest
{

	@Test
	public void testToComplement() throws Exception
	{
		assertEquals(NucleicAcidCode.T, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.A, ComplementationType.DNA));
		assertEquals(NucleicAcidCode.A, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.T, ComplementationType.DNA));
		assertEquals(NucleicAcidCode.G, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.C, ComplementationType.DNA));
		assertEquals(NucleicAcidCode.C, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.G, ComplementationType.DNA));

		assertEquals(NucleicAcidCode.U, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.A, ComplementationType.RNA));
		assertEquals(NucleicAcidCode.A, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.U, ComplementationType.RNA));
		assertEquals(NucleicAcidCode.G, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.C, ComplementationType.RNA));
		assertEquals(NucleicAcidCode.C, ComplementaryBasePairUtils.toComplement(NucleicAcidCode.G, ComplementationType.RNA));
	}

}
