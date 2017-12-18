/*
 * Copyright 2017 Danny Kunz Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package org.omnaest.genetics.translator.domain;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.omnaest.genetics.translator.utils.BitSetUtils;

public enum AminoAcidCode implements Predicate<AminoAcidCode>
{
	A("Alanine", 'A'),
	C("Cysteine", 'C'),
	D("Aspartic acid", 'D'),
	E("Glutamic acid", 'E'),
	F("Phenylalanine", 'F'),
	G("Glycine", 'G'),
	H("Histidine", 'H'),
	I("Isoleucine", 'I'),
	K("Lysine", 'K'),
	L("Leucine", 'L'),
	J("Leucine (L) or Isoleucine (I)", 'J', L.or(I)),
	M("Methionine", 'M'),
	N("Asparagine", 'N'),
	B("Aspartic acid (D) or Asparagine (N)", 'B', D.or(N)),
	O("Pyrrolysine", 'O'),
	P("Proline", 'P'),
	Q("Glutamine", 'Q'),
	R("Arginine", 'R'),
	S("Serine", 'S'),
	T("Threonine", 'T'),
	U("Selenocysteine", 'U'),
	V("Valine", 'V'),
	W("Tryptophan", 'W'),
	Y("Tyrosine", 'Y'),
	Z("Glutamic acid (E) or Glutamine (Q)", 'Z', E.or(Q)),
	STOP("Translation STOP", '*'),
	__("Gap of indeterminate length", '-'),
	X("any", 'X', STOP	.negate()
						.and(__.negate()));

	private Predicate<AminoAcidCode>	predicate;
	private Character					code;
	private String						name;

	private AminoAcidCode(String name, Character code)
	{
		this.name = name;
		this.code = code;
	}

	private AminoAcidCode(String name, Character code, Predicate<AminoAcidCode> predicate)
	{
		this.name = name;
		this.code = code;
		this.predicate = predicate;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public boolean test(AminoAcidCode otherAminoAcid)
	{
		return Predicate.<AminoAcidCode>isEqual(this)
						.or(this.predicate)
						.test(otherAminoAcid);
	}

	/**
	 * Returns the matching {@link AminoAcidCode} for the given code {@link Character}. <br>
	 * <br>
	 * If no matching {@link AminoAcidCode} is found, this returns null
	 *
	 * @param code
	 * @return
	 */
	public static AminoAcidCode valueOf(Character code)
	{
		AminoAcidCode retval = null;
		for (AminoAcidCode aminoAcidCode : AminoAcidCode.values())
		{
			if (aminoAcidCode	.getCode()
								.equals(Character.toUpperCase(code)))
			{
				retval = aminoAcidCode;
				break;
			}
		}
		return retval;
	}

	public static AminoAcidCode valueOf(BitSet bitSet)
	{
		byte ordinal = BitSetUtils.toByte(bitSet);
		if (ordinal > values().length)
		{
			throw new RuntimeException("given bitset does not match any ordinal(max=" + (values().length - 1) + ") of an amino acid code; bits="
					+ BitSetUtils.toBinaryString(bitSet) + " = " + ordinal);
		}
		return AminoAcidCode.values()[ordinal];
	}

	public static List<AminoAcidCode> valuesOf(BitSet bitSet)
	{
		List<AminoAcidCode> retvals = new ArrayList<>();

		byte[] bytes = bitSet.toByteArray();
		for (byte value : bytes)
		{
			AminoAcidCode aminoAcidCode = AminoAcidCode.valueOf(BitSetUtils.valueOf(value));
			assert (aminoAcidCode != null);
			retvals.add(aminoAcidCode);
		}

		return retvals;
	}

	public Character getCode()
	{
		return this.code;
	}

	/**
	 * Returns a further {@link Set} of all matching {@link AminoAcidCode}s including those which are non specific and do only exclude certain amino acids
	 *
	 * @param code
	 * @return
	 */
	public Set<AminoAcidCode> expandedMatchingCodes()
	{
		Set<AminoAcidCode> retval = new LinkedHashSet<>();
		for (AminoAcidCode aminoAcidCode : AminoAcidCode.values())
		{
			if (aminoAcidCode.test(this))
			{
				retval.add(aminoAcidCode);
			}
		}
		return retval;
	}

	public BitSet asBitSet()
	{
		return BitSetUtils.valueOf((byte) this.ordinal());
	}

	public boolean isStart()
	{
		return AminoAcidCode.M.equals(this);
	}

}