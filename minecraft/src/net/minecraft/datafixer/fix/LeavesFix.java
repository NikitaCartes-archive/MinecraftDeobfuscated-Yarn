package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.WordPackedArray;

public class LeavesFix extends DataFix {
	private static final int field_29886 = 128;
	private static final int field_29887 = 64;
	private static final int field_29888 = 32;
	private static final int field_29889 = 16;
	private static final int field_29890 = 8;
	private static final int field_29891 = 4;
	private static final int field_29892 = 2;
	private static final int field_29893 = 1;
	private static final int[][] AXIAL_OFFSETS = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
	private static final int field_29894 = 7;
	private static final int field_29895 = 12;
	private static final int field_29896 = 4096;
	static final Object2IntMap<String> LEAVES_MAP = DataFixUtils.make(new Object2IntOpenHashMap<>(), map -> {
		map.put("minecraft:acacia_leaves", 0);
		map.put("minecraft:birch_leaves", 1);
		map.put("minecraft:dark_oak_leaves", 2);
		map.put("minecraft:jungle_leaves", 3);
		map.put("minecraft:oak_leaves", 4);
		map.put("minecraft:spruce_leaves", 5);
	});
	static final Set<String> LOGS_MAP = ImmutableSet.of(
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
									Int2ObjectMap<LeavesFix.LeavesLogFixer> int2ObjectMap = new Int2ObjectOpenHashMap<>(
										(Map<? extends Integer, ? extends LeavesFix.LeavesLogFixer>)typedxx.getAllTyped(opticFinder3)
											.stream()
											.map(typedxxx -> new LeavesFix.LeavesLogFixer(typedxxx, this.getInputSchema()))
											.collect(Collectors.toMap(LeavesFix.ListFixer::getY, leavesLogFixer -> leavesLogFixer))
									);
									if (int2ObjectMap.values().stream().allMatch(LeavesFix.ListFixer::isFixed)) {
										return typedxx;
									} else {
										List<IntSet> list = Lists.<IntSet>newArrayList();
			
										for (int i = 0; i < 7; i++) {
											list.add(new IntOpenHashSet());
										}
			
										for (LeavesFix.LeavesLogFixer leavesLogFixer : int2ObjectMap.values()) {
											if (!leavesLogFixer.isFixed()) {
												for (int j = 0; j < 4096; j++) {
													int k = leavesLogFixer.needsFix(j);
													if (leavesLogFixer.isLog(k)) {
														((IntSet)list.get(0)).add(leavesLogFixer.getY() << 12 | j);
													} else if (leavesLogFixer.isLeaf(k)) {
														int l = this.getX(j);
														int m = this.getZ(j);
														is[0] |= getBoundaryClassBit(l == 0, l == 15, m == 0, m == 15);
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
												int m = this.getX(l);
												int n = this.getY(l);
												int o = this.getZ(l);
			
												for (int[] js : AXIAL_OFFSETS) {
													int p = m + js[0];
													int q = n + js[1];
													int r = o + js[2];
													if (p >= 0 && p <= 15 && r >= 0 && r <= 15 && q >= 0 && q <= 255) {
														LeavesFix.LeavesLogFixer leavesLogFixer2 = int2ObjectMap.get(q >> 4);
														if (leavesLogFixer2 != null && !leavesLogFixer2.isFixed()) {
															int s = packLocalPos(p, q & 15, r);
															int t = leavesLogFixer2.needsFix(s);
															if (leavesLogFixer2.isLeaf(t)) {
																int u = leavesLogFixer2.getDistanceToLog(t);
																if (u > i) {
																	leavesLogFixer2.computeLeafStates(s, t, i);
																	intSet2.add(packLocalPos(p, q, r));
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
									Dynamic<?> dynamic2 = DataFixUtils.orElse(dynamic.get("UpgradeData").result(), dynamic.emptyMap());
									return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.get("Sides").asByte((byte)0) | is[0]))));
								});
							}
		
							return typed2;
						}
					)
			);
		}
	}

	public static int packLocalPos(int localX, int localY, int localZ) {
		return localY << 8 | localZ << 4 | localX;
	}

	private int getX(int packedLocalPos) {
		return packedLocalPos & 15;
	}

	private int getY(int packedLocalPos) {
		return packedLocalPos >> 8 & 0xFF;
	}

	private int getZ(int packedLocalPos) {
		return packedLocalPos >> 4 & 15;
	}

	public static int getBoundaryClassBit(boolean westernmost, boolean easternmost, boolean northernmost, boolean southernmost) {
		int i = 0;
		if (northernmost) {
			if (easternmost) {
				i |= 2;
			} else if (westernmost) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (southernmost) {
			if (westernmost) {
				i |= 32;
			} else if (easternmost) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (easternmost) {
			i |= 4;
		} else if (westernmost) {
			i |= 64;
		}

		return i;
	}

	public static final class LeavesLogFixer extends LeavesFix.ListFixer {
		private static final String PERSISTENT = "persistent";
		private static final String DECAYABLE = "decayable";
		private static final String DISTANCE = "distance";
		@Nullable
		private IntSet leafIndices;
		@Nullable
		private IntSet logIndices;
		@Nullable
		private Int2IntMap leafStates;

		public LeavesLogFixer(Typed<?> typed, Schema schema) {
			super(typed, schema);
		}

		@Override
		protected boolean needsFix() {
			this.leafIndices = new IntOpenHashSet();
			this.logIndices = new IntOpenHashSet();
			this.leafStates = new Int2IntOpenHashMap();

			for (int i = 0; i < this.properties.size(); i++) {
				Dynamic<?> dynamic = (Dynamic<?>)this.properties.get(i);
				String string = dynamic.get("Name").asString("");
				if (LeavesFix.LEAVES_MAP.containsKey(string)) {
					boolean bl = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");
					this.leafIndices.add(i);
					this.leafStates.put(this.computeFlags(string, bl, 7), i);
					this.properties.set(i, this.createLeafProperties(dynamic, string, bl, 7));
				}

				if (LeavesFix.LOGS_MAP.contains(string)) {
					this.logIndices.add(i);
				}
			}

			return this.leafIndices.isEmpty() && this.logIndices.isEmpty();
		}

		private Dynamic<?> createLeafProperties(Dynamic<?> tag, String name, boolean persistent, int distance) {
			Dynamic<?> dynamic = tag.emptyMap();
			dynamic = dynamic.set("persistent", dynamic.createString(persistent ? "true" : "false"));
			dynamic = dynamic.set("distance", dynamic.createString(Integer.toString(distance)));
			Dynamic<?> dynamic2 = tag.emptyMap();
			dynamic2 = dynamic2.set("Properties", dynamic);
			return dynamic2.set("Name", dynamic2.createString(name));
		}

		public boolean isLog(int i) {
			return this.logIndices.contains(i);
		}

		public boolean isLeaf(int i) {
			return this.leafIndices.contains(i);
		}

		int getDistanceToLog(int i) {
			return this.isLog(i) ? 0 : Integer.parseInt(((Dynamic)this.properties.get(i)).get("Properties").get("distance").asString(""));
		}

		void computeLeafStates(int i, int j, int distance) {
			Dynamic<?> dynamic = (Dynamic<?>)this.properties.get(j);
			String string = dynamic.get("Name").asString("");
			boolean bl = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true");
			int k = this.computeFlags(string, bl, distance);
			if (!this.leafStates.containsKey(k)) {
				int l = this.properties.size();
				this.leafIndices.add(l);
				this.leafStates.put(k, l);
				this.properties.add(this.createLeafProperties(dynamic, string, bl, distance));
			}

			int l = this.leafStates.get(k);
			if (1 << this.blockStateMap.getUnitSize() <= l) {
				WordPackedArray wordPackedArray = new WordPackedArray(this.blockStateMap.getUnitSize() + 1, 4096);

				for (int m = 0; m < 4096; m++) {
					wordPackedArray.set(m, this.blockStateMap.get(m));
				}

				this.blockStateMap = wordPackedArray;
			}

			this.blockStateMap.set(i, l);
		}
	}

	public abstract static class ListFixer {
		protected static final String BLOCK_STATES_KEY = "BlockStates";
		protected static final String NAME_KEY = "Name";
		protected static final String PROPERTIES_KEY = "Properties";
		private final Type<Pair<String, Dynamic<?>>> blockStateType = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
		protected final OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder = DSL.fieldFinder("Palette", DSL.list(this.blockStateType));
		protected final List<Dynamic<?>> properties;
		protected final int y;
		@Nullable
		protected WordPackedArray blockStateMap;

		public ListFixer(Typed<?> typed, Schema schema) {
			if (!Objects.equals(schema.getType(TypeReferences.BLOCK_STATE), this.blockStateType)) {
				throw new IllegalStateException("Block state type is not what was expected.");
			} else {
				Optional<List<Pair<String, Dynamic<?>>>> optional = typed.getOptional(this.paletteFinder);
				this.properties = (List<Dynamic<?>>)optional.map(list -> (List)list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse(ImmutableList.of());
				Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
				this.y = dynamic.get("Y").asInt(0);
				this.computeFixableBlockStates(dynamic);
			}
		}

		protected void computeFixableBlockStates(Dynamic<?> dynamic) {
			if (this.needsFix()) {
				this.blockStateMap = null;
			} else {
				long[] ls = dynamic.get("BlockStates").asLongStream().toArray();
				int i = Math.max(4, DataFixUtils.ceillog2(this.properties.size()));
				this.blockStateMap = new WordPackedArray(i, 4096, ls);
			}
		}

		public Typed<?> method_5083(Typed<?> typed) {
			return this.isFixed()
				? typed
				: typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.blockStateMap.getAlignedArray()))))
					.set(
						this.paletteFinder,
						(List<Pair<String, Dynamic<?>>>)this.properties
							.stream()
							.map(dynamic -> Pair.of(TypeReferences.BLOCK_STATE.typeName(), dynamic))
							.collect(Collectors.toList())
					);
		}

		public boolean isFixed() {
			return this.blockStateMap == null;
		}

		public int needsFix(int index) {
			return this.blockStateMap.get(index);
		}

		protected int computeFlags(String leafBlockName, boolean persistent, int distance) {
			return LeavesFix.LEAVES_MAP.get(leafBlockName) << 5 | (persistent ? 16 : 0) | distance;
		}

		int getY() {
			return this.y;
		}

		protected abstract boolean needsFix();
	}
}
