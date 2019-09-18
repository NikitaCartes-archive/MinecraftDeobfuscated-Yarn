package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.GoToOwnerAndPurrGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.loot.LootTable;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public class CatEntity extends TameableEntity {
	private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.COD, Items.SALMON);
	private static final TrackedData<Integer> CAT_TYPE = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> SLEEPING_WITH_OWNER = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> HEAD_DOWN = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final Map<Integer, Identifier> TEXTURES = SystemUtil.consume(Maps.<Integer, Identifier>newHashMap(), hashMap -> {
		hashMap.put(0, new Identifier("textures/entity/cat/tabby.png"));
		hashMap.put(1, new Identifier("textures/entity/cat/black.png"));
		hashMap.put(2, new Identifier("textures/entity/cat/red.png"));
		hashMap.put(3, new Identifier("textures/entity/cat/siamese.png"));
		hashMap.put(4, new Identifier("textures/entity/cat/british_shorthair.png"));
		hashMap.put(5, new Identifier("textures/entity/cat/calico.png"));
		hashMap.put(6, new Identifier("textures/entity/cat/persian.png"));
		hashMap.put(7, new Identifier("textures/entity/cat/ragdoll.png"));
		hashMap.put(8, new Identifier("textures/entity/cat/white.png"));
		hashMap.put(9, new Identifier("textures/entity/cat/jellie.png"));
		hashMap.put(10, new Identifier("textures/entity/cat/all_black.png"));
	});
	private CatEntity.CatFleeGoal<PlayerEntity> fleeGoal;
	private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
	private float sleepAnimation;
	private float prevSleepAnimation;
	private float tailCurlAnimation;
	private float prevTailCurlAnimation;
	private float headDownAnimation;
	private float prevHeadDownAniamtion;

	public CatEntity(EntityType<? extends CatEntity> entityType, World world) {
		super(entityType, world);
	}

	public Identifier getTexture() {
		return (Identifier)TEXTURES.get(this.getCatType());
	}

	@Override
	protected void initGoals() {
		this.sitGoal = new SitGoal(this);
		this.temptGoal = new CatEntity.TemptGoal(this, 0.6, TAMING_INGREDIENT, true);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(1, new CatEntity.SleepWithOwnerGoal(this));
		this.goalSelector.add(2, this.sitGoal);
		this.goalSelector.add(3, this.temptGoal);
		this.goalSelector.add(5, new GoToOwnerAndPurrGoal(this, 1.1, 8));
		this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
		this.goalSelector.add(7, new CatSitOnBlockGoal(this, 0.8));
		this.goalSelector.add(8, new PounceAtTargetGoal(this, 0.3F));
		this.goalSelector.add(9, new AttackGoal(this));
		this.goalSelector.add(10, new AnimalMateGoal(this, 0.8));
		this.goalSelector.add(11, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5F));
		this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.targetSelector.add(1, new FollowTargetIfTamedGoal(this, RabbitEntity.class, false, null));
		this.targetSelector.add(1, new FollowTargetIfTamedGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	public int getCatType() {
		return this.dataTracker.get(CAT_TYPE);
	}

	public void setCatType(int i) {
		if (i < 0 || i >= 11) {
			i = this.random.nextInt(10);
		}

		this.dataTracker.set(CAT_TYPE, i);
	}

	public void setSleepingWithOwner(boolean bl) {
		this.dataTracker.set(SLEEPING_WITH_OWNER, bl);
	}

	public boolean isSleepingWithOwner() {
		return this.dataTracker.get(SLEEPING_WITH_OWNER);
	}

	public void setHeadDown(boolean bl) {
		this.dataTracker.set(HEAD_DOWN, bl);
	}

	public boolean isHeadDown() {
		return this.dataTracker.get(HEAD_DOWN);
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
	}

	public void setCollarColor(DyeColor dyeColor) {
		this.dataTracker.set(COLLAR_COLOR, dyeColor.getId());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CAT_TYPE, 1);
		this.dataTracker.startTracking(SLEEPING_WITH_OWNER, false);
		this.dataTracker.startTracking(HEAD_DOWN, false);
		this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.RED.getId());
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("CatType", this.getCatType());
		compoundTag.putByte("CollarColor", (byte)this.getCollarColor().getId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setCatType(compoundTag.getInt("CatType"));
		if (compoundTag.containsKey("CollarColor", 99)) {
			this.setCollarColor(DyeColor.byId(compoundTag.getInt("CollarColor")));
		}
	}

	@Override
	public void mobTick() {
		if (this.getMoveControl().isMoving()) {
			double d = this.getMoveControl().getSpeed();
			if (d == 0.6) {
				this.setPose(EntityPose.CROUCHING);
				this.setSprinting(false);
			} else if (d == 1.33) {
				this.setPose(EntityPose.STANDING);
				this.setSprinting(true);
			} else {
				this.setPose(EntityPose.STANDING);
				this.setSprinting(false);
			}
		} else {
			this.setPose(EntityPose.STANDING);
			this.setSprinting(false);
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isTamed()) {
			if (this.isInLove()) {
				return SoundEvents.ENTITY_CAT_PURR;
			} else {
				return this.random.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
			}
		} else {
			return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
		}
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	public void hiss() {
		this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.ENTITY_CAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CAT_DEATH;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void eat(PlayerEntity playerEntity, ItemStack itemStack) {
		if (this.isBreedingItem(itemStack)) {
			this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0F, 1.0F);
		}

		super.eat(playerEntity, itemStack);
	}

	private float method_22327() {
		return (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
	}

	@Override
	public boolean tryAttack(Entity entity) {
		return entity.damage(DamageSource.mob(this), this.method_22327());
	}

	@Override
	public void tick() {
		super.tick();
		if (this.temptGoal != null && this.temptGoal.isActive() && !this.isTamed() && this.age % 100 == 0) {
			this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
		}

		this.updateAnimations();
	}

	private void updateAnimations() {
		if ((this.isSleepingWithOwner() || this.isHeadDown()) && this.age % 5 == 0) {
			this.playSound(SoundEvents.ENTITY_CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
		}

		this.updateSleepAnimation();
		this.updateHeadDownAnimation();
	}

	private void updateSleepAnimation() {
		this.prevSleepAnimation = this.sleepAnimation;
		this.prevTailCurlAnimation = this.tailCurlAnimation;
		if (this.isSleepingWithOwner()) {
			this.sleepAnimation = Math.min(1.0F, this.sleepAnimation + 0.15F);
			this.tailCurlAnimation = Math.min(1.0F, this.tailCurlAnimation + 0.08F);
		} else {
			this.sleepAnimation = Math.max(0.0F, this.sleepAnimation - 0.22F);
			this.tailCurlAnimation = Math.max(0.0F, this.tailCurlAnimation - 0.13F);
		}
	}

	private void updateHeadDownAnimation() {
		this.prevHeadDownAniamtion = this.headDownAnimation;
		if (this.isHeadDown()) {
			this.headDownAnimation = Math.min(1.0F, this.headDownAnimation + 0.1F);
		} else {
			this.headDownAnimation = Math.max(0.0F, this.headDownAnimation - 0.13F);
		}
	}

	@Environment(EnvType.CLIENT)
	public float getSleepAnimation(float f) {
		return MathHelper.lerp(f, this.prevSleepAnimation, this.sleepAnimation);
	}

	@Environment(EnvType.CLIENT)
	public float getTailCurlAnimation(float f) {
		return MathHelper.lerp(f, this.prevTailCurlAnimation, this.tailCurlAnimation);
	}

	@Environment(EnvType.CLIENT)
	public float getHeadDownAnimation(float f) {
		return MathHelper.lerp(f, this.prevHeadDownAniamtion, this.headDownAnimation);
	}

	public CatEntity method_6573(PassiveEntity passiveEntity) {
		CatEntity catEntity = EntityType.CAT.create(this.world);
		if (passiveEntity instanceof CatEntity) {
			if (this.random.nextBoolean()) {
				catEntity.setCatType(this.getCatType());
			} else {
				catEntity.setCatType(((CatEntity)passiveEntity).getCatType());
			}

			if (this.isTamed()) {
				catEntity.setOwnerUuid(this.getOwnerUuid());
				catEntity.setTamed(true);
				if (this.random.nextBoolean()) {
					catEntity.setCollarColor(this.getCollarColor());
				} else {
					catEntity.setCollarColor(((CatEntity)passiveEntity).getCollarColor());
				}
			}
		}

		return catEntity;
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		if (!this.isTamed()) {
			return false;
		} else if (!(animalEntity instanceof CatEntity)) {
			return false;
		} else {
			CatEntity catEntity = (CatEntity)animalEntity;
			return catEntity.isTamed() && super.canBreedWith(animalEntity);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (iWorld.getMoonSize() > 0.9F) {
			this.setCatType(this.random.nextInt(11));
		} else {
			this.setCatType(this.random.nextInt(10));
		}

		if (Feature.SWAMP_HUT.isInsideStructure(iWorld, new BlockPos(this))) {
			this.setCatType(10);
			this.setPersistent();
		}

		return entityData;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.isTamed()) {
			if (this.isOwner(playerEntity)) {
				if (item instanceof DyeItem) {
					DyeColor dyeColor = ((DyeItem)item).getColor();
					if (dyeColor != this.getCollarColor()) {
						this.setCollarColor(dyeColor);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.decrement(1);
						}

						this.setPersistent();
						return true;
					}
				} else if (this.isBreedingItem(itemStack)) {
					if (this.getHealth() < this.getMaximumHealth() && item.isFood()) {
						this.eat(playerEntity, itemStack);
						this.heal((float)item.getFoodComponent().getHunger());
						return true;
					}
				} else if (!this.world.isClient) {
					this.sitGoal.setEnabledWithOwner(!this.isSitting());
				}
			}
		} else if (this.isBreedingItem(itemStack)) {
			this.eat(playerEntity, itemStack);
			if (!this.world.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.setOwner(playerEntity);
					this.showEmoteParticle(true);
					this.sitGoal.setEnabledWithOwner(true);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, (byte)6);
				}
			}

			this.setPersistent();
			return true;
		}

		boolean bl = super.interactMob(playerEntity, hand);
		if (bl) {
			this.setPersistent();
		}

		return bl;
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return TAMING_INGREDIENT.method_8093(itemStack);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 0.5F;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.isTamed() && this.age > 2400;
	}

	@Override
	protected void onTamedChanged() {
		if (this.fleeGoal == null) {
			this.fleeGoal = new CatEntity.CatFleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.goalSelector.remove(this.fleeGoal);
		if (!this.isTamed()) {
			this.goalSelector.add(4, this.fleeGoal);
		}
	}

	static class CatFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final CatEntity cat;

		public CatFleeGoal(CatEntity catEntity, Class<T> class_, float f, double d, double e) {
			super(catEntity, class_, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
			this.cat = catEntity;
		}

		@Override
		public boolean canStart() {
			return !this.cat.isTamed() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.cat.isTamed() && super.shouldContinue();
		}
	}

	static class SleepWithOwnerGoal extends Goal {
		private final CatEntity cat;
		private PlayerEntity owner;
		private BlockPos bedPos;
		private int ticksOnBed;

		public SleepWithOwnerGoal(CatEntity catEntity) {
			this.cat = catEntity;
		}

		@Override
		public boolean canStart() {
			if (!this.cat.isTamed()) {
				return false;
			} else if (this.cat.isSitting()) {
				return false;
			} else {
				LivingEntity livingEntity = this.cat.getOwner();
				if (livingEntity instanceof PlayerEntity) {
					this.owner = (PlayerEntity)livingEntity;
					if (!livingEntity.isSleeping()) {
						return false;
					}

					if (this.cat.squaredDistanceTo(this.owner) > 100.0) {
						return false;
					}

					BlockPos blockPos = new BlockPos(this.owner);
					BlockState blockState = this.cat.world.getBlockState(blockPos);
					if (blockState.getBlock().matches(BlockTags.BEDS)) {
						Direction direction = blockState.get(BedBlock.FACING);
						this.bedPos = new BlockPos(blockPos.getX() - direction.getOffsetX(), blockPos.getY(), blockPos.getZ() - direction.getOffsetZ());
						return !this.method_16098();
					}
				}

				return false;
			}
		}

		private boolean method_16098() {
			for (CatEntity catEntity : this.cat.world.getNonSpectatingEntities(CatEntity.class, new Box(this.bedPos).expand(2.0))) {
				if (catEntity != this.cat && (catEntity.isSleepingWithOwner() || catEntity.isHeadDown())) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean shouldContinue() {
			return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.method_16098();
		}

		@Override
		public void start() {
			if (this.bedPos != null) {
				this.cat.getSitGoal().setEnabledWithOwner(false);
				this.cat.getNavigation().startMovingTo((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.1F);
			}
		}

		@Override
		public void stop() {
			this.cat.setSleepingWithOwner(false);
			float f = this.cat.world.getSkyAngle(1.0F);
			if (this.owner.getSleepTimer() >= 100 && (double)f > 0.77 && (double)f < 0.8 && (double)this.cat.world.getRandom().nextFloat() < 0.7) {
				this.dropMorningGifts();
			}

			this.ticksOnBed = 0;
			this.cat.setHeadDown(false);
			this.cat.getNavigation().stop();
		}

		private void dropMorningGifts() {
			Random random = this.cat.getRand();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			mutable.set(this.cat);
			this.cat
				.teleport(
					(double)(mutable.getX() + random.nextInt(11) - 5),
					(double)(mutable.getY() + random.nextInt(5) - 2),
					(double)(mutable.getZ() + random.nextInt(11) - 5),
					false
				);
			mutable.set(this.cat);
			LootTable lootTable = this.cat.world.getServer().getLootManager().getSupplier(LootTables.CAT_MORNING_GIFT_GAMEPLAY);
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.cat.world)
				.put(LootContextParameters.POSITION, mutable)
				.put(LootContextParameters.THIS_ENTITY, this.cat)
				.setRandom(random);

			for (ItemStack itemStack : lootTable.getDrops(builder.build(LootContextTypes.GIFT))) {
				this.cat
					.world
					.spawnEntity(
						new ItemEntity(
							this.cat.world,
							(double)((float)mutable.getX() - MathHelper.sin(this.cat.bodyYaw * (float) (Math.PI / 180.0))),
							(double)mutable.getY(),
							(double)((float)mutable.getZ() + MathHelper.cos(this.cat.bodyYaw * (float) (Math.PI / 180.0))),
							itemStack
						)
					);
			}
		}

		@Override
		public void tick() {
			if (this.owner != null && this.bedPos != null) {
				this.cat.getSitGoal().setEnabledWithOwner(false);
				this.cat.getNavigation().startMovingTo((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.1F);
				if (this.cat.squaredDistanceTo(this.owner) < 2.5) {
					this.ticksOnBed++;
					if (this.ticksOnBed > 16) {
						this.cat.setSleepingWithOwner(true);
						this.cat.setHeadDown(false);
					} else {
						this.cat.lookAtEntity(this.owner, 45.0F, 45.0F);
						this.cat.setHeadDown(true);
					}
				} else {
					this.cat.setSleepingWithOwner(false);
				}
			}
		}
	}

	static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
		@Nullable
		private PlayerEntity player;
		private final CatEntity cat;

		public TemptGoal(CatEntity catEntity, double d, Ingredient ingredient, boolean bl) {
			super(catEntity, d, ingredient, bl);
			this.cat = catEntity;
		}

		@Override
		public void tick() {
			super.tick();
			if (this.player == null && this.mob.getRand().nextInt(600) == 0) {
				this.player = this.closestPlayer;
			} else if (this.mob.getRand().nextInt(500) == 0) {
				this.player = null;
			}
		}

		@Override
		protected boolean canBeScared() {
			return this.player != null && this.player.equals(this.closestPlayer) ? false : super.canBeScared();
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.cat.isTamed();
		}
	}
}
