package org.omnaest.genetics.translator.domain;

import java.util.List;

public class CodeAndPositionAndSource<C, S> extends CodeAndPosition<C>
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