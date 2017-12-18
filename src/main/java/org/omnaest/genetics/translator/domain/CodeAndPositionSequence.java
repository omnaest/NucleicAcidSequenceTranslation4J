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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.omnaest.utils.ListUtils;

/**
 * @see #valueOf(Stream)
 * @author omnaest
 * @param <C>
 */
public class CodeAndPositionSequence<C> implements Iterable<CodeAndPosition<C>>
{
	private List<CodeAndPosition<C>> codeAndPositionSequence = new ArrayList<>();

	protected CodeAndPositionSequence(Collection<CodeAndPosition<C>> sequence)
	{
		super();
		this.codeAndPositionSequence.addAll(sequence);
	}

	@Override
	public Iterator<CodeAndPosition<C>> iterator()
	{
		return this.codeAndPositionSequence.iterator();
	}

	public Stream<CodeAndPosition<C>> stream()
	{
		return this.codeAndPositionSequence.stream();
	}

	public List<CodeAndPosition<C>> toList()
	{
		return new ArrayList<>(this.codeAndPositionSequence);
	}

	public static <C> CodeAndPositionSequence<C> valueOf(Stream<CodeAndPosition<C>> sequence)
	{
		return valueOf(sequence.collect(Collectors.toList()));
	}

	public static <C> CodeAndPositionSequence<C> valueOf(Collection<CodeAndPosition<C>> sequence)
	{
		return new CodeAndPositionSequence<>(sequence);
	}

	/**
	 * Returns the inverse {@link CodeAndPositionSequence} but the positions of the single {@link CodeAndPosition} will remain unchanged
	 * 
	 * @return
	 */
	public CodeAndPositionSequence<C> inverse()
	{
		return valueOf(ListUtils.inverse(this.codeAndPositionSequence));
	}

	/**
	 * @see NucleicAcidCodeSequence#valueOf(Stream)
	 * @see AminoAcidCodeSequence#valueOf(Stream)
	 * @param factoryMethod
	 * @return
	 */
	public <R> R as(Function<Stream<C>, R> factoryMethod)
	{
		return factoryMethod.apply(this.codeAndPositionSequence	.stream()
																.map(cap -> cap.getCode()));
	}
}
