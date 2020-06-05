/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.WordPackedArray;
import org.jetbrains.annotations.Nullable;

public class LeavesFix
extends DataFix {
    private static final int[][] field_5687 = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
    private static final Object2IntMap<String> LEAVES_MAP = DataFixUtils.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
        object2IntOpenHashMap.put("minecraft:acacia_leaves", 0);
        object2IntOpenHashMap.put("minecraft:birch_leaves", 1);
        object2IntOpenHashMap.put("minecraft:dark_oak_leaves", 2);
        object2IntOpenHashMap.put("minecraft:jungle_leaves", 3);
        object2IntOpenHashMap.put("minecraft:oak_leaves", 4);
        object2IntOpenHashMap.put("minecraft:spruce_leaves", 5);
    });
    private static final Set<String> LOGS_MAP = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

    public LeavesFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        OpticFinder<?> opticFinder = type.findField("Level");
        OpticFinder<?> opticFinder2 = opticFinder.type().findField("Sections");
        Type<?> type2 = opticFinder2.type();
        if (!(type2 instanceof List.ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
        }
        Type type3 = ((List.ListType)type2).getElement();
        OpticFinder opticFinder3 = DSL.typeFinder(type3);
        return this.fixTypeEverywhereTyped("Leaves fix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> {
            int[] is = new int[]{0};
            Typed<?> typed22 = typed.updateTyped(opticFinder2, typed2 -> {
                int m;
                int l;
                Int2ObjectOpenHashMap<LeavesLogFixer> int2ObjectMap = new Int2ObjectOpenHashMap<LeavesLogFixer>(typed2.getAllTyped(opticFinder3).stream().map(typed -> new LeavesLogFixer((Typed<?>)typed, this.getInputSchema())).collect(Collectors.toMap(ListFixer::method_5077, leavesLogFixer -> leavesLogFixer)));
                if (int2ObjectMap.values().stream().allMatch(ListFixer::isFixed)) {
                    return typed2;
                }
                ArrayList<IntOpenHashSet> list = Lists.newArrayList();
                for (int i = 0; i < 7; ++i) {
                    list.add(new IntOpenHashSet());
                }
                for (LeavesLogFixer leavesLogFixer2 : int2ObjectMap.values()) {
                    if (leavesLogFixer2.isFixed()) continue;
                    for (int j = 0; j < 4096; ++j) {
                        int k = leavesLogFixer2.needsFix(j);
                        if (leavesLogFixer2.isLog(k)) {
                            ((IntSet)list.get(0)).add(leavesLogFixer2.method_5077() << 12 | j);
                            continue;
                        }
                        if (!leavesLogFixer2.isLeaf(k)) continue;
                        l = this.method_5052(j);
                        m = this.method_5050(j);
                        is[0] = is[0] | LeavesFix.method_5061(l == 0, l == 15, m == 0, m == 15);
                    }
                }
                for (int i = 1; i < 7; ++i) {
                    IntSet intSet = (IntSet)list.get(i - 1);
                    IntSet intSet2 = (IntSet)list.get(i);
                    IntIterator intIterator = intSet.iterator();
                    while (intIterator.hasNext()) {
                        l = intIterator.nextInt();
                        m = this.method_5052(l);
                        int n = this.method_5062(l);
                        int o = this.method_5050(l);
                        for (int[] js : field_5687) {
                            int u;
                            int s;
                            int t;
                            LeavesLogFixer leavesLogFixer2;
                            int p = m + js[0];
                            int q = n + js[1];
                            int r = o + js[2];
                            if (p < 0 || p > 15 || r < 0 || r > 15 || q < 0 || q > 255 || (leavesLogFixer2 = (LeavesLogFixer)int2ObjectMap.get(q >> 4)) == null || leavesLogFixer2.isFixed() || !leavesLogFixer2.isLeaf(t = leavesLogFixer2.needsFix(s = LeavesFix.method_5051(p, q & 0xF, r))) || (u = leavesLogFixer2.getDistanceToLog(t)) <= i) continue;
                            leavesLogFixer2.computeLeafStates(s, t, i);
                            intSet2.add(LeavesFix.method_5051(p, q, r));
                        }
                    }
                }
                return typed2.updateTyped(opticFinder3, typed -> ((LeavesLogFixer)int2ObjectMap.get(typed.get(DSL.remainderFinder()).get("Y").asInt(0))).method_5083((Typed<?>)typed));
            });
            if (is[0] != 0) {
                typed22 = typed22.update(DSL.remainderFinder(), dynamic -> {
                    Dynamic dynamic2 = DataFixUtils.orElse(dynamic.get("UpgradeData").result(), dynamic.emptyMap());
                    return dynamic.set("UpgradeData", dynamic2.set("Sides", dynamic.createByte((byte)(dynamic2.get("Sides").asByte((byte)0) | is[0]))));
                });
            }
            return typed22;
        }));
    }

    public static int method_5051(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private int method_5052(int i) {
        return i & 0xF;
    }

    private int method_5062(int i) {
        return i >> 8 & 0xFF;
    }

    private int method_5050(int i) {
        return i >> 4 & 0xF;
    }

    public static int method_5061(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = 0;
        if (bl3) {
            i = bl2 ? (i |= 2) : (bl ? (i |= 0x80) : (i |= 1));
        } else if (bl4) {
            i = bl ? (i |= 0x20) : (bl2 ? (i |= 8) : (i |= 0x10));
        } else if (bl2) {
            i |= 4;
        } else if (bl) {
            i |= 0x40;
        }
        return i;
    }

    public static final class LeavesLogFixer
    extends ListFixer {
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
            for (int i = 0; i < this.properties.size(); ++i) {
                Dynamic dynamic = (Dynamic)this.properties.get(i);
                String string = dynamic.get("Name").asString("");
                if (LEAVES_MAP.containsKey(string)) {
                    boolean bl = Objects.equals(dynamic.get("Properties").get("decayable").asString(""), "false");
                    this.leafIndices.add(i);
                    this.leafStates.put(this.computeFlags(string, bl, 7), i);
                    this.properties.set(i, this.createLeafProperties(dynamic, string, bl, 7));
                }
                if (!LOGS_MAP.contains(string)) continue;
                this.logIndices.add(i);
            }
            return this.leafIndices.isEmpty() && this.logIndices.isEmpty();
        }

        private Dynamic<?> createLeafProperties(Dynamic<?> dynamic, String string, boolean bl, int i) {
            Dynamic dynamic2 = dynamic.emptyMap();
            dynamic2 = dynamic2.set("persistent", dynamic2.createString(bl ? "true" : "false"));
            dynamic2 = dynamic2.set("distance", dynamic2.createString(Integer.toString(i)));
            Dynamic dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Properties", dynamic2);
            dynamic3 = dynamic3.set("Name", dynamic3.createString(string));
            return dynamic3;
        }

        public boolean isLog(int i) {
            return this.logIndices.contains(i);
        }

        public boolean isLeaf(int i) {
            return this.leafIndices.contains(i);
        }

        private int getDistanceToLog(int i) {
            if (this.isLog(i)) {
                return 0;
            }
            return Integer.parseInt(((Dynamic)this.properties.get(i)).get("Properties").get("distance").asString(""));
        }

        private void computeLeafStates(int i, int j, int k) {
            int m;
            boolean bl;
            Dynamic dynamic = (Dynamic)this.properties.get(j);
            String string = dynamic.get("Name").asString("");
            int l = this.computeFlags(string, bl = Objects.equals(dynamic.get("Properties").get("persistent").asString(""), "true"), k);
            if (!this.leafStates.containsKey(l)) {
                m = this.properties.size();
                this.leafIndices.add(m);
                this.leafStates.put(l, m);
                this.properties.add(this.createLeafProperties(dynamic, string, bl, k));
            }
            m = this.leafStates.get(l);
            if (1 << this.blockStateMap.getUnitSize() <= m) {
                WordPackedArray wordPackedArray = new WordPackedArray(this.blockStateMap.getUnitSize() + 1, 4096);
                for (int n = 0; n < 4096; ++n) {
                    wordPackedArray.set(n, this.blockStateMap.get(n));
                }
                this.blockStateMap = wordPackedArray;
            }
            this.blockStateMap.set(i, m);
        }
    }

    public static abstract class ListFixer {
        private final Type<Pair<String, Dynamic<?>>> field_5695 = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
        protected final OpticFinder<List<Pair<String, Dynamic<?>>>> field_5693 = DSL.fieldFinder("Palette", DSL.list(this.field_5695));
        protected final List<Dynamic<?>> properties;
        protected final int field_5694;
        @Nullable
        protected WordPackedArray blockStateMap;

        public ListFixer(Typed<?> typed, Schema schema) {
            if (!Objects.equals(schema.getType(TypeReferences.BLOCK_STATE), this.field_5695)) {
                throw new IllegalStateException("Block state type is not what was expected.");
            }
            Optional<List<Pair<String, Dynamic<?>>>> optional = typed.getOptional(this.field_5693);
            this.properties = optional.map(list -> list.stream().map(Pair::getSecond).collect(Collectors.toList())).orElse(ImmutableList.of());
            Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
            this.field_5694 = dynamic.get("Y").asInt(0);
            this.computeFixableBlockStates(dynamic);
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
            if (this.isFixed()) {
                return typed;
            }
            return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.blockStateMap.getAlignedArray())))).set(this.field_5693, this.properties.stream().map(dynamic -> Pair.of(TypeReferences.BLOCK_STATE.typeName(), dynamic)).collect(Collectors.toList()));
        }

        public boolean isFixed() {
            return this.blockStateMap == null;
        }

        public int needsFix(int i) {
            return this.blockStateMap.get(i);
        }

        protected int computeFlags(String leafBlockName, boolean persistent, int i) {
            return LEAVES_MAP.get(leafBlockName) << 5 | (persistent ? 16 : 0) | i;
        }

        int method_5077() {
            return this.field_5694;
        }

        protected abstract boolean needsFix();
    }
}

