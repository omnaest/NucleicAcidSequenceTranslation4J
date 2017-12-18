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
package org.omnaest.genetics.translator.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.omnaest.genetics.translator.utils.BitSetUtils;

public class AminoAcidCodeSequence
{
	protected List<AminoAcidCode> aminoAcidCodes = new ArrayList<>();

	public AminoAcidCodeSequence(AminoAcidCode... aminoAcidCodes)
	{
		this(Arrays.asList(aminoAcidCodes));
	}

	public AminoAcidCodeSequence(Collection<AminoAcidCode> aminoAcidCodes)
	{
		super();
		this.aminoAcidCodes.addAll(aminoAcidCodes);
	}

	public AminoAcidCodeSequence(Stream<AminoAcidCode> aminoAcidCodes)
	{
		this(aminoAcidCodes.collect(Collectors.toList()));
	}

	public static AminoAcidCodeSequence valueOf(String codes)
	{
		return new AminoAcidCodeSequence(Arrays	.stream(ArrayUtils.toObject(codes.toCharArray()))
												.map(code -> AminoAcidCode.valueOf(code))
												.collect(Collectors.toList()));
	}

	public Predicate<? super AminoAcidCodeSequence> asPredicateMatcher()
	{
		BitSet bitSetSignature = this.asBitSet();
		return aminoAcidCodes -> bitSetSignature.equals(aminoAcidCodes.asBitSet());
	}

	public Predicate<? super AminoAcidCodeSequence> asPredicateMatcherFuzzy()
	{
		return aminoAcidCodes ->
		{
			Iterator<AminoAcidCode> validationIterator = aminoAcidCodes	.toList()
																		.iterator();
			return this.aminoAcidCodes	.stream()
										.allMatch(aminoAcidCode -> validationIterator.hasNext() && aminoAcidCode.test(validationIterator.next()));
		};
	}

	private List<AminoAcidCode> toList()
	{
		return new ArrayList<>(this.aminoAcidCodes);
	}

	public BitSet asBitSet()
	{
		byte[] values = ArrayUtils.toPrimitive(this.aminoAcidCodes	.stream()
																	.map(aminoAcidCode -> BitSetUtils.toByte(aminoAcidCode.asBitSet()))
																	.collect(Collectors.toList())
																	.toArray(new Byte[0]));

		return BitSetUtils.valueOf(values);
	}

	public Stream<AminoAcidCode> stream()
	{
		return this.aminoAcidCodes.stream();
	}

	@Override
	public String toString()
	{
		return this.aminoAcidCodes	.stream()
									.filter(code -> code != null)
									.map(code -> String.valueOf(code.getCode()))
									.collect(Collectors.joining());
	}

	public static AminoAcidCodeSequence valueOf(BitSet bitSet)
	{
		return new AminoAcidCodeSequence(AminoAcidCode.valuesOf(bitSet));
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.aminoAcidCodes == null) ? 0 : this.aminoAcidCodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		AminoAcidCodeSequence other = (AminoAcidCodeSequence) obj;
		if (this.aminoAcidCodes == null)
		{
			if (other.aminoAcidCodes != null)
			{
				return false;
			}
		}
		else if (!this.aminoAcidCodes.equals(other.aminoAcidCodes))
		{
			return false;
		}
		return true;
	}

	public boolean contains(AminoAcidCodeSequence aminoAcidCodeSequence)
	{
		boolean retval = false;

		List<AminoAcidCode> externalAminoAcidCodes = aminoAcidCodeSequence.getAminoAcidCodes();

		List<AminoAcidCode> flowingAminoAcidCodes = new ArrayList<>();
		for (AminoAcidCode aminoAcidCode : this.aminoAcidCodes)
		{
			flowingAminoAcidCodes.add(aminoAcidCode);
			if (flowingAminoAcidCodes.size() > externalAminoAcidCodes.size())
			{
				flowingAminoAcidCodes.remove(0);
			}
			if (flowingAminoAcidCodes.size() == externalAminoAcidCodes.size())
			{
				boolean isEqual = this.validateEqualityOfAminoAcidCodes(externalAminoAcidCodes, flowingAminoAcidCodes);
				if (isEqual)
				{
					retval = true;
					break;
				}
			}
		}

		return retval;
	}

	private boolean validateEqualityOfAminoAcidCodes(List<AminoAcidCode> externalAminoAcidCodes, List<AminoAcidCode> flowingAminoAcidCodes)
	{
		return flowingAminoAcidCodes.equals(externalAminoAcidCodes);
	}

	private List<AminoAcidCode> getAminoAcidCodes()
	{
		return new ArrayList<>(this.aminoAcidCodes);
	}

	public List<AminoAcidCode> asList()
	{
		return new ArrayList<>(this.aminoAcidCodes);
	}

	/**
	 * Returns a new {@link AminoAcidCodeSequence} representing a subsequence from the given start with the given length of {@link AminoAcidCode}s
	 *
	 * @param start
	 * @param length
	 * @return
	 */
	public AminoAcidCodeSequence subSequence(int start, int length)
	{
		return new AminoAcidCodeSequence(this.aminoAcidCodes.subList(start, start + length));
	}

	/**
	 * Returns a new sequence instance containing the code sequence from the current instance and the code sequence from the given sequence appended to it
	 *
	 * @param appendedAminoAcidCodeSequence
	 * @return new
	 */
	public AminoAcidCodeSequence asAppendedWith(AminoAcidCodeSequence appendedAminoAcidCodeSequence)
	{
		Collection<AminoAcidCode> aminoAcidCodes2 = new ArrayList<>(this.aminoAcidCodes);
		aminoAcidCodes2.addAll(appendedAminoAcidCodeSequence.aminoAcidCodes);
		return new AminoAcidCodeSequence(aminoAcidCodes2);
	}

	public static interface Builder
	{
		AminoAcidCodeSequence build();

		Builder append(Collection<AminoAcidCode> aminoAcidCodes);

		Builder append(AminoAcidCodeSequence aminoAcidCodeSequence);

		Builder append(AminoAcidCode... aminoAcidCodes);
	}

	public static Builder builder()
	{
		return new Builder()
		{
			private List<AminoAcidCode> aminoAcidCodes = new ArrayList<>();

			@Override
			public Builder append(AminoAcidCodeSequence aminoAcidCodeSequence)
			{
				this.aminoAcidCodes.addAll(aminoAcidCodeSequence.asList());
				return this;
			}

			@Override
			public Builder append(Collection<AminoAcidCode> aminoAcidCodes)
			{
				this.aminoAcidCodes.addAll(aminoAcidCodes);
				return this;
			}

			@Override
			public Builder append(AminoAcidCode... aminoAcidCodes)
			{
				this.aminoAcidCodes.addAll(Arrays.asList(aminoAcidCodes));
				return this;
			}

			@Override
			public AminoAcidCodeSequence build()
			{
				return new AminoAcidCodeSequence(this.aminoAcidCodes);
			}
		};
	}

	public static AminoAcidCodeSequence valueOf(Collection<AminoAcidCode> aminoAcidCodes)
	{
		return new AminoAcidCodeSequence(aminoAcidCodes);
	}

	public static AminoAcidCodeSequence valueOf(Stream<AminoAcidCode> aminoAcidCodes)
	{
		return new AminoAcidCodeSequence(aminoAcidCodes);
	}

}
