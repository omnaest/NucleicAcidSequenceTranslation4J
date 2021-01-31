/*
 * Copyright 2017 Danny Kunz Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package org.omnaest.genomics.translator;

import java.util.function.Consumer;

import org.omnaest.genomics.translator.domain.AminoAcidCode;
import org.omnaest.genomics.translator.domain.CodeAndPosition;
import org.omnaest.genomics.translator.domain.NucleicAcidCode;

/**
 * Wrapper around a raw code which allows to translate the raw code into other representations like an {@link NucleicAcidCode} or {@link AminoAcidCode}
 *
 * @see #asAminoAcidCode()
 * @see #asNucleicAcidCode()
 * @author Omnaest
 */
public interface TranslatableCode
{
    public static interface InvalidTranslationHandler extends Consumer<TranslatableCode>
    {
    }

    /**
     * @see NucleicAcidCode
     * @return
     */
    public NucleicAcidCode asNucleicAcidCode();

    /**
     * @see AminoAcidCode
     * @return
     */
    public AminoAcidCode asAminoAcidCode();

    /**
     * Returns the raw {@link Character}
     * 
     * @return
     */
    public Character getRawCode();

    /**
     * Returns the {@link CodeAndPosition} of the {@link NucleicAcidCode}
     * 
     * @return
     */
    public CodeAndPosition<NucleicAcidCode> asNucleicAcidCodeAndPosition();

    /**
     * Defines a {@link InvalidTranslationHandler} which is called if a translation to an {@link NucleicAcidCode} or {@link AminoAcidCode} failed
     * 
     * @param handler
     * @return
     */
    public TranslatableCode withInvalidTranslationHandler(InvalidTranslationHandler handler);

    /**
     * Returns the {@link CodeAndPosition} of the {@link AminoAcidCode}
     * 
     * @return
     */
    public CodeAndPosition<AminoAcidCode> asAminoAcidCodeAndPosition();
}