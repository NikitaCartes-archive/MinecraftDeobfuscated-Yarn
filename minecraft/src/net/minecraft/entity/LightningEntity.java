package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class LightningEntity extends Entity {
	private static final int field_30062 = 2;
	private static final double field_33906 = 3.0;
	private static final double field_33907 = 15.0;
	private int ambientTick;
	public long seed;
	private int remainingActions;
	private boolean cosmetic;
	@Nullable
	private ServerPlayerEntity channeler;
	private final Set<Entity> struckEntities = Sets.<Entity>newHashSet();
	private int blocksSetOnFire;

	public LightningEntity(EntityType<? extends LightningEntity> entityType, World world) {
		super(entityType, world);
		this.ignoreCameraFrustum = true;
		this.ambientTick = 2;
		this.seed = this.random.nextLong();
		this.remainingActions = this.random.nextInt(3) + 1;
	}

	public void setCosmetic(boolean cosmetic) {
		this.cosmetic = cosmetic;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.WEATHER;
	}

	@Nullable
	public ServerPlayerEntity getChanneler() {
		return this.channeler;
	}

	public void setChanneler(@Nullable ServerPlayerEntity channeler) {
		this.channeler = channeler;
	}

	private void powerLightningRod() {
		BlockPos blockPos = this.getAffectedBlockPos();
		BlockState blockState = this.getWorld().getBlockState(blockPos);
		if (blockState.isOf(Blocks.LIGHTNING_ROD)) {
			((LightningRodBlock)blockState.getBlock()).setPowered(blockState, this.getWorld(), blockPos);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.ambientTick == 2) {
			if (this.getWorld().isClient()) {
				this.getWorld()
					.playSound(
						this.getX(),
						this.getY(),
						this.getZ(),
						SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
						SoundCategory.WEATHER,
						10000.0F,
						0.8F + this.random.nextFloat() * 0.2F,
						false
					);
				this.getWorld()
					.playSound(
						this.getX(),
						this.getY(),
						this.getZ(),
						SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
						SoundCategory.WEATHER,
						2.0F,
						0.5F + this.random.nextFloat() * 0.2F,
						false
					);
			} else {
				Difficulty difficulty = this.getWorld().getDifficulty();
				if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
					this.spawnFire(4);
				}

				this.powerLightningRod();
				cleanOxidation(this.getWorld(), this.getAffectedBlockPos());
				this.emitGameEvent(GameEvent.LIGHTNING_STRIKE);
			}
		}

		this.ambientTick--;
		if (this.ambientTick < 0) {
			if (this.remainingActions == 0) {
				if (this.getWorld() instanceof ServerWorld) {
					List<Entity> list = this.getWorld()
						.getOtherEntities(
							this,
							new Box(this.getX() - 15.0, this.getY() - 15.0, this.getZ() - 15.0, this.getX() + 15.0, this.getY() + 6.0 + 15.0, this.getZ() + 15.0),
							entityx -> entityx.isAlive() && !this.struckEntities.contains(entityx)
						);

					for (ServerPlayerEntity serverPlayerEntity : ((ServerWorld)this.getWorld())
						.getPlayers(serverPlayerEntityx -> serverPlayerEntityx.distanceTo(this) < 256.0F)) {
						Criteria.LIGHTNING_STRIKE.trigger(serverPlayerEntity, this, list);
					}
				}

				this.discard();
			} else if (this.ambientTick < -this.random.nextInt(10)) {
				this.remainingActions--;
				this.ambientTick = 1;
				this.seed = this.random.nextLong();
				this.spawnFire(0);
			}
		}

		if (this.ambientTick >= 0) {
			if (!(this.getWorld() instanceof ServerWorld)) {
				this.getWorld().setLightningTicksLeft(2);
			} else if (!this.cosmetic) {
				List<Entity> list = this.getWorld()
					.getOtherEntities(
						this, new Box(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0), Entity::isAlive
					);

				for (Entity entity : list) {
					entity.onStruckByLightning((ServerWorld)this.getWorld(), this);
				}

				this.struckEntities.addAll(list);
				if (this.channeler != null) {
					Criteria.CHANNELED_LIGHTNING.trigger(this.channeler, list);
				}
			}
		}
	}

	private BlockPos getAffectedBlockPos() {
		Vec3d vec3d = this.getPos();
		return BlockPos.ofFloored(vec3d.x, vec3d.y - 1.0E-6, vec3d.z);
	}

	private void spawnFire(int spreadAttempts) {
		if (!this.cosmetic && !this.getWorld().isClient && this.getWorld().getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
			BlockPos blockPos = this.getBlockPos();
			BlockState blockState = AbstractFireBlock.getState(this.getWorld(), blockPos);
			if (this.getWorld().getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.getWorld(), blockPos)) {
				this.getWorld().setBlockState(blockPos, blockState);
				this.blocksSetOnFire++;
			}

			for (int i = 0; i < spreadAttempts; i++) {
				BlockPos blockPos2 = blockPos.add(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
				blockState = AbstractFireBlock.getState(this.getWorld(), blockPos2);
				if (this.getWorld().getBlockState(blockPos2).isAir() && blockState.canPlaceAt(this.getWorld(), blockPos2)) {
					this.getWorld().setBlockState(blockPos2, blockState);
					this.blocksSetOnFire++;
				}
			}
		}
	}

	private static void cleanOxidation(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		BlockPos blockPos;
		BlockState blockState2;
		if (blockState.isOf(Blocks.LIGHTNING_ROD)) {
			blockPos = pos.offset(((Direction)blockState.get(LightningRodBlock.FACING)).getOpposite());
			blockState2 = world.getBlockState(blockPos);
		} else {
			blockPos = pos;
			blockState2 = blockState;
		}

		if (blockState2.getBlock() instanceof Oxidizable) {
			world.setBlockState(blockPos, Oxidizable.getUnaffectedOxidationState(world.getBlockState(blockPos)));
			BlockPos.Mutable mutable = pos.mutableCopy();
			int i = world.random.nextInt(3) + 3;

			for (int j = 0; j < i; j++) {
				int k = world.random.nextInt(8) + 1;
				cleanOxidationAround(world, blockPos, mutable, k);
			}
		}
	}

	private static void cleanOxidationAround(World world, BlockPos pos, BlockPos.Mutable mutablePos, int count) {
		mutablePos.set(pos);

		for (int i = 0; i < count; i++) {
			Optional<BlockPos> optional = cleanOxidationAround(world, mutablePos);
			if (optional.isEmpty()) {
				break;
			}

			mutablePos.set((Vec3i)optional.get());
		}
	}

	private static Optional<BlockPos> cleanOxidationAround(World world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterateRandomly(world.random, 10, pos, 1)) {
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() instanceof Oxidizable) {
				Oxidizable.getDecreasedOxidationState(blockState).ifPresent(state -> world.setBlockState(blockPos, state));
				world.syncWorldEvent(WorldEvents.ELECTRICITY_SPARKS, blockPos, -1);
				return Optional.of(blockPos);
			}
		}

		return Optional.empty();
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}

	public int getBlocksSetOnFire() {
		return this.blocksSetOnFire;
	}

	public Stream<Entity> getStruckEntities() {
		return this.struckEntities.stream().filter(Entity::isAlive);
	}
}
