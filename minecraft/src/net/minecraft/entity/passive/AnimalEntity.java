package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AnimalEntity extends PassiveEntity {
	protected static final int BREEDING_COOLDOWN = 6000;
	private int loveTicks;
	@Nullable
	private UUID lovingPlayer;

	protected AnimalEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
	}

	@Override
	protected void mobTick() {
		if (this.getBreedingAge() != 0) {
			this.loveTicks = 0;
		}

		super.mobTick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.getBreedingAge() != 0) {
			this.loveTicks = 0;
		}

		if (this.loveTicks > 0) {
			this.loveTicks--;
			if (this.loveTicks % 10 == 0) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.getWorld().addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.loveTicks = 0;
			return super.damage(source, amount);
		}
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK) ? 10.0F : world.getPhototaxisFavor(pos);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("InLove", this.loveTicks);
		if (this.lovingPlayer != null) {
			nbt.putUuid("LoveCause", this.lovingPlayer);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.loveTicks = nbt.getInt("InLove");
		this.lovingPlayer = nbt.containsUuid("LoveCause") ? nbt.getUuid("LoveCause") : null;
	}

	public static boolean isValidNaturalSpawn(EntityType<? extends AnimalEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		boolean bl = SpawnReason.isTrialSpawner(spawnReason) || isLightLevelValidForNaturalSpawn(world, pos);
		return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON) && bl;
	}

	protected static boolean isLightLevelValidForNaturalSpawn(BlockRenderView world, BlockPos pos) {
		return world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	@Override
	public int getXpToDrop() {
		return 1 + this.getWorld().random.nextInt(3);
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.isOf(Items.WHEAT);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.isBreedingItem(itemStack)) {
			int i = this.getBreedingAge();
			if (!this.getWorld().isClient && i == 0 && this.canEat()) {
				this.eat(player, hand, itemStack);
				this.lovePlayer(player);
				return ActionResult.SUCCESS;
			}

			if (this.isBaby()) {
				this.eat(player, hand, itemStack);
				this.growUp(toGrowUpAge(-i), true);
				return ActionResult.success(this.getWorld().isClient);
			}

			if (this.getWorld().isClient) {
				return ActionResult.CONSUME;
			}
		}

		return super.interactMob(player, hand);
	}

	protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
		stack.decrementUnlessCreative(1, player);
	}

	public boolean canEat() {
		return this.loveTicks <= 0;
	}

	public void lovePlayer(@Nullable PlayerEntity player) {
		this.loveTicks = 600;
		if (player != null) {
			this.lovingPlayer = player.getUuid();
		}

		this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);
	}

	public void setLoveTicks(int loveTicks) {
		this.loveTicks = loveTicks;
	}

	public int getLoveTicks() {
		return this.loveTicks;
	}

	@Nullable
	public ServerPlayerEntity getLovingPlayer() {
		if (this.lovingPlayer == null) {
			return null;
		} else {
			PlayerEntity playerEntity = this.getWorld().getPlayerByUuid(this.lovingPlayer);
			return playerEntity instanceof ServerPlayerEntity ? (ServerPlayerEntity)playerEntity : null;
		}
	}

	public boolean isInLove() {
		return this.loveTicks > 0;
	}

	public void resetLoveTicks() {
		this.loveTicks = 0;
	}

	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return other.getClass() != this.getClass() ? false : this.isInLove() && other.isInLove();
		}
	}

	public void breed(ServerWorld world, AnimalEntity other) {
		PassiveEntity passiveEntity = this.createChild(world, other);
		if (passiveEntity != null) {
			passiveEntity.setBaby(true);
			passiveEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
			this.breed(world, other, passiveEntity);
			world.spawnEntityAndPassengers(passiveEntity);
		}
	}

	public void breed(ServerWorld world, AnimalEntity other, @Nullable PassiveEntity baby) {
		Optional.ofNullable(this.getLovingPlayer()).or(() -> Optional.ofNullable(other.getLovingPlayer())).ifPresent(player -> {
			player.incrementStat(Stats.ANIMALS_BRED);
			Criteria.BRED_ANIMALS.trigger(player, this, other, baby);
		});
		this.setBreedingAge(6000);
		other.setBreedingAge(6000);
		this.resetLoveTicks();
		other.resetLoveTicks();
		world.sendEntityStatus(this, EntityStatuses.ADD_BREEDING_PARTICLES);
		if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
			world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_BREEDING_PARTICLES) {
			for (int i = 0; i < 7; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.getWorld().addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
			}
		} else {
			super.handleStatus(status);
		}
	}
}
