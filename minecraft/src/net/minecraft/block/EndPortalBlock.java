package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.EndPlatformFeature;

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
	protected VoxelShape getInsideCollisionShape(BlockState state, World world, BlockPos pos) {
		return state.getOutlineShape(world, pos);
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.canUsePortals(false)) {
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
		if (serverWorld == null) {
			return null;
		} else {
			boolean bl = registryKey == World.END;
			BlockPos blockPos = bl ? ServerWorld.END_SPAWN_POS : serverWorld.getSpawnPos();
			Vec3d vec3d = blockPos.toBottomCenterPos();
			float f;
			Set<PositionFlag> set;
			if (bl) {
				EndPlatformFeature.generate(serverWorld, BlockPos.ofFloored(vec3d).down(), true);
				f = Direction.WEST.asRotation();
				set = PositionFlag.combine(PositionFlag.DELTA, Set.of(PositionFlag.X_ROT));
				if (entity instanceof ServerPlayerEntity) {
					vec3d = vec3d.subtract(0.0, 1.0, 0.0);
				}
			} else {
				f = 0.0F;
				set = PositionFlag.combine(PositionFlag.DELTA, PositionFlag.ROT);
				if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
					return serverPlayerEntity.getRespawnTarget(false, TeleportTarget.NO_OP);
				}

				vec3d = entity.getWorldSpawnPos(serverWorld, blockPos).toBottomCenterPos();
			}

			return new TeleportTarget(
				serverWorld, vec3d, Vec3d.ZERO, f, 0.0F, set, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
			);
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
