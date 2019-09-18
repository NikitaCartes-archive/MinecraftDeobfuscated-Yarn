/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Clearable;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Structure {
    private final List<List<StructureBlockInfo>> blocks = Lists.newArrayList();
    private final List<StructureEntityInfo> entities = Lists.newArrayList();
    private BlockPos size = BlockPos.ORIGIN;
    private String author = "?";

    public BlockPos getSize() {
        return this.size;
    }

    public void setAuthor(String string) {
        this.author = string;
    }

    public String getAuthor() {
        return this.author;
    }

    public void method_15174(World world, BlockPos blockPos, BlockPos blockPos2, boolean bl, @Nullable Block block) {
        if (blockPos2.getX() < 1 || blockPos2.getY() < 1 || blockPos2.getZ() < 1) {
            return;
        }
        BlockPos blockPos3 = blockPos.add(blockPos2).add(-1, -1, -1);
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list2 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list3 = Lists.newArrayList();
        BlockPos blockPos4 = new BlockPos(Math.min(blockPos.getX(), blockPos3.getX()), Math.min(blockPos.getY(), blockPos3.getY()), Math.min(blockPos.getZ(), blockPos3.getZ()));
        BlockPos blockPos5 = new BlockPos(Math.max(blockPos.getX(), blockPos3.getX()), Math.max(blockPos.getY(), blockPos3.getY()), Math.max(blockPos.getZ(), blockPos3.getZ()));
        this.size = blockPos2;
        for (BlockPos blockPos6 : BlockPos.iterate(blockPos4, blockPos5)) {
            BlockPos blockPos7 = blockPos6.subtract(blockPos4);
            BlockState blockState = world.getBlockState(blockPos6);
            if (block != null && block == blockState.getBlock()) continue;
            BlockEntity blockEntity = world.getBlockEntity(blockPos6);
            if (blockEntity != null) {
                CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
                compoundTag.remove("x");
                compoundTag.remove("y");
                compoundTag.remove("z");
                list2.add(new StructureBlockInfo(blockPos7, blockState, compoundTag));
                continue;
            }
            if (blockState.isFullOpaque(world, blockPos6) || blockState.method_21743(world, blockPos6)) {
                list.add(new StructureBlockInfo(blockPos7, blockState, null));
                continue;
            }
            list3.add(new StructureBlockInfo(blockPos7, blockState, null));
        }
        ArrayList<StructureBlockInfo> list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list2);
        list4.addAll(list3);
        this.blocks.clear();
        this.blocks.add(list4);
        if (bl) {
            this.method_15164(world, blockPos4, blockPos5.add(1, 1, 1));
        } else {
            this.entities.clear();
        }
    }

    private void method_15164(World world, BlockPos blockPos, BlockPos blockPos2) {
        List<Entity> list = world.getEntities(Entity.class, new Box(blockPos, blockPos2), entity -> !(entity instanceof PlayerEntity));
        this.entities.clear();
        for (Entity entity2 : list) {
            Vec3d vec3d = new Vec3d(entity2.x - (double)blockPos.getX(), entity2.y - (double)blockPos.getY(), entity2.z - (double)blockPos.getZ());
            CompoundTag compoundTag = new CompoundTag();
            entity2.saveToTag(compoundTag);
            BlockPos blockPos3 = entity2 instanceof PaintingEntity ? ((PaintingEntity)entity2).getDecorationBlockPos().subtract(blockPos) : new BlockPos(vec3d);
            this.entities.add(new StructureEntityInfo(vec3d, blockPos3, compoundTag));
        }
    }

    public List<StructureBlockInfo> method_16445(BlockPos blockPos, StructurePlacementData structurePlacementData, Block block) {
        return this.method_15165(blockPos, structurePlacementData, block, true);
    }

    public List<StructureBlockInfo> method_15165(BlockPos blockPos, StructurePlacementData structurePlacementData, Block block, boolean bl) {
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        BlockBox blockBox = structurePlacementData.method_15124();
        for (StructureBlockInfo structureBlockInfo : structurePlacementData.method_15121(this.blocks, blockPos)) {
            BlockState blockState;
            BlockPos blockPos2;
            BlockPos blockPos3 = blockPos2 = bl ? Structure.method_15171(structurePlacementData, structureBlockInfo.pos).add(blockPos) : structureBlockInfo.pos;
            if (blockBox != null && !blockBox.contains(blockPos2) || (blockState = structureBlockInfo.state).getBlock() != block) continue;
            list.add(new StructureBlockInfo(blockPos2, blockState.rotate(structurePlacementData.getRotation()), structureBlockInfo.tag));
        }
        return list;
    }

    public BlockPos method_15180(StructurePlacementData structurePlacementData, BlockPos blockPos, StructurePlacementData structurePlacementData2, BlockPos blockPos2) {
        BlockPos blockPos3 = Structure.method_15171(structurePlacementData, blockPos);
        BlockPos blockPos4 = Structure.method_15171(structurePlacementData2, blockPos2);
        return blockPos3.subtract(blockPos4);
    }

    public static BlockPos method_15171(StructurePlacementData structurePlacementData, BlockPos blockPos) {
        return Structure.method_15168(blockPos, structurePlacementData.getMirror(), structurePlacementData.getRotation(), structurePlacementData.getPosition());
    }

    public void place(IWorld iWorld, BlockPos blockPos, StructurePlacementData structurePlacementData) {
        structurePlacementData.method_15132();
        this.method_15178(iWorld, blockPos, structurePlacementData);
    }

    public void method_15178(IWorld iWorld, BlockPos blockPos, StructurePlacementData structurePlacementData) {
        this.method_15172(iWorld, blockPos, structurePlacementData, 2);
    }

    public boolean method_15172(IWorld iWorld, BlockPos blockPos, StructurePlacementData structurePlacementData, int i) {
        if (this.blocks.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> list = structurePlacementData.method_15121(this.blocks, blockPos);
        if (list.isEmpty() && (structurePlacementData.shouldIgnoreEntities() || this.entities.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BlockBox blockBox = structurePlacementData.method_15124();
        ArrayList<BlockPos> list2 = Lists.newArrayListWithCapacity(structurePlacementData.shouldPlaceFluids() ? list.size() : 0);
        ArrayList<Pair<BlockPos, CompoundTag>> list3 = Lists.newArrayListWithCapacity(list.size());
        int j = Integer.MAX_VALUE;
        int k = Integer.MAX_VALUE;
        int l = Integer.MAX_VALUE;
        int m = Integer.MIN_VALUE;
        int n = Integer.MIN_VALUE;
        int o = Integer.MIN_VALUE;
        List<StructureBlockInfo> list4 = Structure.process(iWorld, blockPos, structurePlacementData, list);
        for (StructureBlockInfo structureBlockInfo : list4) {
            BlockEntity blockEntity;
            BlockPos blockPos2 = structureBlockInfo.pos;
            if (blockBox != null && !blockBox.contains(blockPos2)) continue;
            FluidState fluidState = structurePlacementData.shouldPlaceFluids() ? iWorld.getFluidState(blockPos2) : null;
            BlockState blockState = structureBlockInfo.state.mirror(structurePlacementData.getMirror()).rotate(structurePlacementData.getRotation());
            if (structureBlockInfo.tag != null) {
                blockEntity = iWorld.getBlockEntity(blockPos2);
                Clearable.clear(blockEntity);
                iWorld.setBlockState(blockPos2, Blocks.BARRIER.getDefaultState(), 20);
            }
            if (!iWorld.setBlockState(blockPos2, blockState, i)) continue;
            j = Math.min(j, blockPos2.getX());
            k = Math.min(k, blockPos2.getY());
            l = Math.min(l, blockPos2.getZ());
            m = Math.max(m, blockPos2.getX());
            n = Math.max(n, blockPos2.getY());
            o = Math.max(o, blockPos2.getZ());
            list3.add(Pair.of(blockPos2, structureBlockInfo.tag));
            if (structureBlockInfo.tag != null && (blockEntity = iWorld.getBlockEntity(blockPos2)) != null) {
                structureBlockInfo.tag.putInt("x", blockPos2.getX());
                structureBlockInfo.tag.putInt("y", blockPos2.getY());
                structureBlockInfo.tag.putInt("z", blockPos2.getZ());
                blockEntity.fromTag(structureBlockInfo.tag);
                blockEntity.applyMirror(structurePlacementData.getMirror());
                blockEntity.applyRotation(structurePlacementData.getRotation());
            }
            if (fluidState == null || !(blockState.getBlock() instanceof FluidFillable)) continue;
            ((FluidFillable)((Object)blockState.getBlock())).tryFillWithFluid(iWorld, blockPos2, blockState, fluidState);
            if (fluidState.isStill()) continue;
            list2.add(blockPos2);
        }
        boolean bl = true;
        Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        while (bl && !list2.isEmpty()) {
            bl = false;
            Iterator iterator = list2.iterator();
            while (iterator.hasNext()) {
                BlockState blockState2;
                Block block;
                BlockPos blockPos2;
                BlockPos blockPos4 = blockPos2 = (BlockPos)iterator.next();
                FluidState fluidState2 = iWorld.getFluidState(blockPos4);
                for (int p = 0; p < directions.length && !fluidState2.isStill(); ++p) {
                    BlockPos blockPos3 = blockPos4.offset(directions[p]);
                    FluidState fluidState3 = iWorld.getFluidState(blockPos3);
                    if (!(fluidState3.getHeight(iWorld, blockPos3) > fluidState2.getHeight(iWorld, blockPos4)) && (!fluidState3.isStill() || fluidState2.isStill())) continue;
                    fluidState2 = fluidState3;
                    blockPos4 = blockPos3;
                }
                if (!fluidState2.isStill() || !((block = (blockState2 = iWorld.getBlockState(blockPos2)).getBlock()) instanceof FluidFillable)) continue;
                ((FluidFillable)((Object)block)).tryFillWithFluid(iWorld, blockPos2, blockState2, fluidState2);
                bl = true;
                iterator.remove();
            }
        }
        if (j <= m) {
            if (!structurePlacementData.method_16444()) {
                BitSetVoxelSet voxelSet = new BitSetVoxelSet(m - j + 1, n - k + 1, o - l + 1);
                int n2 = j;
                int r = k;
                int s = l;
                for (Pair pair : list3) {
                    BlockPos blockPos6 = (BlockPos)pair.getFirst();
                    ((VoxelSet)voxelSet).set(blockPos6.getX() - n2, blockPos6.getY() - r, blockPos6.getZ() - s, true, true);
                }
                Structure.method_20532(iWorld, i, voxelSet, n2, r, s);
            }
            for (Pair pair : list3) {
                BlockEntity blockEntity;
                BlockPos blockPos4 = (BlockPos)pair.getFirst();
                if (!structurePlacementData.method_16444()) {
                    BlockState blockState2;
                    BlockState blockState3 = iWorld.getBlockState(blockPos4);
                    if (blockState3 != (blockState2 = Block.getRenderingState(blockState3, iWorld, blockPos4))) {
                        iWorld.setBlockState(blockPos4, blockState2, i & 0xFFFFFFFE | 0x10);
                    }
                    iWorld.updateNeighbors(blockPos4, blockState2.getBlock());
                }
                if (pair.getSecond() == null || (blockEntity = iWorld.getBlockEntity(blockPos4)) == null) continue;
                blockEntity.markDirty();
            }
        }
        if (!structurePlacementData.shouldIgnoreEntities()) {
            this.method_15179(iWorld, blockPos, structurePlacementData.getMirror(), structurePlacementData.getRotation(), structurePlacementData.getPosition(), blockBox);
        }
        return true;
    }

    public static void method_20532(IWorld iWorld, int i, VoxelSet voxelSet, int j, int k, int l) {
        voxelSet.method_1046((direction, m, n, o) -> {
            BlockState blockState4;
            BlockState blockState2;
            BlockState blockState3;
            BlockPos blockPos = new BlockPos(j + m, k + n, l + o);
            BlockPos blockPos2 = blockPos.offset(direction);
            BlockState blockState = iWorld.getBlockState(blockPos);
            if (blockState != (blockState3 = blockState.getStateForNeighborUpdate(direction, blockState2 = iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2))) {
                iWorld.setBlockState(blockPos, blockState3, i & 0xFFFFFFFE | 0x10);
            }
            if (blockState2 != (blockState4 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState3, iWorld, blockPos2, blockPos))) {
                iWorld.setBlockState(blockPos2, blockState4, i & 0xFFFFFFFE | 0x10);
            }
        });
    }

    public static List<StructureBlockInfo> process(IWorld iWorld, BlockPos blockPos, StructurePlacementData structurePlacementData, List<StructureBlockInfo> list) {
        ArrayList<StructureBlockInfo> list2 = Lists.newArrayList();
        for (StructureBlockInfo structureBlockInfo : list) {
            BlockPos blockPos2 = Structure.method_15171(structurePlacementData, structureBlockInfo.pos).add(blockPos);
            StructureBlockInfo structureBlockInfo2 = new StructureBlockInfo(blockPos2, structureBlockInfo.state, structureBlockInfo.tag);
            Iterator<StructureProcessor> iterator = structurePlacementData.getProcessors().iterator();
            while (structureBlockInfo2 != null && iterator.hasNext()) {
                structureBlockInfo2 = iterator.next().process(iWorld, blockPos, structureBlockInfo, structureBlockInfo2, structurePlacementData);
            }
            if (structureBlockInfo2 == null) continue;
            list2.add(structureBlockInfo2);
        }
        return list2;
    }

    private void method_15179(IWorld iWorld, BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos2, @Nullable BlockBox blockBox) {
        for (StructureEntityInfo structureEntityInfo : this.entities) {
            BlockPos blockPos3 = Structure.method_15168(structureEntityInfo.blockPos, blockMirror, blockRotation, blockPos2).add(blockPos);
            if (blockBox != null && !blockBox.contains(blockPos3)) continue;
            CompoundTag compoundTag = structureEntityInfo.tag;
            Vec3d vec3d = Structure.method_15176(structureEntityInfo.pos, blockMirror, blockRotation, blockPos2);
            Vec3d vec3d2 = vec3d.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            ListTag listTag = new ListTag();
            listTag.add(new DoubleTag(vec3d2.x));
            listTag.add(new DoubleTag(vec3d2.y));
            listTag.add(new DoubleTag(vec3d2.z));
            compoundTag.put("Pos", listTag);
            compoundTag.remove("UUIDMost");
            compoundTag.remove("UUIDLeast");
            Structure.method_17916(iWorld, compoundTag).ifPresent(entity -> {
                float f = entity.applyMirror(blockMirror);
                entity.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f += entity.yaw - entity.applyRotation(blockRotation), entity.pitch);
                iWorld.spawnEntity((Entity)entity);
            });
        }
    }

    private static Optional<Entity> method_17916(IWorld iWorld, CompoundTag compoundTag) {
        try {
            return EntityType.getEntityFromTag(compoundTag, iWorld.getWorld());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public BlockPos method_15166(BlockRotation blockRotation) {
        switch (blockRotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
            }
        }
        return this.size;
    }

    public static BlockPos method_15168(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos2) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        boolean bl = true;
        switch (blockMirror) {
            case LEFT_RIGHT: {
                k = -k;
                break;
            }
            case FRONT_BACK: {
                i = -i;
                break;
            }
            default: {
                bl = false;
            }
        }
        int l = blockPos2.getX();
        int m = blockPos2.getZ();
        switch (blockRotation) {
            case CLOCKWISE_180: {
                return new BlockPos(l + l - i, j, m + m - k);
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos(l - m + k, j, l + m - i);
            }
            case CLOCKWISE_90: {
                return new BlockPos(l + m - k, j, m - l + i);
            }
        }
        return bl ? new BlockPos(i, j, k) : blockPos;
    }

    private static Vec3d method_15176(Vec3d vec3d, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos blockPos) {
        double d = vec3d.x;
        double e = vec3d.y;
        double f = vec3d.z;
        boolean bl = true;
        switch (blockMirror) {
            case LEFT_RIGHT: {
                f = 1.0 - f;
                break;
            }
            case FRONT_BACK: {
                d = 1.0 - d;
                break;
            }
            default: {
                bl = false;
            }
        }
        int i = blockPos.getX();
        int j = blockPos.getZ();
        switch (blockRotation) {
            case CLOCKWISE_180: {
                return new Vec3d((double)(i + i + 1) - d, e, (double)(j + j + 1) - f);
            }
            case COUNTERCLOCKWISE_90: {
                return new Vec3d((double)(i - j) + f, e, (double)(i + j + 1) - d);
            }
            case CLOCKWISE_90: {
                return new Vec3d((double)(i + j + 1) - f, e, (double)(j - i) + d);
            }
        }
        return bl ? new Vec3d(d, e, f) : vec3d;
    }

    public BlockPos method_15167(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation) {
        return Structure.method_15162(blockPos, blockMirror, blockRotation, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos method_15162(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, int i, int j) {
        int k = blockMirror == BlockMirror.FRONT_BACK ? --i : 0;
        int l = blockMirror == BlockMirror.LEFT_RIGHT ? --j : 0;
        BlockPos blockPos2 = blockPos;
        switch (blockRotation) {
            case NONE: {
                blockPos2 = blockPos.add(k, 0, l);
                break;
            }
            case CLOCKWISE_90: {
                blockPos2 = blockPos.add(j - l, 0, k);
                break;
            }
            case CLOCKWISE_180: {
                blockPos2 = blockPos.add(i - k, 0, j - l);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                blockPos2 = blockPos.add(l, 0, i - k);
            }
        }
        return blockPos2;
    }

    public BlockBox calculateBoundingBox(StructurePlacementData structurePlacementData, BlockPos blockPos) {
        BlockRotation blockRotation = structurePlacementData.getRotation();
        BlockPos blockPos2 = structurePlacementData.getPosition();
        BlockPos blockPos3 = this.method_15166(blockRotation);
        BlockMirror blockMirror = structurePlacementData.getMirror();
        int i = blockPos2.getX();
        int j = blockPos2.getZ();
        int k = blockPos3.getX() - 1;
        int l = blockPos3.getY() - 1;
        int m = blockPos3.getZ() - 1;
        BlockBox blockBox = new BlockBox(0, 0, 0, 0, 0, 0);
        switch (blockRotation) {
            case NONE: {
                blockBox = new BlockBox(0, 0, 0, k, l, m);
                break;
            }
            case CLOCKWISE_180: {
                blockBox = new BlockBox(i + i - k, 0, j + j - m, i + i, l, j + j);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                blockBox = new BlockBox(i - j, 0, i + j - m, i - j + k, l, i + j);
                break;
            }
            case CLOCKWISE_90: {
                blockBox = new BlockBox(i + j - k, 0, j - i, i + j, l, j - i + m);
            }
        }
        switch (blockMirror) {
            case NONE: {
                break;
            }
            case FRONT_BACK: {
                this.method_16186(blockRotation, k, m, blockBox, Direction.WEST, Direction.EAST);
                break;
            }
            case LEFT_RIGHT: {
                this.method_16186(blockRotation, m, k, blockBox, Direction.NORTH, Direction.SOUTH);
            }
        }
        blockBox.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return blockBox;
    }

    private void method_16186(BlockRotation blockRotation, int i, int j, BlockBox blockBox, Direction direction, Direction direction2) {
        BlockPos blockPos = BlockPos.ORIGIN;
        blockPos = blockRotation == BlockRotation.CLOCKWISE_90 || blockRotation == BlockRotation.COUNTERCLOCKWISE_90 ? blockPos.offset(blockRotation.rotate(direction), j) : (blockRotation == BlockRotation.CLOCKWISE_180 ? blockPos.offset(direction2, i) : blockPos.offset(direction, i));
        blockBox.offset(blockPos.getX(), 0, blockPos.getZ());
    }

    public CompoundTag toTag(CompoundTag compoundTag) {
        if (this.blocks.isEmpty()) {
            compoundTag.put("blocks", new ListTag());
            compoundTag.put("palette", new ListTag());
        } else {
            ListTag listTag2;
            ArrayList<class_3500> list = Lists.newArrayList();
            class_3500 lv = new class_3500();
            list.add(lv);
            for (int i = 1; i < this.blocks.size(); ++i) {
                list.add(new class_3500());
            }
            ListTag listTag = new ListTag();
            List<StructureBlockInfo> list2 = this.blocks.get(0);
            for (int j = 0; j < list2.size(); ++j) {
                StructureBlockInfo structureBlockInfo = list2.get(j);
                CompoundTag compoundTag2 = new CompoundTag();
                compoundTag2.put("pos", this.createIntListTag(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getY(), structureBlockInfo.pos.getZ()));
                int k = lv.method_15187(structureBlockInfo.state);
                compoundTag2.putInt("state", k);
                if (structureBlockInfo.tag != null) {
                    compoundTag2.put("nbt", structureBlockInfo.tag);
                }
                listTag.add(compoundTag2);
                for (int l = 1; l < this.blocks.size(); ++l) {
                    class_3500 lv2 = (class_3500)list.get(l);
                    lv2.method_15186(this.blocks.get((int)l).get((int)j).state, k);
                }
            }
            compoundTag.put("blocks", listTag);
            if (list.size() == 1) {
                listTag2 = new ListTag();
                for (BlockState blockState : lv) {
                    listTag2.add(NbtHelper.fromBlockState(blockState));
                }
                compoundTag.put("palette", listTag2);
            } else {
                listTag2 = new ListTag();
                for (class_3500 lv3 : list) {
                    ListTag listTag3 = new ListTag();
                    for (BlockState blockState2 : lv3) {
                        listTag3.add(NbtHelper.fromBlockState(blockState2));
                    }
                    listTag2.add(listTag3);
                }
                compoundTag.put("palettes", listTag2);
            }
        }
        ListTag listTag4 = new ListTag();
        for (StructureEntityInfo structureEntityInfo : this.entities) {
            CompoundTag compoundTag3 = new CompoundTag();
            compoundTag3.put("pos", this.createDoubleListTag(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
            compoundTag3.put("blockPos", this.createIntListTag(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ()));
            if (structureEntityInfo.tag != null) {
                compoundTag3.put("nbt", structureEntityInfo.tag);
            }
            listTag4.add(compoundTag3);
        }
        compoundTag.put("entities", listTag4);
        compoundTag.put("size", this.createIntListTag(this.size.getX(), this.size.getY(), this.size.getZ()));
        compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return compoundTag;
    }

    public void fromTag(CompoundTag compoundTag) {
        int i;
        ListTag listTag3;
        this.blocks.clear();
        this.entities.clear();
        ListTag listTag = compoundTag.getList("size", 3);
        this.size = new BlockPos(listTag.getInt(0), listTag.getInt(1), listTag.getInt(2));
        ListTag listTag2 = compoundTag.getList("blocks", 10);
        if (compoundTag.containsKey("palettes", 9)) {
            listTag3 = compoundTag.getList("palettes", 9);
            for (i = 0; i < listTag3.size(); ++i) {
                this.method_15177(listTag3.getListTag(i), listTag2);
            }
        } else {
            this.method_15177(compoundTag.getList("palette", 10), listTag2);
        }
        listTag3 = compoundTag.getList("entities", 10);
        for (i = 0; i < listTag3.size(); ++i) {
            CompoundTag compoundTag2 = listTag3.getCompoundTag(i);
            ListTag listTag4 = compoundTag2.getList("pos", 6);
            Vec3d vec3d = new Vec3d(listTag4.getDouble(0), listTag4.getDouble(1), listTag4.getDouble(2));
            ListTag listTag5 = compoundTag2.getList("blockPos", 3);
            BlockPos blockPos = new BlockPos(listTag5.getInt(0), listTag5.getInt(1), listTag5.getInt(2));
            if (!compoundTag2.containsKey("nbt")) continue;
            CompoundTag compoundTag3 = compoundTag2.getCompound("nbt");
            this.entities.add(new StructureEntityInfo(vec3d, blockPos, compoundTag3));
        }
    }

    private void method_15177(ListTag listTag, ListTag listTag2) {
        int i;
        class_3500 lv = new class_3500();
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        for (i = 0; i < listTag.size(); ++i) {
            lv.method_15186(NbtHelper.toBlockState(listTag.getCompoundTag(i)), i);
        }
        for (i = 0; i < listTag2.size(); ++i) {
            CompoundTag compoundTag = listTag2.getCompoundTag(i);
            ListTag listTag3 = compoundTag.getList("pos", 3);
            BlockPos blockPos = new BlockPos(listTag3.getInt(0), listTag3.getInt(1), listTag3.getInt(2));
            BlockState blockState = lv.method_15185(compoundTag.getInt("state"));
            CompoundTag compoundTag2 = compoundTag.containsKey("nbt") ? compoundTag.getCompound("nbt") : null;
            list.add(new StructureBlockInfo(blockPos, blockState, compoundTag2));
        }
        list.sort(Comparator.comparingInt(structureBlockInfo -> structureBlockInfo.pos.getY()));
        this.blocks.add(list);
    }

    private ListTag createIntListTag(int ... is) {
        ListTag listTag = new ListTag();
        for (int i : is) {
            listTag.add(new IntTag(i));
        }
        return listTag;
    }

    private ListTag createDoubleListTag(double ... ds) {
        ListTag listTag = new ListTag();
        for (double d : ds) {
            listTag.add(new DoubleTag(d));
        }
        return listTag;
    }

    public static class StructureEntityInfo {
        public final Vec3d pos;
        public final BlockPos blockPos;
        public final CompoundTag tag;

        public StructureEntityInfo(Vec3d vec3d, BlockPos blockPos, CompoundTag compoundTag) {
            this.pos = vec3d;
            this.blockPos = blockPos;
            this.tag = compoundTag;
        }
    }

    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag tag;

        public StructureBlockInfo(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
            this.pos = blockPos;
            this.state = blockState;
            this.tag = compoundTag;
        }

        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.tag);
        }
    }

    static class class_3500
    implements Iterable<BlockState> {
        public static final BlockState field_15590 = Blocks.AIR.getDefaultState();
        private final IdList<BlockState> field_15591 = new IdList(16);
        private int field_15592;

        private class_3500() {
        }

        public int method_15187(BlockState blockState) {
            int i = this.field_15591.getId(blockState);
            if (i == -1) {
                i = this.field_15592++;
                this.field_15591.set(blockState, i);
            }
            return i;
        }

        @Nullable
        public BlockState method_15185(int i) {
            BlockState blockState = this.field_15591.get(i);
            return blockState == null ? field_15590 : blockState;
        }

        @Override
        public Iterator<BlockState> iterator() {
            return this.field_15591.iterator();
        }

        public void method_15186(BlockState blockState, int i) {
            this.field_15591.set(blockState, i);
        }
    }
}

