package net.minecraft.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class TntEntity extends Entity implements Ownable {
	private static final TrackedData<Integer> FUSE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<BlockState> BLOCK_STATE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.BLOCK_STATE);
	private static final int DEFAULT_FUSE = 80;
	private static final String BLOCK_STATE_NBT_KEY = "block_state";
	public static final String FUSE_NBT_KEY = "fuse";
	private static final ExplosionBehavior TELEPORTED_EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
		@Override
		public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
			return state.isOf(Blocks.NETHER_PORTAL) ? false : super.canDestroyBlock(explosion, world, pos, state, power);
		}

		@Override
		public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
			return blockState.isOf(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlastResistance(explosion, world, pos, blockState, fluidState);
		}
	};
	@Nullable
	private LivingEntity causingEntity;
	private boolean teleported;

	public TntEntity(EntityType<? extends TntEntity> entityType, World world) {
		super(entityType, world);
		this.intersectionChecked = true;
	}

	public TntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
		this(EntityType.TNT, world);
		this.setPosition(x, y, z);
		double d = world.random.nextDouble() * (float) (Math.PI * 2);
		this.setVelocity(-Math.sin(d) * 0.02, 0.2F, -Math.cos(d) * 0.02);
		this.setFuse(80);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
		this.causingEntity = igniter;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(FUSE, 80);
		builder.add(BLOCK_STATE, Blocks.TNT.getDefaultState());
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	public boolean canHit() {
		return !this.isRemoved();
	}

	@Override
	protected double getGravity() {
		return 0.04;
	}

	@Override
	public void tick() {
		this.tickPortalTeleportation();
		this.applyGravity();
		this.move(MovementType.SELF, this.getVelocity());
		this.setVelocity(this.getVelocity().multiply(0.98));
		if (this.isOnGround()) {
			this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
		}

		int i = this.getFuse() - 1;
		this.setFuse(i);
		if (i <= 0) {
			this.discard();
			if (!this.getWorld().isClient) {
				this.explode();
			}
		} else {
			this.updateWaterState();
			if (this.getWorld().isClient) {
				this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	private void explode() {
		float f = 4.0F;
		this.getWorld()
			.createExplosion(
				this,
				Explosion.createDamageSource(this.getWorld(), this),
				this.teleported ? TELEPORTED_EXPLOSION_BEHAVIOR : null,
				this.getX(),
				this.getBodyY(0.0625),
				this.getZ(),
				4.0F,
				false,
				World.ExplosionSourceType.TNT
			);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("fuse", (short)this.getFuse());
		nbt.put("block_state", NbtHelper.fromBlockState(this.getBlockState()));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.setFuse(nbt.getShort("fuse"));
		if (nbt.contains("block_state", NbtElement.COMPOUND_TYPE)) {
			this.setBlockState(NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("block_state")));
		}
	}

	@Nullable
	public LivingEntity getOwner() {
		return this.causingEntity;
	}

	@Override
	public void copyFrom(Entity original) {
		super.copyFrom(original);
		if (original instanceof TntEntity tntEntity) {
			this.causingEntity = tntEntity.causingEntity;
		}
	}

	public void setFuse(int fuse) {
		this.dataTracker.set(FUSE, fuse);
	}

	public int getFuse() {
		return this.dataTracker.get(FUSE);
	}

	public void setBlockState(BlockState state) {
		this.dataTracker.set(BLOCK_STATE, state);
	}

	public BlockState getBlockState() {
		return this.dataTracker.get(BLOCK_STATE);
	}

	private void setTeleported(boolean teleported) {
		this.teleported = teleported;
	}

	@Nullable
	@Override
	public Entity teleportTo(TeleportTarget teleportTarget) {
		Entity entity = super.teleportTo(teleportTarget);
		if (entity instanceof TntEntity tntEntity) {
			tntEntity.setTeleported(true);
		}

		return entity;
	}
}
