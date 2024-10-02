package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class HorseEntity extends AbstractHorseEntity implements VariantHolder<HorseColor> {
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(HorseEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final EntityDimensions BABY_BASE_DIMENSIONS = EntityType.HORSE
		.getDimensions()
		.withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, EntityType.HORSE.getHeight() + 0.125F, 0.0F))
		.scaled(0.5F);

	public HorseEntity(EntityType<? extends HorseEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initAttributes(Random random) {
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue((double)getChildHealthBonus(random::nextInt));
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(getChildMovementSpeedBonus(random::nextDouble));
		this.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).setBaseValue(getChildJumpStrengthBonus(random::nextDouble));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getHorseVariant());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setHorseVariant(nbt.getInt("Variant"));
	}

	private void setHorseVariant(int variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	private int getHorseVariant() {
		return this.dataTracker.get(VARIANT);
	}

	private void setHorseVariant(HorseColor color, HorseMarking marking) {
		this.setHorseVariant(color.getId() & 0xFF | marking.getId() << 8 & 0xFF00);
	}

	public HorseColor getVariant() {
		return HorseColor.byId(this.getHorseVariant() & 0xFF);
	}

	public void setVariant(HorseColor horseColor) {
		this.setHorseVariant(horseColor.getId() & 0xFF | this.getHorseVariant() & -256);
	}

	public HorseMarking getMarking() {
		return HorseMarking.byIndex((this.getHorseVariant() & 0xFF00) >> 8);
	}

	@Override
	protected void playWalkSound(BlockSoundGroup group) {
		super.playWalkSound(group);
		if (this.random.nextInt(10) == 0) {
			this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, group.getVolume() * 0.6F, group.getPitch());
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_HORSE_DEATH;
	}

	@Nullable
	@Override
	protected SoundEvent getEatSound() {
		return SoundEvents.ENTITY_HORSE_EAT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_HORSE_HURT;
	}

	@Override
	protected SoundEvent getAngrySound() {
		return SoundEvents.ENTITY_HORSE_ANGRY;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		boolean bl = !this.isBaby() && this.isTame() && player.shouldCancelInteraction();
		if (!this.hasPassengers() && !bl) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (!itemStack.isEmpty()) {
				if (this.isBreedingItem(itemStack)) {
					return this.interactHorse(player, itemStack);
				}

				if (!this.isTame()) {
					this.playAngrySound();
					return ActionResult.SUCCESS;
				}
			}

			return super.interactMob(player, hand);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return !(other instanceof DonkeyEntity) && !(other instanceof HorseEntity) ? false : this.canBreed() && ((AbstractHorseEntity)other).canBreed();
		}
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		if (entity instanceof DonkeyEntity) {
			MuleEntity muleEntity = EntityType.MULE.create(world, SpawnReason.BREEDING);
			if (muleEntity != null) {
				this.setChildAttributes(entity, muleEntity);
			}

			return muleEntity;
		} else {
			HorseEntity horseEntity = (HorseEntity)entity;
			HorseEntity horseEntity2 = EntityType.HORSE.create(world, SpawnReason.BREEDING);
			if (horseEntity2 != null) {
				int i = this.random.nextInt(9);
				HorseColor horseColor;
				if (i < 4) {
					horseColor = this.getVariant();
				} else if (i < 8) {
					horseColor = horseEntity.getVariant();
				} else {
					horseColor = Util.getRandom(HorseColor.values(), this.random);
				}

				int j = this.random.nextInt(5);
				HorseMarking horseMarking;
				if (j < 2) {
					horseMarking = this.getMarking();
				} else if (j < 4) {
					horseMarking = horseEntity.getMarking();
				} else {
					horseMarking = Util.getRandom(HorseMarking.values(), this.random);
				}

				horseEntity2.setHorseVariant(horseColor, horseMarking);
				this.setChildAttributes(entity, horseEntity2);
			}

			return horseEntity2;
		}
	}

	@Override
	public boolean canUseSlot(EquipmentSlot slot) {
		return true;
	}

	@Override
	protected void damageArmor(DamageSource source, float amount) {
		this.damageEquipment(source, amount, new EquipmentSlot[]{EquipmentSlot.BODY});
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		Random random = world.getRandom();
		HorseColor horseColor;
		if (entityData instanceof HorseEntity.HorseData) {
			horseColor = ((HorseEntity.HorseData)entityData).color;
		} else {
			horseColor = Util.getRandom(HorseColor.values(), random);
			entityData = new HorseEntity.HorseData(horseColor);
		}

		this.setHorseVariant(horseColor, Util.getRandom(HorseMarking.values(), random));
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
	}

	public static class HorseData extends PassiveEntity.PassiveData {
		public final HorseColor color;

		public HorseData(HorseColor color) {
			super(true);
			this.color = color;
		}
	}
}
