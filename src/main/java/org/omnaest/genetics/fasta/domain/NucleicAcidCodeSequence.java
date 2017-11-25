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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.omnaest.genetics.fasta.translator.NucleicAcidCode;
import org.omnaest.genetics.fasta.translator.TranslationUtils;

public class NucleicAcidCodeSequence
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

	public static NucleicAcidCodeSequence valueOf(String codes)
	{
		return new NucleicAcidCodeSequence(Arrays	.stream(ArrayUtils.toObject(codes.toCharArray()))
													.map(code -> NucleicAcidCode.valueOf(code))
													.collect(Collectors.toList()));
	}
}
