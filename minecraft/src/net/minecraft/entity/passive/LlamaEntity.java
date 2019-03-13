package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
	private static final TrackedData<Integer> field_6998 = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_6995 = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_6996 = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private boolean field_6999;
	@Nullable
	private LlamaEntity field_7000;
	@Nullable
	private LlamaEntity field_6997;

	public LlamaEntity(EntityType<? extends LlamaEntity> entityType, World world) {
		super(entityType, world);
	}

	@Environment(EnvType.CLIENT)
	public boolean isTrader() {
		return false;
	}

	private void setStrength(int i) {
		this.field_6011.set(field_6998, Math.max(1, Math.min(5, i)));
	}

	private void method_6796() {
		int i = this.random.nextFloat() < 0.04F ? 5 : 3;
		this.setStrength(1 + this.random.nextInt(i));
	}

	public int getStrength() {
		return this.field_6011.get(field_6998);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
		compoundTag.putInt("Strength", this.getStrength());
		if (!this.decorationItem.method_5438(1).isEmpty()) {
			compoundTag.method_10566("DecorItem", this.decorationItem.method_5438(1).method_7953(new CompoundTag()));
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		this.setStrength(compoundTag.getInt("Strength"));
		super.method_5749(compoundTag);
		this.setVariant(compoundTag.getInt("Variant"));
		if (compoundTag.containsKey("DecorItem", 10)) {
			this.decorationItem.method_5447(1, ItemStack.method_7915(compoundTag.getCompound("DecorItem")));
		}

		this.method_6731();
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.field_6201.add(2, new FormCaravanGoal(this, 2.1F));
		this.field_6201.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0F));
		this.field_6201.add(3, new EscapeDangerGoal(this, 1.2));
		this.field_6201.add(4, new AnimalMateGoal(this, 1.0));
		this.field_6201.add(5, new FollowParentGoal(this, 1.0));
		this.field_6201.add(6, new class_1394(this, 0.7));
		this.field_6201.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.field_6201.add(8, new LookAroundGoal(this));
		this.field_6185.add(1, new LlamaEntity.class_1504(this));
		this.field_6185.add(2, new LlamaEntity.class_1502(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6998, 0);
		this.field_6011.startTracking(field_6995, -1);
		this.field_6011.startTracking(field_6996, 0);
	}

	public int getVariant() {
		return MathHelper.clamp(this.field_6011.get(field_6996), 0, 3);
	}

	public void setVariant(int i) {
		this.field_6011.set(field_6996, i);
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
			this.field_6002
				.method_8406(
					ParticleTypes.field_11211,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					0.0,
					0.0,
					0.0
				);
			if (!this.field_6002.isClient) {
				this.method_5615(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.method_6755()) {
			bl = true;
			if (!this.field_6002.isClient) {
				this.method_6745(j);
			}
		}

		if (bl && !this.isSilent()) {
			this.field_6002
				.method_8465(
					null, this.x, this.y, this.z, SoundEvents.field_14884, this.method_5634(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
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
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
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

	@Override
	protected SoundEvent method_6747() {
		return SoundEvents.field_14586;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14682;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15031;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15189;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(SoundEvents.field_14795, 0.15F, 1.0F);
	}

	@Override
	protected void method_6705() {
		this.method_5783(SoundEvents.field_15097, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	@Override
	public void method_6757() {
		SoundEvent soundEvent = this.method_6747();
		if (soundEvent != null) {
			this.method_5783(soundEvent, this.getSoundVolume(), this.getSoundPitch());
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
			this.method_5783(SoundEvents.field_14554, 0.5F, 1.0F);
		}
	}

	@Override
	protected void method_6731() {
		if (!this.field_6002.isClient) {
			super.method_6731();
			this.method_6799(method_6794(this.decorationItem.method_5438(1)));
		}
	}

	private void method_6799(@Nullable DyeColor dyeColor) {
		this.field_6011.set(field_6995, dyeColor == null ? -1 : dyeColor.getId());
	}

	@Nullable
	private static DyeColor method_6794(ItemStack itemStack) {
		Block block = Block.getBlockFromItem(itemStack.getItem());
		return block instanceof CarpetBlock ? ((CarpetBlock)block).getColor() : null;
	}

	@Nullable
	public DyeColor method_6800() {
		int i = this.field_6011.get(field_6995);
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

	public LlamaEntity method_6804(PassiveEntity passiveEntity) {
		LlamaEntity llamaEntity = this.createChild();
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

	protected LlamaEntity createChild() {
		return EntityType.LLAMA.method_5883(this.field_6002);
	}

	private void method_6792(LivingEntity livingEntity) {
		LlamaSpitEntity llamaSpitEntity = new LlamaSpitEntity(this.field_6002, this);
		double d = livingEntity.x - this.x;
		double e = livingEntity.method_5829().minY + (double)(livingEntity.getHeight() / 3.0F) - llamaSpitEntity.y;
		double f = livingEntity.z - this.z;
		float g = MathHelper.sqrt(d * d + f * f) * 0.2F;
		llamaSpitEntity.setVelocity(d, e + (double)g, f, 1.5F, 10.0F);
		this.field_6002
			.method_8465(
				null, this.x, this.y, this.z, SoundEvents.field_14789, this.method_5634(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
			);
		this.field_6002.spawnEntity(llamaSpitEntity);
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
					for (Entity entity : this.getPassengersDeep()) {
						entity.damage(DamageSource.FALL, (float)i);
					}
				}
			}

			BlockState blockState = this.field_6002.method_8320(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z));
			if (!blockState.isAir() && !this.isSilent()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.field_6002
					.method_8465(
						null, this.x, this.y, this.z, blockSoundGroup.method_10594(), this.method_5634(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F
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
			super(llamaEntity, WolfEntity.class, 16, false, true, livingEntity -> !((WolfEntity)livingEntity).isTamed());
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
