/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
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
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

public class Heightmap {
    private static final Predicate<BlockState> ALWAYS_TRUE = blockState -> !blockState.isAir();
    private static final Predicate<BlockState> SUFFOCATES = blockState -> blockState.getMaterial().blocksMovement();
    private final PackedIntegerArray storage;
    private final Predicate<BlockState> blockPredicate;
    private final Chunk chunk;

    public Heightmap(Chunk chunk, Type type) {
        this.blockPredicate = type.getBlockPredicate();
        this.chunk = chunk;
        int i = (int)Math.ceil(Math.log(chunk.getHeight() + 1) / Math.log(2.0));
        this.storage = new PackedIntegerArray(i, 256);
    }

    public static void populateHeightmaps(Chunk chunk, Set<Type> types) {
        int i = types.size();
        ObjectArrayList objectList = new ObjectArrayList(i);
        Iterator objectListIterator = objectList.iterator();
        int j = chunk.getHighestNonEmptySectionYOffset() + 16;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = 0; k < 16; ++k) {
            block1: for (int l = 0; l < 16; ++l) {
                for (Type type : types) {
                    objectList.add(chunk.getHeightmap(type));
                }
                for (int m = j - 1; m >= chunk.getBottomHeightLimit(); --m) {
                    mutable.set(k, m, l);
                    BlockState blockState = chunk.getBlockState(mutable);
                    if (blockState.isOf(Blocks.AIR)) continue;
                    while (objectListIterator.hasNext()) {
                        Heightmap heightmap = (Heightmap)objectListIterator.next();
                        if (!heightmap.blockPredicate.test(blockState)) continue;
                        heightmap.set(k, l, m + 1);
                        objectListIterator.remove();
                    }
                    if (objectList.isEmpty()) continue block1;
                    objectListIterator.back(i);
                }
            }
        }
    }

    public boolean trackUpdate(int x, int y, int z, BlockState state) {
        int i = this.get(x, z);
        if (y <= i - 2) {
            return false;
        }
        if (this.blockPredicate.test(state)) {
            if (y >= i) {
                this.set(x, z, y + 1);
                return true;
            }
        } else if (i - 1 == y) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int j = y - 1; j >= this.chunk.getBottomHeightLimit(); --j) {
                mutable.set(x, j, z);
                if (!this.blockPredicate.test(this.chunk.getBlockState(mutable))) continue;
                this.set(x, z, j + 1);
                return true;
            }
            this.set(x, z, this.chunk.getBottomHeightLimit());
            return true;
        }
        return false;
    }

    public int get(int x, int z) {
        return this.get(Heightmap.toIndex(x, z));
    }

    private int get(int index) {
        return this.storage.get(index) + this.chunk.getBottomHeightLimit();
    }

    private void set(int x, int z, int height) {
        this.storage.set(Heightmap.toIndex(x, z), height - this.chunk.getBottomHeightLimit());
    }

    public void setTo(long[] heightmap) {
        System.arraycopy(heightmap, 0, this.storage.getStorage(), 0, heightmap.length);
    }

    public long[] asLongArray() {
        return this.storage.getStorage();
    }

    private static int toIndex(int x, int z) {
        return x + z * 16;
    }

    static /* synthetic */ Predicate method_16683() {
        return ALWAYS_TRUE;
    }

    static /* synthetic */ Predicate method_16681() {
        return SUFFOCATES;
    }

    public static enum Type implements StringIdentifiable
    {
        WORLD_SURFACE_WG("WORLD_SURFACE_WG", Purpose.WORLDGEN, Heightmap.method_16683()),
        WORLD_SURFACE("WORLD_SURFACE", Purpose.CLIENT, Heightmap.method_16683()),
        OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Purpose.WORLDGEN, Heightmap.method_16681()),
        OCEAN_FLOOR("OCEAN_FLOOR", Purpose.LIVE_WORLD, Heightmap.method_16681()),
        MOTION_BLOCKING("MOTION_BLOCKING", Purpose.CLIENT, blockState -> blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()),
        MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Purpose.LIVE_WORLD, blockState -> (blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock));

        public static final Codec<Type> CODEC;
        private final String name;
        private final Purpose purpose;
        private final Predicate<BlockState> blockPredicate;
        private static final Map<String, Type> BY_NAME;

        private Type(String name, Purpose purpose, Predicate<BlockState> blockPredicate) {
            this.name = name;
            this.purpose = purpose;
            this.blockPredicate = blockPredicate;
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

        @Nullable
        public static Type byName(String name) {
            return BY_NAME.get(name);
        }

        public Predicate<BlockState> getBlockPredicate() {
            return this.blockPredicate;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Type::values, Type::byName);
            BY_NAME = Util.make(Maps.newHashMap(), hashMap -> {
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

