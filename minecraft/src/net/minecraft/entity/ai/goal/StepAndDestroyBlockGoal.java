package net.minecraft.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class StepAndDestroyBlockGoal extends MoveToTargetPosGoal {
	private final Block targetBlock;
	private final MobEntity owner;
	private int counter;

	public StepAndDestroyBlockGoal(Block block, MobEntityWithAi mobEntityWithAi, double d, int i) {
		super(mobEntityWithAi, d, 24, i);
		this.targetBlock = block;
		this.owner = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		if (!this.owner.world.getGameRules().getBoolean("mobGriefing")) {
			return false;
		} else {
			return this.owner.getRand().nextInt(20) != 0 ? false : super.canStart();
		}
	}

	@Override
	protected int getInterval(MobEntityWithAi mobEntityWithAi) {
		return 0;
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue();
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.owner.fallDistance = 1.0F;
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
		World world = this.owner.world;
		BlockPos blockPos = new BlockPos(this.owner);
		BlockPos blockPos2 = this.tweakToProperPos(blockPos, world);
		Random random = this.owner.getRand();
		if (this.hasReached() && blockPos2 != null) {
			if (this.counter > 0) {
				this.owner.velocityY = 0.3;
				if (!world.isClient) {
					double d = 0.08;
					((ServerWorld)world)
						.method_14199(
							new ItemStackParticleParameters(ParticleTypes.field_11218, new ItemStack(Items.field_8803)),
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
				this.owner.velocityY = -0.3;
				if (this.counter % 6 == 0) {
					this.tickStepping(world, this.targetPos);
				}
			}

			if (this.counter > 60) {
				world.clearBlockState(blockPos2);
				if (!world.isClient) {
					for (int i = 0; i < 20; i++) {
						double e = random.nextGaussian() * 0.02;
						double f = random.nextGaussian() * 0.02;
						double g = random.nextGaussian() * 0.02;
						((ServerWorld)world)
							.method_14199(ParticleTypes.field_11203, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1, e, f, g, 0.15F);
					}

					this.onDestroyBlock(world, this.targetPos);
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
		Block block = viewableWorld.getBlockState(blockPos).getBlock();
		return block == this.targetBlock && viewableWorld.getBlockState(blockPos.up()).isAir() && viewableWorld.getBlockState(blockPos.up(2)).isAir();
	}
}
