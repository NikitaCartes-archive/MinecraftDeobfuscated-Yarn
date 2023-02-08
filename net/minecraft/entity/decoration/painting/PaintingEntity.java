/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration.painting;

import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PaintingVariantTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PaintingEntity
extends AbstractDecorationEntity
implements VariantHolder<RegistryEntry<PaintingVariant>> {
    private static final TrackedData<RegistryEntry<PaintingVariant>> VARIANT = DataTracker.registerData(PaintingEntity.class, TrackedDataHandlerRegistry.PAINTING_VARIANT);
    private static final RegistryKey<PaintingVariant> DEFAULT_VARIANT = PaintingVariants.KEBAB;
    public static final String VARIANT_NBT_KEY = "variant";

    private static RegistryEntry<PaintingVariant> getDefaultVariant() {
        return Registries.PAINTING_VARIANT.entryOf(DEFAULT_VARIANT);
    }

    public PaintingEntity(EntityType<? extends PaintingEntity> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>)entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(VARIANT, PaintingEntity.getDefaultVariant());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (VARIANT.equals(data)) {
            this.updateAttachmentPosition();
        }
    }

    @Override
    public void setVariant(RegistryEntry<PaintingVariant> variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    @Override
    public RegistryEntry<PaintingVariant> getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public static Optional<PaintingEntity> placePainting(World world, BlockPos pos, Direction facing) {
        PaintingEntity paintingEntity = new PaintingEntity(world, pos);
        ArrayList<RegistryEntry> list = new ArrayList<RegistryEntry>();
        Registries.PAINTING_VARIANT.iterateEntries(PaintingVariantTags.PLACEABLE).forEach(list::add);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        paintingEntity.setFacing(facing);
        list.removeIf(variant -> {
            paintingEntity.setVariant((RegistryEntry<PaintingVariant>)variant);
            return !paintingEntity.canStayAttached();
        });
        if (list.isEmpty()) {
            return Optional.empty();
        }
        int i = list.stream().mapToInt(PaintingEntity::getSize).max().orElse(0);
        list.removeIf(variant -> PaintingEntity.getSize(variant) < i);
        Optional optional = Util.getRandomOrEmpty(list, paintingEntity.random);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        paintingEntity.setVariant((RegistryEntry)optional.get());
        paintingEntity.setFacing(facing);
        return Optional.of(paintingEntity);
    }

    private static int getSize(RegistryEntry<PaintingVariant> variant) {
        return variant.value().getWidth() * variant.value().getHeight();
    }

    private PaintingEntity(World world, BlockPos pos) {
        super(EntityType.PAINTING, world, pos);
    }

    public PaintingEntity(World world, BlockPos pos, Direction direction, RegistryEntry<PaintingVariant> variant) {
        this(world, pos);
        this.setVariant(variant);
        this.setFacing(direction);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        PaintingEntity.writeVariantToNbt(nbt, (RegistryEntry<PaintingVariant>)this.getVariant());
        nbt.putByte("facing", (byte)this.facing.getHorizontal());
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        RegistryEntry registryEntry = PaintingEntity.readVariantFromNbt(nbt).orElseGet(PaintingEntity::getDefaultVariant);
        this.setVariant(registryEntry);
        this.facing = Direction.fromHorizontal(nbt.getByte("facing"));
        super.readCustomDataFromNbt(nbt);
        this.setFacing(this.facing);
    }

    public static void writeVariantToNbt(NbtCompound nbt, RegistryEntry<PaintingVariant> variant) {
        nbt.putString(VARIANT_NBT_KEY, variant.getKey().orElse(DEFAULT_VARIANT).getValue().toString());
    }

    public static Optional<RegistryEntry<PaintingVariant>> readVariantFromNbt(NbtCompound nbt) {
        return Optional.ofNullable(Identifier.tryParse(nbt.getString(VARIANT_NBT_KEY))).map(id -> RegistryKey.of(RegistryKeys.PAINTING_VARIANT, id)).flatMap(Registries.PAINTING_VARIANT::getEntry);
    }

    @Override
    public int getWidthPixels() {
        return ((PaintingVariant)this.getVariant().value()).getWidth();
    }

    @Override
    public int getHeightPixels() {
        return ((PaintingVariant)this.getVariant().value()).getHeight();
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            return;
        }
        this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0f, 1.0f);
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (playerEntity.getAbilities().creativeMode) {
                return;
            }
        }
        this.dropItem(Items.PAINTING);
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0f, 1.0f);
    }

    @Override
    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.setPosition(x, y, z);
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.setPosition(x, y, z);
    }

    @Override
    public Vec3d getSyncedPos() {
        return Vec3d.of(this.attachmentPos);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.setFacing(Direction.byId(packet.getEntityData()));
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(Items.PAINTING);
    }

    @Override
    public /* synthetic */ Object getVariant() {
        return this.getVariant();
    }
}

