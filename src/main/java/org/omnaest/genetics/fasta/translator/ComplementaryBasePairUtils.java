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

import java.util.Map;

import org.omnaest.utils.MapUtils;

/**
 * Helper for complementary base pairs
 * 
 * @author omnaest
 */
public class ComplementaryBasePairUtils
{
	private static Map<NucleicAcidCode, NucleicAcidCode> dnaComplements = MapUtils	.builder()
																					.put(NucleicAcidCode.A, NucleicAcidCode.T)
																					.put(NucleicAcidCode.T, NucleicAcidCode.A)
																					.put(NucleicAcidCode.G, NucleicAcidCode.C)
																					.put(NucleicAcidCode.C, NucleicAcidCode.G)
																					.build();

	private static Map<NucleicAcidCode, NucleicAcidCode> rnaComplements = MapUtils	.builder()
																					.put(NucleicAcidCode.A, NucleicAcidCode.U)
																					.put(NucleicAcidCode.U, NucleicAcidCode.A)
																					.put(NucleicAcidCode.G, NucleicAcidCode.C)
																					.put(NucleicAcidCode.C, NucleicAcidCode.G)
																					.build();

	public enum ComplementationType
	{
		DNA(dnaComplements), RNA(rnaComplements);

		private Map<NucleicAcidCode, NucleicAcidCode> mapping;

		private ComplementationType(Map<NucleicAcidCode, NucleicAcidCode> mapping)
		{
			this.mapping = mapping;
		}

		public Map<NucleicAcidCode, NucleicAcidCode> getMapping()
		{
			return this.mapping;
		}

	}

	public static NucleicAcidCode toComplement(NucleicAcidCode code, ComplementationType complementationType)
	{
		return complementationType	.getMapping()
									.get(code);
	}

}
