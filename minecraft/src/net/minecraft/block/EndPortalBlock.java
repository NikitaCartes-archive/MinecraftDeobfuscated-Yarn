package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalBlock extends BlockWithEntity {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 6.0, 0.0, 16.0, 12.0, 16.0);

	protected EndPortalBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new EndPortalBlockEntity(pos, state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world instanceof ServerWorld
			&& !entity.hasVehicle()
			&& !entity.hasPassengers()
			&& entity.canUsePortals()
			&& VoxelShapes.matchesAnywhere(
				VoxelShapes.cuboid(entity.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))),
				state.getOutlineShape(world, pos),
				BooleanBiFunction.AND
			)) {
			RegistryKey<World> registryKey = world.getRegistryKey() == World.END ? World.OVERWORLD : World.END;
			ServerWorld serverWorld = ((ServerWorld)world).getServer().getWorld(registryKey);
			if (serverWorld == null) {
				return;
			}

			entity.moveToWorld(serverWorld);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)pos.getX() + random.nextDouble();
		double e = (double)pos.getY() + 0.8;
		double f = (double)pos.getZ() + random.nextDouble();
		world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canBucketPlace(BlockState state, Fluid fluid) {
		return false;
	}
}
