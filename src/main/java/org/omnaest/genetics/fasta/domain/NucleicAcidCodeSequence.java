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
package org.omnaest.genetics.fasta.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.omnaest.genetics.fasta.translator.NucleicAcidCode;
import org.omnaest.genetics.fasta.translator.TranslationUtils;
import org.omnaest.genetics.fasta.translator.TranslationUtils.CodeAndPosition;
import org.omnaest.utils.ListUtils;

public class NucleicAcidCodeSequence implements Iterable<NucleicAcidCode>
{
	private List<NucleicAcidCode> nucleicAcidCodes = new ArrayList<>();

	public NucleicAcidCodeSequence(Collection<NucleicAcidCode> nucleicAcidCodes)
	{
		super();
		this.nucleicAcidCodes.addAll(nucleicAcidCodes);
	}

	public AminoAcidCodeSequence asAminoAcidCodeSequence()
	{
		return new AminoAcidCodeSequence(TranslationUtils	.transform(this.nucleicAcidCodes.stream())
															.map(frames -> frames.getCodeOfFirstFrame())
															.collect(Collectors.toList()));
	}

	public Stream<NucleicAcidCode> stream()
	{
		return this.nucleicAcidCodes.stream();
	}

	@Override
	public Iterator<NucleicAcidCode> iterator()
	{
		return this.nucleicAcidCodes.iterator();
	}

	public static NucleicAcidCodeSequence valueOf(String codes)
	{
		return new NucleicAcidCodeSequence(Arrays	.stream(ArrayUtils.toObject(codes.toCharArray()))
													.map(code -> NucleicAcidCode.valueOf(code))
													.collect(Collectors.toList()));
	}

	public static NucleicAcidCodeSequence valueOf(Collection<NucleicAcidCode> sequence)
	{
		return new NucleicAcidCodeSequence(sequence);
	}

	public static NucleicAcidCodeSequence valueOf(Stream<NucleicAcidCode> sequence)
	{
		return new NucleicAcidCodeSequence(sequence.collect(Collectors.toList()));
	}

	public NucleicAcidCode[] toArray()
	{
		return this.nucleicAcidCodes.toArray(new NucleicAcidCode[this.nucleicAcidCodes.size()]);
	}

	/**
	 * Returns this {@link NucleicAcidCodeSequence} as a {@link Stream} of {@link CodeAndPosition} where the position = 0,1,2,...
	 * 
	 * @return
	 */
	public CodeAndPositionSequence<NucleicAcidCode> asCodeAndPositionSequence()
	{
		return CodeAndPositionSequence.valueOf(IntStream.range(0, this.nucleicAcidCodes.size())
														.mapToObj(position -> new CodeAndPosition<>(this.nucleicAcidCodes.get(position), position)));
	}

	public List<NucleicAcidCode> toList()
	{
		return new ArrayList<>(this.nucleicAcidCodes);
	}

	/**
	 * Returns the code sequence representation like e.g. "GTATAAAGAGGCAGGCTGCGGA"
	 */
	@Override
	public String toString()
	{
		return this	.toList()
					.stream()
					.map(code -> code.getCode())
					.map(String::valueOf)
					.collect(Collectors.joining());
	}

	/**
	 * Returns the {@link NucleicAcidCodeSequence} in reverse order
	 * 
	 * @return
	 */
	public NucleicAcidCodeSequence inverse()
	{
		return valueOf(ListUtils.inverse(this.nucleicAcidCodes));
	}

	public int size()
	{
		return this.nucleicAcidCodes.size();
	}
}
