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
package org.omnaest.genomics.translator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.omnaest.genomics.translator.domain.AminoAcidCode;
import org.omnaest.genomics.translator.domain.NucleicAcidCode;

public class CodonTableUtils
{
    private static Map<List<NucleicAcidCode>, AminoAcidCode> dnaCodonMap = new ConcurrentHashMap<>();
    private static Map<List<NucleicAcidCode>, AminoAcidCode> rnaCodonMap = new ConcurrentHashMap<>();

    static
    {
        initDNACodonTable();
        initRNACodonTable(dnaCodonMap);
    }

    private static void initDNACodonTable()
    {
        /*
         * Ala / A
         * GCT, GCC, GCA, GCG GCN
         */
        addDNACodonTranslation("GCT", AminoAcidCode.A);
        addDNACodonTranslation("GCC", AminoAcidCode.A);
        addDNACodonTranslation("GCA", AminoAcidCode.A);
        addDNACodonTranslation("GCG", AminoAcidCode.A);
        addDNACodonTranslation("GCN", AminoAcidCode.A);

        /*
         * Leu / L
         * TTA, TTG, CTT, CTC, CTA, CTG YTR, CTN
         */
        addDNACodonTranslation("TTA", AminoAcidCode.L);
        addDNACodonTranslation("TTG", AminoAcidCode.L);
        addDNACodonTranslation("CTT", AminoAcidCode.L);
        addDNACodonTranslation("CTC", AminoAcidCode.L);
        addDNACodonTranslation("CTA", AminoAcidCode.L);
        addDNACodonTranslation("CTG", AminoAcidCode.L);
        addDNACodonTranslation("YTR", AminoAcidCode.L);
        addDNACodonTranslation("CTN", AminoAcidCode.L);

        /*
         * Arg / R
         * CGT, CGC, CGA, CGG, AGA, AGG CGN, MGR
         */
        addDNACodonTranslation("CGT", AminoAcidCode.R);
        addDNACodonTranslation("CGC", AminoAcidCode.R);
        addDNACodonTranslation("CGA", AminoAcidCode.R);
        addDNACodonTranslation("CGG", AminoAcidCode.R);
        addDNACodonTranslation("AGA", AminoAcidCode.R);
        addDNACodonTranslation("AGG", AminoAcidCode.R);
        addDNACodonTranslation("CGN", AminoAcidCode.R);
        addDNACodonTranslation("MGR", AminoAcidCode.R);

        /*
         * Lys / K
         * AAA, AAG AAR
         */
        addDNACodonTranslation("AAA", AminoAcidCode.K);
        addDNACodonTranslation("AAG", AminoAcidCode.K);
        addDNACodonTranslation("AAR", AminoAcidCode.K);

        /*
         * Asn / N
         * AAT, AAC AAY
         */
        addDNACodonTranslation("AAT", AminoAcidCode.N);
        addDNACodonTranslation("AAC", AminoAcidCode.N);
        addDNACodonTranslation("AAY", AminoAcidCode.N);

        /*
         * START POSITION
         * Met / M
         * ATG
         */
        addDNACodonTranslation("ATG", AminoAcidCode.M);

        /*
         * Asp / D
         * GAT, GAC GAY
         */
        addDNACodonTranslation("GAT", AminoAcidCode.D);
        addDNACodonTranslation("GAC", AminoAcidCode.D);
        addDNACodonTranslation("GAY", AminoAcidCode.D);

        /*
         * Phe / F
         * TTT, TTC TTY
         */
        addDNACodonTranslation("TTT", AminoAcidCode.F);
        addDNACodonTranslation("TTC", AminoAcidCode.F);
        addDNACodonTranslation("TTY", AminoAcidCode.F);

        /*
         * Cys / C
         * TGT, TGC TGY
         */
        addDNACodonTranslation("TGT", AminoAcidCode.C);
        addDNACodonTranslation("TGC", AminoAcidCode.C);
        addDNACodonTranslation("TGY", AminoAcidCode.C);

        /*
         * Pro / P
         * CCT, CCC, CCA, CCG CCN
         */
        addDNACodonTranslation("CCT", AminoAcidCode.P);
        addDNACodonTranslation("CCC", AminoAcidCode.P);
        addDNACodonTranslation("CCA", AminoAcidCode.P);
        addDNACodonTranslation("CCG", AminoAcidCode.P);
        addDNACodonTranslation("CCN", AminoAcidCode.P);

        /*
         * Gln / Q
         * CAA, CAG CAR
         */
        addDNACodonTranslation("CAA", AminoAcidCode.Q);
        addDNACodonTranslation("CAG", AminoAcidCode.Q);
        addDNACodonTranslation("CAR", AminoAcidCode.Q);

        /*
         * Ser / S
         * TCT, TCC, TCA, TCG, AGT, AGC TCN, AGY
         */
        addDNACodonTranslation("TCT", AminoAcidCode.S);
        addDNACodonTranslation("TCC", AminoAcidCode.S);
        addDNACodonTranslation("TCA", AminoAcidCode.S);
        addDNACodonTranslation("TCG", AminoAcidCode.S);
        addDNACodonTranslation("AGT", AminoAcidCode.S);
        addDNACodonTranslation("AGC", AminoAcidCode.S);
        addDNACodonTranslation("TCN", AminoAcidCode.S);
        addDNACodonTranslation("AGY", AminoAcidCode.S);

        /*
         * Glu / E
         * GAA, GAG GAR
         */
        addDNACodonTranslation("GAA", AminoAcidCode.E);
        addDNACodonTranslation("GAG", AminoAcidCode.E);
        addDNACodonTranslation("GAR", AminoAcidCode.E);

        /*
         * Thr / T
         * ACT, ACC, ACA, ACG ACN
         */
        addDNACodonTranslation("ACT", AminoAcidCode.T);
        addDNACodonTranslation("ACC", AminoAcidCode.T);
        addDNACodonTranslation("ACA", AminoAcidCode.T);
        addDNACodonTranslation("ACG", AminoAcidCode.T);
        addDNACodonTranslation("ACN", AminoAcidCode.T);

        /*
         * Gly / G
         * GGT, GGC, GGA, GGG GGN
         */
        addDNACodonTranslation("GGT", AminoAcidCode.G);
        addDNACodonTranslation("GGC", AminoAcidCode.G);
        addDNACodonTranslation("GGA", AminoAcidCode.G);
        addDNACodonTranslation("GGG", AminoAcidCode.G);
        addDNACodonTranslation("GGN", AminoAcidCode.G);

        /*
         * Trp / W
         * TGG
         */
        addDNACodonTranslation("TGG", AminoAcidCode.W);

        /*
         * His / H
         * CAT, CAC CAY
         */
        addDNACodonTranslation("CAT", AminoAcidCode.H);
        addDNACodonTranslation("CAC", AminoAcidCode.H);
        addDNACodonTranslation("CAY", AminoAcidCode.H);

        /*
         * Tyr / Y
         * TAT, TAC TAY
         */
        addDNACodonTranslation("TAT", AminoAcidCode.Y);
        addDNACodonTranslation("TAC", AminoAcidCode.Y);
        addDNACodonTranslation("TAY", AminoAcidCode.Y);

        /*
         * Ile / I
         * ATT, ATC, ATA ATH
         */
        addDNACodonTranslation("ATT", AminoAcidCode.I);
        addDNACodonTranslation("ATC", AminoAcidCode.I);
        addDNACodonTranslation("ATA", AminoAcidCode.I);
        addDNACodonTranslation("ATH", AminoAcidCode.I);

        /*
         * Val / V
         * GTT, GTC, GTA, GTG GTN
         */
        addDNACodonTranslation("GTT", AminoAcidCode.V);
        addDNACodonTranslation("GTC", AminoAcidCode.V);
        addDNACodonTranslation("GTA", AminoAcidCode.V);
        addDNACodonTranslation("GTG", AminoAcidCode.V);
        addDNACodonTranslation("GTN", AminoAcidCode.V);

        /*
         * STOP
         * TAA, TGA, TAG TAR, TRA
         */
        addDNACodonTranslation("TAA", AminoAcidCode.STOP);
        addDNACodonTranslation("TGA", AminoAcidCode.STOP);
        addDNACodonTranslation("TAG", AminoAcidCode.STOP);
        addDNACodonTranslation("TAR", AminoAcidCode.STOP);
        addDNACodonTranslation("TRA", AminoAcidCode.STOP);
    }

