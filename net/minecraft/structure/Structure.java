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

    public void setAuthor(String name) {
        this.author = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void saveFromWorld(World world, BlockPos start, BlockPos size, boolean includeEntities, @Nullable Block ignoredBlock) {
        if (size.getX() < 1 || size.getY() < 1 || size.getZ() < 1) {
            return;
        }
        BlockPos blockPos = start.add(size).add(-1, -1, -1);
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list2 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list3 = Lists.newArrayList();
        BlockPos blockPos2 = new BlockPos(Math.min(start.getX(), blockPos.getX()), Math.min(start.getY(), blockPos.getY()), Math.min(start.getZ(), blockPos.getZ()));
        BlockPos blockPos3 = new BlockPos(Math.max(start.getX(), blockPos.getX()), Math.max(start.getY(), blockPos.getY()), Math.max(start.getZ(), blockPos.getZ()));
        this.size = size;
        for (BlockPos blockPos4 : BlockPos.iterate(blockPos2, blockPos3)) {
            BlockPos blockPos5 = blockPos4.subtract(blockPos2);
            BlockState blockState = world.getBlockState(blockPos4);
            if (ignoredBlock != null && ignoredBlock == blockState.getBlock()) continue;
            BlockEntity blockEntity = world.getBlockEntity(blockPos4);
            if (blockEntity != null) {
                CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
                compoundTag.remove("x");
                compoundTag.remove("y");
                compoundTag.remove("z");
                list2.add(new StructureBlockInfo(blockPos5, blockState, compoundTag));
                continue;
            }
            if (blockState.isFullOpaque(world, blockPos4) || blockState.isFullCube(world, blockPos4)) {
                list.add(new StructureBlockInfo(blockPos5, blockState, null));
                continue;
            }
            list3.add(new StructureBlockInfo(blockPos5, blockState, null));
        }
        ArrayList<StructureBlockInfo> list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list2);
        list4.addAll(list3);
        this.blocks.clear();
        this.blocks.add(list4);
        if (includeEntities) {
            this.addEntitiesFromWorld(world, blockPos2, blockPos3.add(1, 1, 1));
        } else {
            this.entities.clear();
        }
    }

    private void addEntitiesFromWorld(World world, BlockPos firstCorner, BlockPos secondCorner) {
        List<Entity> list = world.getEntities(Entity.class, new Box(firstCorner, secondCorner), entity -> !(entity instanceof PlayerEntity));
        this.entities.clear();
        for (Entity entity2 : list) {
            Vec3d vec3d = new Vec3d(entity2.getX() - (double)firstCorner.getX(), entity2.getY() - (double)firstCorner.getY(), entity2.getZ() - (double)firstCorner.getZ());
            CompoundTag compoundTag = new CompoundTag();
            entity2.saveToTag(compoundTag);
            BlockPos blockPos = entity2 instanceof PaintingEntity ? ((PaintingEntity)entity2).getDecorationBlockPos().subtract(firstCorner) : new BlockPos(vec3d);
            this.entities.add(new StructureEntityInfo(vec3d, blockPos, compoundTag));
        }
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block) {
        return this.getInfosForBlock(pos, placementData, block, true);
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block, boolean transformed) {
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        BlockBox blockBox = placementData.getBoundingBox();
        for (StructureBlockInfo structureBlockInfo : placementData.getRandomBlockInfos(this.blocks, pos)) {
            BlockState blockState;
            BlockPos blockPos;
            BlockPos blockPos2 = blockPos = transformed ? Structure.transform(placementData, structureBlockInfo.pos).add(pos) : structureBlockInfo.pos;
            if (blockBox != null && !blockBox.contains(blockPos) || (blockState = structureBlockInfo.state).getBlock() != block) continue;
            list.add(new StructureBlockInfo(blockPos, blockState.rotate(placementData.getRotation()), structureBlockInfo.tag));
        }
        return list;
    }

    public BlockPos transformBox(StructurePlacementData placementData1, BlockPos pos1, StructurePlacementData placementData2, BlockPos pos2) {
        BlockPos blockPos = Structure.transform(placementData1, pos1);
        BlockPos blockPos2 = Structure.transform(placementData2, pos2);
        return blockPos.subtract(blockPos2);
    }

    public static BlockPos transform(StructurePlacementData placementData, BlockPos pos) {
        return Structure.transformAround(pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition());
    }

    public void place(IWorld world, BlockPos pos, StructurePlacementData placementData) {
        placementData.calculateBoundingBox();
        this.placeAndNotifyListeners(world, pos, placementData);
    }

    public void placeAndNotifyListeners(IWorld world, BlockPos pos, StructurePlacementData data) {
        this.place(world, pos, data, 2);
    }

    public boolean place(IWorld world, BlockPos pos, StructurePlacementData placementData, int flags) {
        if (this.blocks.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> list = placementData.getRandomBlockInfos(this.blocks, pos);
        if (list.isEmpty() && (placementData.shouldIgnoreEntities() || this.entities.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BlockBox blockBox = placementData.getBoundingBox();
        ArrayList<BlockPos> list2 = Lists.newArrayListWithCapacity(placementData.shouldPlaceFluids() ? list.size() : 0);
        ArrayList<Pair<BlockPos, CompoundTag>> list3 = Lists.newArrayListWithCapacity(list.size());
        int i = Integer.MAX_VALUE;
        int j = Integer.MAX_VALUE;
        int k = Integer.MAX_VALUE;
        int l = Integer.MIN_VALUE;
        int m = Integer.MIN_VALUE;
        int n = Integer.MIN_VALUE;
        List<StructureBlockInfo> list4 = Structure.process(world, pos, placementData, list);
        for (StructureBlockInfo structureBlockInfo : list4) {
            BlockEntity blockEntity;
            BlockPos blockPos = structureBlockInfo.pos;
            if (blockBox != null && !blockBox.contains(blockPos)) continue;
            FluidState fluidState = placementData.shouldPlaceFluids() ? world.getFluidState(blockPos) : null;
            BlockState blockState = structureBlockInfo.state.mirror(placementData.getMirror()).rotate(placementData.getRotation());
            if (structureBlockInfo.tag != null) {
                blockEntity = world.getBlockEntity(blockPos);
                Clearable.clear(blockEntity);
                world.setBlockState(blockPos, Blocks.BARRIER.getDefaultState(), 20);
            }
            if (!world.setBlockState(blockPos, blockState, flags)) continue;
            i = Math.min(i, blockPos.getX());
            j = Math.min(j, blockPos.getY());
            k = Math.min(k, blockPos.getZ());
            l = Math.max(l, blockPos.getX());
            m = Math.max(m, blockPos.getY());
            n = Math.max(n, blockPos.getZ());
            list3.add(Pair.of(blockPos, structureBlockInfo.tag));
            if (structureBlockInfo.tag != null && (blockEntity = world.getBlockEntity(blockPos)) != null) {
                structureBlockInfo.tag.putInt("x", blockPos.getX());
                structureBlockInfo.tag.putInt("y", blockPos.getY());
                structureBlockInfo.tag.putInt("z", blockPos.getZ());
                blockEntity.fromTag(structureBlockInfo.tag);
                blockEntity.applyMirror(placementData.getMirror());
                blockEntity.applyRotation(placementData.getRotation());
            }
            if (fluidState == null || !(blockState.getBlock() instanceof FluidFillable)) continue;
            ((FluidFillable)((Object)blockState.getBlock())).tryFillWithFluid(world, blockPos, blockState, fluidState);
            if (fluidState.isStill()) continue;
            list2.add(blockPos);
        }
        boolean bl = true;
        Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        while (bl && !list2.isEmpty()) {
            bl = false;
            Iterator iterator = list2.iterator();
            while (iterator.hasNext()) {
                BlockState blockState2;
                Block block;
                BlockPos blockPos;
                BlockPos blockPos3 = blockPos = (BlockPos)iterator.next();
                FluidState fluidState2 = world.getFluidState(blockPos3);
                for (int o = 0; o < directions.length && !fluidState2.isStill(); ++o) {
                    BlockPos blockPos2 = blockPos3.offset(directions[o]);
                    FluidState fluidState3 = world.getFluidState(blockPos2);
                    if (!(fluidState3.getHeight(world, blockPos2) > fluidState2.getHeight(world, blockPos3)) && (!fluidState3.isStill() || fluidState2.isStill())) continue;
                    fluidState2 = fluidState3;
                    blockPos3 = blockPos2;
                }
                if (!fluidState2.isStill() || !((block = (blockState2 = world.getBlockState(blockPos)).getBlock()) instanceof FluidFillable)) continue;
                ((FluidFillable)((Object)block)).tryFillWithFluid(world, blockPos, blockState2, fluidState2);
                bl = true;
                iterator.remove();
            }
        }
        if (i <= l) {
            if (!placementData.shouldUpdateNeighbors()) {
                BitSetVoxelSet voxelSet = new BitSetVoxelSet(l - i + 1, m - j + 1, n - k + 1);
                int n2 = i;
                int q = j;
                int r = k;
                for (Pair pair : list3) {
                    BlockPos blockPos5 = (BlockPos)pair.getFirst();
                    ((VoxelSet)voxelSet).set(blockPos5.getX() - n2, blockPos5.getY() - q, blockPos5.getZ() - r, true, true);
                }
                Structure.updateCorner(world, flags, voxelSet, n2, q, r);
            }
            for (Pair pair : list3) {
                BlockEntity blockEntity;
                BlockPos blockPos3 = (BlockPos)pair.getFirst();
                if (!placementData.shouldUpdateNeighbors()) {
                    BlockState blockState2;
                    BlockState blockState3 = world.getBlockState(blockPos3);
                    if (blockState3 != (blockState2 = Block.getRenderingState(blockState3, world, blockPos3))) {
                        world.setBlockState(blockPos3, blockState2, flags & 0xFFFFFFFE | 0x10);
                    }
                    world.updateNeighbors(blockPos3, blockState2.getBlock());
                }
                if (pair.getSecond() == null || (blockEntity = world.getBlockEntity(blockPos3)) == null) continue;
                blockEntity.markDirty();
            }
        }
        if (!placementData.shouldIgnoreEntities()) {
            this.spawnEntities(world, pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition(), blockBox);
        }
        return true;
    }

    public static void updateCorner(IWorld world, int flags, VoxelSet voxelSet, int startX, int startY, int startZ) {
        voxelSet.forEachDirection((direction, m, n, o) -> {
            BlockState blockState4;
            BlockState blockState2;
            BlockState blockState3;
            BlockPos blockPos = new BlockPos(startX + m, startY + n, startZ + o);
            BlockPos blockPos2 = blockPos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState != (blockState3 = blockState.getStateForNeighborUpdate(direction, blockState2 = world.getBlockState(blockPos2), world, blockPos, blockPos2))) {
                world.setBlockState(blockPos, blockState3, flags & 0xFFFFFFFE | 0x10);
            }
            if (blockState2 != (blockState4 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState3, world, blockPos2, blockPos))) {
                world.setBlockState(blockPos2, blockState4, flags & 0xFFFFFFFE | 0x10);
            }
        });
    }

    public static List<StructureBlockInfo> process(IWorld world, BlockPos pos, StructurePlacementData placementData, List<StructureBlockInfo> blockInfos) {
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        for (StructureBlockInfo structureBlockInfo : blockInfos) {
            BlockPos blockPos = Structure.transform(placementData, structureBlockInfo.pos).add(pos);
            StructureBlockInfo structureBlockInfo2 = new StructureBlockInfo(blockPos, structureBlockInfo.state, structureBlockInfo.tag);
            Iterator<StructureProcessor> iterator = placementData.getProcessors().iterator();
            while (structureBlockInfo2 != null && iterator.hasNext()) {
                structureBlockInfo2 = iterator.next().process(world, pos, structureBlockInfo, structureBlockInfo2, placementData);
            }
            if (structureBlockInfo2 == null) continue;
            list.add(structureBlockInfo2);
        }
        return list;
    }

    private void spawnEntities(IWorld world, BlockPos pos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos pivot, @Nullable BlockBox area) {
        for (StructureEntityInfo structureEntityInfo : this.entities) {
            BlockPos blockPos = Structure.transformAround(structureEntityInfo.blockPos, blockMirror, blockRotation, pivot).add(pos);
            if (area != null && !area.contains(blockPos)) continue;
            CompoundTag compoundTag = structureEntityInfo.tag;
            Vec3d vec3d = Structure.transformAround(structureEntityInfo.pos, blockMirror, blockRotation, pivot);
            Vec3d vec3d2 = vec3d.add(pos.getX(), pos.getY(), pos.getZ());
            ListTag listTag = new ListTag();
            listTag.add(DoubleTag.of(vec3d2.x));
            listTag.add(DoubleTag.of(vec3d2.y));
            listTag.add(DoubleTag.of(vec3d2.z));
            compoundTag.put("Pos", listTag);
            compoundTag.remove("UUIDMost");
            compoundTag.remove("UUIDLeast");
            Structure.getEntity(world, compoundTag).ifPresent(entity -> {
                float f = entity.applyMirror(blockMirror);
                entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f += entity.yaw - entity.applyRotation(blockRotation), entity.pitch);
                world.spawnEntity((Entity)entity);
            });
        }
    }

    private static Optional<Entity> getEntity(IWorld iWorld, CompoundTag compoundTag) {
        try {
            return EntityType.getEntityFromTag(compoundTag, iWorld.getWorld());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public BlockPos getRotatedSize(BlockRotation blockRotation) {
        switch (blockRotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
            }
        }
        return this.size;
    }

    public static BlockPos transformAround(BlockPos pos, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos pivot) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
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
        int l = pivot.getX();
        int m = pivot.getZ();
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
        return bl ? new BlockPos(i, j, k) : pos;
    }

    private static Vec3d transformAround(Vec3d point, BlockMirror blockMirror, BlockRotation blockRotation, BlockPos pivot) {
        double d = point.x;
        double e = point.y;
        double f = point.z;
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
        int i = pivot.getX();
        int j = pivot.getZ();
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
        return bl ? new Vec3d(d, e, f) : point;
    }

    public BlockPos offsetByTransformedSize(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation) {
        return Structure.applyTransformedOffset(blockPos, blockMirror, blockRotation, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos applyTransformedOffset(BlockPos blockPos, BlockMirror blockMirror, BlockRotation blockRotation, int offsetX, int offsetZ) {
        int i = blockMirror == BlockMirror.FRONT_BACK ? --offsetX : 0;
        int j = blockMirror == BlockMirror.LEFT_RIGHT ? --offsetZ : 0;
        BlockPos blockPos2 = blockPos;
        switch (blockRotation) {
            case NONE: {
                blockPos2 = blockPos.add(i, 0, j);
                break;
            }
            case CLOCKWISE_90: {
                blockPos2 = blockPos.add(offsetZ - j, 0, i);
                break;
            }
            case CLOCKWISE_180: {
                blockPos2 = blockPos.add(offsetX - i, 0, offsetZ - j);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                blockPos2 = blockPos.add(j, 0, offsetX - i);
            }
        }
        return blockPos2;
    }

    public BlockBox calculateBoundingBox(StructurePlacementData placementData, BlockPos pos) {
        BlockRotation blockRotation = placementData.getRotation();
        BlockPos blockPos = placementData.getPosition();
        BlockPos blockPos2 = this.getRotatedSize(blockRotation);
        BlockMirror blockMirror = placementData.getMirror();
        int i = blockPos.getX();
        int j = blockPos.getZ();
        int k = blockPos2.getX() - 1;
        int l = blockPos2.getY() - 1;
        int m = blockPos2.getZ() - 1;
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
                this.mirrorBoundingBox(blockRotation, k, m, blockBox, Direction.WEST, Direction.EAST);
                break;
            }
            case LEFT_RIGHT: {
                this.mirrorBoundingBox(blockRotation, m, k, blockBox, Direction.NORTH, Direction.SOUTH);
            }
        }
        blockBox.offset(pos.getX(), pos.getY(), pos.getZ());
        return blockBox;
    }

    private void mirrorBoundingBox(BlockRotation rotation, int offsetX, int offsetZ, BlockBox boundingBox, Direction direction, Direction direction2) {
        BlockPos blockPos = BlockPos.ORIGIN;
        blockPos = rotation == BlockRotation.CLOCKWISE_90 || rotation == BlockRotation.COUNTERCLOCKWISE_90 ? blockPos.offset(rotation.rotate(direction), offsetZ) : (rotation == BlockRotation.CLOCKWISE_180 ? blockPos.offset(direction2, offsetX) : blockPos.offset(direction, offsetX));
        boundingBox.offset(blockPos.getX(), 0, blockPos.getZ());
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (this.blocks.isEmpty()) {
            tag.put("blocks", new ListTag());
            tag.put("palette", new ListTag());
        } else {
            ListTag listTag2;
            ArrayList<Palette> list = Lists.newArrayList();
            Palette palette = new Palette();
            list.add(palette);
            for (int i = 1; i < this.blocks.size(); ++i) {
                list.add(new Palette());
            }
            ListTag listTag = new ListTag();
            List<StructureBlockInfo> list2 = this.blocks.get(0);
            for (int j = 0; j < list2.size(); ++j) {
                StructureBlockInfo structureBlockInfo = list2.get(j);
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.put("pos", this.createIntListTag(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getY(), structureBlockInfo.pos.getZ()));
                int k = palette.getId(structureBlockInfo.state);
                compoundTag.putInt("state", k);
                if (structureBlockInfo.tag != null) {
                    compoundTag.put("nbt", structureBlockInfo.tag);
                }
                listTag.add(compoundTag);
                for (int l = 1; l < this.blocks.size(); ++l) {
                    Palette palette2 = (Palette)list.get(l);
                    palette2.set(this.blocks.get((int)l).get((int)j).state, k);
                }
            }
            tag.put("blocks", listTag);
            if (list.size() == 1) {
                listTag2 = new ListTag();
                for (BlockState blockState : palette) {
                    listTag2.add(NbtHelper.fromBlockState(blockState));
                }
                tag.put("palette", listTag2);
            } else {
                listTag2 = new ListTag();
                for (Palette palette3 : list) {
                    ListTag listTag3 = new ListTag();
                    for (BlockState blockState2 : palette3) {
                        listTag3.add(NbtHelper.fromBlockState(blockState2));
                    }
                    listTag2.add(listTag3);
                }
                tag.put("palettes", listTag2);
            }
        }
        ListTag listTag4 = new ListTag();
        for (StructureEntityInfo structureEntityInfo : this.entities) {
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.put("pos", this.createDoubleListTag(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
            compoundTag2.put("blockPos", this.createIntListTag(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ()));
            if (structureEntityInfo.tag != null) {
                compoundTag2.put("nbt", structureEntityInfo.tag);
            }
            listTag4.add(compoundTag2);
        }
        tag.put("entities", listTag4);
        tag.put("size", this.createIntListTag(this.size.getX(), this.size.getY(), this.size.getZ()));
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        int i;
        ListTag listTag3;
        this.blocks.clear();
        this.entities.clear();
        ListTag listTag = tag.getList("size", 3);
        this.size = new BlockPos(listTag.getInt(0), listTag.getInt(1), listTag.getInt(2));
        ListTag listTag2 = tag.getList("blocks", 10);
        if (tag.contains("palettes", 9)) {
            listTag3 = tag.getList("palettes", 9);
            for (i = 0; i < listTag3.size(); ++i) {
                this.method_15177(listTag3.getList(i), listTag2);
            }
        } else {
            this.method_15177(tag.getList("palette", 10), listTag2);
        }
        listTag3 = tag.getList("entities", 10);
        for (i = 0; i < listTag3.size(); ++i) {
            CompoundTag compoundTag = listTag3.getCompound(i);
            ListTag listTag4 = compoundTag.getList("pos", 6);
            Vec3d vec3d = new Vec3d(listTag4.getDouble(0), listTag4.getDouble(1), listTag4.getDouble(2));
            ListTag listTag5 = compoundTag.getList("blockPos", 3);
            BlockPos blockPos = new BlockPos(listTag5.getInt(0), listTag5.getInt(1), listTag5.getInt(2));
            if (!compoundTag.contains("nbt")) continue;
            CompoundTag compoundTag2 = compoundTag.getCompound("nbt");
            this.entities.add(new StructureEntityInfo(vec3d, blockPos, compoundTag2));
        }
    }

    private void method_15177(ListTag listTag, ListTag listTag2) {
        int i;
        Palette palette = new Palette();
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        for (i = 0; i < listTag.size(); ++i) {
            palette.set(NbtHelper.toBlockState(listTag.getCompound(i)), i);
        }
        for (i = 0; i < listTag2.size(); ++i) {
            CompoundTag compoundTag = listTag2.getCompound(i);
            ListTag listTag3 = compoundTag.getList("pos", 3);
            BlockPos blockPos = new BlockPos(listTag3.getInt(0), listTag3.getInt(1), listTag3.getInt(2));
            BlockState blockState = palette.getState(compoundTag.getInt("state"));
            CompoundTag compoundTag2 = compoundTag.contains("nbt") ? compoundTag.getCompound("nbt") : null;
            list.add(new StructureBlockInfo(blockPos, blockState, compoundTag2));
        }
        list.sort(Comparator.comparingInt(structureBlockInfo -> structureBlockInfo.pos.getY()));
        this.blocks.add(list);
    }

    private ListTag createIntListTag(int ... is) {
        ListTag listTag = new ListTag();
        for (int i : is) {
            listTag.add(IntTag.of(i));
        }
        return listTag;
    }

    private ListTag createDoubleListTag(double ... ds) {
        ListTag listTag = new ListTag();
        for (double d : ds) {
            listTag.add(DoubleTag.of(d));
        }
        return listTag;
    }

    public static class StructureEntityInfo {
        public final Vec3d pos;
        public final BlockPos blockPos;
        public final CompoundTag tag;

        public StructureEntityInfo(Vec3d pos, BlockPos blockPos, CompoundTag tag) {
            this.pos = pos;
            this.blockPos = blockPos;
            this.tag = tag;
        }
    }

    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag tag;

        public StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag tag) {
            this.pos = pos;
            this.state = state;
            this.tag = tag;
        }

        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.tag);
        }
    }

    static class Palette
    implements Iterable<BlockState> {
        public static final BlockState AIR = Blocks.AIR.getDefaultState();
        private final IdList<BlockState> ids = new IdList(16);
        private int currentIndex;

        private Palette() {
        }

        public int getId(BlockState state) {
            int i = this.ids.getId(state);
            if (i == -1) {
                i = this.currentIndex++;
                this.ids.set(state, i);
            }
            return i;
        }

        @Nullable
        public BlockState getState(int id) {
            BlockState blockState = this.ids.get(id);
            return blockState == null ? AIR : blockState;
        }

        @Override
        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void set(BlockState state, int id) {
            this.ids.set(state, id);
        }
    }
}

