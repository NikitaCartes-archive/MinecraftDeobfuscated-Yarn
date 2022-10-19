/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.LongJumpTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class BiasedLongJumpTask<E extends MobEntity>
extends LongJumpTask<E> {
    private final TagKey<Block> favoredBlocks;
    private final float biasChance;
    private final List<LongJumpTask.Target> unfavoredTargets = new ArrayList<LongJumpTask.Target>();
    private boolean useBias;

    public BiasedLongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function<E, SoundEvent> entityToSound, TagKey<Block> favoredBlocks, float biasChance, BiPredicate<E, BlockPos> jumpToPredicate) {
        super(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, jumpToPredicate);
        this.favoredBlocks = favoredBlocks;
        this.biasChance = biasChance;
    }

    @Override
    protected void run(ServerWorld serverWorld, E mobEntity, long l) {
        super.run(serverWorld, mobEntity, l);
        this.unfavoredTargets.clear();
        this.useBias = ((LivingEntity)mobEntity).getRandom().nextFloat() < this.biasChance;
    }

    @Override
    protected Optional<LongJumpTask.Target> getTarget(ServerWorld world) {
        if (!this.useBias) {
            return super.getTarget(world);
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        while (!this.targets.isEmpty()) {
            Optional<LongJumpTask.Target> optional = super.getTarget(world);
            if (!optional.isPresent()) continue;
            LongJumpTask.Target target = optional.get();
            if (world.getBlockState(mutable.set((Vec3i)target.getPos(), Direction.DOWN)).isIn(this.favoredBlocks)) {
                return optional;
            }
            this.unfavoredTargets.add(target);
        }
        if (!this.unfavoredTargets.isEmpty()) {
            return Optional.of(this.unfavoredTargets.remove(0));
        }
        return Optional.empty();
    }
}