    private static void initRNACodonTable(Map<List<NucleicAcidCode>, AminoAcidCode> dnaCodonMap)
    {
        UnaryOperator<NucleicAcidCode> dnaCodeToRnaCodeMapper = c -> NucleicAcidCode.T.equals(c) ? NucleicAcidCode.U : c;
        rnaCodonMap.putAll(dnaCodonMap.entrySet()
                                      .stream()
                                      .collect(Collectors.toMap(entry -> entry.getKey()
                                                                              .stream()
                                                                              .map(dnaCodeToRnaCodeMapper)
                                                                              .collect(Collectors.toList()),
                                                                entry -> entry.getValue())));

    }

    private static void addDNACodonTranslation(String nucleicAcidCodes, AminoAcidCode aminoAcidCode)
    {
        addDNACodonTranslation(ArrayUtils.toObject(nucleicAcidCodes.toCharArray()), aminoAcidCode);
    }

    private static void addDNACodonTranslation(Character[] nucleicAcidCodes, AminoAcidCode aminoAcidCode)
    {
        addDNACodonTranslation(Arrays.stream(nucleicAcidCodes)
                                     .map(NucleicAcidCode::valueOf)
                                     .collect(Collectors.toList())
                                     .toArray(new NucleicAcidCode[0]),
                               aminoAcidCode);
    }

    private static void addDNACodonTranslation(NucleicAcidCode[] nucleicAcidCodes, AminoAcidCode aminoAcidCode)
    {
        dnaCodonMap.put(Arrays.asList(nucleicAcidCodes), aminoAcidCode);
    }

    public static AminoAcidCode translate(List<NucleicAcidCode> nucleicAcidCodes)
    {
        return nucleicAcidCodes != null && nucleicAcidCodes.size() == 3 ? translateByDNAOrRNA(nucleicAcidCodes) : null;
    }

    private static AminoAcidCode translateByDNAOrRNA(List<NucleicAcidCode> nucleicAcidCodes)
    {
        return dnaCodonMap.getOrDefault(nucleicAcidCodes, rnaCodonMap.get(nucleicAcidCodes));
    }

}
