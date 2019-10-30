/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class Heightmap {
    private static final Predicate<BlockState> ALWAYS_TRUE = blockState -> !blockState.isAir();
    private static final Predicate<BlockState> SUFFOCATES = blockState -> blockState.getMaterial().blocksMovement();
    private final PackedIntegerArray storage = new PackedIntegerArray(9, 256);
    private final Predicate<BlockState> blockPredicate;
    private final Chunk chunk;

    public Heightmap(Chunk chunk, Type type) {
        this.blockPredicate = type.getBlockPredicate();
        this.chunk = chunk;
    }

    public static void populateHeightmaps(Chunk chunk, Set<Type> set) {
        int i = set.size();
        ObjectArrayList objectList = new ObjectArrayList(i);
        Iterator objectListIterator = objectList.iterator();
        int j = chunk.getHighestNonEmptySectionYOffset() + 16;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int k = 0; k < 16; ++k) {
                block10: for (int l = 0; l < 16; ++l) {
                    for (Type type : set) {
                        objectList.add(chunk.getHeightmap(type));
                    }
                    for (int m = j - 1; m >= 0; --m) {
                        pooledMutable.method_10113(k, m, l);
                        BlockState blockState = chunk.getBlockState(pooledMutable);
                        if (blockState.getBlock() == Blocks.AIR) continue;
                        while (objectListIterator.hasNext()) {
                            Heightmap heightmap = (Heightmap)objectListIterator.next();
                            if (!heightmap.blockPredicate.test(blockState)) continue;
                            heightmap.set(k, l, m + 1);
                            objectListIterator.remove();
                        }
                        if (objectList.isEmpty()) continue block10;
                        objectListIterator.back(i);
                    }
                }
            }
        }
    }

    public boolean trackUpdate(int i, int j, int k, BlockState blockState) {
        int l = this.get(i, k);
        if (j <= l - 2) {
            return false;
        }
        if (this.blockPredicate.test(blockState)) {
            if (j >= l) {
                this.set(i, k, j + 1);
                return true;
            }
        } else if (l - 1 == j) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int m = j - 1; m >= 0; --m) {
                mutable.set(i, m, k);
                if (!this.blockPredicate.test(this.chunk.getBlockState(mutable))) continue;
                this.set(i, k, m + 1);
                return true;
            }
            this.set(i, k, 0);
            return true;
        }
        return false;
    }

    public int get(int i, int j) {
        return this.get(Heightmap.toIndex(i, j));
    }

    private int get(int i) {
        return this.storage.get(i);
    }

    private void set(int i, int j, int k) {
        this.storage.set(Heightmap.toIndex(i, j), k);
    }

    public void setTo(long[] ls) {
        System.arraycopy(ls, 0, this.storage.getStorage(), 0, ls.length);
    }

    public long[] asLongArray() {
        return this.storage.getStorage();
    }

    private static int toIndex(int i, int j) {
        return i + j * 16;
    }

    static /* synthetic */ Predicate method_16683() {
        return ALWAYS_TRUE;
    }

    static /* synthetic */ Predicate method_16681() {
        return SUFFOCATES;
    }

    public static enum Type {
        WORLD_SURFACE_WG("WORLD_SURFACE_WG", Purpose.WORLDGEN, Heightmap.method_16683()),
        WORLD_SURFACE("WORLD_SURFACE", Purpose.CLIENT, Heightmap.method_16683()),
        OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Purpose.WORLDGEN, Heightmap.method_16681()),
        OCEAN_FLOOR("OCEAN_FLOOR", Purpose.LIVE_WORLD, Heightmap.method_16681()),
        MOTION_BLOCKING("MOTION_BLOCKING", Purpose.CLIENT, blockState -> blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()),
        MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Purpose.LIVE_WORLD, blockState -> (blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock));

        private final String name;
        private final Purpose purpose;
        private final Predicate<BlockState> blockPredicate;
        private static final Map<String, Type> BY_NAME;

        private Type(String string2, Purpose purpose, Predicate<BlockState> predicate) {
            this.name = string2;
            this.purpose = purpose;
            this.blockPredicate = predicate;
        }

        public String getName() {
            return this.name;
        }

        public boolean shouldSendToClient() {
            return this.purpose == Purpose.CLIENT;
        }

        @Environment(value=EnvType.CLIENT)
        public boolean isStoredServerSide() {
            return this.purpose != Purpose.WORLDGEN;
        }

        public static Type byName(String string) {
            return BY_NAME.get(string);
        }

        public Predicate<BlockState> getBlockPredicate() {
            return this.blockPredicate;
        }

        static {
            BY_NAME = Util.create(Maps.newHashMap(), hashMap -> {
                for (Type type : Type.values()) {
                    hashMap.put(type.name, type);
                }
            });
        }
    }

    public static enum Purpose {
        WORLDGEN,
        LIVE_WORLD,
        CLIENT;

    }
}

