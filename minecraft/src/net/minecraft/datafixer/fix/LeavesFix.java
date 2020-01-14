package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.PackedIntegerArray;

public class LeavesFix extends DataFix {
	private static final int[][] field_5687 = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
	private static final Object2IntMap<String> field_5688 = DataFixUtils.make(new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> {
		object2IntOpenHashMap.put("minecraft:acacia_leaves", 0);
		object2IntOpenHashMap.put("minecraft:birch_leaves", 1);
		object2IntOpenHashMap.put("minecraft:dark_oak_leaves", 2);
		object2IntOpenHashMap.put("minecraft:jungle_leaves", 3);
		object2IntOpenHashMap.put("minecraft:oak_leaves", 4);
		object2IntOpenHashMap.put("minecraft:spruce_leaves", 5);
	});
	private static final Set<String> field_5686 = ImmutableSet.of(
		"minecraft:acacia_bark",
		"minecraft:birch_bark",
		"minecraft:dark_oak_bark",
		"minecraft:jungle_bark",
		"minecraft:oak_bark",
		"minecraft:spruce_bark",
		"minecraft:acacia_log",
		"minecraft:birch_log",
		"minecraft:dark_oak_log",
		"minecraft:jungle_log",
		"minecraft:oak_log",
		"minecraft:spruce_log",
		"minecraft:stripped_acacia_log",
		"minecraft:stripped_birch_log",
		"minecraft:stripped_dark_oak_log",
		"minecraft:stripped_jungle_log",
		"minecraft:stripped_oak_log",
		"minecraft:stripped_spruce_log"
	);

	public LeavesFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
		OpticFinder<?> opticFinder = type.findField("Level");
		OpticFinder<?> opticFinder2 = opticFinder.type().findField("Sections");
		Type<?> type2 = opticFinder2.type();
		if (!(type2 instanceof ListType)) {
			throw new IllegalStateException("Expecting sections to be a list.");
		} else {
			Type<?> type3 = ((ListType)type2).getElement();
			OpticFinder<?> opticFinder3 = DSL.typeFinder(type3);
			return this.fixTypeEverywhereTyped(
				"Leaves fix",
				type,
				typed -> typed.updateTyped(
						opticFinder,
						typedx -> {
							int[] is = new int[]{0};
							Typed<?> typed2 = typedx.updateTyped(
								opticFinder2,
								typedxx -> {
									Int2ObjectMap<LeavesFix.class_1192> int2ObjectMap = new Int2ObjectOpenHashMap<>(
										(Map<? extends Integer, ? extends LeavesFix.class_1192>)typedxx.getAllTyped(opticFinder3)
											.stream()
											.map(typedxxx -> new LeavesFix.class_1192(typedxxx, this.getInputSchema()))
											.collect(Collectors.toMap(LeavesFix.ListFixer::method_5077, arg -> arg))
									);
									if (int2ObjectMap.values().stream().allMatch(LeavesFix.ListFixer::method_5079)) {
										return typedxx;
									} else {
										List<IntSet> list = Lists.<IntSet>newArrayList();

										for (int i = 0; i < 7; i++) {
											list.add(new IntOpenHashSet());
										}

										for (LeavesFix.class_1192 lv : int2ObjectMap.values()) {
											if (!lv.method_5079()) {
												for (int j = 0; j < 4096; j++) {
													int k = lv.method_5075(j);
													if (lv.method_5068(k)) {
														((IntSet)list.get(0)).add(lv.method_5077() << 12 | j);
													} else if (lv.method_5071(k)) {
														int l = this.method_5052(j);
														int m = this.method_5050(j);
														is[0] |= method_5061(l == 0, l == 15, m == 0, m == 15);
													}
												}
											}
										}

										for (int i = 1; i < 7; i++) {
											IntSet intSet = (IntSet)list.get(i - 1);
											IntSet intSet2 = (IntSet)list.get(i);
											IntIterator intIterator = intSet.iterator();

											while (intIterator.hasNext()) {
												int l = intIterator.nextInt();
												int m = this.method_5052(l);
												int n = this.method_5062(l);
												int o = this.method_5050(l);

												for (int[] js : field_5687) {
													int p = m + js[0];
													int q = n + js[1];
													int r = o + js[2];
													if (p >= 0 && p <= 15 && r >= 0 && r <= 15 && q >= 0 && q <= 255) {
														LeavesFix.class_1192 lv2 = int2ObjectMap.get(q >> 4);
														if (lv2 != null && !lv2.method_5079()) {
															int s = method_5051(p, q & 15, r);
															int t = lv2.method_5075(s);
															if (lv2.method_5071(t)) {
																int u = lv2.method_5065(t);
																if (u > i) {
																	lv2.method_5070(s, t, i);
																	intSet2.add(method_5051(p, q, r));
																}
															}
														}
													}
												}
											}
										}

										return typedxx.updateTyped(opticFinder3, typedxxx -> int2ObjectMap.get(typedxxx.get(DSL.remainderFinder()).get("Y").asInt(0)).method_5083(typedxxx));
									}
								}
							);
							if (is[0] != 0) {
								typed2 = typed2.update(DSL.remainderFinder(), dynamic -> {
									Dynamic<?> dynamic2 = DataFixUtils.orElse(dynamic.get("UpgradeData").get(), dynamic.emptyMap());
									return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.get("Sides").asByte((byte)0) | is[0]))));
								});
							}

