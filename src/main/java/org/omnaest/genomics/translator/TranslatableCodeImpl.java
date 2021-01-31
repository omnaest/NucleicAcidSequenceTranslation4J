/*
 * Copyright 2017 Danny Kunz Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package org.omnaest.genomics.translator;

import org.omnaest.genomics.translator.domain.AminoAcidCode;
import org.omnaest.genomics.translator.domain.CodeAndPosition;
import org.omnaest.genomics.translator.domain.NucleicAcidCode;

public class TranslatableCodeImpl implements TranslatableCode
{
    private Character                 code;
    private long                      position;
    private InvalidTranslationHandler handler = tc ->
                                              {
                                              };

    @Override
    public TranslatableCode withInvalidTranslationHandler(InvalidTranslationHandler handler)
    {
        this.handler = handler;
        return this;
    }

    public TranslatableCodeImpl(Character code, long position)
    {
        super();
        this.code = code;
        this.position = position;
    }

    @Override
    public NucleicAcidCode asNucleicAcidCode()
    {
        NucleicAcidCode retval = NucleicAcidCode.valueOf(this.code);

        if (retval == null)
        {
            this.handler.accept(this);
        }

        return retval;
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.code);
    }

    @Override
    public AminoAcidCode asAminoAcidCode()
    {
        AminoAcidCode retval = AminoAcidCode.valueOf(this.code);

        if (retval == null)
        {
            this.handler.accept(this);
        }

        return retval;
    }

    @Override
    public Character getRawCode()
    {
        return this.code;
    }

    @Override
    public CodeAndPosition<NucleicAcidCode> asNucleicAcidCodeAndPosition()
    {
        return new CodeAndPosition<>(this.asNucleicAcidCode(), this.position);
    }

    @Override
    public CodeAndPosition<AminoAcidCode> asAminoAcidCodeAndPosition()
    {
        return new CodeAndPosition<>(this.asAminoAcidCode(), this.position);
    }

}
