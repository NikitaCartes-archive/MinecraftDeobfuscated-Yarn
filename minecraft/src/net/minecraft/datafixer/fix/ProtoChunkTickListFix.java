package net.minecraft.datafixer.fix;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.mutable.MutableInt;

public class ProtoChunkTickListFix extends DataFix {
	private static final int field_35446 = 16;
	private static final ImmutableSet<String> ALWAYS_WATERLOGGED_BLOCK_IDS = ImmutableSet.of(
		"minecraft:bubble_column", "minecraft:kelp", "minecraft:kelp_plant", "minecraft:seagrass", "minecraft:tall_seagrass"
	);

	public ProtoChunkTickListFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		OpticFinder<?> opticFinder = type.findField("Level");
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("Sections");
		OpticFinder<?> opticFinder3 = ((ListType)opticFinder2.type()).getElement().finder();
		OpticFinder<?> opticFinder4 = opticFinder3.type().findField("block_states");
		OpticFinder<?> opticFinder5 = opticFinder3.type().findField("biomes");
		OpticFinder<?> opticFinder6 = opticFinder4.type().findField("palette");
		OpticFinder<?> opticFinder7 = opticFinder.type().findField("TileTicks");
		return this.fixTypeEverywhereTyped(
			"ChunkProtoTickListFix",
			type,
			typed -> typed.updateTyped(
					opticFinder,
					typedx -> {
						typedx = typedx.update(
							DSL.remainderFinder(),
							dynamicx -> DataFixUtils.orElse(
									dynamicx.get("LiquidTicks").result().map(dynamic2x -> dynamicx.set("fluid_ticks", dynamic2x).remove("LiquidTicks")), dynamicx
								)
						);
						Dynamic<?> dynamic = typedx.get(DSL.remainderFinder());
						MutableInt mutableInt = new MutableInt();
						Int2ObjectMap<Supplier<ProtoChunkTickListFix.class_6741>> int2ObjectMap = new Int2ObjectArrayMap<>();
						typedx.getOptionalTyped(opticFinder2)
							.ifPresent(
								typedxx -> typedxx.getAllTyped(opticFinder3)
										.forEach(
											typedxxx -> {
												Dynamic<?> dynamicx = typedxxx.get(DSL.remainderFinder());
												int ix = dynamicx.get("Y").asInt(Integer.MAX_VALUE);
												if (ix != Integer.MAX_VALUE) {
													if (typedxxx.getOptionalTyped(opticFinder5).isPresent()) {
														mutableInt.setValue(Math.min(ix, mutableInt.getValue()));
													}
						
													typedxxx.getOptionalTyped(opticFinder4)
														.ifPresent(
															typedxxxx -> int2ObjectMap.put(
																	ix,
																	Suppliers.memoize(
																		() -> {
																			List<? extends Dynamic<?>> list = (List<? extends Dynamic<?>>)typedxxxx.getOptionalTyped(opticFinder6)
																				.map(
																					typedxxxxxx -> (List)typedxxxxxx.write().result().map(dynamicxx -> dynamicxx.asList(Function.identity())).orElse(Collections.emptyList())
																				)
																				.orElse(Collections.emptyList());
																			long[] ls = typedxxxx.get(DSL.remainderFinder()).get("data").asLongStream().toArray();
																			return new ProtoChunkTickListFix.class_6741(list, ls);
																		}
																	)
																)
														);
												}
											}
										)
							);
						byte b = mutableInt.getValue().byteValue();
						typedx = typedx.update(DSL.remainderFinder(), dynamicx -> dynamicx.update("yPos", dynamicxx -> dynamicxx.createByte(b)));
						if (!typedx.getOptionalTyped(opticFinder7).isPresent() && !dynamic.get("fluid_ticks").result().isPresent()) {
							int i = dynamic.get("xPos").asInt(0);
							int j = dynamic.get("zPos").asInt(0);
							Dynamic<?> dynamic2 = this.fixToBeTicked(dynamic, int2ObjectMap, b, i, j, "LiquidsToBeTicked", ProtoChunkTickListFix::getFluidBlockIdToBeTicked);
							Dynamic<?> dynamic3 = this.fixToBeTicked(dynamic, int2ObjectMap, b, i, j, "ToBeTicked", ProtoChunkTickListFix::getBlockIdToBeTicked);
							Optional<? extends Pair<? extends Typed<?>, ?>> optional = opticFinder7.type().readTyped(dynamic3).result();
							if (optional.isPresent()) {
								typedx = typedx.set(opticFinder7, (Typed)((Pair)optional.get()).getFirst());
							}
		
							return typedx.update(DSL.remainderFinder(), dynamic2x -> dynamic2x.remove("ToBeTicked").remove("LiquidsToBeTicked").set("fluid_ticks", dynamic2));
						} else {
							return typedx;
						}
					}
				)
		);
	}

	private Dynamic<?> fixToBeTicked(
		Dynamic<?> dynamic,
		Int2ObjectMap<Supplier<ProtoChunkTickListFix.class_6741>> int2ObjectMap,
		byte b,
		int i,
		int j,
		String string,
		Function<Dynamic<?>, String> function
	) {
		Stream<Dynamic<?>> stream = Stream.empty();
		List<? extends Dynamic<?>> list = dynamic.get(string).asList(Function.identity());

		for (int k = 0; k < list.size(); k++) {
			int l = k + b;
			Supplier<ProtoChunkTickListFix.class_6741> supplier = int2ObjectMap.get(l);
			Stream<? extends Dynamic<?>> stream2 = ((Dynamic)list.get(k))
				.asStream()
				.mapToInt(dynamicx -> dynamicx.asShort((short)-1))
				.filter(ix -> ix > 0)
				.mapToObj(lx -> this.method_39255(dynamic, supplier, i, l, j, lx, function));
			stream = Stream.concat(stream, stream2);
		}

		return dynamic.createList(stream);
	}

	private static String getBlockIdToBeTicked(@Nullable Dynamic<?> dynamic) {
		return dynamic != null ? dynamic.get("Name").asString("minecraft:air") : "minecraft:air";
	}

	private static String getFluidBlockIdToBeTicked(@Nullable Dynamic<?> dynamic) {
		if (dynamic == null) {
			return "minecraft:empty";
		} else {
			String string = dynamic.get("Name").asString("");
			if ("minecraft:water".equals(string)) {
				return dynamic.get("Properties").get("level").asInt(0) == 0 ? "minecraft:water" : "minecraft:flowing_water";
			} else if ("minecraft:lava".equals(string)) {
				return dynamic.get("Properties").get("level").asInt(0) == 0 ? "minecraft:lava" : "minecraft:flowing_lava";
			} else {
				return !ALWAYS_WATERLOGGED_BLOCK_IDS.contains(string) && !dynamic.get("Properties").get("waterlogged").asBoolean(false)
					? "minecraft:empty"
					: "minecraft:water";
			}
		}
	}

	private Dynamic<?> method_39255(
		Dynamic<?> dynamic, @Nullable Supplier<ProtoChunkTickListFix.class_6741> supplier, int i, int j, int k, int l, Function<Dynamic<?>, String> function
	) {
		int m = l & 15;
		int n = l >>> 4 & 15;
		int o = l >>> 8 & 15;
		String string = (String)function.apply(supplier != null ? ((ProtoChunkTickListFix.class_6741)supplier.get()).method_39265(m, n, o) : null);
		return dynamic.createMap(
			ImmutableMap.builder()
				.put(dynamic.createString("i"), dynamic.createString(string))
				.put(dynamic.createString("x"), dynamic.createInt(i * 16 + m))
				.put(dynamic.createString("y"), dynamic.createInt(j * 16 + n))
				.put(dynamic.createString("z"), dynamic.createInt(k * 16 + o))
				.put(dynamic.createString("t"), dynamic.createInt(0))
				.put(dynamic.createString("p"), dynamic.createInt(0))
				.build()
		);
	}

	public static final class class_6741 {
		private static final long field_35448 = 4L;
		private final List<? extends Dynamic<?>> field_35449;
		private final long[] field_35450;
		private final int field_35451;
		private final long field_35452;
		private final int field_35453;

		public class_6741(List<? extends Dynamic<?>> list, long[] ls) {
			this.field_35449 = list;
			this.field_35450 = ls;
			this.field_35451 = Math.max(4, ChunkHeightAndBiomeFix.ceilLog2(list.size()));
			this.field_35452 = (1L << this.field_35451) - 1L;
			this.field_35453 = (char)(64 / this.field_35451);
		}

		@Nullable
		public Dynamic<?> method_39265(int i, int j, int k) {
			int l = this.field_35449.size();
			if (l < 1) {
				return null;
			} else if (l == 1) {
				return (Dynamic<?>)this.field_35449.get(0);
			} else {
				int m = this.method_39267(i, j, k);
				int n = m / this.field_35453;
				if (n >= 0 && n < this.field_35450.length) {
					long o = this.field_35450[n];
					int p = (m - n * this.field_35453) * this.field_35451;
					int q = (int)(o >> p & this.field_35452);
					return q >= 0 && q < l ? (Dynamic)this.field_35449.get(q) : null;
				} else {
					return null;
				}
			}
		}

		private int method_39267(int i, int j, int k) {
			return (j << 4 | k) << 4 | i;
		}

		public List<? extends Dynamic<?>> method_39264() {
			return this.field_35449;
		}

		public long[] method_39266() {
			return this.field_35450;
		}
	}
}
