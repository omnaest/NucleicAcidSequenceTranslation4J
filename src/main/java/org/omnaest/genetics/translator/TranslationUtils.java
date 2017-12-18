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

package org.omnaest.genetics.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.omnaest.genetics.translator.ComplementaryBasePairUtils.ComplementationType;
import org.omnaest.genetics.translator.domain.AminoAcidCode;
import org.omnaest.genetics.translator.domain.AminoAcidCodeSequence;
import org.omnaest.genetics.translator.domain.CodeAndPosition;
import org.omnaest.genetics.translator.domain.CodeAndPositionAndSource;
import org.omnaest.genetics.translator.domain.CodeAndPositionSequence;
import org.omnaest.genetics.translator.domain.NucleicAcidCode;
import org.omnaest.genetics.translator.domain.NucleicAcidCodeSequence;
import org.omnaest.utils.ArrayUtils;
import org.omnaest.utils.ListUtils;
import org.omnaest.utils.StreamUtils;

public class TranslationUtils
{
	private static class NucleicAcidCodeSequenceTranslationImpl implements NucleicAcidCodeSequenceTranslation
	{
		private Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> codeAndPositionAndSourceStream;

		public NucleicAcidCodeSequenceTranslationImpl(Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> codeAndPositionAndSourceStream)
		{
			this.codeAndPositionAndSourceStream = codeAndPositionAndSourceStream;
		}

		@Override
		public Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> asCodeAndPositionAndSourceSequence()
		{
			return this.codeAndPositionAndSourceStream;
		}

		@Override
		public Stream<AminoAcidCode> asCodeSequence()
		{
			return this.codeAndPositionAndSourceStream.map(cap -> cap.getCode());
		}

		@Override
		public AminoAcidCodeSequence asAminoAcidCodeSequence()
		{
			return new AminoAcidCodeSequence(this.asCodeSequence());
		}
	}

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

	public static interface SequenceTranslation<C, S>
	{
		public Stream<CodeAndPositionAndSource<C, S>> asCodeAndPositionAndSourceSequence();

		public Stream<C> asCodeSequence();

	}

	public static interface NucleicAcidCodeSequenceTranslation extends SequenceTranslation<AminoAcidCode, NucleicAcidCode>
	{
		public AminoAcidCodeSequence asAminoAcidCodeSequence();
	}

	/**
	 * Similar to {@link #translate(int, NucleicAcidCodeSequence)}
	 * 
	 * @param frame
	 * @param sequence
	 * @return
	 */
	public static NucleicAcidCodeSequenceTranslation translate(int frame, String sequence)
	{
		return translate(frame, NucleicAcidCodeSequence.valueOf(sequence));
	}

	/**
	 * Similar to {@link #translateAllFrames(NucleicAcidCodeSequence)}
	 * 
	 * @param sequence
	 * @return
	 */
	public static Stream<NucleicAcidCodeSequenceTranslation> translateAllFrames(String sequence)
	{
		return translateAllFrames(NucleicAcidCodeSequence.valueOf(sequence));
	}

	/**
	 * Translates all three frames using {@link #multiTranslate(NucleicAcidCodeSequence)}
	 * 
	 * @param sequence
	 * @return
	 */
	public static Stream<NucleicAcidCodeSequenceTranslation> translateAllFrames(NucleicAcidCodeSequence sequence)
	{
		return IntStream.range(0, 3)
						.mapToObj(frame -> translate(frame, sequence));
	}

	public static Stream<NucleicAcidCodeSequenceTranslation> translateAllReverseFrames(NucleicAcidCodeSequence sequence)
	{
		return IntStream.range(0, 3)
						.mapToObj(frame -> translate(frame, sequence.inverse()))
						.map(translation -> new NucleicAcidCodeSequenceTranslationImpl(StreamUtils.reverse(translation.asCodeAndPositionAndSourceSequence())));
	}

	/**
	 * A {@link TranslationBuilder} allows to build {@link NucleicAcidCodeSequenceTranslation}s for the normal three frames and the reverse ones
	 * 
	 * @see #get()
	 * @author omnaest
	 */
	public static interface TranslationBuilder
	{
		public TranslationBuilder frames(int... frames);

		public TranslationBuilder allFrames();

		public TranslationBuilder reverseFrames(int... frames);

