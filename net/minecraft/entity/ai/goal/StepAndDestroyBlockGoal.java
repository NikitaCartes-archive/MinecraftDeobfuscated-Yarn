/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class StepAndDestroyBlockGoal
extends MoveToTargetPosGoal {
    private final Block targetBlock;
    private final MobEntity stepAndDestroyMob;
    private int counter;
    private static final int MAX_COOLDOWN = 20;

    public StepAndDestroyBlockGoal(Block targetBlock, PathAwareEntity mob, double speed, int maxYDifference) {
        super(mob, speed, 24, maxYDifference);
        this.targetBlock = targetBlock;
        this.stepAndDestroyMob = mob;
    }

    @Override
    public boolean canStart() {
        if (!this.stepAndDestroyMob.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        if (this.findTargetPos()) {
            this.cooldown = StepAndDestroyBlockGoal.toGoalTicks(20);
            return true;
        }
        this.cooldown = this.getInterval(this.mob);
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        this.stepAndDestroyMob.fallDistance = 1.0f;
    }

    @Override
    public void start() {
        super.start();
        this.counter = 0;
    }

    public void tickStepping(WorldAccess world, BlockPos pos) {
    }

    public void onDestroyBlock(World world, BlockPos pos) {
    }

    @Override
    public void tick() {
        super.tick();
        World world = this.stepAndDestroyMob.world;
        BlockPos blockPos = this.stepAndDestroyMob.getBlockPos();
        BlockPos blockPos2 = this.tweakToProperPos(blockPos, world);
        Random random = this.stepAndDestroyMob.getRandom();
        if (this.hasReached() && blockPos2 != null) {
            double d;
            Vec3d vec3d;
            if (this.counter > 0) {
                vec3d = this.stepAndDestroyMob.getVelocity();
                this.stepAndDestroyMob.setVelocity(vec3d.x, 0.3, vec3d.z);
                if (!world.isClient) {
                    d = 0.08;
                    ((ServerWorld)world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.EGG)), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.7, (double)blockPos2.getZ() + 0.5, 3, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, 0.15f);
                }
            }
            if (this.counter % 2 == 0) {
                vec3d = this.stepAndDestroyMob.getVelocity();
                this.stepAndDestroyMob.setVelocity(vec3d.x, -0.3, vec3d.z);
                if (this.counter % 6 == 0) {
                    this.tickStepping(world, this.targetPos);
                }
            }
            if (this.counter > 60) {
                world.removeBlock(blockPos2, false);
                if (!world.isClient) {
                    for (int i = 0; i < 20; ++i) {
                        d = random.nextGaussian() * 0.02;
                        double e = random.nextGaussian() * 0.02;
                        double f = random.nextGaussian() * 0.02;
                        ((ServerWorld)world).spawnParticles(ParticleTypes.POOF, (double)blockPos2.getX() + 0.5, blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1, d, e, f, 0.15f);
                    }
                    this.onDestroyBlock(world, blockPos2);
                }
            }
            ++this.counter;
        }
    }

    @Nullable
    private BlockPos tweakToProperPos(BlockPos pos, BlockView world) {
        BlockPos[] blockPoss;
        if (world.getBlockState(pos).isOf(this.targetBlock)) {
            return pos;
        }
        for (BlockPos blockPos : blockPoss = new BlockPos[]{pos.down(), pos.west(), pos.east(), pos.north(), pos.south(), pos.down().down()}) {
            if (!world.getBlockState(blockPos).isOf(this.targetBlock)) continue;
            return blockPos;
        }
        return null;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        Chunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        if (chunk != null) {
            return chunk.getBlockState(pos).isOf(this.targetBlock) && chunk.getBlockState(pos.up()).isAir() && chunk.getBlockState(pos.up(2)).isAir();
        }
        return false;
    }
}

