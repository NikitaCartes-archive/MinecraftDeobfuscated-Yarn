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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class StepAndDestroyBlockGoal extends MoveToTargetPosGoal {
	private final Block field_6587;
	private final MobEntity owner;
	private int counter;

	public StepAndDestroyBlockGoal(Block block, MobEntityWithAi mobEntityWithAi, double d, int i) {
		super(mobEntityWithAi, d, 24, i);
		this.field_6587 = block;
		this.owner = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		if (!this.owner.field_6002.getGameRules().getBoolean("mobGriefing")) {
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

	public void method_6307(IWorld iWorld, BlockPos blockPos) {
	}

	public void method_6309(World world, BlockPos blockPos) {
	}

	@Override
	public void tick() {
		super.tick();
		World world = this.owner.field_6002;
		BlockPos blockPos = new BlockPos(this.owner);
		BlockPos blockPos2 = this.method_6308(blockPos, world);
		Random random = this.owner.getRand();
		if (this.hasReached() && blockPos2 != null) {
			if (this.counter > 0) {
				Vec3d vec3d = this.owner.method_18798();
				this.owner.setVelocity(vec3d.x, 0.3, vec3d.z);
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
				Vec3d vec3d = this.owner.method_18798();
				this.owner.setVelocity(vec3d.x, -0.3, vec3d.z);
				if (this.counter % 6 == 0) {
					this.method_6307(world, this.field_6512);
				}
			}

			if (this.counter > 60) {
				world.method_8650(blockPos2);
				if (!world.isClient) {
					for (int i = 0; i < 20; i++) {
						double d = random.nextGaussian() * 0.02;
						double e = random.nextGaussian() * 0.02;
						double f = random.nextGaussian() * 0.02;
						((ServerWorld)world)
							.method_14199(ParticleTypes.field_11203, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1, d, e, f, 0.15F);
					}

					this.method_6309(world, this.field_6512);
				}
			}

			this.counter++;
		}
	}

	@Nullable
	private BlockPos method_6308(BlockPos blockPos, BlockView blockView) {
		if (blockView.method_8320(blockPos).getBlock() == this.field_6587) {
			return blockPos;
		} else {
			BlockPos[] blockPoss = new BlockPos[]{blockPos.down(), blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south(), blockPos.down().down()};

			for (BlockPos blockPos2 : blockPoss) {
				if (blockView.method_8320(blockPos2).getBlock() == this.field_6587) {
					return blockPos2;
				}
			}

			return null;
		}
	}

	@Override
	protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.method_8320(blockPos).getBlock();
		return block == this.field_6587 && viewableWorld.method_8320(blockPos.up()).isAir() && viewableWorld.method_8320(blockPos.up(2)).isAir();
	}
}
