package net.minecraft.entity.passive;

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
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class OcelotEntity extends AnimalEntity {
	private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.field_8429, Items.field_8209);
	private static final TrackedData<Boolean> TRUSTING = DataTracker.registerData(OcelotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private OcelotEntity.OcelotFleeGoal<PlayerEntity> fleeGoal;
	private OcelotEntity.OcelotTemptGoal temptGoal;

	public OcelotEntity(EntityType<? extends OcelotEntity> entityType, World world) {
		super(entityType, world);
		this.updateFleeing();
	}

	private boolean isTrusting() {
		return this.dataTracker.get(TRUSTING);
	}

	private void setTrusting(boolean bl) {
		this.dataTracker.set(TRUSTING, bl);
		this.updateFleeing();
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("Trusting", this.isTrusting());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setTrusting(compoundTag.getBoolean("Trusting"));
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
	public boolean canImmediatelyDespawn(double d) {
		return !this.isTrusting() && this.age > 2400;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_16437;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 900;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_16441;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_16442;
	}

	@Override
	public boolean tryAttack(Entity entity) {
		return entity.damage(DamageSource.mob(this), 3.0F);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if ((this.temptGoal == null || this.temptGoal.isActive())
			&& !this.isTrusting()
			&& this.isBreedingItem(itemStack)
			&& playerEntity.squaredDistanceTo(this) < 9.0) {
			this.eat(playerEntity, itemStack);
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
			return super.interactMob(playerEntity, hand);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 41) {
			this.showEmoteParticle(true);
		} else if (b == 40) {
			this.showEmoteParticle(false);
		} else {
			super.handleStatus(b);
		}
	}

	private void showEmoteParticle(boolean bl) {
		ParticleParameters particleParameters = ParticleTypes.field_11201;
		if (!bl) {
			particleParameters = ParticleTypes.field_11251;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleParameters,
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
			this.fleeGoal = new OcelotEntity.OcelotFleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.goalSelector.remove(this.fleeGoal);
		if (!this.isTrusting()) {
			this.goalSelector.add(4, this.fleeGoal);
		}
	}

	public OcelotEntity method_16104(PassiveEntity passiveEntity) {
		return EntityType.OCELOT.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return TAMING_INGREDIENT.method_8093(itemStack);
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return this.random.nextInt(3) != 0;
	}

	@Override
	public boolean canSpawn(ViewableWorld viewableWorld) {
		if (viewableWorld.intersectsEntities(this) && !viewableWorld.intersectsFluid(this.getBoundingBox())) {
			BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
			if (blockPos.getY() < viewableWorld.getSeaLevel()) {
				return false;
			}

			BlockState blockState = viewableWorld.getBlockState(blockPos.down());
			Block block = blockState.getBlock();
			if (block == Blocks.field_10219 || blockState.matches(BlockTags.field_15503)) {
				return true;
			}
		}

		return false;
	}

	protected void spawnKittens() {
		for (int i = 0; i < 2; i++) {
			OcelotEntity ocelotEntity = EntityType.OCELOT.create(this.world);
			ocelotEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			ocelotEntity.setBreedingAge(-24000);
			this.world.spawnEntity(ocelotEntity);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (iWorld.getRandom().nextInt(7) == 0) {
			this.spawnKittens();
		}

		return entityData;
	}

	static class OcelotFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final OcelotEntity ocelot;

		public OcelotFleeGoal(OcelotEntity ocelotEntity, Class<T> class_, float f, double d, double e) {
			super(ocelotEntity, class_, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
			this.ocelot = ocelotEntity;
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

		public OcelotTemptGoal(OcelotEntity ocelotEntity, double d, Ingredient ingredient, boolean bl) {
			super(ocelotEntity, d, ingredient, bl);
			this.ocelot = ocelotEntity;
		}

		@Override
		protected boolean canBeScared() {
			return super.canBeScared() && !this.ocelot.isTrusting();
		}
	}
}
