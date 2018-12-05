package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticle;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class class_1382 extends class_1367 {
	private final Block field_6587;
	private final MobEntity field_6589;
	private int field_6588;

	public class_1382(Block block, MobEntityWithAi mobEntityWithAi, double d, int i) {
		super(mobEntityWithAi, d, 24, i);
		this.field_6587 = block;
		this.field_6589 = mobEntityWithAi;
	}

	@Override
	public boolean canStart() {
		if (!this.field_6589.world.getGameRules().getBoolean("mobGriefing")) {
			return false;
		} else {
			return this.field_6589.getRand().nextInt(20) != 0 ? false : super.canStart();
		}
	}

	@Override
	protected int method_6293(MobEntityWithAi mobEntityWithAi) {
		return 0;
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue();
	}

	@Override
	public void onRemove() {
		super.onRemove();
		this.field_6589.fallDistance = 1.0F;
	}

	@Override
	public void start() {
		super.start();
		this.field_6588 = 0;
	}

	public void method_6307(IWorld iWorld, BlockPos blockPos) {
	}

	public void method_6309(World world, BlockPos blockPos) {
	}

	@Override
	public void tick() {
		super.tick();
		World world = this.field_6589.world;
		BlockPos blockPos = new BlockPos(this.field_6589);
		BlockPos blockPos2 = this.method_6308(blockPos, world);
		Random random = this.field_6589.getRand();
		if (this.method_6295() && blockPos2 != null) {
			if (this.field_6588 > 0) {
				this.field_6589.velocityY = 0.3;
				if (!world.isRemote) {
					double d = 0.08;
					((ServerWorld)world)
						.method_14199(
							new ItemStackParticle(ParticleTypes.field_11218, new ItemStack(Items.field_8803)),
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

			if (this.field_6588 % 2 == 0) {
				this.field_6589.velocityY = -0.3;
				if (this.field_6588 % 6 == 0) {
					this.method_6307(world, this.field_6512);
				}
			}

			if (this.field_6588 > 60) {
				world.clearBlockState(blockPos2);
				if (!world.isRemote) {
					for (int i = 0; i < 20; i++) {
						double e = random.nextGaussian() * 0.02;
						double f = random.nextGaussian() * 0.02;
						double g = random.nextGaussian() * 0.02;
						((ServerWorld)world)
							.method_14199(ParticleTypes.field_11203, (double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5, 1, e, f, g, 0.15F);
					}

					this.method_6309(world, this.field_6512);
				}
			}

			this.field_6588++;
		}
	}

	@Nullable
	private BlockPos method_6308(BlockPos blockPos, BlockView blockView) {
		if (blockView.getBlockState(blockPos).getBlock() == this.field_6587) {
			return blockPos;
		} else {
			BlockPos[] blockPoss = new BlockPos[]{blockPos.down(), blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south(), blockPos.down().down()};

			for (BlockPos blockPos2 : blockPoss) {
				if (blockView.getBlockState(blockPos2).getBlock() == this.field_6587) {
					return blockPos2;
				}
			}

			return null;
		}
	}

	@Override
	protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.getBlockState(blockPos).getBlock();
		return block == this.field_6587 && viewableWorld.getBlockState(blockPos.up()).isAir() && viewableWorld.getBlockState(blockPos.up(2)).isAir();
	}
}
