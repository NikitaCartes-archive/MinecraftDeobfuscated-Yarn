package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class HorseEntity extends HorseBaseEntity {
	private static final UUID HORSE_ARMOR_BONUS_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(HorseEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public HorseEntity(EntityType<? extends HorseEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initAttributes() {
		this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)this.getChildHealthBonus());
		this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.getChildMovementSpeedBonus());
		this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("Variant", this.getVariant());
		if (!this.items.getStack(1).isEmpty()) {
			tag.put("ArmorItem", this.items.getStack(1).toTag(new CompoundTag()));
		}
	}

	public ItemStack getArmorType() {
		return this.getEquippedStack(EquipmentSlot.CHEST);
	}

	private void equipArmor(ItemStack stack) {
		this.equipStack(EquipmentSlot.CHEST, stack);
		this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setVariant(tag.getInt("Variant"));
		if (tag.contains("ArmorItem", 10)) {
			ItemStack itemStack = ItemStack.fromTag(tag.getCompound("ArmorItem"));
			if (!itemStack.isEmpty() && this.canEquip(itemStack)) {
				this.items.setStack(1, itemStack);
			}
		}

		this.updateSaddle();
	}

	private void setVariant(int variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	private int getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	private void setVariant(HorseColor color, HorseMarking marking) {
		this.setVariant(color.getIndex() & 0xFF | marking.getIndex() << 8 & 0xFF00);
	}

	public HorseColor getColor() {
		return HorseColor.byIndex(this.getVariant() & 0xFF);
	}

	public HorseMarking getMarking() {
		return HorseMarking.byIndex((this.getVariant() & 0xFF00) >> 8);
	}

	@Override
	protected void updateSaddle() {
		super.updateSaddle();
		this.setArmorTypeFromStack(this.items.getStack(1));
		this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
	}

	private void setArmorTypeFromStack(ItemStack stack) {
		this.equipArmor(stack);
		if (!this.world.isClient) {
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(HORSE_ARMOR_BONUS_UUID);
			if (this.canEquip(stack)) {
				int i = ((HorseArmorItem)stack.getItem()).getBonus();
				if (i != 0) {
					this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
						.addTemporaryModifier(new EntityAttributeModifier(HORSE_ARMOR_BONUS_UUID, "Horse armor bonus", (double)i, EntityAttributeModifier.Operation.ADDITION));
				}
			}
		}
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		ItemStack itemStack = this.getArmorType();
		super.onInventoryChanged(sender);
		ItemStack itemStack2 = this.getArmorType();
		if (this.age > 20 && this.canEquip(itemStack2) && itemStack != itemStack2) {
			this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
		}
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
		super.getAmbientSound();
		return SoundEvents.ENTITY_HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.ENTITY_HORSE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		super.getHurtSound(source);
		return SoundEvents.ENTITY_HORSE_HURT;
	}

	@Override
	protected SoundEvent getAngrySound() {
		super.getAngrySound();
		return SoundEvents.ENTITY_HORSE_ANGRY;
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		boolean bl = !itemStack.isEmpty();
		if (bl && itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(player, hand);
		} else {
			if (!this.isBaby()) {
				if (this.isTame() && player.shouldCancelInteraction()) {
					this.openInventory(player);
					return true;
				}

				if (this.hasPassengers()) {
					return super.interactMob(player, hand);
				}
			}

			if (bl) {
				if (this.receiveFood(player, itemStack)) {
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return true;
				}

				if (itemStack.useOnEntity(player, this, hand)) {
					return true;
				}

				if (!this.isTame()) {
					this.playAngrySound();
					return true;
				}

				boolean bl2 = !this.isBaby() && !this.isSaddled() && itemStack.getItem() == Items.SADDLE;
				if (this.canEquip(itemStack) || bl2) {
					this.openInventory(player);
					return true;
				}
			}

			if (this.isBaby()) {
				return super.interactMob(player, hand);
			} else {
				this.putPlayerOnBack(player);
				return true;
			}
		}
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (other == this) {
			return false;
		} else {
			return !(other instanceof DonkeyEntity) && !(other instanceof HorseEntity) ? false : this.canBreed() && ((HorseBaseEntity)other).canBreed();
		}
	}

	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		HorseBaseEntity horseBaseEntity;
		if (mate instanceof DonkeyEntity) {
			horseBaseEntity = EntityType.MULE.create(this.world);
		} else {
			HorseEntity horseEntity = (HorseEntity)mate;
			horseBaseEntity = EntityType.HORSE.create(this.world);
			int i = this.random.nextInt(9);
			HorseColor horseColor;
			if (i < 4) {
				horseColor = this.getColor();
			} else if (i < 8) {
				horseColor = horseEntity.getColor();
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

			((HorseEntity)horseBaseEntity).setVariant(horseColor, horseMarking);
		}

		this.setChildAttributes(mate, horseBaseEntity);
		return horseBaseEntity;
	}

	@Override
	public boolean canEquip() {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack item) {
		return item.getItem() instanceof HorseArmorItem;
	}

	@Nullable
	@Override
	public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
		HorseColor horseColor;
		if (entityData instanceof HorseEntity.HorseData) {
			horseColor = ((HorseEntity.HorseData)entityData).variant;
		} else {
			horseColor = Util.getRandom(HorseColor.values(), this.random);
			entityData = new HorseEntity.HorseData(horseColor);
		}

		this.setVariant(horseColor, Util.getRandom(HorseMarking.values(), this.random));
		return super.initialize(world, difficulty, spawnType, entityData, entityTag);
	}

	public static class HorseData extends PassiveEntity.PassiveData {
		public final HorseColor variant;

		public HorseData(HorseColor horseColor) {
			this.variant = horseColor;
		}
	}
}
