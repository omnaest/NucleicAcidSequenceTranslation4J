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
package org.omnaest.genetics.translator.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class BitSetUtils
{
	public static BitSet valueOf(byte value)
	{
		return BitSet.valueOf(new byte[] { value });
	}

	public static BitSet valueOf(byte... values)
	{
		return BitSet.valueOf(values);
	}

	public static String toBinaryString(BitSet bitSet)
	{
		List<String> retvals = new ArrayList<>();
		for (byte value : bitSet.toByteArray())
		{
			String binaryString = Integer.toBinaryString(value);
			retvals.add(binaryString);
		}
		return StringUtils.join(retvals, "");
	}

	public static byte toByte(BitSet bitSet)
	{
		byte[] values = bitSet.toByteArray();
		return values.length > 0 ? values[0] : 0;
	}
}