		public TranslationBuilder allReverseFrames();

		public Stream<NucleicAcidCodeSequenceTranslation> get();
	}

	/**
	 * Similar to {@link #translate(NucleicAcidCodeSequence)}
	 * 
	 * @param sequence
	 * @return
	 */
	public static TranslationBuilder translate(String sequence)
	{
		return translate(NucleicAcidCodeSequence.valueOf(sequence));
	}

	/**
	 * Returns a {@link TranslationBuilder}
	 * 
	 * @param sequence
	 * @return
	 */
	public static TranslationBuilder translate(NucleicAcidCodeSequence sequence)
	{
		return new TranslationBuilder()
		{
			private Set<Integer>	frames			= new LinkedHashSet<>();
			private Set<Integer>	reverseFrames	= new LinkedHashSet<>();

			@Override
			public TranslationBuilder frames(int... frames)
			{
				this.frames.addAll(Arrays.asList(org.apache.commons.lang3.ArrayUtils.toObject(frames)));
				return this;
			}

			@Override
			public TranslationBuilder allFrames()
			{
				return this.frames(0, 1, 2);
			}

			@Override
			public TranslationBuilder reverseFrames(int... frames)
			{
				this.reverseFrames.addAll(Arrays.asList(org.apache.commons.lang3.ArrayUtils.toObject(frames)));
				return this;
			}

			@Override
			public TranslationBuilder allReverseFrames()
			{
				return this.reverseFrames(0, 1, 2);
			}

			@Override
			public Stream<NucleicAcidCodeSequenceTranslation> get()
			{
				return Stream.concat(	this.frames	.stream()
													.map(frame -> translate(frame, sequence)),
										this.reverseFrames	.stream()
															.map(frame -> translateReverse(frame, sequence)));
			}

		};
	}

	/**
	 * Similar to {@link #translateAllFramesAndReverseFrames(NucleicAcidCodeSequence)}
	 * 
	 * @param sequence
	 * @return
	 */
	public static Stream<NucleicAcidCodeSequenceTranslation> translateAllFramesAndReverseFrames(String sequence)
	{
		return translateAllFramesAndReverseFrames(NucleicAcidCodeSequence.valueOf(sequence));
	}

	/**
	 * Returns
	 * 
	 * @param sequence
	 * @return
	 */
	public static Stream<NucleicAcidCodeSequenceTranslation> translateAllFramesAndReverseFrames(NucleicAcidCodeSequence sequence)
	{
		return Stream.concat(translateAllFrames(sequence), translateAllReverseFrames(sequence));
	}

	/**
	 * Similar to {@link #translateCodeAndPosition(int, Stream)}
	 * 
	 * @param frame
	 * @param sequence
	 * @return
	 */
	public static NucleicAcidCodeSequenceTranslation translate(int frame, NucleicAcidCodeSequence sequence)
	{
		return translateCodeAndPosition(frame, sequence.asCodeAndPositionSequence());
	}

	/**
	 * Returns the reverse translation.<br>
	 * <br>
	 * E.g. the dna sequence "GCGATATCGCAAA" has following reverse strands:<br>
	 * <br>
	 * 3'5' Frame 1: F A I S<br>
	 * 3'5' Frame 2: L R Y R<br>
	 * 3'5' Frame 3: C D I<br>
	 * <br>
	 * <br>
	 * {@link #translateReverse(int, NucleicAcidCodeSequence)} for frame = 0 would return "SIAF" which is 3'5' frame but in reverse direction (as it would be
	 * read by the 5'3' direction)
	 * 
	 * @param frame
	 * @param sequence
	 * @return
	 */
	public static NucleicAcidCodeSequenceTranslation translateReverse(int frame, NucleicAcidCodeSequence sequence)
	{
		ComplementationType complementationType = ComplementationType.DNA;
		return new NucleicAcidCodeSequenceTranslationImpl(StreamUtils.reverse(translate(frame, sequence	.inverse()
																										.asReverseStrand(complementationType)).asCodeAndPositionAndSourceSequence()));
	}

