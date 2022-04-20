/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration.painting;

import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.class_7406;
import net.minecraft.class_7408;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PaintingEntity
extends AbstractDecorationEntity {
    private static final TrackedData<RegistryEntry<PaintingMotive>> field_38941 = DataTracker.registerData(PaintingEntity.class, TrackedDataHandlerRegistry.field_39017);
    private static final RegistryKey<PaintingMotive> field_38942 = class_7408.KEBAB;

    private static RegistryEntry<PaintingMotive> method_43405() {
        return Registry.PAINTING_MOTIVE.entryOf(field_38942);
    }

    public PaintingEntity(EntityType<? extends PaintingEntity> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>)entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(field_38941, PaintingEntity.method_43405());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (data == field_38941) {
            this.updateAttachmentPosition();
        }
    }

    private void method_43402(RegistryEntry<PaintingMotive> registryEntry) {
        this.dataTracker.set(field_38941, registryEntry);
    }

    public RegistryEntry<PaintingMotive> method_43404() {
        return this.dataTracker.get(field_38941);
    }

    public static Optional<PaintingEntity> method_43401(World world, BlockPos blockPos, Direction direction) {
        PaintingEntity paintingEntity = new PaintingEntity(world, blockPos);
        ArrayList<RegistryEntry> list = new ArrayList<RegistryEntry>();
        Registry.PAINTING_MOTIVE.iterateEntries(class_7406.PLACEABLE).forEach(list::add);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        paintingEntity.setFacing(direction);
        list.removeIf(registryEntry -> {
            paintingEntity.method_43402((RegistryEntry<PaintingMotive>)registryEntry);
            return !paintingEntity.canStayAttached();
        });
        if (list.isEmpty()) {
            return Optional.empty();
        }
        int i = list.stream().mapToInt(PaintingEntity::method_43403).max().orElse(0);
        list.removeIf(registryEntry -> PaintingEntity.method_43403(registryEntry) < i);
        Optional optional = Util.getRandomOrEmpty(list, paintingEntity.random);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        paintingEntity.method_43402((RegistryEntry)optional.get());
        paintingEntity.setFacing(direction);
        return Optional.of(paintingEntity);
    }

    private static int method_43403(RegistryEntry<PaintingMotive> registryEntry) {
        return registryEntry.value().getWidth() * registryEntry.value().getHeight();
    }

    private PaintingEntity(World world, BlockPos pos) {
        super(EntityType.PAINTING, world, pos);
    }

    public PaintingEntity(World world, BlockPos pos, Direction direction, RegistryEntry<PaintingMotive> registryEntry) {
        this(world, pos);
        this.method_43402(registryEntry);
        this.setFacing(direction);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("variant", this.method_43404().getKey().orElse(field_38942).getValue().toString());
        nbt.putByte("facing", (byte)this.facing.getHorizontal());
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        RegistryKey<PaintingMotive> registryKey = RegistryKey.of(Registry.MOTIVE_KEY, Identifier.tryParse(nbt.getString("variant")));
        this.method_43402(Registry.PAINTING_MOTIVE.getEntry(registryKey).orElseGet(PaintingEntity::method_43405));
        this.facing = Direction.fromHorizontal(nbt.getByte("facing"));
        super.readCustomDataFromNbt(nbt);
        this.setFacing(this.facing);
    }

    @Override
    public int getWidthPixels() {
        return this.method_43404().value().getWidth();
    }

    @Override
    public int getHeightPixels() {
        return this.method_43404().value().getHeight();
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
    public Vec3d method_43390() {
        return Vec3d.of(this.attachmentPos);
    }

    @Override
    public Packet<?> createSpawnPacket() {
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
}

