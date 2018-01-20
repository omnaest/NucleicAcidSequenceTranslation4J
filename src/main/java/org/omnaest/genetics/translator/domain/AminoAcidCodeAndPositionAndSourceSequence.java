package org.omnaest.genetics.translator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public String toString()
    {
        return "AminoAcidCodeAndPositionAndSourceSequence [" + this.asAminoAcidCodeSequence() + "]";
    }

}
