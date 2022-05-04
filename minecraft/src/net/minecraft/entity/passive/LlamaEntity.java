package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DyedCarpetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class LlamaEntity extends AbstractDonkeyEntity implements RangedAttackMob {
	private static final int MAX_STRENGTH = 5;
	private static final int VARIANTS = 4;
	private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.WHEAT, Blocks.HAY_BLOCK.asItem());
	private static final TrackedData<Integer> STRENGTH = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> CARPET_COLOR = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	boolean spit;
	@Nullable
	private LlamaEntity following;
	@Nullable
	private LlamaEntity follower;

	public LlamaEntity(EntityType<? extends LlamaEntity> entityType, World world) {
		super(entityType, world);
	}

	public boolean isTrader() {
		return false;
	}

	private void setStrength(int strength) {
		this.dataTracker.set(STRENGTH, Math.max(1, Math.min(5, strength)));
	}

	private void initializeStrength(AbstractRandom random) {
		int i = random.nextFloat() < 0.04F ? 5 : 3;
		this.setStrength(1 + random.nextInt(i));
	}

	public int getStrength() {
		return this.dataTracker.get(STRENGTH);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getVariant());
		nbt.putInt("Strength", this.getStrength());
		if (!this.items.getStack(1).isEmpty()) {
			nbt.put("DecorItem", this.items.getStack(1).writeNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setStrength(nbt.getInt("Strength"));
		super.readCustomDataFromNbt(nbt);
		this.setVariant(nbt.getInt("Variant"));
		if (nbt.contains("DecorItem", NbtElement.COMPOUND_TYPE)) {
			this.items.setStack(1, ItemStack.fromNbt(nbt.getCompound("DecorItem")));
		}

		this.updateSaddle();
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goalSelector.add(2, new FormCaravanGoal(this, 2.1F));
		this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0F));
		this.goalSelector.add(3, new EscapeDangerGoal(this, 1.2));
		this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(5, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.HAY_BLOCK), false));
		this.goalSelector.add(6, new FollowParentGoal(this, 1.0));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.7));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(9, new LookAroundGoal(this));
		this.targetSelector.add(1, new LlamaEntity.SpitRevengeGoal(this));
		this.targetSelector.add(2, new LlamaEntity.ChaseWolvesGoal(this));
	}

	public static DefaultAttributeContainer.Builder createLlamaAttributes() {
		return createAbstractDonkeyAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(STRENGTH, 0);
		this.dataTracker.startTracking(CARPET_COLOR, -1);
		this.dataTracker.startTracking(VARIANT, 0);
	}

	public int getVariant() {
		return MathHelper.clamp(this.dataTracker.get(VARIANT), 0, 3);
	}

	public void setVariant(int variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	@Override
	protected int getInventorySize() {
		return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			float f = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
			float g = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
			float h = 0.3F;
			passenger.setPosition(
				this.getX() + (double)(0.3F * g), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.getZ() - (double)(0.3F * f)
			);
		}
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.getHeight() * 0.6;
	}

	@Nullable
	@Override
	public LivingEntity getPrimaryPassenger() {
		return null;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return TAMING_INGREDIENT.test(stack);
	}

	@Override
	protected boolean receiveFood(PlayerEntity player, ItemStack item) {
		int i = 0;
		int j = 0;
		float f = 0.0F;
		boolean bl = false;
		if (item.isOf(Items.WHEAT)) {
			i = 10;
			j = 3;
			f = 2.0F;
		} else if (item.isOf(Blocks.HAY_BLOCK.asItem())) {
			i = 90;
			j = 6;
			f = 10.0F;
			if (this.isTame() && this.getBreedingAge() == 0 && this.canEat()) {
				bl = true;
				this.lovePlayer(player);
			}
		}

		if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isBaby() && i > 0) {
			this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.growUp(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
			bl = true;
			if (!this.world.isClient) {
				this.addTemper(j);
			}
		}

		if (bl && !this.isSilent()) {
			SoundEvent soundEvent = this.getEatSound();
			if (soundEvent != null) {
				this.world
					.playSound(
						null,
						this.getX(),
						this.getY(),
						this.getZ(),
						this.getEatSound(),
						this.getSoundCategory(),
						1.0F,
						1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
					);
			}
		}

		return bl;
	}

	@Override
	protected boolean isImmobile() {
		return this.isDead() || this.isEatingGrass();
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		AbstractRandom abstractRandom = world.getRandom();
		this.initializeStrength(abstractRandom);
		int i;
		if (entityData instanceof LlamaEntity.LlamaData) {
			i = ((LlamaEntity.LlamaData)entityData).variant;
		} else {
			i = abstractRandom.nextInt(4);
			entityData = new LlamaEntity.LlamaData(i);
		}

		this.setVariant(i);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	protected SoundEvent getAngrySound() {
		return SoundEvents.ENTITY_LLAMA_ANGRY;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_LLAMA_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_LLAMA_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_LLAMA_DEATH;
	}

	@Nullable
	@Override
	protected SoundEvent getEatSound() {
		return SoundEvents.ENTITY_LLAMA_EAT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15F, 1.0F);
	}

	@Override
	protected void playAddChestSound() {
		this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public void playAngrySound() {
		SoundEvent soundEvent = this.getAngrySound();
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public int getInventoryColumns() {
		return this.getStrength();
	}

	@Override
	public boolean hasArmorSlot() {
		return true;
	}

	@Override
	public boolean hasArmorInSlot() {
		return !this.items.getStack(1).isEmpty();
	}

	@Override
	public boolean isHorseArmor(ItemStack item) {
		return item.isIn(ItemTags.WOOL_CARPETS);
	}

	@Override
	public boolean canBeSaddled() {
		return false;
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		DyeColor dyeColor = this.getCarpetColor();
		super.onInventoryChanged(sender);
		DyeColor dyeColor2 = this.getCarpetColor();
		if (this.age > 20 && dyeColor2 != null && dyeColor2 != dyeColor) {
			this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5F, 1.0F);
		}
	}

	@Override
	protected void updateSaddle() {
		if (!this.world.isClient) {
			super.updateSaddle();
			this.setCarpetColor(getColorFromCarpet(this.items.getStack(1)));
		}
	}

	private void setCarpetColor(@Nullable DyeColor color) {
		this.dataTracker.set(CARPET_COLOR, color == null ? -1 : color.getId());
	}

	@Nullable
	private static DyeColor getColorFromCarpet(ItemStack color) {
		Block block = Block.getBlockFromItem(color.getItem());
		return block instanceof DyedCarpetBlock ? ((DyedCarpetBlock)block).getDyeColor() : null;
	}

	@Nullable
	public DyeColor getCarpetColor() {
		int i = this.dataTracker.get(CARPET_COLOR);
		return i == -1 ? null : DyeColor.byId(i);
	}

	@Override
	public int getMaxTemper() {
		return 30;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return other != this && other instanceof LlamaEntity && this.canBreed() && ((LlamaEntity)other).canBreed();
	}

	public LlamaEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		LlamaEntity llamaEntity = this.createChild();
		this.setChildAttributes(passiveEntity, llamaEntity);
		LlamaEntity llamaEntity2 = (LlamaEntity)passiveEntity;
		int i = this.random.nextInt(Math.max(this.getStrength(), llamaEntity2.getStrength())) + 1;
		if (this.random.nextFloat() < 0.03F) {
			i++;
		}

		llamaEntity.setStrength(i);
		llamaEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : llamaEntity2.getVariant());
		return llamaEntity;
	}

	protected LlamaEntity createChild() {
		return EntityType.LLAMA.create(this.world);
	}

	private void spitAt(LivingEntity target) {
		LlamaSpitEntity llamaSpitEntity = new LlamaSpitEntity(this.world, this);
		double d = target.getX() - this.getX();
		double e = target.getBodyY(0.3333333333333333) - llamaSpitEntity.getY();
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f) * 0.2F;
		llamaSpitEntity.setVelocity(d, e + g, f, 1.5F, 10.0F);
		if (!this.isSilent()) {
			this.world
				.playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					SoundEvents.ENTITY_LLAMA_SPIT,
					this.getSoundCategory(),
					1.0F,
					1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}

		this.world.spawnEntity(llamaSpitEntity);
		this.spit = true;
	}

	void setSpit(boolean spit) {
		this.spit = spit;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		int i = this.computeFallDamage(fallDistance, damageMultiplier);
		if (i <= 0) {
			return false;
		} else {
			if (fallDistance >= 6.0F) {
				this.damage(damageSource, (float)i);
				if (this.hasPassengers()) {
					for (Entity entity : this.getPassengersDeep()) {
						entity.damage(damageSource, (float)i);
					}
				}
			}

			this.playBlockFallSound();
			return true;
		}
	}

	public void stopFollowing() {
		if (this.following != null) {
			this.following.follower = null;
		}

		this.following = null;
	}

	public void follow(LlamaEntity llama) {
		this.following = llama;
		this.following.follower = this;
	}

	public boolean hasFollower() {
		return this.follower != null;
	}

	public boolean isFollowing() {
		return this.following != null;
	}

	@Nullable
	public LlamaEntity getFollowing() {
		return this.following;
	}

	@Override
	protected double getFollowLeashSpeed() {
		return 2.0;
	}

	@Override
	protected void walkToParent() {
		if (!this.isFollowing() && this.isBaby()) {
			super.walkToParent();
		}
	}

	@Override
	public boolean eatsGrass() {
		return false;
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		this.spitAt(target);
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, 0.75 * (double)this.getStandingEyeHeight(), (double)this.getWidth() * 0.5);
	}

	static class ChaseWolvesGoal extends ActiveTargetGoal<WolfEntity> {
		public ChaseWolvesGoal(LlamaEntity llama) {
			super(llama, WolfEntity.class, 16, false, true, wolf -> !((WolfEntity)wolf).isTamed());
		}

		@Override
		protected double getFollowRange() {
			return super.getFollowRange() * 0.25;
		}
	}

	static class LlamaData extends PassiveEntity.PassiveData {
		public final int variant;

		LlamaData(int variant) {
			super(true);
			this.variant = variant;
		}
	}

	static class SpitRevengeGoal extends RevengeGoal {
		public SpitRevengeGoal(LlamaEntity llama) {
			super(llama);
		}

		@Override
		public boolean shouldContinue() {
			if (this.mob instanceof LlamaEntity llamaEntity && llamaEntity.spit) {
				llamaEntity.setSpit(false);
				return false;
			}

			return super.shouldContinue();
		}
	}
}
