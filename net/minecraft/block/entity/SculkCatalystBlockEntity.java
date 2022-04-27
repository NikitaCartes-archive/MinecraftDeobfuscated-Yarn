/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkCatalystBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkCatalystBlockEntity
extends BlockEntity
implements GameEventListener {
    private final BlockPositionSource positionSource;
    private final SculkSpreadManager spreadManager;

    public SculkCatalystBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SCULK_CATALYST, pos, state);
        this.positionSource = new BlockPositionSource(this.pos);
        this.spreadManager = SculkSpreadManager.create();
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d pos) {
        Entity entity;
        if (event == GameEvent.ENTITY_DIE && (entity = emitter.sourceEntity()) instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            if (!livingEntity.isExperienceDroppingDisabled()) {
                this.spreadManager.spread(new BlockPos(pos.withBias(Direction.UP, 0.5)), livingEntity.getXpToDrop());
                livingEntity.disableExperienceDropping();
                LivingEntity livingEntity2 = livingEntity.getAttacker();
                if (livingEntity2 instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity2;
                    DamageSource damageSource = livingEntity.getRecentDamageSource() == null ? DamageSource.player(serverPlayerEntity) : livingEntity.getRecentDamageSource();
                    Criteria.KILL_MOB_NEAR_SCULK_CATALYST.trigger(serverPlayerEntity, emitter.sourceEntity(), damageSource);
                }
                SculkCatalystBlock.bloom(world, this.pos, this.getCachedState(), world.getRandom());
            }
            return true;
        }
        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SculkCatalystBlockEntity blockEntity) {
        blockEntity.spreadManager.tick(world, pos, world.getRandom(), true);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.spreadManager.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        this.spreadManager.writeNbt(nbt);
        super.writeNbt(nbt);
    }

    @VisibleForTesting
    public SculkSpreadManager getSpreadManager() {
        return this.spreadManager;
    }

    private static /* synthetic */ Integer method_41518(SculkSpreadManager.Cursor cursor) {
        return 1;
    }
}

