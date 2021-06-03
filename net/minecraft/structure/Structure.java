/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class Structure {
    public static final String PALETTE_KEY = "palette";
    public static final String PALETTES_KEY = "palettes";
    public static final String ENTITIES_KEY = "entities";
    public static final String BLOCKS_KEY = "blocks";
    public static final String BLOCKS_POS_KEY = "pos";
    public static final String BLOCKS_STATE_KEY = "state";
    public static final String BLOCKS_NBT_KEY = "nbt";
    public static final String ENTITIES_POS_KEY = "pos";
    public static final String ENTITIES_BLOCK_POS_KEY = "blockPos";
    public static final String ENTITIES_NBT_KEY = "nbt";
    public static final String SIZE_KEY = "size";
    static final int field_31698 = 16;
    private final List<PalettedBlockInfoList> blockInfoLists = Lists.newArrayList();
    private final List<StructureEntityInfo> entities = Lists.newArrayList();
    private Vec3i size = Vec3i.ZERO;
    private String author = "?";

    public Vec3i getSize() {
        return this.size;
    }

    public void setAuthor(String name) {
        this.author = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void saveFromWorld(World world, BlockPos start, Vec3i dimensions, boolean includeEntities, @Nullable Block ignoredBlock) {
        if (dimensions.getX() < 1 || dimensions.getY() < 1 || dimensions.getZ() < 1) {
            return;
        }
        BlockPos blockPos = start.add(dimensions).add(-1, -1, -1);
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list2 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list3 = Lists.newArrayList();
        BlockPos blockPos2 = new BlockPos(Math.min(start.getX(), blockPos.getX()), Math.min(start.getY(), blockPos.getY()), Math.min(start.getZ(), blockPos.getZ()));
        BlockPos blockPos3 = new BlockPos(Math.max(start.getX(), blockPos.getX()), Math.max(start.getY(), blockPos.getY()), Math.max(start.getZ(), blockPos.getZ()));
        this.size = dimensions;
        for (BlockPos blockPos4 : BlockPos.iterate(blockPos2, blockPos3)) {
            StructureBlockInfo structureBlockInfo;
            BlockPos blockPos5 = blockPos4.subtract(blockPos2);
            BlockState blockState = world.getBlockState(blockPos4);
            if (ignoredBlock != null && blockState.isOf(ignoredBlock)) continue;
            BlockEntity blockEntity = world.getBlockEntity(blockPos4);
            if (blockEntity != null) {
                NbtCompound nbtCompound = blockEntity.writeNbt(new NbtCompound());
                nbtCompound.remove("x");
                nbtCompound.remove("y");
                nbtCompound.remove("z");
                structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, nbtCompound.copy());
            } else {
                structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, null);
            }
            Structure.method_28054(structureBlockInfo, list, list2, list3);
        }
        List<StructureBlockInfo> list4 = Structure.method_28055(list, list2, list3);
        this.blockInfoLists.clear();
        this.blockInfoLists.add(new PalettedBlockInfoList(list4));
        if (includeEntities) {
            this.addEntitiesFromWorld(world, blockPos2, blockPos3.add(1, 1, 1));
        } else {
            this.entities.clear();
        }
    }

    private static void method_28054(StructureBlockInfo structureBlockInfo, List<StructureBlockInfo> list, List<StructureBlockInfo> list2, List<StructureBlockInfo> list3) {
        if (structureBlockInfo.nbt != null) {
            list2.add(structureBlockInfo);
        } else if (!structureBlockInfo.state.getBlock().hasDynamicBounds() && structureBlockInfo.state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) {
            list.add(structureBlockInfo);
        } else {
            list3.add(structureBlockInfo);
        }
    }

    private static List<StructureBlockInfo> method_28055(List<StructureBlockInfo> list, List<StructureBlockInfo> list2, List<StructureBlockInfo> list3) {
        Comparator<StructureBlockInfo> comparator = Comparator.comparingInt(structureBlockInfo -> structureBlockInfo.pos.getY()).thenComparingInt(structureBlockInfo -> structureBlockInfo.pos.getX()).thenComparingInt(structureBlockInfo -> structureBlockInfo.pos.getZ());
        list.sort(comparator);
        list3.sort(comparator);
        list2.sort(comparator);
        ArrayList<StructureBlockInfo> list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list3);
        list4.addAll(list2);
        return list4;
    }

    private void addEntitiesFromWorld(World world, BlockPos firstCorner, BlockPos secondCorner) {
        List<Entity> list = world.getEntitiesByClass(Entity.class, new Box(firstCorner, secondCorner), entity -> !(entity instanceof PlayerEntity));
        this.entities.clear();
        for (Entity entity2 : list) {
            Vec3d vec3d = new Vec3d(entity2.getX() - (double)firstCorner.getX(), entity2.getY() - (double)firstCorner.getY(), entity2.getZ() - (double)firstCorner.getZ());
            NbtCompound nbtCompound = new NbtCompound();
            entity2.saveNbt(nbtCompound);
            BlockPos blockPos = entity2 instanceof PaintingEntity ? ((PaintingEntity)entity2).getDecorationBlockPos().subtract(firstCorner) : new BlockPos(vec3d);
            this.entities.add(new StructureEntityInfo(vec3d, blockPos, nbtCompound.copy()));
        }
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block) {
        return this.getInfosForBlock(pos, placementData, block, true);
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block, boolean transformed) {
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        BlockBox blockBox = placementData.getBoundingBox();
        if (this.blockInfoLists.isEmpty()) {
            return Collections.emptyList();
        }
        for (StructureBlockInfo structureBlockInfo : placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAllOf(block)) {
            BlockPos blockPos;
            BlockPos blockPos2 = blockPos = transformed ? Structure.transform(placementData, structureBlockInfo.pos).add(pos) : structureBlockInfo.pos;
            if (blockBox != null && !blockBox.contains(blockPos)) continue;
            list.add(new StructureBlockInfo(blockPos, structureBlockInfo.state.rotate(placementData.getRotation()), structureBlockInfo.nbt));
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

    public boolean place(ServerWorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, Random random, int i) {
        if (this.blockInfoLists.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> list = placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAll();
        if (list.isEmpty() && (placementData.shouldIgnoreEntities() || this.entities.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BlockBox blockBox = placementData.getBoundingBox();
        ArrayList<BlockPos> list2 = Lists.newArrayListWithCapacity(placementData.shouldPlaceFluids() ? list.size() : 0);
        ArrayList<BlockPos> list3 = Lists.newArrayListWithCapacity(placementData.shouldPlaceFluids() ? list.size() : 0);
        ArrayList<Pair<BlockPos, NbtCompound>> list4 = Lists.newArrayListWithCapacity(list.size());
        int j = Integer.MAX_VALUE;
        int k = Integer.MAX_VALUE;
        int l = Integer.MAX_VALUE;
        int m = Integer.MIN_VALUE;
        int n = Integer.MIN_VALUE;
        int o = Integer.MIN_VALUE;
        List<StructureBlockInfo> list5 = Structure.process(world, pos, pivot, placementData, list);
        for (StructureBlockInfo structureBlockInfo : list5) {
            BlockEntity blockEntity;
            BlockPos blockPos = structureBlockInfo.pos;
            if (blockBox != null && !blockBox.contains(blockPos)) continue;
            FluidState fluidState = placementData.shouldPlaceFluids() ? world.getFluidState(blockPos) : null;
            BlockState blockState = structureBlockInfo.state.mirror(placementData.getMirror()).rotate(placementData.getRotation());
            if (structureBlockInfo.nbt != null) {
                blockEntity = world.getBlockEntity(blockPos);
                Clearable.clear(blockEntity);
                world.setBlockState(blockPos, Blocks.BARRIER.getDefaultState(), Block.NO_REDRAW | Block.FORCE_STATE);
            }
            if (!world.setBlockState(blockPos, blockState, i)) continue;
            j = Math.min(j, blockPos.getX());
            k = Math.min(k, blockPos.getY());
            l = Math.min(l, blockPos.getZ());
            m = Math.max(m, blockPos.getX());
            n = Math.max(n, blockPos.getY());
            o = Math.max(o, blockPos.getZ());
            list4.add(Pair.of(blockPos, structureBlockInfo.nbt));
            if (structureBlockInfo.nbt != null && (blockEntity = world.getBlockEntity(blockPos)) != null) {
                structureBlockInfo.nbt.putInt("x", blockPos.getX());
                structureBlockInfo.nbt.putInt("y", blockPos.getY());
                structureBlockInfo.nbt.putInt("z", blockPos.getZ());
                if (blockEntity instanceof LootableContainerBlockEntity) {
                    structureBlockInfo.nbt.putLong("LootTableSeed", random.nextLong());
                }
                blockEntity.readNbt(structureBlockInfo.nbt);
            }
            if (fluidState == null) continue;
            if (blockState.getFluidState().isStill()) {
                list3.add(blockPos);
                continue;
            }
            if (!(blockState.getBlock() instanceof FluidFillable)) continue;
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
                Object block;
                BlockPos blockPos = (BlockPos)iterator.next();
                FluidState fluidState2 = world.getFluidState(blockPos);
                for (int p = 0; p < directions.length && !fluidState2.isStill(); ++p) {
                    BlockPos blockPos3 = blockPos.offset(directions[p]);
                    FluidState fluidState = world.getFluidState(blockPos3);
                    if (!fluidState.isStill() || list3.contains(blockPos3)) continue;
                    fluidState2 = fluidState;
                }
                if (!fluidState2.isStill() || !((block = (blockState2 = world.getBlockState(blockPos)).getBlock()) instanceof FluidFillable)) continue;
                ((FluidFillable)block).tryFillWithFluid(world, blockPos, blockState2, fluidState2);
                bl = true;
                iterator.remove();
            }
        }
        if (j <= m) {
            if (!placementData.shouldUpdateNeighbors()) {
                BitSetVoxelSet voxelSet = new BitSetVoxelSet(m - j + 1, n - k + 1, o - l + 1);
                int n2 = j;
                int r = k;
                int p = l;
                for (Pair pair : list4) {
                    BlockPos blockPos4 = (BlockPos)pair.getFirst();
                    ((VoxelSet)voxelSet).set(blockPos4.getX() - n2, blockPos4.getY() - r, blockPos4.getZ() - p);
                }
                Structure.updateCorner(world, i, voxelSet, n2, r, p);
            }
            for (Pair pair : list4) {
                BlockEntity blockEntity;
                BlockPos blockPos5 = (BlockPos)pair.getFirst();
                if (!placementData.shouldUpdateNeighbors()) {
                    BlockState blockState3;
                    BlockState blockState2 = world.getBlockState(blockPos5);
                    if (blockState2 != (blockState3 = Block.postProcessState(blockState2, world, blockPos5))) {
                        world.setBlockState(blockPos5, blockState3, i & ~Block.NOTIFY_NEIGHBORS | Block.FORCE_STATE);
                    }
                    world.updateNeighbors(blockPos5, blockState3.getBlock());
                }
                if (pair.getSecond() == null || (blockEntity = world.getBlockEntity(blockPos5)) == null) continue;
                blockEntity.markDirty();
            }
        }
        if (!placementData.shouldIgnoreEntities()) {
            this.spawnEntities(world, pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition(), blockBox, placementData.method_27265());
        }
        return true;
    }

    public static void updateCorner(WorldAccess world, int flags, VoxelSet voxelSet, int startX, int startY, int startZ) {
        voxelSet.forEachDirection((direction, m, n, o) -> {
            BlockState blockState4;
            BlockState blockState2;
            BlockState blockState3;
            BlockPos blockPos = new BlockPos(startX + m, startY + n, startZ + o);
            BlockPos blockPos2 = blockPos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState != (blockState3 = blockState.getStateForNeighborUpdate(direction, blockState2 = world.getBlockState(blockPos2), world, blockPos, blockPos2))) {
                world.setBlockState(blockPos, blockState3, flags & ~Block.NOTIFY_NEIGHBORS);
            }
            if (blockState2 != (blockState4 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState3, world, blockPos2, blockPos))) {
                world.setBlockState(blockPos2, blockState4, flags & ~Block.NOTIFY_NEIGHBORS);
            }
        });
    }

    public static List<StructureBlockInfo> process(WorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, List<StructureBlockInfo> list) {
        ArrayList<StructureBlockInfo> list2 = Lists.newArrayList();
        for (StructureBlockInfo structureBlockInfo : list) {
            BlockPos blockPos = Structure.transform(placementData, structureBlockInfo.pos).add(pos);
            StructureBlockInfo structureBlockInfo2 = new StructureBlockInfo(blockPos, structureBlockInfo.state, structureBlockInfo.nbt != null ? structureBlockInfo.nbt.copy() : null);
            Iterator<StructureProcessor> iterator = placementData.getProcessors().iterator();
            while (structureBlockInfo2 != null && iterator.hasNext()) {
                structureBlockInfo2 = iterator.next().process(world, pos, pivot, structureBlockInfo, structureBlockInfo2, placementData);
            }
            if (structureBlockInfo2 == null) continue;
            list2.add(structureBlockInfo2);
        }
        return list2;
    }

    private void spawnEntities(ServerWorldAccess world, BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot, @Nullable BlockBox area, boolean bl) {
        for (StructureEntityInfo structureEntityInfo : this.entities) {
            BlockPos blockPos = Structure.transformAround(structureEntityInfo.blockPos, mirror, rotation, pivot).add(pos);
            if (area != null && !area.contains(blockPos)) continue;
            NbtCompound nbtCompound = structureEntityInfo.nbt.copy();
            Vec3d vec3d = Structure.transformAround(structureEntityInfo.pos, mirror, rotation, pivot);
            Vec3d vec3d2 = vec3d.add(pos.getX(), pos.getY(), pos.getZ());
            NbtList nbtList = new NbtList();
            nbtList.add(NbtDouble.of(vec3d2.x));
            nbtList.add(NbtDouble.of(vec3d2.y));
            nbtList.add(NbtDouble.of(vec3d2.z));
            nbtCompound.put("Pos", nbtList);
            nbtCompound.remove("UUID");
            Structure.getEntity(world, nbtCompound).ifPresent(entity -> {
                float f = entity.applyMirror(mirror);
                entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f += entity.getYaw() - entity.applyRotation(rotation), entity.getPitch());
                if (bl && entity instanceof MobEntity) {
                    ((MobEntity)entity).initialize(world, world.getLocalDifficulty(new BlockPos(vec3d2)), SpawnReason.STRUCTURE, null, nbtCompound);
                }
                world.spawnEntityAndPassengers((Entity)entity);
            });
        }
    }

    private static Optional<Entity> getEntity(ServerWorldAccess world, NbtCompound nbt) {
        try {
            return EntityType.getEntityFromNbt(nbt, world.toServerWorld());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public Vec3i getRotatedSize(BlockRotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                return new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
            }
        }
        return this.size;
    }

    public static BlockPos transformAround(BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean bl = true;
        switch (mirror) {
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
        switch (rotation) {
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

    public static Vec3d transformAround(Vec3d point, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {
        double d = point.x;
        double e = point.y;
        double f = point.z;
        boolean bl = true;
        switch (mirror) {
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
        switch (rotation) {
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

    public BlockPos offsetByTransformedSize(BlockPos pos, BlockMirror mirror, BlockRotation rotation) {
        return Structure.applyTransformedOffset(pos, mirror, rotation, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos applyTransformedOffset(BlockPos pos, BlockMirror mirror, BlockRotation rotation, int offsetX, int offsetZ) {
        int i = mirror == BlockMirror.FRONT_BACK ? --offsetX : 0;
        int j = mirror == BlockMirror.LEFT_RIGHT ? --offsetZ : 0;
        BlockPos blockPos = pos;
        switch (rotation) {
            case NONE: {
                blockPos = pos.add(i, 0, j);
                break;
            }
            case CLOCKWISE_90: {
                blockPos = pos.add(offsetZ - j, 0, i);
                break;
            }
            case CLOCKWISE_180: {
                blockPos = pos.add(offsetX - i, 0, offsetZ - j);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                blockPos = pos.add(j, 0, offsetX - i);
            }
        }
        return blockPos;
    }

    public BlockBox calculateBoundingBox(StructurePlacementData placementData, BlockPos pos) {
        return this.calculateBoundingBox(pos, placementData.getRotation(), placementData.getPosition(), placementData.getMirror());
    }

    public BlockBox calculateBoundingBox(BlockPos pos, BlockRotation rotation, BlockPos pivot, BlockMirror mirror) {
        return Structure.createBox(pos, rotation, pivot, mirror, this.size);
    }

    @VisibleForTesting
    protected static BlockBox createBox(BlockPos pos, BlockRotation rotation, BlockPos pivot, BlockMirror mirror, Vec3i dimensions) {
        Vec3i vec3i = dimensions.add(-1, -1, -1);
        BlockPos blockPos = Structure.transformAround(BlockPos.ORIGIN, mirror, rotation, pivot);
        BlockPos blockPos2 = Structure.transformAround(BlockPos.ORIGIN.add(vec3i), mirror, rotation, pivot);
        return BlockBox.create(blockPos, blockPos2).move(pos);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        if (this.blockInfoLists.isEmpty()) {
            nbt.put(BLOCKS_KEY, new NbtList());
            nbt.put(PALETTE_KEY, new NbtList());
        } else {
            ArrayList<Palette> list = Lists.newArrayList();
            Palette palette = new Palette();
            list.add(palette);
            for (int i = 1; i < this.blockInfoLists.size(); ++i) {
                list.add(new Palette());
            }
            NbtList nbtList = new NbtList();
            List<StructureBlockInfo> list2 = this.blockInfoLists.get(0).getAll();
            for (int j = 0; j < list2.size(); ++j) {
                StructureBlockInfo structureBlockInfo = list2.get(j);
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.put("pos", this.createNbtIntList(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getY(), structureBlockInfo.pos.getZ()));
                int k = palette.getId(structureBlockInfo.state);
                nbtCompound.putInt(BLOCKS_STATE_KEY, k);
                if (structureBlockInfo.nbt != null) {
                    nbtCompound.put("nbt", structureBlockInfo.nbt);
                }
                nbtList.add(nbtCompound);
                for (int l = 1; l < this.blockInfoLists.size(); ++l) {
                    Palette palette2 = (Palette)list.get(l);
                    palette2.set(this.blockInfoLists.get((int)l).getAll().get((int)j).state, k);
                }
            }
            nbt.put(BLOCKS_KEY, nbtList);
            if (list.size() == 1) {
                nbtList2 = new NbtList();
                for (BlockState blockState : palette) {
                    nbtList2.add(NbtHelper.fromBlockState(blockState));
                }
                nbt.put(PALETTE_KEY, nbtList2);
            } else {
                nbtList2 = new NbtList();
                for (Palette palette3 : list) {
                    NbtList nbtList3 = new NbtList();
                    for (BlockState blockState2 : palette3) {
                        nbtList3.add(NbtHelper.fromBlockState(blockState2));
                    }
                    nbtList2.add(nbtList3);
                }
                nbt.put(PALETTES_KEY, nbtList2);
            }
        }
        NbtList nbtList4 = new NbtList();
        for (StructureEntityInfo structureEntityInfo : this.entities) {
            NbtCompound nbtCompound2 = new NbtCompound();
            nbtCompound2.put("pos", this.createNbtDoubleList(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
            nbtCompound2.put(ENTITIES_BLOCK_POS_KEY, this.createNbtIntList(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ()));
            if (structureEntityInfo.nbt != null) {
                nbtCompound2.put("nbt", structureEntityInfo.nbt);
            }
            nbtList4.add(nbtCompound2);
        }
        nbt.put(ENTITIES_KEY, nbtList4);
        nbt.put(SIZE_KEY, this.createNbtIntList(this.size.getX(), this.size.getY(), this.size.getZ()));
        nbt.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        int i;
        NbtList nbtList3;
        this.blockInfoLists.clear();
        this.entities.clear();
        NbtList nbtList = nbt.getList(SIZE_KEY, 3);
        this.size = new Vec3i(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
        NbtList nbtList2 = nbt.getList(BLOCKS_KEY, 10);
        if (nbt.contains(PALETTES_KEY, 9)) {
            nbtList3 = nbt.getList(PALETTES_KEY, 9);
            for (i = 0; i < nbtList3.size(); ++i) {
                this.loadPalettedBlockInfo(nbtList3.getList(i), nbtList2);
            }
        } else {
            this.loadPalettedBlockInfo(nbt.getList(PALETTE_KEY, 10), nbtList2);
        }
        nbtList3 = nbt.getList(ENTITIES_KEY, 10);
        for (i = 0; i < nbtList3.size(); ++i) {
            NbtCompound nbtCompound = nbtList3.getCompound(i);
            NbtList nbtList4 = nbtCompound.getList("pos", 6);
            Vec3d vec3d = new Vec3d(nbtList4.getDouble(0), nbtList4.getDouble(1), nbtList4.getDouble(2));
            NbtList nbtList5 = nbtCompound.getList(ENTITIES_BLOCK_POS_KEY, 3);
            BlockPos blockPos = new BlockPos(nbtList5.getInt(0), nbtList5.getInt(1), nbtList5.getInt(2));
            if (!nbtCompound.contains("nbt")) continue;
            NbtCompound nbtCompound2 = nbtCompound.getCompound("nbt");
            this.entities.add(new StructureEntityInfo(vec3d, blockPos, nbtCompound2));
        }
    }

    private void loadPalettedBlockInfo(NbtList paletteNbt, NbtList blocksNbt) {
        Palette palette = new Palette();
        for (int i = 0; i < paletteNbt.size(); ++i) {
            palette.set(NbtHelper.toBlockState(paletteNbt.getCompound(i)), i);
        }
        ArrayList<StructureBlockInfo> list = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list2 = Lists.newArrayList();
        ArrayList<StructureBlockInfo> list3 = Lists.newArrayList();
        for (int j = 0; j < blocksNbt.size(); ++j) {
            NbtCompound nbtCompound = blocksNbt.getCompound(j);
            NbtList nbtList = nbtCompound.getList("pos", 3);
            BlockPos blockPos = new BlockPos(nbtList.getInt(0), nbtList.getInt(1), nbtList.getInt(2));
            BlockState blockState = palette.getState(nbtCompound.getInt(BLOCKS_STATE_KEY));
            NbtCompound nbtCompound2 = nbtCompound.contains("nbt") ? nbtCompound.getCompound("nbt") : null;
            StructureBlockInfo structureBlockInfo = new StructureBlockInfo(blockPos, blockState, nbtCompound2);
            Structure.method_28054(structureBlockInfo, list, list2, list3);
        }
        List<StructureBlockInfo> list4 = Structure.method_28055(list, list2, list3);
        this.blockInfoLists.add(new PalettedBlockInfoList(list4));
    }

    private NbtList createNbtIntList(int ... ints) {
        NbtList nbtList = new NbtList();
        for (int i : ints) {
            nbtList.add(NbtInt.of(i));
        }
        return nbtList;
    }

    private NbtList createNbtDoubleList(double ... doubles) {
        NbtList nbtList = new NbtList();
        for (double d : doubles) {
            nbtList.add(NbtDouble.of(d));
        }
        return nbtList;
    }

    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final NbtCompound nbt;

        public StructureBlockInfo(BlockPos pos, BlockState state, @Nullable NbtCompound nbt) {
            this.pos = pos;
            this.state = state;
            this.nbt = nbt;
        }

        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
        }
    }

    public static final class PalettedBlockInfoList {
        private final List<StructureBlockInfo> infos;
        private final Map<Block, List<StructureBlockInfo>> blockToInfos = Maps.newHashMap();

        PalettedBlockInfoList(List<StructureBlockInfo> infos) {
            this.infos = infos;
        }

        public List<StructureBlockInfo> getAll() {
            return this.infos;
        }

        public List<StructureBlockInfo> getAllOf(Block block2) {
            return this.blockToInfos.computeIfAbsent(block2, block -> this.infos.stream().filter(structureBlockInfo -> structureBlockInfo.state.isOf((Block)block)).collect(Collectors.toList()));
        }
    }

    public static class StructureEntityInfo {
        public final Vec3d pos;
        public final BlockPos blockPos;
        public final NbtCompound nbt;

        public StructureEntityInfo(Vec3d pos, BlockPos blockPos, NbtCompound nbt) {
            this.pos = pos;
            this.blockPos = blockPos;
            this.nbt = nbt;
        }
    }

    static class Palette
    implements Iterable<BlockState> {
        public static final BlockState AIR = Blocks.AIR.getDefaultState();
        private final IdList<BlockState> ids = new IdList(16);
        private int currentIndex;

        Palette() {
        }

        public int getId(BlockState state) {
            int i = this.ids.getRawId(state);
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

