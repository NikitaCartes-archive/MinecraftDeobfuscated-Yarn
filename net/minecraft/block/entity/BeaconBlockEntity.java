/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeaconBlockEntity
extends BlockEntity
implements NamedScreenHandlerFactory,
Nameable {
    private static final int field_31304 = 4;
    public static final StatusEffect[][] EFFECTS_BY_LEVEL = new StatusEffect[][]{{StatusEffects.SPEED, StatusEffects.HASTE}, {StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST}, {StatusEffects.STRENGTH}, {StatusEffects.REGENERATION}};
    private static final Set<StatusEffect> EFFECTS = Arrays.stream(EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
    public static final int LEVEL_PROPERTY_INDEX = 0;
    public static final int PRIMARY_PROPERTY_INDEX = 1;
    public static final int SECONDARY_PROPERTY_INDEX = 2;
    public static final int PROPERTY_COUNT = 3;
    private static final int field_31305 = 10;
    private static final Text CONTAINER_NAME_TEXT = Text.translatable("container.beacon");
    List<BeamSegment> beamSegments = Lists.newArrayList();
    private List<BeamSegment> field_19178 = Lists.newArrayList();
    int level;
    private int minY;
    @Nullable
    StatusEffect primary;
    @Nullable
    StatusEffect secondary;
    @Nullable
    private Text customName;
    private ContainerLock lock = ContainerLock.EMPTY;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BeaconBlockEntity.this.level;
                case 1 -> StatusEffect.getRawIdNullable(BeaconBlockEntity.this.primary);
                case 2 -> StatusEffect.getRawIdNullable(BeaconBlockEntity.this.secondary);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    BeaconBlockEntity.this.level = value;
                    break;
                }
                case 1: {
                    if (!BeaconBlockEntity.this.world.isClient && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
                        BeaconBlockEntity.playSound(BeaconBlockEntity.this.world, BeaconBlockEntity.this.pos, SoundEvents.BLOCK_BEACON_POWER_SELECT);
                    }
                    BeaconBlockEntity.this.primary = BeaconBlockEntity.getPotionEffectById(value);
                    break;
                }
                case 2: {
                    BeaconBlockEntity.this.secondary = BeaconBlockEntity.getPotionEffectById(value);
                }
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public BeaconBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BEACON, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity) {
        int m;
        BlockPos blockPos;
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (blockEntity.minY < j) {
            blockPos = pos;
            blockEntity.field_19178 = Lists.newArrayList();
            blockEntity.minY = blockPos.getY() - 1;
        } else {
            blockPos = new BlockPos(i, blockEntity.minY + 1, k);
        }
        BeamSegment beamSegment = blockEntity.field_19178.isEmpty() ? null : blockEntity.field_19178.get(blockEntity.field_19178.size() - 1);
        int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);
        for (m = 0; m < 10 && blockPos.getY() <= l; ++m) {
            block18: {
                BlockState blockState;
                block16: {
                    float[] fs;
                    block17: {
                        blockState = world.getBlockState(blockPos);
                        Block block = blockState.getBlock();
                        if (!(block instanceof Stainable)) break block16;
                        fs = ((Stainable)((Object)block)).getColor().getColorComponents();
                        if (blockEntity.field_19178.size() > 1) break block17;
                        beamSegment = new BeamSegment(fs);
                        blockEntity.field_19178.add(beamSegment);
                        break block18;
                    }
                    if (beamSegment == null) break block18;
                    if (Arrays.equals(fs, beamSegment.color)) {
                        beamSegment.increaseHeight();
                    } else {
                        beamSegment = new BeamSegment(new float[]{(beamSegment.color[0] + fs[0]) / 2.0f, (beamSegment.color[1] + fs[1]) / 2.0f, (beamSegment.color[2] + fs[2]) / 2.0f});
                        blockEntity.field_19178.add(beamSegment);
                    }
                    break block18;
                }
                if (beamSegment != null && (blockState.getOpacity(world, blockPos) < 15 || blockState.isOf(Blocks.BEDROCK))) {
                    beamSegment.increaseHeight();
                } else {
                    blockEntity.field_19178.clear();
                    blockEntity.minY = l;
                    break;
                }
            }
            blockPos = blockPos.up();
            ++blockEntity.minY;
        }
        m = blockEntity.level;
        if (world.getTime() % 80L == 0L) {
            if (!blockEntity.beamSegments.isEmpty()) {
                blockEntity.level = BeaconBlockEntity.updateLevel(world, i, j, k);
            }
            if (blockEntity.level > 0 && !blockEntity.beamSegments.isEmpty()) {
                BeaconBlockEntity.applyPlayerEffects(world, pos, blockEntity.level, blockEntity.primary, blockEntity.secondary);
                BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }
        if (blockEntity.minY >= l) {
            blockEntity.minY = world.getBottomY() - 1;
            boolean bl = m > 0;
            blockEntity.beamSegments = blockEntity.field_19178;
            if (!world.isClient) {
                boolean bl2;
                boolean bl3 = bl2 = blockEntity.level > 0;
                if (!bl && bl2) {
                    BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);
                    for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, new Box(i, j, k, i, j - 4, k).expand(10.0, 5.0, 10.0))) {
                        Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, blockEntity.level);
                    }
                } else if (bl && !bl2) {
                    BeaconBlockEntity.playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
                }
            }
        }
    }

    private static int updateLevel(World world, int x, int y, int z) {
        int k;
        int i = 0;
        int j = 1;
        while (j <= 4 && (k = y - j) >= world.getBottomY()) {
            boolean bl = true;
            block1: for (int l = x - j; l <= x + j && bl; ++l) {
                for (int m = z - j; m <= z + j; ++m) {
                    if (world.getBlockState(new BlockPos(l, k, m)).isIn(BlockTags.BEACON_BASE_BLOCKS)) continue;
                    bl = false;
                    continue block1;
                }
            }
            if (!bl) break;
            i = j++;
        }
        return i;
    }

    @Override
    public void markRemoved() {
        BeaconBlockEntity.playSound(this.world, this.pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
        super.markRemoved();
    }

    private static void applyPlayerEffects(World world, BlockPos pos, int beaconLevel, @Nullable StatusEffect primaryEffect, @Nullable StatusEffect secondaryEffect) {
        if (world.isClient || primaryEffect == null) {
            return;
        }
        double d = beaconLevel * 10 + 10;
        int i = 0;
        if (beaconLevel >= 4 && primaryEffect == secondaryEffect) {
            i = 1;
        }
        int j = (9 + beaconLevel * 2) * 20;
        Box box = new Box(pos).expand(d).stretch(0.0, world.getHeight(), 0.0);
        List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
        for (PlayerEntity playerEntity : list) {
            playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, i, true, true));
        }
        if (beaconLevel >= 4 && primaryEffect != secondaryEffect && secondaryEffect != null) {
            for (PlayerEntity playerEntity : list) {
                playerEntity.addStatusEffect(new StatusEffectInstance(secondaryEffect, j, 0, true, true));
            }
        }
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public List<BeamSegment> getBeamSegments() {
        return this.level == 0 ? ImmutableList.of() : this.beamSegments;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Nullable
    static StatusEffect getPotionEffectById(int id) {
        StatusEffect statusEffect = StatusEffect.byRawId(id);
        return EFFECTS.contains(statusEffect) ? statusEffect : null;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.primary = BeaconBlockEntity.getPotionEffectById(nbt.getInt("Primary"));
        this.secondary = BeaconBlockEntity.getPotionEffectById(nbt.getInt("Secondary"));
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
        this.lock = ContainerLock.fromNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Primary", StatusEffect.getRawIdNullable(this.primary));
        nbt.putInt("Secondary", StatusEffect.getRawIdNullable(this.secondary));
        nbt.putInt("Levels", this.level);
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        this.lock.writeNbt(nbt);
    }

    public void setCustomName(@Nullable Text customName) {
        this.customName = customName;
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName())) {
            return new BeaconScreenHandler(i, playerInventory, this.propertyDelegate, ScreenHandlerContext.create(this.world, this.getPos()));
        }
        return null;
    }

    @Override
    public Text getDisplayName() {
        return this.getName();
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return CONTAINER_NAME_TEXT;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.minY = world.getBottomY() - 1;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }

    public static class BeamSegment {
        final float[] color;
        private int height;

        public BeamSegment(float[] color) {
            this.color = color;
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
}

