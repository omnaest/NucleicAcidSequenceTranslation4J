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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.omnaest.genetics.fasta.domain.AminoAcidCodeSequence;
import org.omnaest.genetics.fasta.domain.NucleicAcidCodeSequence;
import org.omnaest.utils.ArrayUtils;
import org.omnaest.utils.ListUtils;
import org.omnaest.utils.StreamUtils;

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
			private AminoAcidCodeByFrames		aminoAcidCodeFrames		= new AminoAcidCodeByFrames(new AminoAcidCode[3], new int[3],
																									new NucleicAcidCode[3][3]);

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
							this.aminoAcidCodeFrames.setSourcesOfNthFrame(frame, nucleicAcidCodes.toArray(new NucleicAcidCode[3]));
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
	public static class AminoAcidCodeByFrames extends CodeByFrames<AminoAcidCode, NucleicAcidCode>
	{
		public AminoAcidCodeByFrames(AminoAcidCode[] framesWithCode, int[] sourcePositions, NucleicAcidCode[][] framesWithSources)
		{
			super(framesWithCode, sourcePositions, framesWithSources);
		}

		@Override
		public AminoAcidCodeByFrames clone()
		{
			return new AminoAcidCodeByFrames(ArrayUtils.clone(this.framesWithCode), this.sourcePositions, ArrayUtils.deepClone(this.framesWithSources));
		}

	}

	public static class NucleicAcidCodesByFrames extends CodeByFrames<List<NucleicAcidCode>, List<NucleicAcidCode>>
	{
		public NucleicAcidCodesByFrames(List<NucleicAcidCode>[] framesWithCode, int[] sourcePositions)
		{
			super(framesWithCode, sourcePositions, null);
		}
	}

	public static class CodeByFrames<C, S>
	{
		protected C[]	framesWithCode;
		protected S[][]	framesWithSources;
		protected int[]	sourcePositions;

		public CodeByFrames(C[] framesWithCode, int[] sourcePositions, S[][] framesWithSources)
		{
			super();
			this.framesWithCode = framesWithCode;
			this.sourcePositions = sourcePositions;
			this.framesWithSources = framesWithSources;
		}

		public S[] getSourcesOfFirstFrame()
		{
			return this.getSourcesOfNthFrame(0);
		}

		public S[] getSourcesOfSecondFrame()
		{
			return this.getSourcesOfNthFrame(1);
		}

		public S[] getSourcesOfThirdFrame()
		{
			return this.getSourcesOfNthFrame(2);
		}

		/**
		 * frameIndex = 0,1,2,...
		 * 
		 * @param frameIndex
		 * @return
		 */
		public S[] getSourcesOfNthFrame(int frameIndex)
		{
			return this.framesWithSources[frameIndex];
		}

		public CodeByFrames<C, S> setSourcesOfNthFrame(int frameIndex, S[] sources)
		{
			this.framesWithSources[frameIndex] = sources;
			return this;
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

		/**
		 * index = 0,1,2,...
		 * 
		 * @param frameIndex
		 * @return
		 */
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

	/**
	 * Transforms a {@link NucleicAcidCodeSequence} into a {@link Stream} of {@link AminoAcidCode}s<br>
	 * <br>
	 * frame=0,1,2,...
	 * 
	 * @param frameIndex
	 * @param nucleicAcidCodeSequence
	 * @return
	 */
	public static Stream<AminoAcidCode> transform(int frameIndex, NucleicAcidCodeSequence nucleicAcidCodeSequence)
	{
		return transform(nucleicAcidCodeSequence).map(frame -> frame.getCodeOfNThFrame(frameIndex));
	}

	public static Stream<AminoAcidCodeByFrames> transform(NucleicAcidCodeSequence nucleicAcidCodeSequence)
	{
		return transform(nucleicAcidCodeSequence.stream());
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

	public static NucleicAcidCodeSequence toNucleicAcidCodeSequence(String sequence)
	{
		return NucleicAcidCodeSequence.valueOf(sequence);
	}

	public static class CodeAndPosition<C>
	{
		private C		code;
		private long	position;

		public CodeAndPosition(C code, long position)
		{
			super();
			this.code = code;
			this.position = position;
		}

		public C getCode()
		{
			return this.code;
		}

		public long getPosition()
		{
			return this.position;
		}

		@Override
		public String toString()
		{
			return "CodeAndPosition [code=" + this.code + ", position=" + this.position + "]";
		}

	}

	public static class CodeAndPositionAndSource<C, S> extends CodeAndPosition<C>
	{
		private List<CodeAndPosition<S>> sources;

		public CodeAndPositionAndSource(C code, long position, List<CodeAndPosition<S>> sources)
		{
			super(code, position);
			this.sources = sources;
		}

		public List<CodeAndPosition<S>> getSources()
		{
			return this.sources;
		}

		@Override
		public String toString()
		{
			return "CodeAndPositionAndSource [sources=" + this.sources + ", getCode()=" + this.getCode() + ", getPosition()=" + this.getPosition() + "]";
		}

	}

	public static interface SequenceTranslation<C, S>
	{
		public Stream<CodeAndPositionAndSource<C, S>> asCodeAndPositionSequence();

		public Stream<C> asCodeSequence();

	}

	public static interface NucleicAcidCodeSequenceTranslation extends SequenceTranslation<AminoAcidCode, NucleicAcidCode>
	{
		public AminoAcidCodeSequence asAminoAcidCodeSequence();
	}

	public static NucleicAcidCodeSequenceTranslation translate(int frame, NucleicAcidCodeSequence sequence)
	{
		return translate(frame, sequence.stream());
	}

	public static NucleicAcidCodeSequenceTranslation translate(int frame, Stream<NucleicAcidCode> sequence)
	{
		AtomicLong position = new AtomicLong();
		return translateCodeAndPosition(frame, sequence.map(code -> new CodeAndPosition<>(code, position.getAndIncrement())));
	}

	public static NucleicAcidCodeSequenceTranslation translateCodeAndPosition(int frame, Stream<CodeAndPosition<NucleicAcidCode>> sequence)
	{
		AtomicLong position = new AtomicLong();
		Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> retval = StreamUtils	.framed(3, sequence)
																								.map(codes ->
																								{
																									List<CodeAndPosition<NucleicAcidCode>> codesList = Arrays.asList(codes);
																									AminoAcidCode code = CodonTableUtils.translate(codesList.stream()
																																							.map(cap -> cap.getCode())
																																							.collect(Collectors.toList()));

																									return new CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>(code,
																																										position.getAndIncrement(),
																																										codesList);
																								});
		return new NucleicAcidCodeSequenceTranslation()
		{
			@Override
			public Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> asCodeAndPositionSequence()
			{
				return retval;
			}

			@Override
			public Stream<AminoAcidCode> asCodeSequence()
			{
				return retval.map(cap -> cap.getCode());
			}

			@Override
			public AminoAcidCodeSequence asAminoAcidCodeSequence()
			{
				return new AminoAcidCodeSequence(this.asCodeSequence());
			}

		};
	}
}
