package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.VineProjectileEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class ViciousPotatoBlock extends Block {
	private static final TypeFilter<Entity, LivingEntity> ENTITY_FILTER = TypeFilter.instanceOf(LivingEntity.class);
	public static final BooleanProperty ENABLED = Properties.ENABLED;
	public static final MapCodec<ViciousPotatoBlock> CODEC = createCodec(ViciousPotatoBlock::new);

	public ViciousPotatoBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(ENABLED, Boolean.valueOf(false)));
	}

	@Override
	public MapCodec<ViciousPotatoBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (world instanceof ServerWorld serverWorld) {
			boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
			if (bl) {
				this.method_59161(serverWorld, pos, 5.0F);
			}
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		if (this.method_59161(world, pos, 5.0F)) {
			world.setBlockState(pos, state.with(ENABLED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
			world.scheduleBlockTick(pos, this, 20 + random.nextInt(100));
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		float f = random.nextFloat() * 0.7F;
		if (this.method_59161(world, pos, f) && random.nextFloat() < 1.0F - f * f) {
			world.scheduleBlockTick(pos, this, 20 + random.nextInt(1 + (int)(f * 100.0F)));
		} else {
			world.setBlockState(pos, state.with(ENABLED, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
		}
	}

	private boolean method_59161(ServerWorld world, BlockPos pos, float f) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		Box box = Box.of(vec3d, 16.0, 16.0, 16.0);
		List<LivingEntity> list = new ArrayList();
		world.collectEntitiesByType(ENTITY_FILTER, box, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR, list, 10);
		Optional<LivingEntity> optional = Util.getRandomOrEmpty(list, world.getRandom());
		if (optional.isEmpty()) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)optional.get();
			Vec3d vec3d2 = livingEntity.getBoundingBox().getCenter();
			Vec3d vec3d3 = vec3d2.subtract(vec3d).normalize();
			Vec3d vec3d4 = vec3d3.add(vec3d);
			VineProjectileEntity vineProjectileEntity = EntityType.VINE_PROJECTILE.create(world);
			vineProjectileEntity.setStrength(f);
			vineProjectileEntity.setPosition(vec3d4.getX(), vec3d4.getY(), vec3d4.getZ());
			vineProjectileEntity.setVelocity(vec3d3.x, vec3d3.y, vec3d3.z, 0.5F, 0.0F);
			world.spawnEntity(vineProjectileEntity);
			world.syncWorldEvent(WorldEvents.DISPENSER_LAUNCHES_PROJECTILE, pos, 0);
			return true;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ENABLED);
	}
}
