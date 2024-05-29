package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class EndPortalBlock extends BlockWithEntity implements Portal {
	public static final MapCodec<EndPortalBlock> CODEC = createCodec(EndPortalBlock::new);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 6.0, 0.0, 16.0, 12.0, 16.0);

	@Override
	public MapCodec<EndPortalBlock> getCodec() {
		return CODEC;
	}

	protected EndPortalBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new EndPortalBlockEntity(pos, state);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.canUsePortals()
			&& VoxelShapes.matchesAnywhere(
				VoxelShapes.cuboid(entity.getBoundingBox().offset((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()))),
				state.getOutlineShape(world, pos),
				BooleanBiFunction.AND
			)) {
			if (!world.isClient && world.getRegistryKey() == World.END && entity instanceof ServerPlayerEntity serverPlayerEntity && !serverPlayerEntity.seenCredits) {
				serverPlayerEntity.detachForDimensionChange();
				return;
			}

			entity.tryUsePortal(this, pos);
		}
	}

	@Override
	public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
		RegistryKey<World> registryKey = world.getRegistryKey() == World.END ? World.OVERWORLD : World.END;
		ServerWorld serverWorld = world.getServer().getWorld(registryKey);
		boolean bl = registryKey == World.END;
		BlockPos blockPos = bl ? ServerWorld.END_SPAWN_POS : serverWorld.getSpawnPos();
		Vec3d vec3d = blockPos.toBottomCenterPos();
		if (bl) {
			this.createEndSpawnPlatform(serverWorld, BlockPos.ofFloored(vec3d).down());
			if (entity instanceof ServerPlayerEntity) {
				vec3d = vec3d.subtract(0.0, 1.0, 0.0);
			}
		} else {
			if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
				return serverPlayerEntity.getRespawnTarget(false, TeleportTarget.NO_OP);
			}

			vec3d = entity.getWorldSpawnPos(serverWorld, blockPos).toBottomCenterPos();
		}

		return new TeleportTarget(serverWorld, vec3d, entity.getVelocity(), entity.getYaw(), entity.getPitch(), TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
	}

	private void createEndSpawnPlatform(ServerWorld world, BlockPos pos) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int k = -1; k < 3; k++) {
					BlockPos blockPos = mutable.set(pos).move(j, k, i);
					Block block = k == -1 ? Blocks.OBSIDIAN : Blocks.AIR;
					if (!world.getBlockState(blockPos).isOf(block)) {
						world.breakBlock(blockPos, true, null);
						world.setBlockState(blockPos, block.getDefaultState());
					}
				}
			}
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
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected boolean canBucketPlace(BlockState state, Fluid fluid) {
		return false;
	}
}