	/**
	 * Similar to {@link #translateCodeAndPosition(int, Stream)}
	 * 
	 * @param frame
	 * @param sequence
	 * @return
	 */
	public static NucleicAcidCodeSequenceTranslation translate(int frame, Stream<NucleicAcidCode> sequence)
	{
		AtomicLong position = new AtomicLong();
		return translateCodeAndPosition(frame, sequence.map(code -> new CodeAndPosition<>(code, position.getAndIncrement())));
	}

	public static NucleicAcidCodeSequenceTranslation translateCodeAndPosition(int frame, CodeAndPositionSequence<NucleicAcidCode> sequence)
	{
		return translateCodeAndPosition(frame, sequence.stream());
	}

	/**
	 * Translates a given {@link Stream} of {@link CodeAndPosition}s of {@link NucleicAcidCode}s into {@link AminoAcidCode}s using the given open reading
	 * frame.<br>
	 * <br>
	 * 
	 * @param frame
	 *            = 0,1,2
	 * @param sequence
	 * @return
	 */
	public static NucleicAcidCodeSequenceTranslation translateCodeAndPosition(int frame, Stream<CodeAndPosition<NucleicAcidCode>> sequence)
	{
		AtomicLong position = new AtomicLong();
		Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> retval = StreamUtils	.framed(3, sequence.skip(frame))
																								.map(codes ->
																								{
																									List<CodeAndPosition<NucleicAcidCode>> codesList = Arrays.asList(codes);
																									AminoAcidCode code = CodonTableUtils.translate(codesList.stream()
																																							.map(cap -> cap != null
																																									? cap.getCode()
																																									: null)
																																							.collect(Collectors.toList()));

																									return code == null ? null
																											: new CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>(	code,
																																											position.getAndIncrement(),
																																											codesList);
																								})
																								.filter(cap -> cap != null);
		return new NucleicAcidCodeSequenceTranslationImpl(retval);
	}

	public static interface MultiNucleicAcidCodeSequenceTranslation
	{
		public NucleicAcidCodeSequenceTranslation getForFrame(int frame);

		public Stream<NucleicAcidCodeSequenceTranslation> asStream();
	}

	public static MultiNucleicAcidCodeSequenceTranslation multiTranslate(NucleicAcidCodeSequence sequence)
	{
		return multiTranslate(sequence.asCodeAndPositionSequence());
	}

	public static MultiNucleicAcidCodeSequenceTranslation multiTranslate(CodeAndPositionSequence<NucleicAcidCode> sequence)
	{
		return multiTranslate(sequence.stream());
	}

	public static MultiNucleicAcidCodeSequenceTranslation multiTranslate(Stream<CodeAndPosition<NucleicAcidCode>> sequence)
	{
		List<CodeAndPosition<NucleicAcidCode>> rawSequence = sequence.collect(Collectors.toList());
		List<NucleicAcidCodeSequenceTranslation> translatedFrames = IntStream	.range(0, 3)
																				.mapToObj(frame -> translateCodeAndPosition(frame, rawSequence.stream()))
																				.collect(Collectors.toList());
		return new MultiNucleicAcidCodeSequenceTranslation()
		{
			@Override
			public NucleicAcidCodeSequenceTranslation getForFrame(int frame)
			{
				return translatedFrames.get(frame);
			}

			@Override
			public Stream<NucleicAcidCodeSequenceTranslation> asStream()
			{
				return translatedFrames.stream();
			}

		};
	}

	public static NucleicAcidCodeSequence reverseStrand(NucleicAcidCodeSequence sequence, ComplementationType complementationType)
	{
		return NucleicAcidCodeSequence.valueOf(sequence	.stream()
														.map(code -> ComplementaryBasePairUtils.toComplement(code, complementationType)));
	}

	public static NucleicAcidCodeSequence translateFromRNAToDNA(NucleicAcidCodeSequence sequence)
	{
		return translateFromDNAToRNA(sequence);
	}

	public static NucleicAcidCodeSequence translateFromDNAToRNA(NucleicAcidCodeSequence sequence)
	{
		return NucleicAcidCodeSequence.valueOf(sequence	.stream()
														.map(code ->
														{
															NucleicAcidCode retval = code;

															if (NucleicAcidCode.T.equals(code))
															{
																retval = NucleicAcidCode.U;
															}
															else if (NucleicAcidCode.U.equals(code))
															{
																retval = NucleicAcidCode.T;
															}

															return retval;
														}));

	}
}
