/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration.painting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PaintingEntity
extends AbstractDecorationEntity {
    public PaintingMotive motive;

    public PaintingEntity(EntityType<? extends PaintingEntity> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>)entityType, world);
    }

    public PaintingEntity(World world, BlockPos pos, Direction direction) {
        super(EntityType.PAINTING, world, pos);
        PaintingMotive paintingMotive;
        ArrayList<PaintingMotive> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = Registry.PAINTING_MOTIVE.iterator();
        while (iterator.hasNext()) {
            this.motive = paintingMotive = (PaintingMotive)iterator.next();
            this.setFacing(direction);
            if (!this.canStayAttached()) continue;
            list.add(paintingMotive);
            int j = paintingMotive.getWidth() * paintingMotive.getHeight();
            if (j <= i) continue;
            i = j;
        }
        if (!list.isEmpty()) {
            Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                paintingMotive = (PaintingMotive)iterator2.next();
                if (paintingMotive.getWidth() * paintingMotive.getHeight() >= i) continue;
                iterator2.remove();
            }
            this.motive = (PaintingMotive)list.get(this.random.nextInt(list.size()));
        }
        this.setFacing(direction);
    }

    @Environment(value=EnvType.CLIENT)
    public PaintingEntity(World world, BlockPos pos, Direction direction, PaintingMotive motive) {
        this(world, pos, direction);
        this.motive = motive;
        this.setFacing(direction);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("Motive", Registry.PAINTING_MOTIVE.getId(this.motive).toString());
        nbt.putByte("Facing", (byte)this.facing.getHorizontal());
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.motive = Registry.PAINTING_MOTIVE.get(Identifier.tryParse(nbt.getString("Motive")));
        this.facing = Direction.fromHorizontal(nbt.getByte("Facing"));
        super.readCustomDataFromNbt(nbt);
        this.setFacing(this.facing);
    }

    @Override
    public int getWidthPixels() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getWidth();
    }

    @Override
    public int getHeightPixels() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getHeight();
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            return;
        }
        this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0f, 1.0f);
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (playerEntity.abilities.creativeMode) {
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
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        BlockPos blockPos = this.attachmentPos.add(x - this.getX(), y - this.getY(), z - this.getZ());
        this.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new PaintingSpawnS2CPacket(this);
    }
}

