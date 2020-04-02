/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MobSpawnerBlockEntity
extends BlockEntity
implements Tickable {
    private final MobSpawnerLogic logic = new MobSpawnerLogic(){

        @Override
        public void sendStatus(int status) {
            MobSpawnerBlockEntity.this.world.addBlockAction(MobSpawnerBlockEntity.this.pos, Blocks.SPAWNER, status, 0);
        }

        @Override
        public World getWorld() {
            return MobSpawnerBlockEntity.this.world;
        }

        @Override
        public BlockPos getPos() {
            return MobSpawnerBlockEntity.this.pos;
        }

        @Override
        public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
            super.setSpawnEntry(spawnEntry);
            if (this.getWorld() != null) {
                BlockState blockState = this.getWorld().getBlockState(this.getPos());
                this.getWorld().updateListeners(MobSpawnerBlockEntity.this.pos, blockState, blockState, 4);
            }
        }
    };

    public MobSpawnerBlockEntity() {
        super(BlockEntityType.MOB_SPAWNER);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.logic.fromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        this.logic.toTag(tag);
        return tag;
    }

    @Override
    public void tick() {
        this.logic.update();
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 1, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        CompoundTag compoundTag = this.toTag(new CompoundTag());
        compoundTag.remove("SpawnPotentials");
        return compoundTag;
    }

    @Override
    public boolean onBlockAction(int i, int j) {
        if (this.logic.method_8275(i)) {
            return true;
        }
        return super.onBlockAction(i, j);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public MobSpawnerLogic getLogic() {
        return this.logic;
    }
}

