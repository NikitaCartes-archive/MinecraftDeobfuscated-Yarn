package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1374;
import net.minecraft.class_1376;
import net.minecraft.class_1387;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class LlamaEntity extends AbstractDonkeyEntity implements RangedAttacker {
	private static final TrackedData<Integer> ATTR_STRENGTH = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_6995 = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean field_6999;
	@Nullable
	private LlamaEntity field_7000;
	@Nullable
	private LlamaEntity field_6997;

	public LlamaEntity(World world) {
		super(EntityType.LLAMA, world);
	}

	private void setStrength(int i) {
		this.dataTracker.set(ATTR_STRENGTH, Math.max(1, Math.min(5, i)));
	}

	private void method_6796() {
		int i = this.random.nextFloat() < 0.04F ? 5 : 3;
		this.setStrength(1 + this.random.nextInt(i));
	}

	public int getStrength() {
		return this.dataTracker.get(ATTR_STRENGTH);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
		compoundTag.putInt("Strength", this.getStrength());
		if (!this.field_6962.getInvStack(1).isEmpty()) {
			compoundTag.put("DecorItem", this.field_6962.getInvStack(1).toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.setStrength(compoundTag.getInt("Strength"));
		super.readCustomDataFromTag(compoundTag);
		this.setVariant(compoundTag.getInt("Variant"));
		if (compoundTag.containsKey("DecorItem", 10)) {
			this.field_6962.setInvStack(1, ItemStack.fromTag(compoundTag.getCompound("DecorItem")));
		}

		this.method_6731();
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new class_1387(this, 1.2));
		this.goalSelector.add(2, new FormCaravanGoal(this, 2.1F));
		this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0F));
		this.goalSelector.add(3, new class_1374(this, 1.2));
		this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(5, new FollowParentGoal(this, 1.0));
		this.goalSelector.add(6, new class_1394(this, 0.7));
		this.goalSelector.add(7, new class_1361(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new class_1376(this));
		this.targetSelector.add(1, new LlamaEntity.class_1504(this));
		this.targetSelector.add(2, new LlamaEntity.class_1502(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ATTR_STRENGTH, 0);
		this.dataTracker.startTracking(field_6995, -1);
		this.dataTracker.startTracking(ATTR_VARIANT, 0);
	}

	public int getVariant() {
		return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 3);
	}

	public void setVariant(int i) {
		this.dataTracker.set(ATTR_VARIANT, i);
	}

	@Override
	protected int getInventorySize() {
		return this.hasChest() ? 2 + 3 * this.method_6702() : super.getInventorySize();
	}

	@Override
	public void method_5865(Entity entity) {
		if (this.hasPassenger(entity)) {
			float f = MathHelper.cos(this.field_6283 * (float) (Math.PI / 180.0));
			float g = MathHelper.sin(this.field_6283 * (float) (Math.PI / 180.0));
			float h = 0.3F;
			entity.setPosition(this.x + (double)(0.3F * g), this.y + this.getMountedHeightOffset() + entity.getHeightOffset(), this.z - (double)(0.3F * f));
		}
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)this.getHeight() * 0.67;
	}

	@Override
	public boolean method_5956() {
		return false;
	}

	@Override
	protected boolean method_6742(PlayerEntity playerEntity, ItemStack itemStack) {
		int i = 0;
		int j = 0;
		float f = 0.0F;
		boolean bl = false;
		Item item = itemStack.getItem();
		if (item == Items.field_8861) {
			i = 10;
			j = 3;
			f = 2.0F;
		} else if (item == Blocks.field_10359.getItem()) {
			i = 90;
			j = 6;
			f = 10.0F;
			if (this.isTame() && this.getBreedingAge() == 0 && this.method_6482()) {
				bl = true;
				this.method_6480(playerEntity);
			}
		}

		if (this.getHealth() < this.getHealthMaximum() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isChild() && i > 0) {
			this.world
				.addParticle(
					ParticleTypes.field_11211,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					0.0,
					0.0,
					0.0
				);
			if (!this.world.isClient) {
				this.method_5615(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.method_6755()) {
			bl = true;
			if (!this.world.isClient) {
				this.method_6745(j);
			}
		}

		if (bl && !this.isSilent()) {
			this.world
				.playSound(
					null, this.x, this.y, this.z, SoundEvents.field_14884, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}

		return bl;
	}

	@Override
	protected boolean method_6062() {
		return this.getHealth() <= 0.0F || this.isEating();
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.method_6796();
		int i;
		if (entityData instanceof LlamaEntity.class_1503) {
			i = ((LlamaEntity.class_1503)entityData).field_7001;
		} else {
			i = this.random.nextInt(4);
			entityData = new LlamaEntity.class_1503(i);
		}

		this.setVariant(i);
		return entityData;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_6807() {
		return this.method_6800() != null;
	}

	@Override
	protected SoundEvent method_6747() {
		return SoundEvents.field_14586;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14682;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15031;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15189;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14795, 0.15F, 1.0F);
	}

	@Override
	protected void method_6705() {
		this.playSound(SoundEvents.field_15097, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public void method_6757() {
		SoundEvent soundEvent = this.method_6747();
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	public int method_6702() {
		return this.getStrength();
	}

	@Override
	public boolean method_6735() {
		return true;
	}

	@Override
	public boolean method_6773(ItemStack itemStack) {
		Item item = itemStack.getItem();
		return ItemTags.field_15542.contains(item);
	}

	@Override
	public boolean method_6765() {
		return false;
	}

	@Override
	public void onInvChange(Inventory inventory) {
		DyeColor dyeColor = this.method_6800();
		super.onInvChange(inventory);
		DyeColor dyeColor2 = this.method_6800();
		if (this.age > 20 && dyeColor2 != null && dyeColor2 != dyeColor) {
			this.playSound(SoundEvents.field_14554, 0.5F, 1.0F);
		}
	}

	@Override
	protected void method_6731() {
		if (!this.world.isClient) {
			super.method_6731();
			this.method_6799(method_6794(this.field_6962.getInvStack(1)));
		}
	}

	private void method_6799(@Nullable DyeColor dyeColor) {
		this.dataTracker.set(field_6995, dyeColor == null ? -1 : dyeColor.getId());
	}

	@Nullable
	private static DyeColor method_6794(ItemStack itemStack) {
		Block block = Block.getBlockFromItem(itemStack.getItem());
		return block instanceof CarpetBlock ? ((CarpetBlock)block).getColor() : null;
	}

	@Nullable
	public DyeColor method_6800() {
		int i = this.dataTracker.get(field_6995);
		return i == -1 ? null : DyeColor.byId(i);
	}

	@Override
	public int method_6755() {
		return 30;
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		return animalEntity != this && animalEntity instanceof LlamaEntity && this.method_6734() && ((LlamaEntity)animalEntity).method_6734();
	}

	public LlamaEntity createChild(PassiveEntity passiveEntity) {
		LlamaEntity llamaEntity = new LlamaEntity(this.world);
		this.method_6743(passiveEntity, llamaEntity);
		LlamaEntity llamaEntity2 = (LlamaEntity)passiveEntity;
		int i = this.random.nextInt(Math.max(this.getStrength(), llamaEntity2.getStrength())) + 1;
		if (this.random.nextFloat() < 0.03F) {
			i++;
		}

		llamaEntity.setStrength(i);
		llamaEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : llamaEntity2.getVariant());
		return llamaEntity;
	}

	private void method_6792(LivingEntity livingEntity) {
		LlamaSpitEntity llamaSpitEntity = new LlamaSpitEntity(this.world, this);
		double d = livingEntity.x - this.x;
		double e = livingEntity.getBoundingBox().minY + (double)(livingEntity.getHeight() / 3.0F) - llamaSpitEntity.y;
		double f = livingEntity.z - this.z;
		float g = MathHelper.sqrt(d * d + f * f) * 0.2F;
		llamaSpitEntity.setVelocity(d, e + (double)g, f, 1.5F, 10.0F);
		this.world
			.playSound(
				null, this.x, this.y, this.z, SoundEvents.field_14789, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
			);
		this.world.spawnEntity(llamaSpitEntity);
		this.field_6999 = true;
	}

	private void method_6808(boolean bl) {
		this.field_6999 = bl;
	}

	@Override
	public void handleFallDamage(float f, float g) {
		int i = MathHelper.ceil((f * 0.5F - 3.0F) * g);
		if (i > 0) {
			if (f >= 6.0F) {
				this.damage(DamageSource.FALL, (float)i);
				if (this.hasPassengers()) {
					for (Entity entity : this.method_5736()) {
						entity.damage(DamageSource.FALL, (float)i);
					}
				}
			}

			BlockState blockState = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z));
			if (!blockState.isAir() && !this.isSilent()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.world
					.playSound(
						null,
						this.x,
						this.y,
						this.z,
						blockSoundGroup.getStepSound(),
						this.getSoundCategory(),
						blockSoundGroup.getVolume() * 0.5F,
						blockSoundGroup.getPitch() * 0.75F
					);
			}
		}
	}

	public void method_6797() {
		if (this.field_7000 != null) {
			this.field_7000.field_6997 = null;
		}

		this.field_7000 = null;
	}

	public void method_6791(LlamaEntity llamaEntity) {
		this.field_7000 = llamaEntity;
		this.field_7000.field_6997 = this;
	}

	public boolean method_6793() {
		return this.field_6997 != null;
	}

	public boolean isFollowing() {
		return this.field_7000 != null;
	}

	@Nullable
	public LlamaEntity getFollowing() {
		return this.field_7000;
	}

	@Override
	protected double method_6148() {
		return 2.0;
	}

	@Override
	protected void method_6746() {
		if (!this.isFollowing() && this.isChild()) {
			super.method_6746();
		}
	}

	@Override
	public boolean method_6762() {
		return false;
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		this.method_6792(livingEntity);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return false;
	}

	@Override
	public void setArmsRaised(boolean bl) {
	}

	static class class_1502 extends FollowTargetGoal<WolfEntity> {
		public class_1502(LlamaEntity llamaEntity) {
			super(llamaEntity, WolfEntity.class, 16, false, true, null);
		}

		@Override
		public boolean canStart() {
			if (super.canStart() && this.field_6644 != null && !this.field_6644.isTamed()) {
				return true;
			} else {
				this.entity.setTarget(null);
				return false;
			}
		}

		@Override
		protected double getFollowRange() {
			return super.getFollowRange() * 0.25;
		}
	}

	static class class_1503 implements EntityData {
		public final int field_7001;

		private class_1503(int i) {
			this.field_7001 = i;
		}
	}

	static class class_1504 extends class_1399 {
		public class_1504(LlamaEntity llamaEntity) {
			super(llamaEntity);
		}

		@Override
		public boolean shouldContinue() {
			if (this.entity instanceof LlamaEntity) {
				LlamaEntity llamaEntity = (LlamaEntity)this.entity;
				if (llamaEntity.field_6999) {
					llamaEntity.method_6808(false);
					return false;
				}
			}

			return super.shouldContinue();
		}
	}
}
