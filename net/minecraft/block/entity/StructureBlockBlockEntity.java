/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public class StructureBlockBlockEntity
extends BlockEntity {
    private static final int field_31367 = 5;
    public static final int field_31364 = 48;
    public static final int field_31365 = 48;
    public static final String AUTHOR_KEY = "author";
    private Identifier structureName;
    private String author = "";
    private String metadata = "";
    private BlockPos offset = new BlockPos(0, 1, 0);
    private Vec3i size = Vec3i.ZERO;
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private StructureBlockMode mode;
    private boolean ignoreEntities = true;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox = true;
    private float integrity = 1.0f;
    private long seed;

    public StructureBlockBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.STRUCTURE_BLOCK, pos, state);
        this.mode = state.get(StructureBlock.MODE);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("name", this.getStructureName());
        nbt.putString(AUTHOR_KEY, this.author);
        nbt.putString("metadata", this.metadata);
        nbt.putInt("posX", this.offset.getX());
        nbt.putInt("posY", this.offset.getY());
        nbt.putInt("posZ", this.offset.getZ());
        nbt.putInt("sizeX", this.size.getX());
        nbt.putInt("sizeY", this.size.getY());
        nbt.putInt("sizeZ", this.size.getZ());
        nbt.putString("rotation", this.rotation.toString());
        nbt.putString("mirror", this.mirror.toString());
        nbt.putString("mode", this.mode.toString());
        nbt.putBoolean("ignoreEntities", this.ignoreEntities);
        nbt.putBoolean("powered", this.powered);
        nbt.putBoolean("showair", this.showAir);
        nbt.putBoolean("showboundingbox", this.showBoundingBox);
        nbt.putFloat("integrity", this.integrity);
        nbt.putLong("seed", this.seed);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.setStructureName(nbt.getString("name"));
        this.author = nbt.getString(AUTHOR_KEY);
        this.metadata = nbt.getString("metadata");
        int i = MathHelper.clamp(nbt.getInt("posX"), -48, 48);
        int j = MathHelper.clamp(nbt.getInt("posY"), -48, 48);
        int k = MathHelper.clamp(nbt.getInt("posZ"), -48, 48);
        this.offset = new BlockPos(i, j, k);
        int l = MathHelper.clamp(nbt.getInt("sizeX"), 0, 48);
        int m = MathHelper.clamp(nbt.getInt("sizeY"), 0, 48);
        int n = MathHelper.clamp(nbt.getInt("sizeZ"), 0, 48);
        this.size = new Vec3i(l, m, n);
        try {
            this.rotation = BlockRotation.valueOf(nbt.getString("rotation"));
        } catch (IllegalArgumentException illegalArgumentException) {
            this.rotation = BlockRotation.NONE;
        }
        try {
            this.mirror = BlockMirror.valueOf(nbt.getString("mirror"));
        } catch (IllegalArgumentException illegalArgumentException) {
            this.mirror = BlockMirror.NONE;
        }
        try {
            this.mode = StructureBlockMode.valueOf(nbt.getString("mode"));
        } catch (IllegalArgumentException illegalArgumentException) {
            this.mode = StructureBlockMode.DATA;
        }
        this.ignoreEntities = nbt.getBoolean("ignoreEntities");
        this.powered = nbt.getBoolean("powered");
        this.showAir = nbt.getBoolean("showair");
        this.showBoundingBox = nbt.getBoolean("showboundingbox");
        this.integrity = nbt.contains("integrity") ? nbt.getFloat("integrity") : 1.0f;
        this.seed = nbt.getLong("seed");
        this.updateBlockMode();
    }

    private void updateBlockMode() {
        if (this.world == null) {
            return;
        }
        BlockPos blockPos = this.getPos();
        BlockState blockState = this.world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
            this.world.setBlockState(blockPos, (BlockState)blockState.with(StructureBlock.MODE, this.mode), Block.NOTIFY_LISTENERS);
        }
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public boolean openScreen(PlayerEntity player) {
        if (!player.isCreativeLevelTwoOp()) {
            return false;
        }
        if (player.getEntityWorld().isClient) {
            player.openStructureBlockScreen(this);
        }
        return true;
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public String getStructurePath() {
        return this.structureName == null ? "" : this.structureName.getPath();
    }

    public boolean hasStructureName() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String name) {
        this.setStructureName(ChatUtil.isEmpty(name) ? null : Identifier.tryParse(name));
    }

    public void setStructureName(@Nullable Identifier structureName) {
        this.structureName = structureName;
    }

    public void setAuthor(LivingEntity entity) {
        this.author = entity.getName().getString();
    }

    public BlockPos getOffset() {
        return this.offset;
    }

    public void setOffset(BlockPos pos) {
        this.offset = pos;
    }

    public Vec3i getSize() {
        return this.size;
    }

    public void setSize(Vec3i size) {
        this.size = size;
    }

    public BlockMirror getMirror() {
        return this.mirror;
    }

    public void setMirror(BlockMirror mirror) {
        this.mirror = mirror;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public void setRotation(BlockRotation rotation) {
        this.rotation = rotation;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public StructureBlockMode getMode() {
        return this.mode;
    }

    public void setMode(StructureBlockMode mode) {
        this.mode = mode;
        BlockState blockState = this.world.getBlockState(this.getPos());
        if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
            this.world.setBlockState(this.getPos(), (BlockState)blockState.with(StructureBlock.MODE, mode), Block.NOTIFY_LISTENERS);
        }
    }

    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }

    public void setIgnoreEntities(boolean ignoreEntities) {
        this.ignoreEntities = ignoreEntities;
    }

    public float getIntegrity() {
        return this.integrity;
    }

    public void setIntegrity(float integrity) {
        this.integrity = integrity;
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean detectStructureSize() {
        if (this.mode != StructureBlockMode.SAVE) {
            return false;
        }
        BlockPos blockPos = this.getPos();
        int i = 80;
        BlockPos blockPos2 = new BlockPos(blockPos.getX() - 80, this.world.getBottomY(), blockPos.getZ() - 80);
        BlockPos blockPos3 = new BlockPos(blockPos.getX() + 80, this.world.getTopY() - 1, blockPos.getZ() + 80);
        Stream<BlockPos> stream = this.streamCornerPos(blockPos2, blockPos3);
        return StructureBlockBlockEntity.getStructureBox(blockPos, stream).filter(blockBox -> {
            int i = blockBox.getMaxX() - blockBox.getMinX();
            int j = blockBox.getMaxY() - blockBox.getMinY();
            int k = blockBox.getMaxZ() - blockBox.getMinZ();
            if (i > 1 && j > 1 && k > 1) {
                this.offset = new BlockPos(blockBox.getMinX() - blockPos.getX() + 1, blockBox.getMinY() - blockPos.getY() + 1, blockBox.getMinZ() - blockPos.getZ() + 1);
                this.size = new Vec3i(i - 1, j - 1, k - 1);
                this.markDirty();
                BlockState blockState = this.world.getBlockState(blockPos);
                this.world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
                return true;
            }
            return false;
        }).isPresent();
    }

    /**
     * Streams positions of {@link StructureBlockMode#CORNER} mode structure blocks with matching names.
     */
    private Stream<BlockPos> streamCornerPos(BlockPos start, BlockPos end) {
        return BlockPos.stream(start, end).filter(pos -> this.world.getBlockState((BlockPos)pos).isOf(Blocks.STRUCTURE_BLOCK)).map(this.world::getBlockEntity).filter(blockEntity -> blockEntity instanceof StructureBlockBlockEntity).map(blockEntity -> (StructureBlockBlockEntity)blockEntity).filter(blockEntity -> blockEntity.mode == StructureBlockMode.CORNER && Objects.equals(this.structureName, blockEntity.structureName)).map(BlockEntity::getPos);
    }

    private static Optional<BlockBox> getStructureBox(BlockPos pos, Stream<BlockPos> corners) {
        Iterator iterator = corners.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        BlockPos blockPos = (BlockPos)iterator.next();
        BlockBox blockBox = new BlockBox(blockPos);
        if (iterator.hasNext()) {
            iterator.forEachRemaining(blockBox::encompass);
        } else {
            blockBox.encompass(pos);
        }
        return Optional.of(blockBox);
    }

    public boolean saveStructure() {
        return this.saveStructure(true);
    }

    public boolean saveStructure(boolean bl) {
        Structure structure;
        if (this.mode != StructureBlockMode.SAVE || this.world.isClient || this.structureName == null) {
            return false;
        }
        BlockPos blockPos = this.getPos().add(this.offset);
        ServerWorld serverWorld = (ServerWorld)this.world;
        StructureManager structureManager = serverWorld.getStructureManager();
        try {
            structure = structureManager.getStructureOrBlank(this.structureName);
        } catch (InvalidIdentifierException invalidIdentifierException) {
            return false;
        }
        structure.saveFromWorld(this.world, blockPos, this.size, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
        structure.setAuthor(this.author);
        if (bl) {
            try {
                return structureManager.saveStructure(this.structureName);
            } catch (InvalidIdentifierException invalidIdentifierException) {
                return false;
            }
        }
        return true;
    }

    public boolean loadStructure(ServerWorld world) {
        return this.loadStructure(world, true);
    }

    private static Random createRandom(long seed) {
        if (seed == 0L) {
            return new Random(Util.getMeasuringTimeMs());
        }
        return new Random(seed);
    }

    public boolean loadStructure(ServerWorld world, boolean bl) {
        Optional<Structure> optional;
        if (this.mode != StructureBlockMode.LOAD || this.structureName == null) {
            return false;
        }
        StructureManager structureManager = world.getStructureManager();
        try {
            optional = structureManager.getStructure(this.structureName);
        } catch (InvalidIdentifierException invalidIdentifierException) {
            return false;
        }
        if (!optional.isPresent()) {
            return false;
        }
        return this.place(world, bl, optional.get());
    }

    public boolean place(ServerWorld world, boolean bl, Structure structure) {
        Vec3i vec3i;
        boolean bl2;
        BlockPos blockPos = this.getPos();
        if (!ChatUtil.isEmpty(structure.getAuthor())) {
            this.author = structure.getAuthor();
        }
        if (!(bl2 = this.size.equals(vec3i = structure.getSize()))) {
            this.size = vec3i;
            this.markDirty();
            BlockState blockState = world.getBlockState(blockPos);
            world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
        }
        if (!bl || bl2) {
            StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities);
            if (this.integrity < 1.0f) {
                structurePlacementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0f, 1.0f))).setRandom(StructureBlockBlockEntity.createRandom(this.seed));
            }
            BlockPos blockPos2 = blockPos.add(this.offset);
            structure.place(world, blockPos2, blockPos2, structurePlacementData, StructureBlockBlockEntity.createRandom(this.seed), Block.NOTIFY_LISTENERS);
            return true;
        }
        return false;
    }

    public void unloadStructure() {
        if (this.structureName == null) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)this.world;
        StructureManager structureManager = serverWorld.getStructureManager();
        structureManager.unloadStructure(this.structureName);
    }

    public boolean isStructureAvailable() {
        if (this.mode != StructureBlockMode.LOAD || this.world.isClient || this.structureName == null) {
            return false;
        }
        ServerWorld serverWorld = (ServerWorld)this.world;
        StructureManager structureManager = serverWorld.getStructureManager();
        try {
            return structureManager.getStructure(this.structureName).isPresent();
        } catch (InvalidIdentifierException invalidIdentifierException) {
            return false;
        }
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public boolean shouldShowAir() {
        return this.showAir;
    }

    public void setShowAir(boolean showAir) {
        this.showAir = showAir;
    }

    public boolean shouldShowBoundingBox() {
        return this.showBoundingBox;
    }

    public void setShowBoundingBox(boolean showBoundingBox) {
        this.showBoundingBox = showBoundingBox;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }

    private static /* synthetic */ void method_35293(ServerWorld serverWorld, BlockPos blockPos) {
        serverWorld.setBlockState(blockPos, Blocks.STRUCTURE_VOID.getDefaultState(), Block.NOTIFY_LISTENERS);
    }

    public static enum Action {
        UPDATE_DATA,
        SAVE_AREA,
        LOAD_AREA,
        SCAN_AREA;

    }
}

