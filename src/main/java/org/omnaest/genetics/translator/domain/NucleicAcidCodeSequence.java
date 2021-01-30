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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.omnaest.genetics.translator.ComplementaryBasePairUtils.ComplementationType;
import org.omnaest.genetics.translator.TranslationUtils;
import org.omnaest.utils.ListUtils;
import org.omnaest.utils.ObjectUtils;
import org.omnaest.utils.list.enumeration.CompressableEnumList;
import org.omnaest.utils.list.enumeration.ConstantCompressableEnumList;

/**
 * Storage of a sequence of {@link NucleicAcidCode}s
 * 
 * @see #usingInMemoryCompression()
 * @see #valueOf(String)
 * @see #valueOf(Stream)
 * @see #valueOf(Collection)
 * @see AminoAcidCodeSequence
 * @author omnaest
 */
public class NucleicAcidCodeSequence implements Iterable<NucleicAcidCode>, CodeSequence<NucleicAcidCode>
{
    private CompressableEnumList<NucleicAcidCode> codesEnumList;

    public NucleicAcidCodeSequence(Collection<NucleicAcidCode> nucleicAcidCodes)
    {
        super();
        this.codesEnumList = new ConstantCompressableEnumList<>(NucleicAcidCode.class, nucleicAcidCodes);
    }

    /**
     * Uses an {@link BitSet} internally to store the {@link NucleicAcidCode} sequence, which uses only as much bits as needed per {@link NucleicAcidCode}
     * 
     * @see #usingInMemoryCompression(boolean)
     * @return
     */
    public NucleicAcidCodeSequence usingInMemoryCompression()
    {
        return this.usingInMemoryCompression(true);
    }

    /**
     * @see #usingInMemoryCompression()
     * @param active
     * @return
     */
    public NucleicAcidCodeSequence usingInMemoryCompression(boolean active)
    {
        this.codesEnumList.usingInMemoryCompression(active);
        return this;
    }

    public AminoAcidCodeSequence asAminoAcidCodeSequence()
    {
        return new AminoAcidCodeSequence(TranslationUtils.transform(this.codesEnumList.stream())
                                                         .map(frames -> frames.getCodeOfFirstFrame())
                                                         .collect(Collectors.toList()));
    }

    public Stream<NucleicAcidCode> stream()
    {
        return this.codesEnumList.stream();
    }

    @Override
    public Iterator<NucleicAcidCode> iterator()
    {
        return this.codesEnumList.iterator();
    }

    public static NucleicAcidCodeSequence valueOf(String codes)
    {
        return new NucleicAcidCodeSequence(Arrays.stream(ArrayUtils.toObject(codes.toCharArray()))
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
        return this.codesEnumList.toArray(new NucleicAcidCode[this.codesEnumList.size()]);
    }

    /**
     * Returns this {@link NucleicAcidCodeSequence} as a {@link Stream} of {@link CodeAndPosition} where the position = 0,1,2,...
     * 
     * @return
     */
    public CodeAndPositionSequence<NucleicAcidCode> asCodeAndPositionSequence()
    {
        return CodeAndPositionSequence.valueOf(IntStream.range(0, this.codesEnumList.size())
                                                        .mapToObj(position -> new CodeAndPosition<>(this.codesEnumList.get(position), position)));
    }

    public List<NucleicAcidCode> toList()
    {
        return new ArrayList<>(this.codesEnumList);
    }

    /**
     * Returns the code sequence representation like e.g. "GTATAAAGAGGCAGGCTGCGGA"
     */
    @Override
    public String toString()
    {
        return this.toList()
                   .stream()
                   .map(code -> ObjectUtils.getOrDefaultIfNotNull(code, c -> code.getRawCode(), c -> ' '))
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
        return valueOf(ListUtils.inverse(this.codesEnumList)).usingInMemoryCompression(this.codesEnumList.isInMemoryCompressionActive());
    }

    public int size()
    {
        return this.codesEnumList.size();
    }

    public NucleicAcidCodeSequence asReverseStrand(ComplementationType complementationType)
    {
        return TranslationUtils.reverseStrand(this, complementationType)
                               .usingInMemoryCompression(this.codesEnumList.isInMemoryCompressionActive());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.codesEnumList == null) ? 0 : this.codesEnumList.hashCode());
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
        NucleicAcidCodeSequence other = (NucleicAcidCodeSequence) obj;
        if (this.codesEnumList == null)
        {
            if (other.codesEnumList != null)
            {
                return false;
            }
        }
        else if (!this.codesEnumList.equals(other.codesEnumList))
        {
            return false;
        }
        return true;
    }

    /**
     * Returns an empty {@link NucleicAcidCodeSequence}
     * 
     * @return
     */
    public static NucleicAcidCodeSequence empty()
    {
        return valueOf("");
    }

}
