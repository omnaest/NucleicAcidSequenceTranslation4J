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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.omnaest.genetics.fasta.domain.AminoAcidCodeSequence;
import org.omnaest.utils.ListUtils;

public class TranslationUtils
{
	private static interface NucleicAcidCodeFilterMapper extends Predicate<NucleicAcidCode>, Function<NucleicAcidCode, AminoAcidCodeByFrames>
	{
	}

	private static interface AminoAcidCodeFilterMapper
			extends Predicate<AminoAcidCodeAndPosition>, Function<AminoAcidCodeAndPosition, List<AminoAcidCodeAndPosition>>
	{
	}

	private static NucleicAcidCodeFilterMapper createTransformFilterMapper()
	{
		return new NucleicAcidCodeFilterMapper()
		{
			@SuppressWarnings("unchecked")
			private NucleicAcidCodesByFrames	nucleicAcidCodeFrames	= new NucleicAcidCodesByFrames(	Arrays	.asList(new ArrayList<NucleicAcidCode>(),
																														new ArrayList<NucleicAcidCode>(),
																														new ArrayList<NucleicAcidCode>())
																												.toArray(new List[0]),
																										new int[3]);
			private AminoAcidCodeByFrames		aminoAcidCodeFrames		= new AminoAcidCodeByFrames(new AminoAcidCode[3], new int[3]);

			private AtomicInteger readPosition = new AtomicInteger();

			@Override
			public boolean test(NucleicAcidCode nucleicAcidCode)
			{
				int frameSize = 3;
				for (int frame = 0; frame < frameSize; frame++)
				{
					if (this.readPosition.get() >= frame)
					{
						List<NucleicAcidCode> nucleicAcidCodes = this.nucleicAcidCodeFrames.getCodeOfNThFrame(frame);

						nucleicAcidCodes.add(nucleicAcidCode);

						if (nucleicAcidCodes.size() == frameSize)
						{
							AminoAcidCode aminoAcidCode = CodonTableUtils.translate(nucleicAcidCodes);
							this.aminoAcidCodeFrames.setCodeOfNThFrame(frame, aminoAcidCode);
							this.aminoAcidCodeFrames.setSourcePositionOfNThFrame(frame, this.readPosition.get() - 2);
							nucleicAcidCodes.clear();
						}
					}
				}
				return (this.readPosition.incrementAndGet()) % frameSize == 0;
			}

			@Override
			public AminoAcidCodeByFrames apply(NucleicAcidCode nucleicAcidCode)
			{
				return this.aminoAcidCodeFrames.clone();
			}

		};
	}

	private static AminoAcidCodeFilterMapper createValidProteineSequenceFilterMapper()
	{
		return new AminoAcidCodeFilterMapper()
		{
			List<AminoAcidCodeAndPosition>	aminoAcidCodes	= new ArrayList<>();
			AtomicBoolean					validStartCodon	= new AtomicBoolean(false);

			@Override
			public boolean test(AminoAcidCodeAndPosition aminoAcidCodeAndPosition)
			{
				boolean retval = false;
				AminoAcidCode aminoAcidCode = aminoAcidCodeAndPosition.getAminoAcidCode();
				if (aminoAcidCode != null)
				{
					if (aminoAcidCode.isStart())
					{
						this.aminoAcidCodes.clear();
						this.aminoAcidCodes.add(aminoAcidCodeAndPosition);
						this.validStartCodon.set(true);
					}
					else if (AminoAcidCode.STOP.equals(aminoAcidCode))
					{
						if (this.validStartCodon.get() && !this.aminoAcidCodes.isEmpty())
						{
							retval = true;
						}
						this.validStartCodon.set(false);
					}
					else if (this.validStartCodon.get())
					{
						this.aminoAcidCodes.add(aminoAcidCodeAndPosition);
					}
				}
				return retval;
			}

			@Override
			public List<AminoAcidCodeAndPosition> apply(AminoAcidCodeAndPosition aminoAcidCode)
			{
				return this.aminoAcidCodes;
			}
		};
	}

	/**
	 * Reading a {@link NucleicAcidCode} triplet encodes a single {@link AminoAcidCode}. As a result, when the start of the code is unknown, there are three
	 * frames resulting from the the same {@link NucleicAcidCode} sequence.<br>
	 * The code of the respective reading frame can be retrieved by the {@link #getCodeOfFirstFrame()}, {@link #getCodeOfSecondFrame()} and
	 * {@link #getCodeOfThirdFrame()} methods.
	 *
	 * @see #getCodeOfFirstFrame()
	 * @see #getCodeOfSecondFrame()
	 * @see #getCodeOfThirdFrame()
	 * @see #getCodeOfNThFrame(int)
	 * @author Omnaest
	 * @param <C>
	 */
	public static class AminoAcidCodeByFrames extends CodeByFrames<AminoAcidCode>
	{
		public AminoAcidCodeByFrames(AminoAcidCode[] framesWithCode, int[] sourcePositions)
		{
			super(framesWithCode, sourcePositions);
		}

