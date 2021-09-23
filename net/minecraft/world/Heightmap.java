/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Heightmap {
    private static final Logger LOGGER = LogManager.getLogger();
    static final Predicate<BlockState> NOT_AIR = state -> !state.isAir();
    static final Predicate<BlockState> SUFFOCATES = state -> state.getMaterial().blocksMovement();
    private final PaletteStorage storage;
    private final Predicate<BlockState> blockPredicate;
    private final Chunk chunk;

    public Heightmap(Chunk chunk, Type type) {
        this.blockPredicate = type.getBlockPredicate();
        this.chunk = chunk;
        int i = MathHelper.ceilLog2(chunk.getHeight() + 1);
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
                for (int m = j - 1; m >= chunk.getBottomY(); --m) {
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
            for (int j = y - 1; j >= this.chunk.getBottomY(); --j) {
                mutable.set(x, j, z);
                if (!this.blockPredicate.test(this.chunk.getBlockState(mutable))) continue;
                this.set(x, z, j + 1);
                return true;
            }
            this.set(x, z, this.chunk.getBottomY());
            return true;
        }
        return false;
    }

    public int get(int x, int z) {
        return this.get(Heightmap.toIndex(x, z));
    }

    public int method_35334(int i, int j) {
        return this.get(Heightmap.toIndex(i, j)) - 1;
    }

    private int get(int index) {
        return this.storage.get(index) + this.chunk.getBottomY();
    }

    private void set(int x, int z, int height) {
        this.storage.set(Heightmap.toIndex(x, z), height - this.chunk.getBottomY());
    }

    public void setTo(Chunk chunk, Type type, long[] ls) {
        long[] ms = this.storage.getData();
        if (ms.length == ls.length) {
            System.arraycopy(ls, 0, ms, 0, ls.length);
            return;
        }
        LOGGER.warn("Ignoring heightmap data for chunk " + chunk.getPos() + ", size does not match; expected: " + ms.length + ", got: " + ls.length);
        Heightmap.populateHeightmaps(chunk, EnumSet.of(type));
    }

    public long[] asLongArray() {
        return this.storage.getData();
    }

    private static int toIndex(int x, int z) {
        return x + z * 16;
    }

    public static enum Type implements StringIdentifiable
    {
        WORLD_SURFACE_WG("WORLD_SURFACE_WG", Purpose.WORLDGEN, NOT_AIR),
        WORLD_SURFACE("WORLD_SURFACE", Purpose.CLIENT, NOT_AIR),
        OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Purpose.WORLDGEN, SUFFOCATES),
        OCEAN_FLOOR("OCEAN_FLOOR", Purpose.LIVE_WORLD, SUFFOCATES),
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

