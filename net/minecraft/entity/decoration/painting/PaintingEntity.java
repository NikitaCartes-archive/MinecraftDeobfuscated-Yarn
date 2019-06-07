/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration.painting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
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

    public PaintingEntity(World world, BlockPos blockPos, Direction direction) {
        super(EntityType.PAINTING, world, blockPos);
        PaintingMotive paintingMotive;
        ArrayList<PaintingMotive> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = Registry.MOTIVE.iterator();
        while (iterator.hasNext()) {
            this.motive = paintingMotive = (PaintingMotive)iterator.next();
            this.setFacing(direction);
            if (!this.method_6888()) continue;
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
    public PaintingEntity(World world, BlockPos blockPos, Direction direction, PaintingMotive paintingMotive) {
        this(world, blockPos, direction);
        this.motive = paintingMotive;
        this.setFacing(direction);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putString("Motive", Registry.MOTIVE.getId(this.motive).toString());
        super.writeCustomDataToTag(compoundTag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.motive = Registry.MOTIVE.get(Identifier.tryParse(compoundTag.getString("Motive")));
        super.readCustomDataFromTag(compoundTag);
    }

    @Override
    public int getWidthPixels() {
        return this.motive.getWidth();
    }

    @Override
    public int getHeightPixels() {
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
    public void setPositionAndAngles(double d, double e, double f, float g, float h) {
        this.setPosition(d, e, f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        BlockPos blockPos = this.blockPos.add(d - this.x, e - this.y, f - this.z);
        this.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new PaintingSpawnS2CPacket(this);
    }
}

