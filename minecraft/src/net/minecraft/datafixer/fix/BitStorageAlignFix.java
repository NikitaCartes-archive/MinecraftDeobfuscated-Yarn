package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.stream.LongStream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.MathHelper;

public class BitStorageAlignFix extends DataFix {
	private static final int ELEMENT_BIT_SHIFT = 6;
	private static final int CHUNK_WIDTH = 16;
	private static final int CHUNK_LENGTH = 16;
	private static final int MAX_BLOCK_STATE_ID = 4096;
	private static final int HEIGHT_VALUE_BITS = 9;
	private static final int MAX_HEIGHT_VALUE = 256;

	public BitStorageAlignFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = type.findFieldType("Level");
		OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type2);
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("Sections");
		Type<?> type3 = ((ListType)opticFinder2.type()).getElement();
		OpticFinder<?> opticFinder3 = DSL.typeFinder(type3);
		Type<Pair<String, Dynamic<?>>> type4 = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
		OpticFinder<List<Pair<String, Dynamic<?>>>> opticFinder4 = DSL.fieldFinder("Palette", DSL.list(type4));
		return this.fixTypeEverywhereTyped(
			"BitStorageAlignFix",
			type,
			this.getOutputSchema().getType(TypeReferences.CHUNK),
			chunk -> chunk.updateTyped(opticFinder, level -> this.fixHeightmaps(fixLevel(opticFinder2, opticFinder3, opticFinder4, level)))
		);
	}

	private Typed<?> fixHeightmaps(Typed<?> fixedLevel) {
		return fixedLevel.update(
			DSL.remainderFinder(),
			levelDynamic -> levelDynamic.update(
					"Heightmaps",
					heightmapsDynamic -> heightmapsDynamic.updateMapValues(
							heightmap -> heightmap.mapSecond(heightmapDynamic -> fixBitStorageArray(levelDynamic, heightmapDynamic, 256, 9))
						)
				)
		);
	}

	private static Typed<?> fixLevel(
		OpticFinder<?> levelSectionsFinder, OpticFinder<?> sectionFinder, OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder, Typed<?> level
	) {
		return level.updateTyped(
			levelSectionsFinder,
			levelSection -> levelSection.updateTyped(
					sectionFinder,
					section -> {
						int i = (Integer)section.getOptional(paletteFinder).map(palette -> Math.max(4, DataFixUtils.ceillog2(palette.size()))).orElse(0);
						return i != 0 && !MathHelper.isPowerOfTwo(i)
							? section.update(
								DSL.remainderFinder(),
								sectionDynamic -> sectionDynamic.update("BlockStates", statesDynamic -> fixBitStorageArray(sectionDynamic, statesDynamic, 4096, i))
							)
							: section;
					}
				)
		);
	}

	private static Dynamic<?> fixBitStorageArray(Dynamic<?> sectionDynamic, Dynamic<?> statesDynamic, int maxValue, int elementBits) {
		long[] ls = statesDynamic.asLongStream().toArray();
		long[] ms = resizePackedIntArray(maxValue, elementBits, ls);
		return sectionDynamic.createLongList(LongStream.of(ms));
	}

	public static long[] resizePackedIntArray(int maxValue, int elementBits, long[] elements) {
		int i = elements.length;
		if (i == 0) {
			return elements;
		} else {
			long l = (1L << elementBits) - 1L;
			int j = 64 / elementBits;
			int k = (maxValue + j - 1) / j;
			long[] ls = new long[k];
			int m = 0;
			int n = 0;
			long o = 0L;
			int p = 0;
			long q = elements[0];
			long r = i > 1 ? elements[1] : 0L;

			for (int s = 0; s < maxValue; s++) {
				int t = s * elementBits;
				int u = t >> 6;
				int v = (s + 1) * elementBits - 1 >> 6;
				int w = t ^ u << 6;
				if (u != p) {
					q = r;
					r = u + 1 < i ? elements[u + 1] : 0L;
					p = u;
				}

				long x;
				if (u == v) {
					x = q >>> w & l;
				} else {
					int y = 64 - w;
					x = (q >>> w | r << y) & l;
				}

				int y = n + elementBits;
				if (y >= 64) {
					ls[m++] = o;
					o = x;
					n = elementBits;
				} else {
					o |= x << n;
					n = y;
				}
			}

			if (o != 0L) {
				ls[m] = o;
			}

			return ls;
		}
	}
}
