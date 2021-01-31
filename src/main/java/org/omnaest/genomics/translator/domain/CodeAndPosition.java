package org.omnaest.genomics.translator.domain;

public class CodeAndPosition<C>
{
    private C    code;
    private long position;

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