							return typed2;
						}
					)
			);
		}
	}

	public static int method_5051(int i, int j, int k) {
		return j << 8 | k << 4 | i;
	}

	private int method_5052(int i) {
		return i & 15;
	}

	private int method_5062(int i) {
		return i >> 8 & 0xFF;
	}

	private int method_5050(int i) {
		return i >> 4 & 15;
	}

	public static int method_5061(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		int i = 0;
		if (bl3) {
			if (bl2) {
				i |= 2;
			} else if (bl) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (bl4) {
			if (bl) {
				i |= 32;
			} else if (bl2) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (bl2) {
			i |= 4;
		} else if (bl) {
			i |= 64;
		}

		return i;
	}

	public abstract static class ListFixer {
		private final Type<Pair<String, Dynamic<?>>> field_5695 = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
		protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_5693 = DSL.fieldFinder("Palette", DSL.list(this.field_5695));
		protected final List<Dynamic<?>> data;
		protected final int field_5694;
		@Nullable
		protected PackedIntegerArray field_5696;

		public ListFixer(Typed<?> typed, Schema schema) {
			if (!Objects.equals(schema.getType(TypeReferences.BLOCK_STATE), this.field_5695)) {
				throw new IllegalStateException("Block state type is not what was expected.");
			} else {
				Optional<List<Pair<String, Dynamic<?>>>> optional = typed.getOptional(this.field_5693);
				this.data = (List<Dynamic<?>>)optional.map(list -> (List)list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse(ImmutableList.of());
				Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
				this.field_5694 = dynamic.get("Y").asInt(0);
				this.method_5074(dynamic);
			}
		}

		protected void method_5074(Dynamic<?> dynamic) {
			if (this.needsFix()) {
				this.field_5696 = null;
			} else {
				long[] ls = ((LongStream)dynamic.get("BlockStates").asLongStreamOpt().get()).toArray();
				int i = Math.max(4, DataFixUtils.ceillog2(this.data.size()));
				this.field_5696 = new PackedIntegerArray(i, 4096, ls);
			}
		}

		public Typed<?> method_5083(Typed<?> typed) {
			return this.method_5079()
				? typed
				: typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.field_5696.getStorage()))))
					.set(
						this.field_5693,
						(List<Pair<String, Dynamic<?>>>)this.data.stream().map(dynamic -> Pair.of(TypeReferences.BLOCK_STATE.typeName(), dynamic)).collect(Collectors.toList())
					);
		}

		public boolean method_5079() {
			return this.field_5696 == null;
		}

		public int method_5075(int i) {
			return this.field_5696.get(i);
		}

		protected int method_5082(String string, boolean bl, int i) {
			return LeavesFix.field_5688.get(string) << 5 | (bl ? 16 : 0) | i;
		}

		int method_5077() {
			return this.field_5694;
		}

		protected abstract boolean needsFix();
	}

	public static final class class_1192 extends LeavesFix.ListFixer {
		@Nullable
		private IntSet field_5689;
		@Nullable
		private IntSet field_5691;
		@Nullable
		private Int2IntMap field_5690;

		public class_1192(Typed<?> typed, Schema schema) {
			super(typed, schema);
		}

		@Override
		protected boolean needsFix() {
			this.field_5689 = new IntOpenHashSet();
			this.field_5691 = new IntOpenHashSet();
			this.field_5690 = new Int2IntOpenHashMap();

			for (int i = 0; i < this.data.size(); i++) {
				Dynamic<?> dynamic = (Dynamic<?>)this.data.get(i);
				String string = dynamic.get("Name").asString("");
				if (LeavesFix.field_5688.containsKey(string)) {
					boolean bl = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");
					this.field_5689.add(i);
					this.field_5690.put(this.method_5082(string, bl, 7), i);
					this.data.set(i, this.method_5072(dynamic, string, bl, 7));
				}

				if (LeavesFix.field_5686.contains(string)) {
					this.field_5691.add(i);
				}
			}

			return this.field_5689.isEmpty() && this.field_5691.isEmpty();
		}

		private Dynamic<?> method_5072(Dynamic<?> dynamic, String string, boolean bl, int i) {
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			dynamic2 = dynamic2.set("persistent", dynamic2.createString(bl ? "true" : "false"));
			dynamic2 = dynamic2.set("distance", dynamic2.createString(Integer.toString(i)));
			Dynamic<?> dynamic3 = dynamic.emptyMap();
			dynamic3 = dynamic3.set("Properties", dynamic2);
			return dynamic3.set("Name", dynamic3.createString(string));
		}

		public boolean method_5068(int i) {
			return this.field_5691.contains(i);
		}

		public boolean method_5071(int i) {
			return this.field_5689.contains(i);
		}

		private int method_5065(int i) {
			return this.method_5068(i) ? 0 : Integer.parseInt(((Dynamic)this.data.get(i)).get("Properties").get("distance").asString(""));
		}

		private void method_5070(int i, int j, int k) {
			Dynamic<?> dynamic = (Dynamic<?>)this.data.get(j);
			String string = dynamic.get("Name").asString("");
			boolean bl = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true");
			int l = this.method_5082(string, bl, k);
			if (!this.field_5690.containsKey(l)) {
				int m = this.data.size();
				this.field_5689.add(m);
				this.field_5690.put(l, m);
				this.data.add(this.method_5072(dynamic, string, bl, k));
			}

			int m = this.field_5690.get(l);
			if (1 << this.field_5696.getElementBits() <= m) {
				PackedIntegerArray packedIntegerArray = new PackedIntegerArray(this.field_5696.getElementBits() + 1, 4096);

				for (int n = 0; n < 4096; n++) {
					packedIntegerArray.set(n, this.field_5696.get(n));
				}

				this.field_5696 = packedIntegerArray;
			}

			this.field_5696.set(i, m);
		}
	}
}