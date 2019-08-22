package net.minecraft.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class StepAndDestroyBlockGoal extends MoveToTargetPosGoal {
	private final Block targetBlock;
	private final MobEntity stepAndDestroyMob;
	private int counter;

	public StepAndDestroyBlockGoal(Block block, MobEntityWithAi mobEntityWithAi, double d, int i) {
		super(mobEntityWithAi, d, 24, i);
		this.targetBlock = block;
		this.stepAndDestroyMob = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		if (!this.stepAndDestroyMob.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			return false;
		} else if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else if (this.hasAvailableTarget()) {
			this.cooldown = 20;
			return true;
		} else {
			this.cooldown = this.getInterval(this.mob);
			return false;
		}
	}

	private boolean hasAvailableTarget() {
		return this.targetPos != null && this.isTargetPos(this.mob.world, this.targetPos) ? true : this.findTargetPos();
	}

	@Override
	public void stop() {
		super.stop();
		this.stepAndDestroyMob.fallDistance = 1.0F;
	}

	@Override
	public void start() {
		super.start();
		this.counter = 0;
	}

	public void tickStepping(IWorld iWorld, BlockPos blockPos) {
	}

	public void onDestroyBlock(World world, BlockPos blockPos) {
	}

	@Override
	public void tick() {
		super.tick();
		World world = this.stepAndDestroyMob.world;
		BlockPos blockPos = new BlockPos(this.stepAndDestroyMob);
		BlockPos blockPos2 = this.tweakToProperPos(blockPos, world);
		Random random = this.stepAndDestroyMob.getRand();
		if (this.hasReached() && blockPos2 != null) {
			if (this.counter > 0) {
				Vec3d vec3d = this.stepAndDestroyMob.getVelocity();
				this.stepAndDestroyMob.setVelocity(vec3d.x, 0.3, vec3d.z);
				if (!world.isClient) {
					double d = 0.08;
					((ServerWorld)world)
						.spawnParticles(
							new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.EGG)),
							(double)blockPos2.getX() + 0.5,
							(double)blockPos2.getY() + 0.7,
							(double)blockPos2.getZ() + 0.5,
							3,
							((double)random.nextFloat() - 0.5) * 0.08,
							((double)random.nextFloat() - 0.5) * 0.08,
							((double)random.nextFloat() - 0.5) * 0.08,
							0.15F
						);
				}
			}

			if (this.counter % 2 == 0) {
				Vec3d vec3d = this.stepAndDestroyMob.getVelocity();
				this.stepAndDestroyMob.setVelocity(vec3d.x, -0.3, vec3d.z);
				if (this.counter % 6 == 0) {
					this.tickStepping(world, this.targetPos);
				}
			}

			if (this.counter > 60) {
				world.clearBlockState(blockPos2, false);
				if (!world.isClient) {
					for (int i = 0; i < 20; i++) {
						double d = random.nextGaussian() * 0.02;
						double e = random.nextGaussian() * 0.02;
						double f = random.nextGaussian() * 0.02;
						((ServerWorld)world)
							.spawnParticles(ParticleTypes.POOF, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1, d, e, f, 0.15F);
					}

					this.onDestroyBlock(world, blockPos2);
				}
			}

			this.counter++;
		}
	}

	@Nullable
	private BlockPos tweakToProperPos(BlockPos blockPos, BlockView blockView) {
		if (blockView.getBlockState(blockPos).getBlock() == this.targetBlock) {
			return blockPos;
		} else {
			BlockPos[] blockPoss = new BlockPos[]{blockPos.down(), blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south(), blockPos.down().down()};

			for (BlockPos blockPos2 : blockPoss) {
				if (blockView.getBlockState(blockPos2).getBlock() == this.targetBlock) {
					return blockPos2;
				}
			}

			return null;
		}
	}

	@Override
	protected boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos) {
		Chunk chunk = viewableWorld.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
		return chunk == null
			? false
			: chunk.getBlockState(blockPos).getBlock() == this.targetBlock && chunk.getBlockState(blockPos.up()).isAir() && chunk.getBlockState(blockPos.up(2)).isAir();
	}
}
