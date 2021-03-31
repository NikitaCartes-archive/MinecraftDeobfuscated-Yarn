package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class PowderSnowBlock extends Block implements FluidDrainable {
	private static final int field_31216 = 12;
	private static final float field_31217 = 0.9F;
	private static final float field_31218 = 1.5F;
	private static final float field_31219 = 2.5F;
	private static final VoxelShape field_31220 = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

	public PowderSnowBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!(entity instanceof LivingEntity) || ((LivingEntity)entity).getBlockState().isOf(Blocks.POWDER_SNOW)) {
			entity.slowMovement(state, new Vec3d(0.9F, 1.5, 0.9F));
		}

		entity.setInPowderSnow(true);
		if (entity.isOnFire()) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			world.addBlockBreakParticles(pos, state);
		}

		if (world.isClient) {
			entity.extinguish();
		} else {
			entity.setOnFire(false);
		}

		if (!entity.isSpectator() && (entity.lastRenderX != entity.getX() || entity.lastRenderZ != entity.getZ()) && world.random.nextBoolean()) {
			spawnParticles(world, new Vec3d(entity.getX(), (double)pos.getY(), entity.getZ()));
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (context instanceof EntityShapeContext) {
			EntityShapeContext entityShapeContext = (EntityShapeContext)context;
			Optional<Entity> optional = entityShapeContext.getEntity();
			if (optional.isPresent()) {
				Entity entity = (Entity)optional.get();
				if (entity.fallDistance > 2.5F) {
					return field_31220;
				}

				boolean bl = entity instanceof FallingBlockEntity;
				if (bl || canWalkOnPowderSnow(entity) && context.isAbove(VoxelShapes.fullCube(), pos, false) && !context.isDescending()) {
					return super.getCollisionShape(state, world, pos, context);
				}
			}
		}

		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	public static void spawnParticles(World world, Vec3d pos) {
		if (world.isClient) {
			Random random = world.getRandom();
			double d = pos.y + 1.0;

			for (int i = 0; i < random.nextInt(3); i++) {
				world.addParticle(
					ParticleTypes.SNOWFLAKE,
					pos.x,
					d,
					pos.z,
					(double)((-1.0F + random.nextFloat() * 2.0F) / 12.0F),
					0.05F,
					(double)((-1.0F + random.nextFloat() * 2.0F) / 12.0F)
				);
			}
		}
	}

	public static boolean canWalkOnPowderSnow(Entity entity) {
		if (entity.getType().isIn(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
			return true;
		} else {
			return entity instanceof LivingEntity ? ((LivingEntity)entity).getEquippedStack(EquipmentSlot.FEET).isOf(Items.LEATHER_BOOTS) : false;
		}
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
		if (!world.isClient()) {
			world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
		}

		return new ItemStack(Items.POWDER_SNOW_BUCKET);
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return true;
	}
}
