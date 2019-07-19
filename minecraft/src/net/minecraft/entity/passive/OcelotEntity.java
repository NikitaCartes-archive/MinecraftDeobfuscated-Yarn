package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class OcelotEntity extends AnimalEntity {
	private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.COD, Items.SALMON);
	private static final TrackedData<Boolean> TRUSTING = DataTracker.registerData(OcelotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private OcelotEntity.FleeGoal<PlayerEntity> fleeGoal;
	private OcelotEntity.OcelotTemptGoal temptGoal;

	public OcelotEntity(EntityType<? extends OcelotEntity> entityType, World world) {
		super(entityType, world);
		this.updateFleeing();
	}

	private boolean isTrusting() {
		return this.dataTracker.get(TRUSTING);
	}

	private void setTrusting(boolean trusting) {
		this.dataTracker.set(TRUSTING, trusting);
		this.updateFleeing();
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("Trusting", this.isTrusting());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setTrusting(tag.getBoolean("Trusting"));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TRUSTING, false);
	}

	@Override
	protected void initGoals() {
		this.temptGoal = new OcelotEntity.OcelotTemptGoal(this, 0.6, TAMING_INGREDIENT, true);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(3, this.temptGoal);
		this.goalSelector.add(7, new PounceAtTargetGoal(this, 0.3F));
		this.goalSelector.add(8, new AttackGoal(this));
		this.goalSelector.add(9, new AnimalMateGoal(this, 0.8));
		this.goalSelector.add(10, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5F));
		this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.targetSelector.add(1, new FollowTargetGoal(this, ChickenEntity.class, false));
		this.targetSelector.add(1, new FollowTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	public void mobTick() {
		if (this.getMoveControl().isMoving()) {
			double d = this.getMoveControl().getSpeed();
			if (d == 0.6) {
				this.setSneaking(true);
				this.setSprinting(false);
			} else if (d == 1.33) {
				this.setSneaking(false);
				this.setSprinting(true);
			} else {
				this.setSneaking(false);
				this.setSprinting(false);
			}
		} else {
			this.setSneaking(false);
			this.setSprinting(false);
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isTrusting() && this.age > 2400;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	public void handleFallDamage(float fallDistance, float damageMultiplier) {
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_OCELOT_AMBIENT;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 900;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_OCELOT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_OCELOT_DEATH;
	}

	@Override
	public boolean tryAttack(Entity target) {
		return target.damage(DamageSource.mob(this), 3.0F);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if ((this.temptGoal == null || this.temptGoal.isActive()) && !this.isTrusting() && this.isBreedingItem(itemStack) && player.squaredDistanceTo(this) < 9.0) {
			this.eat(player, itemStack);
			if (!this.world.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.setTrusting(true);
					this.showEmoteParticle(true);
					this.world.sendEntityStatus(this, (byte)41);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, (byte)40);
				}
			}

			return true;
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 41) {
			this.showEmoteParticle(true);
		} else if (status == 40) {
			this.showEmoteParticle(false);
		} else {
			super.handleStatus(status);
		}
	}

	private void showEmoteParticle(boolean positive) {
		ParticleEffect particleEffect = ParticleTypes.HEART;
		if (!positive) {
			particleEffect = ParticleTypes.SMOKE;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleEffect,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	protected void updateFleeing() {
		if (this.fleeGoal == null) {
			this.fleeGoal = new OcelotEntity.FleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.goalSelector.remove(this.fleeGoal);
		if (!this.isTrusting()) {
			this.goalSelector.add(4, this.fleeGoal);
		}
	}

	public OcelotEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.OCELOT.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return TAMING_INGREDIENT.test(stack);
	}

	public static boolean method_20666(EntityType<OcelotEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return random.nextInt(3) != 0;
	}

	@Override
	public boolean canSpawn(CollisionView world) {
		if (world.intersectsEntities(this) && !world.intersectsFluid(this.getBoundingBox())) {
			BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().y1, this.z);
			if (blockPos.getY() < world.getSeaLevel()) {
				return false;
			}

			BlockState blockState = world.getBlockState(blockPos.down());
			Block block = blockState.getBlock();
			if (block == Blocks.GRASS_BLOCK || blockState.matches(BlockTags.LEAVES)) {
				return true;
			}
		}

		return false;
	}

	protected void spawnKittens() {
		for (int i = 0; i < 2; i++) {
			OcelotEntity ocelotEntity = EntityType.OCELOT.create(this.world);
			ocelotEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			ocelotEntity.setBreedingAge(-24000);
			this.world.spawnEntity(ocelotEntity);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		entityData = super.initialize(world, difficulty, spawnType, entityData, entityTag);
		if (world.getRandom().nextInt(7) == 0) {
			this.spawnKittens();
		}

		return entityData;
	}

	static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final OcelotEntity ocelot;

		public FleeGoal(OcelotEntity ocelot, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
			super(ocelot, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
			this.ocelot = ocelot;
		}

		@Override
		public boolean canStart() {
			return !this.ocelot.isTrusting() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.ocelot.isTrusting() && super.shouldContinue();
		}
	}

	static class OcelotTemptGoal extends TemptGoal {
		private final OcelotEntity ocelot;

		public OcelotTemptGoal(OcelotEntity ocelot, double speed, Ingredient food, boolean canBeScared) {
			super(ocelot, speed, food, canBeScared);
			this.ocelot = ocelot;
		}

		@Override
		protected boolean canBeScared() {
			return super.canBeScared() && !this.ocelot.isTrusting();
		}
	}
}
