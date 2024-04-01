package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PotatoPortalBlock extends Block {
	public static final MapCodec<PotatoPortalBlock> CODEC = createCodec(PotatoPortalBlock::new);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 24.0, 13.0);

	@Override
	public MapCodec<PotatoPortalBlock> getCodec() {
		return CODEC;
	}

	public PotatoPortalBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canBucketPlace(BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.canUsePortals()) {
			entity.setInNetherPortal(pos);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(100) == 0) {
			world.playSound(
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				SoundEvents.ENTITY_PLAGUEWHALE_AMBIENT,
				SoundCategory.BLOCKS,
				0.5F,
				random.nextFloat() * 0.4F + 0.8F,
				false
			);
		}

		for (int i = 0; i < 4; i++) {
			double d = (double)pos.getX() + random.nextDouble();
			double e = (double)pos.getY() + random.nextDouble();
			double f = (double)pos.getZ() + random.nextDouble();
			double g = ((double)random.nextFloat() - 0.5) * 0.5;
			double h = ((double)random.nextFloat() - 0.5) * 0.5;
			double j = ((double)random.nextFloat() - 0.5) * 0.5;
			int k = random.nextInt(2) * 2 - 1;
			if (!world.getBlockState(pos.west()).isOf(this) && !world.getBlockState(pos.east()).isOf(this)) {
				d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
				g = (double)(random.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
				j = (double)(random.nextFloat() * 2.0F * (float)k);
			}

			world.addParticle(ParticleTypes.LIGHTNING, d, e, f, g, h, j);
			world.addParticle(ParticleTypes.REVERSE_LIGHTNING, d, e, f, g, h, j);
		}
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
