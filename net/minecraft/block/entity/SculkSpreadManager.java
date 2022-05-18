/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SculkSpreadManager {
    public static final int field_37609 = 24;
    public static final int field_37610 = 1000;
    public static final float field_37611 = 0.5f;
    private static final int field_37613 = 32;
    public static final int field_37612 = 11;
    final boolean worldGen;
    private final TagKey<Block> replaceableTag;
    private final int extraBlockChance;
    private final int maxDistance;
    private final int spreadChance;
    private final int decayChance;
    private List<Cursor> cursors = new ArrayList<Cursor>();
    private static final Logger LOGGER = LogUtils.getLogger();

    public SculkSpreadManager(boolean worldGen, TagKey<Block> replaceableTag, int extraBlockChance, int maxDistance, int spreadChance, int decayChance) {
        this.worldGen = worldGen;
        this.replaceableTag = replaceableTag;
        this.extraBlockChance = extraBlockChance;
        this.maxDistance = maxDistance;
        this.spreadChance = spreadChance;
        this.decayChance = decayChance;
    }

    public static SculkSpreadManager create() {
        return new SculkSpreadManager(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static SculkSpreadManager createWorldGen() {
        return new SculkSpreadManager(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> getReplaceableTag() {
        return this.replaceableTag;
    }

    public int getExtraBlockChance() {
        return this.extraBlockChance;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public int getSpreadChance() {
        return this.spreadChance;
    }

    public int getDecayChance() {
        return this.decayChance;
    }

    public boolean isWorldGen() {
        return this.worldGen;
    }

    @VisibleForTesting
    public List<Cursor> getCursors() {
        return this.cursors;
    }

    public void clearCursors() {
        this.cursors.clear();
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("cursors", NbtElement.LIST_TYPE)) {
            this.cursors.clear();
            List list = Cursor.CODEC.listOf().parse(new Dynamic<NbtList>(NbtOps.INSTANCE, nbt.getList("cursors", NbtElement.COMPOUND_TYPE))).resultOrPartial(LOGGER::error).orElseGet(ArrayList::new);
            int i = Math.min(list.size(), 32);
            for (int j = 0; j < i; ++j) {
                this.addCursor((Cursor)list.get(j));
            }
        }
    }

    public void writeNbt(NbtCompound nbt) {
        Cursor.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.cursors).resultOrPartial(LOGGER::error).ifPresent(cursorsNbt -> nbt.put("cursors", (NbtElement)cursorsNbt));
    }

    public void spread(BlockPos pos, int charge) {
        while (charge > 0) {
            int i = Math.min(charge, 1000);
            this.addCursor(new Cursor(pos, i));
            charge -= i;
        }
    }

    private void addCursor(Cursor cursor) {
        if (this.cursors.size() >= 32) {
            return;
        }
        this.cursors.add(cursor);
    }

    public void tick(WorldAccess world, BlockPos pos2, Random random, boolean shouldConvertToBlock) {
        BlockPos blockPos;
        if (this.cursors.isEmpty()) {
            return;
        }
        ArrayList<Cursor> list = new ArrayList<Cursor>();
        HashMap<BlockPos, Cursor> map = new HashMap<BlockPos, Cursor>();
        Object2IntOpenHashMap<BlockPos> object2IntMap = new Object2IntOpenHashMap<BlockPos>();
        for (Cursor cursor : this.cursors) {
            cursor.spread(world, pos2, random, this, shouldConvertToBlock);
            if (cursor.charge <= 0) {
                world.syncWorldEvent(WorldEvents.SCULK_CHARGE, cursor.getPos(), 0);
                continue;
            }
            blockPos = cursor.getPos();
            object2IntMap.computeInt(blockPos, (pos, charge) -> (charge == null ? 0 : charge) + cursor.charge);
            Cursor cursor2 = (Cursor)map.get(blockPos);
            if (cursor2 == null) {
                map.put(blockPos, cursor);
                list.add(cursor);
                continue;
            }
            if (!this.isWorldGen() && cursor.charge + cursor2.charge <= 1000) {
                cursor2.merge(cursor);
                continue;
            }
            list.add(cursor);
            if (cursor.charge >= cursor2.charge) continue;
            map.put(blockPos, cursor);
        }
        for (Object2IntMap.Entry entry : object2IntMap.object2IntEntrySet()) {
            Set<Direction> collection;
            blockPos = (BlockPos)entry.getKey();
            int i = entry.getIntValue();
            Cursor cursor3 = (Cursor)map.get(blockPos);
            Set<Direction> set = collection = cursor3 == null ? null : cursor3.getFaces();
            if (i <= 0 || collection == null) continue;
            int j = (int)(Math.log1p(i) / (double)2.3f) + 1;
            int k = (j << 6) + MultifaceGrowthBlock.directionsToFlag(collection);
            world.syncWorldEvent(WorldEvents.SCULK_CHARGE, blockPos, k);
        }
        this.cursors = list;
    }

    public static class Cursor {
        private static final ObjectArrayList<Vec3i> OFFSETS = Util.make(new ObjectArrayList(18), objectArrayList -> BlockPos.stream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter(pos -> (pos.getX() == 0 || pos.getY() == 0 || pos.getZ() == 0) && !pos.equals(BlockPos.ORIGIN)).map(BlockPos::toImmutable).forEach(objectArrayList::add));
        public static final int field_37622 = 1;
        private BlockPos pos;
        int charge;
        private int update;
        private int decay;
        @Nullable
        private Set<Direction> faces;
        private static final Codec<Set<Direction>> DIRECTION_SET_CODEC = Direction.CODEC.listOf().xmap(directions -> Sets.newEnumSet(directions, Direction.class), Lists::newArrayList);
        public static final Codec<Cursor> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockPos.CODEC.fieldOf("pos")).forGetter(Cursor::getPos), ((MapCodec)Codec.intRange(0, 1000).fieldOf("charge")).orElse(0).forGetter(Cursor::getCharge), ((MapCodec)Codec.intRange(0, 1).fieldOf("decay_delay")).orElse(1).forGetter(Cursor::getDecay), ((MapCodec)Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay")).orElse(0).forGetter(cursor -> cursor.update), DIRECTION_SET_CODEC.optionalFieldOf("facings").forGetter(cursor -> Optional.ofNullable(cursor.getFaces()))).apply((Applicative<Cursor, ?>)instance, Cursor::new));

        private Cursor(BlockPos pos, int charge, int decay, int update, Optional<Set<Direction>> faces) {
            this.pos = pos;
            this.charge = charge;
            this.decay = decay;
            this.update = update;
            this.faces = faces.orElse(null);
        }

        public Cursor(BlockPos pos, int charge) {
            this(pos, charge, 1, 0, Optional.empty());
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public int getCharge() {
            return this.charge;
        }

        public int getDecay() {
            return this.decay;
        }

        @Nullable
        public Set<Direction> getFaces() {
            return this.faces;
        }

        private boolean canSpread(WorldAccess world, BlockPos pos, boolean worldGen) {
            if (this.charge <= 0) {
                return false;
            }
            if (worldGen) {
                return true;
            }
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                return serverWorld.shouldTickBlockPos(pos);
            }
            return false;
        }

        public void spread(WorldAccess world, BlockPos pos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
            if (!this.canSpread(world, pos, spreadManager.worldGen)) {
                return;
            }
            if (this.update > 0) {
                --this.update;
                return;
            }
            BlockState blockState = world.getBlockState(this.pos);
            SculkSpreadable sculkSpreadable = Cursor.getSpreadable(blockState);
            if (shouldConvertToBlock && sculkSpreadable.spread(world, this.pos, blockState, this.faces, spreadManager.isWorldGen())) {
                if (sculkSpreadable.shouldConvertToSpreadable()) {
                    blockState = world.getBlockState(this.pos);
                    sculkSpreadable = Cursor.getSpreadable(blockState);
                }
                world.playSound(null, this.pos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            this.charge = sculkSpreadable.spread(this, world, pos, random, spreadManager, shouldConvertToBlock);
            if (this.charge <= 0) {
                sculkSpreadable.spreadAtSamePosition(world, blockState, this.pos, random);
                return;
            }
            BlockPos blockPos = Cursor.getSpreadPos(world, this.pos, random);
            if (blockPos != null) {
                sculkSpreadable.spreadAtSamePosition(world, blockState, this.pos, random);
                this.pos = blockPos.toImmutable();
                if (spreadManager.isWorldGen() && !this.pos.isWithinDistance(new Vec3i(pos.getX(), this.pos.getY(), pos.getZ()), 15.0)) {
                    this.charge = 0;
                    return;
                }
                blockState = world.getBlockState(blockPos);
            }
            if (blockState.getBlock() instanceof SculkSpreadable) {
                this.faces = MultifaceGrowthBlock.collectDirections(blockState);
            }
            this.decay = sculkSpreadable.getDecay(this.decay);
            this.update = sculkSpreadable.getUpdate();
        }

        void merge(Cursor cursor) {
            this.charge += cursor.charge;
            cursor.charge = 0;
            this.update = Math.min(this.update, cursor.update);
        }

        private static SculkSpreadable getSpreadable(BlockState state) {
            SculkSpreadable sculkSpreadable;
            Block block = state.getBlock();
            return block instanceof SculkSpreadable ? (sculkSpreadable = (SculkSpreadable)((Object)block)) : SculkSpreadable.VEIN_ONLY_SPREADER;
        }

        private static List<Vec3i> shuffleOffsets(Random random) {
            return Util.copyShuffled(OFFSETS, random);
        }

        @Nullable
        private static BlockPos getSpreadPos(WorldAccess world, BlockPos pos, Random random) {
            BlockPos.Mutable mutable = pos.mutableCopy();
            BlockPos.Mutable mutable2 = pos.mutableCopy();
            for (Vec3i vec3i : Cursor.shuffleOffsets(random)) {
                mutable2.set((Vec3i)pos, vec3i);
                BlockState blockState = world.getBlockState(mutable2);
                if (!(blockState.getBlock() instanceof SculkSpreadable) || !Cursor.canSpread(world, pos, mutable2)) continue;
                mutable.set(mutable2);
                if (!SculkVeinBlock.veinCoversSculkReplaceable(world, blockState, mutable2)) continue;
                break;
            }
            return mutable.equals(pos) ? null : mutable;
        }

        private static boolean canSpread(WorldAccess world, BlockPos sourcePos, BlockPos targetPos) {
            if (sourcePos.getManhattanDistance(targetPos) == 1) {
                return true;
            }
            BlockPos blockPos = targetPos.subtract(sourcePos);
            Direction direction = Direction.from(Direction.Axis.X, blockPos.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction2 = Direction.from(Direction.Axis.Y, blockPos.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction3 = Direction.from(Direction.Axis.Z, blockPos.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            if (blockPos.getX() == 0) {
                return Cursor.canSpread(world, sourcePos, direction2) || Cursor.canSpread(world, sourcePos, direction3);
            }
            if (blockPos.getY() == 0) {
                return Cursor.canSpread(world, sourcePos, direction) || Cursor.canSpread(world, sourcePos, direction3);
            }
            return Cursor.canSpread(world, sourcePos, direction) || Cursor.canSpread(world, sourcePos, direction2);
        }

        private static boolean canSpread(WorldAccess world, BlockPos pos, Direction direction) {
            BlockPos blockPos = pos.offset(direction);
            return !world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction.getOpposite());
        }
    }
}

