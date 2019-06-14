package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BrewingStandBlock extends BlockWithEntity {
	public static final BooleanProperty[] field_10700 = new BooleanProperty[]{Properties.field_12554, Properties.field_12500, Properties.field_12531};
	protected static final VoxelShape field_10701 = VoxelShapes.method_1084(
		Block.method_9541(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.method_9541(7.0, 0.0, 7.0, 9.0, 14.0, 9.0)
	);

	public BrewingStandBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10700[0], Boolean.valueOf(false))
				.method_11657(field_10700[1], Boolean.valueOf(false))
				.method_11657(field_10700[2], Boolean.valueOf(false))
		);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new BrewingStandBlockEntity();
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_10701;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				playerEntity.openContainer((BrewingStandBlockEntity)blockEntity);
				playerEntity.incrementStat(Stats.field_15407);
			}

			return true;
		}
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				((BrewingStandBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)((float)blockPos.getX() + 0.4F + random.nextFloat() * 0.2F);
		double e = (double)((float)blockPos.getY() + 0.7F + random.nextFloat() * 0.3F);
		double f = (double)((float)blockPos.getZ() + 0.4F + random.nextFloat() * 0.2F);
		world.addParticle(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				ItemScatterer.method_5451(world, blockPos, (BrewingStandBlockEntity)blockEntity);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return Container.method_7608(world.method_8321(blockPos));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10700[0], field_10700[1], field_10700[2]);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
