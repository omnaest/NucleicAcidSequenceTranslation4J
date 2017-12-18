/*
 * Copyright 2017 Danny Kunz Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package org.omnaest.genetics.translator.domain;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public enum NucleicAcidCode implements Predicate<Character>
{
	A("Adenine", 'A', nucleicAcidCode -> 'A' == Character.toUpperCase(nucleicAcidCode)),
	C("Cytosine", 'C', nucleicAcidCode -> 'C' == Character.toUpperCase(nucleicAcidCode)),
	G("Guanine", 'G', nucleicAcidCode -> 'G' == Character.toUpperCase(nucleicAcidCode)),
	T("Thymine", 'T', nucleicAcidCode -> 'T' == Character.toUpperCase(nucleicAcidCode)),
	U("Uracil", 'U', nucleicAcidCode -> 'U' == Character.toUpperCase(nucleicAcidCode)),
	R("A or G; puRine", 'R', A.or(G)),
	Y("C, T or U; pYrimidines", 'Y', C	.or(T)
										.or(U)),
	K("G, T or U; bases which are Ketones", 'K', G	.or(T)
													.or(U)),
	M("A or C; bases with aMino groups", 'M', A.or(C)),
	S("C or G; Strong interaction", 'S', C.or(G)),
	W("A, T or U; Weak interaction", 'W', A	.or(T)
											.or(U)),
	B("not A (i.e. C, G, T or U); B comes after A ", 'B', A.negate()),
	D("not C (i.e. A, G, T or U); D comes after C", 'D', C.negate()),
	H("not G (i.e., A, C, T or U); H comes after G", 'H', G.negate()),
	V("neither T nor U (i.e. A, C or G); V comes after U", 'V', T	.negate()
																	.and(U.negate())),
	N("A C G T U;Nucleic acid ", 'N', A	.or(C)
										.or(G)
										.or(T)
										.or(U)),
	___("gap of indeterminate length", '-', nucleicAcidCode -> "-".equals(nucleicAcidCode));

	private Predicate<Character>	predicate;
	private Character				code;

	private NucleicAcidCode(String name, Character code, Predicate<Character> predicate)
	{
		this.code = code;
		this.predicate = predicate;

	}

	@Override
	public boolean test(Character t)
	{
		return this.predicate.test(t);
	}

	/**
	 * Returns the raw {@link Character} code
	 * 
	 * @return
	 */
	public Character getRawCode()
	{
		return this.code;
	}

	/**
	 * Returns the matching {@link NucleicAcidCode} for the given code {@link Character}. <br>
	 * <br>
	 * If no matching {@link NucleicAcidCode} is found, this returns null
	 *
	 * @param code
	 * @return
	 */
	public static NucleicAcidCode valueOf(Character code)
	{
		NucleicAcidCode retval = null;
		for (NucleicAcidCode nucleicAcidCode : NucleicAcidCode.values())
		{
			if (nucleicAcidCode	.getRawCode()
								.equals(Character.toUpperCase(code)))
			{
				retval = nucleicAcidCode;
				break;
			}
		}
		return retval;
	}

	/**
	 * Returns a further {@link Set} of all fuzzy matching {@link NucleicAcidCode}s
	 *
	 * @param code
	 * @return
	 */
	public Set<NucleicAcidCode> matchingCodes()
	{
		Set<NucleicAcidCode> retval = new LinkedHashSet<>();
		for (NucleicAcidCode nucleicAcidCode : NucleicAcidCode.values())
		{
			if (nucleicAcidCode.test(this.getRawCode()))
			{
				retval.add(nucleicAcidCode);
			}
		}
		return retval;
	}
}