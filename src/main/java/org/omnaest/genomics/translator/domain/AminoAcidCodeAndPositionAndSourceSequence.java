/*******************************************************************************
 * Copyright 2021 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.omnaest.genomics.translator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.omnaest.utils.ListUtils;

/**
 * @see #valueOf(Stream)
 * @see #valueOf(List)
 * @author omnaest
 */
public class AminoAcidCodeAndPositionAndSourceSequence
{
    private List<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> sequence = new ArrayList<>();

    protected AminoAcidCodeAndPositionAndSourceSequence(List<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> sequence)
    {
        this.sequence.addAll(sequence);
    }

    public static AminoAcidCodeAndPositionAndSourceSequence valueOf(List<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> sequence)
    {
        return new AminoAcidCodeAndPositionAndSourceSequence(sequence);
    }

    public static AminoAcidCodeAndPositionAndSourceSequence valueOf(Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> sequence)
    {
        return new AminoAcidCodeAndPositionAndSourceSequence(sequence.collect(Collectors.toList()));
    }

    public Stream<CodeAndPositionAndSource<AminoAcidCode, NucleicAcidCode>> asCodeAndPositionAndSourceStream()
    {
        return this.sequence.stream();
    }

    /**
     * Returns the current {@link AminoAcidCodeAndPositionAndSourceSequence} as {@link AminoAcidCodeSequence}
     * 
     * @return
     */
    public AminoAcidCodeSequence asAminoAcidCodeSequence()
    {
        return AminoAcidCodeSequence.valueOf(this.asCodeAndPositionAndSourceStream()
                                                 .map(capas -> capas.getCode()));
    }

    /**
     * Returns the {@link NucleicAcidCode} source of the current {@link AminoAcidCodeAndPositionAndSourceSequence} as {@link NucleicAcidCodeSequence}
     * 
     * @return
     */
    public NucleicAcidCodeSequence asNucleicAcidCodeSequence()
    {
        List<NucleicAcidCode> codes = this.asCodeAndPositionAndSourceStream()
                                          .flatMap(capas -> capas.getSources()
                                                                 .stream()
                                                                 .map(CodeAndPosition<NucleicAcidCode>::getCode))
                                          .collect(Collectors.toList());
        return NucleicAcidCodeSequence.valueOf(codes);
    }

    @Override
    public String toString()
    {
        return "AminoAcidCodeAndPositionAndSourceSequence [" + this.asAminoAcidCodeSequence() + "]";
    }

    public AminoAcidCodeAndPositionAndSourceSequence subSequence(int start, int length)
    {
        return valueOf(this.sequence.subList(start, start + length));
    }

    /**
     * Returns the sequence in reverse order. The position information is kept from the original sequence.
     * 
     * @return
     */
    public AminoAcidCodeAndPositionAndSourceSequence reverse()
    {
        return valueOf(ListUtils.inverse(this.sequence));
    }

}