		@Override
		public AminoAcidCodeByFrames clone()
		{
			return new AminoAcidCodeByFrames((AminoAcidCode[]) ArrayUtils.clone(this.framesWithCode), this.sourcePositions);
		}
	}

	public static class NucleicAcidCodesByFrames extends CodeByFrames<List<NucleicAcidCode>>
	{
		public NucleicAcidCodesByFrames(List<NucleicAcidCode>[] framesWithCode, int[] sourcePositions)
		{
			super(framesWithCode, sourcePositions);
		}
	}

	public static class CodeByFrames<C>
	{
		protected C[]	framesWithCode;
		protected int[]	sourcePositions;

		public CodeByFrames(C[] framesWithCode, int[] sourcePositions)
		{
			super();
			this.framesWithCode = framesWithCode;
			this.sourcePositions = sourcePositions;
		}

		public void setCodeOfNThFrame(int index, C code)
		{
			this.framesWithCode[index] = code;
		}

		public C getCodeOfFirstFrame()
		{
			return this.getCodeOfNThFrame(0);
		}

		public C getCodeOfSecondFrame()
		{
			return this.getCodeOfNThFrame(1);
		}

		public C getCodeOfThirdFrame()
		{
			return this.getCodeOfNThFrame(2);
		}

		public C getCodeOfNThFrame(int frameIndex)
		{
			return this.framesWithCode != null && this.framesWithCode.length > frameIndex ? this.framesWithCode[frameIndex] : null;
		}

		public int getSourcePositionOfNThFrame(int frameIndex)
		{
			assert (frameIndex >= 0 && frameIndex < this.sourcePositions.length);
			return this.sourcePositions[frameIndex];
		}

		public int getSourcePositionOfFirstFrame()
		{
			return this.getSourcePositionOfNThFrame(0);
		}

		public int getSourcePositionOfSecondFrame()
		{
			return this.getSourcePositionOfNThFrame(1);
		}

		public int getSourcePositionOfThirdFrame()
		{
			return this.getSourcePositionOfNThFrame(2);
		}

		public void setSourcePositionOfNThFrame(int frameIndex, int position)
		{
			assert (frameIndex >= 0 && frameIndex < this.sourcePositions.length);
			this.sourcePositions[frameIndex] = position;
		}

		@Override
		public String toString()
		{
			return "CodeByFrames [framesWithCode=" + Arrays.toString(this.framesWithCode) + "]";
		}

	}

	public static Stream<AminoAcidCodeByFrames> transform(Stream<NucleicAcidCode> stream)
	{
		NucleicAcidCodeFilterMapper filterMapper = createTransformFilterMapper();
		return stream	.filter(filterMapper)
						.map(filterMapper);
	}

	public static class AminoAcidCodeSequenceAndPosition extends DataAndPosition<AminoAcidCodeSequence>
	{
		public AminoAcidCodeSequenceAndPosition(AminoAcidCodeSequence aminoAcidCodeSequence, int sourcePosition)
		{
			super(aminoAcidCodeSequence, sourcePosition);
		}

		public AminoAcidCodeSequence getAminoAcidCodeSequence()
		{
			return this.getData();
		}
	}

	public static class AminoAcidCodeAndPosition extends DataAndPosition<AminoAcidCode>
	{
		public AminoAcidCodeAndPosition(AminoAcidCode aminoAcidCode, int sourcePosition)
		{
			super(aminoAcidCode, sourcePosition);
		}

		public AminoAcidCode getAminoAcidCode()
		{
			return this.getData();
		}
	}

	public static class DataAndPosition<D>
	{
		private D	data;
		private int	sourcePosition;

		public DataAndPosition(D data, int sourcePosition)
		{
			super();
			this.data = data;
			this.sourcePosition = sourcePosition;
		}

		protected D getData()
		{
			return this.data;
		}

		public int getSourcePosition()
		{
			return this.sourcePosition;
		}

	}

	public static Stream<AminoAcidCodeSequenceAndPosition> filterValidProteinSequence(Stream<AminoAcidCodeAndPosition> stream)
	{
		AminoAcidCodeFilterMapper filterMapper = createValidProteineSequenceFilterMapper();
		return stream	.filter(filterMapper)
						.map(filterMapper)
						.map(aminoAcidCodeAndPositions -> new AminoAcidCodeSequenceAndPosition(	new AminoAcidCodeSequence(aminoAcidCodeAndPositions	.stream()
																																					.map(codeAndPosition -> codeAndPosition.getAminoAcidCode())
																																					.collect(Collectors.toList())),
																								ListUtils	.first(aminoAcidCodeAndPositions)
																											.getSourcePosition()));
	}

	protected TranslationUtils()
	{
	}
}
