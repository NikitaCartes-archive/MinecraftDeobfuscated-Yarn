/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.class_7124;
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
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class class_7128 {
    public static final int field_37609 = 24;
    public static final int field_37610 = 1000;
    public static final float field_37611 = 0.5f;
    private static final int field_37613 = 32;
    public static final int field_37612 = 11;
    final boolean field_37614;
    private final TagKey<Block> field_37615;
    private final int field_37616;
    private final int field_37617;
    private final int field_37618;
    private final int field_37619;
    private List<class_7129> field_37620 = new ArrayList<class_7129>();
    private static final Logger field_37621 = LogUtils.getLogger();

    public class_7128(boolean bl, TagKey<Block> tagKey, int i, int j, int k, int l) {
        this.field_37614 = bl;
        this.field_37615 = tagKey;
        this.field_37616 = i;
        this.field_37617 = j;
        this.field_37618 = k;
        this.field_37619 = l;
    }

    public static class_7128 method_41478() {
        return new class_7128(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static class_7128 method_41485() {
        return new class_7128(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> method_41487() {
        return this.field_37615;
    }

    public int method_41488() {
        return this.field_37616;
    }

    public int method_41489() {
        return this.field_37617;
    }

    public int method_41490() {
        return this.field_37618;
    }

    public int method_41491() {
        return this.field_37619;
    }

    public boolean method_41492() {
        return this.field_37614;
    }

    @VisibleForTesting
    public List<class_7129> method_41493() {
        return this.field_37620;
    }

    public void method_41494() {
        this.field_37620.clear();
    }

    public void method_41483(NbtCompound nbtCompound) {
        if (nbtCompound.contains("cursors", 9)) {
            this.field_37620.clear();
            List list = class_7129.field_37623.listOf().parse(new Dynamic<NbtList>(NbtOps.INSTANCE, nbtCompound.getList("cursors", 10))).resultOrPartial(field_37621::error).orElseGet(ArrayList::new);
            int i = Math.min(list.size(), 32);
            for (int j = 0; j < i; ++j) {
                this.method_41480((class_7129)list.get(j));
            }
        }
    }

    public void method_41486(NbtCompound nbtCompound) {
        class_7129.field_37623.listOf().encodeStart(NbtOps.INSTANCE, this.field_37620).resultOrPartial(field_37621::error).ifPresent(nbtElement -> nbtCompound.put("cursors", (NbtElement)nbtElement));
    }

    public void method_41482(BlockPos blockPos, int i) {
        while (i > 0) {
            int j = Math.min(i, 1000);
            this.method_41480(new class_7129(blockPos, j));
            i -= j;
        }
    }

    private void method_41480(class_7129 arg) {
        if (this.field_37620.size() >= 32) {
            return;
        }
        this.field_37620.add(arg);
    }

    public void method_41479(WorldAccess worldAccess, BlockPos blockPos2, Random random, boolean bl) {
        if (this.field_37620.isEmpty()) {
            return;
        }
        ArrayList<class_7129> list = new ArrayList<class_7129>();
        HashMap<BlockPos, class_7129> map = new HashMap<BlockPos, class_7129>();
        Object2IntOpenHashMap<BlockPos> object2IntMap = new Object2IntOpenHashMap<BlockPos>();
        for (class_7129 class_71292 : this.field_37620) {
            class_71292.method_41499(worldAccess, blockPos2, random, this, bl);
            if (class_71292.field_37626 <= 0) {
                worldAccess.syncWorldEvent(3006, class_71292.method_41495(), 0);
                continue;
            }
            BlockPos blockPos22 = class_71292.method_41495();
            object2IntMap.computeInt(blockPos22, (blockPos, integer) -> (integer == null ? 0 : integer) + arg.field_37626);
            class_7129 lv2 = (class_7129)map.get(blockPos22);
            if (lv2 == null) {
                map.put(blockPos22, class_71292);
                list.add(class_71292);
                continue;
            }
            if (!this.method_41492() && class_71292.field_37626 + lv2.field_37626 <= 1000) {
                lv2.method_41501(class_71292);
                continue;
            }
            list.add(class_71292);
            if (class_71292.field_37626 >= lv2.field_37626) continue;
            map.put(blockPos22, class_71292);
        }
        for (Object2IntMap.Entry entry : object2IntMap.object2IntEntrySet()) {
            Set<Direction> collection;
            BlockPos blockPos22 = (BlockPos)entry.getKey();
            int i = entry.getIntValue();
            class_7129 lv3 = (class_7129)map.get(blockPos22);
            Set<Direction> set = collection = lv3 == null ? null : lv3.method_41512();
            if (i <= 0 || collection == null) continue;
            int j = (int)(Math.log1p(i) / (double)2.3f) + 1;
            int k = (j << 6) + AbstractLichenBlock.method_41439(collection);
            worldAccess.syncWorldEvent(3006, blockPos22, k);
        }
        this.field_37620 = list;
    }

    public static class class_7129 {
        private static final List<Vec3i> field_37624 = Util.make(new ArrayList(18), arrayList -> BlockPos.stream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter(blockPos -> (blockPos.getX() == 0 || blockPos.getY() == 0 || blockPos.getZ() == 0) && !blockPos.equals(BlockPos.ORIGIN)).map(BlockPos::toImmutable).forEach(arrayList::add));
        public static final int field_37622 = 1;
        private BlockPos field_37625;
        int field_37626;
        private int field_37627;
        private int field_37628;
        @Nullable
        private Set<Direction> field_37629;
        private static final Codec<Set<Direction>> field_37630 = Direction.CODEC.listOf().xmap(list -> Sets.newEnumSet(list, Direction.class), Lists::newArrayList);
        public static final Codec<class_7129> field_37623 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockPos.CODEC.fieldOf("pos")).forGetter(class_7129::method_41495), ((MapCodec)Codec.intRange(0, 1000).fieldOf("charge")).orElse(0).forGetter(class_7129::method_41508), ((MapCodec)Codec.intRange(0, 1).fieldOf("decay_delay")).orElse(1).forGetter(class_7129::method_41510), ((MapCodec)Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay")).orElse(0).forGetter(arg -> arg.field_37627), field_37630.optionalFieldOf("facings").forGetter(arg -> Optional.ofNullable(arg.method_41512()))).apply((Applicative<class_7129, ?>)instance, class_7129::new));

        private class_7129(BlockPos blockPos, int i, int j, int k, Optional<Set<Direction>> optional) {
            this.field_37625 = blockPos;
            this.field_37626 = i;
            this.field_37628 = j;
            this.field_37627 = k;
            this.field_37629 = optional.orElse(null);
        }

        public class_7129(BlockPos blockPos, int i) {
            this(blockPos, i, 1, 0, Optional.empty());
        }

        public BlockPos method_41495() {
            return this.field_37625;
        }

        public int method_41508() {
            return this.field_37626;
        }

        public int method_41510() {
            return this.field_37628;
        }

        @Nullable
        public Set<Direction> method_41512() {
            return this.field_37629;
        }

        private boolean method_41500(WorldAccess worldAccess, BlockPos blockPos, boolean bl) {
            if (this.field_37626 <= 0) {
                return false;
            }
            if (bl) {
                return true;
            }
            if (worldAccess instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)worldAccess;
                return serverWorld.shouldTickBlockPos(blockPos);
            }
            return false;
        }

        public void method_41499(WorldAccess worldAccess, BlockPos blockPos, Random random, class_7128 arg, boolean bl) {
            if (!this.method_41500(worldAccess, blockPos, arg.field_37614)) {
                return;
            }
            if (this.field_37627 > 0) {
                --this.field_37627;
                return;
            }
            BlockState blockState = worldAccess.getBlockState(this.field_37625);
            class_7124 lv = class_7129.method_41503(blockState);
            if (bl && lv.method_41469(worldAccess, this.field_37625, blockState, this.field_37629, arg.method_41492())) {
                if (lv.method_41472()) {
                    blockState = worldAccess.getBlockState(this.field_37625);
                    lv = class_7129.method_41503(blockState);
                }
                worldAccess.playSound(null, this.field_37625, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            this.field_37626 = lv.method_41471(this, worldAccess, blockPos, random, arg, bl);
            if (this.field_37626 <= 0) {
                lv.method_41468(worldAccess, blockState, this.field_37625, random);
                return;
            }
            BlockPos blockPos2 = class_7129.method_41498(worldAccess, this.field_37625, random);
            if (blockPos2 != null) {
                lv.method_41468(worldAccess, blockState, this.field_37625, random);
                this.field_37625 = blockPos2.toImmutable();
                if (arg.method_41492() && !this.field_37625.isWithinDistance(new Vec3i(blockPos.getX(), this.field_37625.getY(), blockPos.getZ()), 15.0)) {
                    this.field_37626 = 0;
                    return;
                }
                blockState = worldAccess.getBlockState(blockPos2);
            }
            if (blockState.getBlock() instanceof class_7124) {
                this.field_37629 = AbstractLichenBlock.method_41440(blockState);
            }
            this.field_37628 = lv.method_41473(this.field_37628);
            this.field_37627 = lv.method_41467();
        }

        void method_41501(class_7129 arg) {
            this.field_37626 += arg.field_37626;
            arg.field_37626 = 0;
            this.field_37627 = Math.min(this.field_37627, arg.field_37627);
        }

        private static class_7124 method_41503(BlockState blockState) {
            class_7124 lv;
            Block block = blockState.getBlock();
            return block instanceof class_7124 ? (lv = (class_7124)((Object)block)) : class_7124.field_37602;
        }

        private static List<Vec3i> method_41507(Random random) {
            ArrayList<Vec3i> list = new ArrayList<Vec3i>(field_37624);
            Collections.shuffle(list, random);
            return list;
        }

        @Nullable
        private static BlockPos method_41498(WorldAccess worldAccess, BlockPos blockPos, Random random) {
            BlockPos.Mutable mutable = blockPos.mutableCopy();
            BlockPos.Mutable mutable2 = blockPos.mutableCopy();
            for (Vec3i vec3i : class_7129.method_41507(random)) {
                mutable2.set((Vec3i)blockPos, vec3i);
                BlockState blockState = worldAccess.getBlockState(mutable2);
                if (!(blockState.getBlock() instanceof class_7124) || !class_7129.method_41496(worldAccess, blockPos, mutable2)) continue;
                mutable.set(mutable2);
                if (!SculkVeinBlock.method_41513(worldAccess, blockState, mutable2)) continue;
                break;
            }
            return mutable.equals(blockPos) ? null : mutable;
        }

        private static boolean method_41496(WorldAccess worldAccess, BlockPos blockPos, BlockPos blockPos2) {
            if (blockPos.getManhattanDistance(blockPos2) == 1) {
                return true;
            }
            BlockPos blockPos3 = blockPos2.subtract(blockPos);
            Direction direction = Direction.from(Direction.Axis.X, blockPos3.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction2 = Direction.from(Direction.Axis.Y, blockPos3.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction3 = Direction.from(Direction.Axis.Z, blockPos3.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            if (blockPos3.getX() == 0) {
                return class_7129.method_41497(worldAccess, blockPos, direction2) || class_7129.method_41497(worldAccess, blockPos, direction3);
            }
            if (blockPos3.getY() == 0) {
                return class_7129.method_41497(worldAccess, blockPos, direction) || class_7129.method_41497(worldAccess, blockPos, direction3);
            }
            return class_7129.method_41497(worldAccess, blockPos, direction) || class_7129.method_41497(worldAccess, blockPos, direction2);
        }

        private static boolean method_41497(WorldAccess worldAccess, BlockPos blockPos, Direction direction) {
            BlockPos blockPos2 = blockPos.offset(direction);
            return !worldAccess.getBlockState(blockPos2).isSideSolidFullSquare(worldAccess, blockPos2, direction.getOpposite());
        }
    }
}

