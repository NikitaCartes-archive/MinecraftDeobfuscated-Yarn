package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class ChunkToProtoChunkFix extends DataFix {
	private static final int field_29881 = 16;

	public ChunkToProtoChunkFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
		Type<?> type3 = type.findFieldType("Level");
		Type<?> type4 = type2.findFieldType("Level");
		Type<?> type5 = type3.findFieldType("TileTicks");
		OpticFinder<?> opticFinder = DSL.fieldFinder("Level", type3);
		OpticFinder<?> opticFinder2 = DSL.fieldFinder("TileTicks", type5);
		return TypeRewriteRule.seq(
			this.fixTypeEverywhereTyped(
				"ChunkToProtoChunkFix",
				type,
				this.getOutputSchema().getType(TypeReferences.CHUNK),
				typed -> typed.updateTyped(
						opticFinder,
						type4,
						typedx -> {
							Optional<? extends Stream<? extends Dynamic<?>>> optional = typedx.getOptionalTyped(opticFinder2)
								.flatMap(typedxx -> typedxx.write().result())
								.flatMap(dynamicx -> dynamicx.asStreamOpt().result());
							Dynamic<?> dynamic = typedx.get(DSL.remainderFinder());
							boolean bl = dynamic.get("TerrainPopulated").asBoolean(false)
								&& (!dynamic.get("LightPopulated").asNumber().result().isPresent() || dynamic.get("LightPopulated").asBoolean(false));
							dynamic = dynamic.set("Status", dynamic.createString(bl ? "mobs_spawned" : "empty"));
							dynamic = dynamic.set("hasLegacyStructureData", dynamic.createBoolean(true));
							Dynamic<?> dynamic3;
							if (bl) {
								Optional<ByteBuffer> optional2 = dynamic.get("Biomes").asByteBufferOpt().result();
								if (optional2.isPresent()) {
									ByteBuffer byteBuffer = (ByteBuffer)optional2.get();
									int[] is = new int[256];

									for (int i = 0; i < is.length; i++) {
										if (i < byteBuffer.capacity()) {
											is[i] = byteBuffer.get(i) & 255;
										}
									}

									dynamic = dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(is)));
								}

								Dynamic<?> dynamic2 = dynamic;
								List<ShortList> list = (List<ShortList>)IntStream.range(0, 16).mapToObj(ix -> new ShortArrayList()).collect(Collectors.toList());
								if (optional.isPresent()) {
									((Stream)optional.get()).forEach(dynamicx -> {
										int ix = dynamicx.get("x").asInt(0);
										int j = dynamicx.get("y").asInt(0);
										int k = dynamicx.get("z").asInt(0);
										short s = method_15675(ix, j, k);
										((ShortList)list.get(j >> 4)).add(s);
									});
									dynamic = dynamic.set(
										"ToBeTicked", dynamic.createList(list.stream().map(shortList -> dynamic2.createList(shortList.stream().map(dynamic2::createShort))))
									);
								}

								dynamic3 = DataFixUtils.orElse(typedx.set(DSL.remainderFinder(), dynamic).write().result(), dynamic);
							} else {
								dynamic3 = dynamic;
							}

							return (Typed)((Pair)type4.readTyped(dynamic3).result().orElseThrow(() -> new IllegalStateException("Could not read the new chunk"))).getFirst();
						}
					)
			),
			this.writeAndRead(
				"Structure biome inject", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE)
			)
		);
	}

	private static short method_15675(int i, int j, int k) {
		return (short)(i & 15 | (j & 15) << 4 | (k & 15) << 8);
	}
}
