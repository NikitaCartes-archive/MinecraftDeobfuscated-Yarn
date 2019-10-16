package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class HorseEntity extends HorseBaseEntity {
	private static final UUID HORSE_ARMOR_BONUS_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(HorseEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final String[] HORSE_TEX = new String[]{
		"textures/entity/horse/horse_white.png",
		"textures/entity/horse/horse_creamy.png",
		"textures/entity/horse/horse_chestnut.png",
		"textures/entity/horse/horse_brown.png",
		"textures/entity/horse/horse_black.png",
		"textures/entity/horse/horse_gray.png",
		"textures/entity/horse/horse_darkbrown.png"
	};
	private static final String[] HORSE_TEX_ID = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
	private static final String[] HORSE_MARKING_TEX = new String[]{
		null,
		"textures/entity/horse/horse_markings_white.png",
		"textures/entity/horse/horse_markings_whitefield.png",
		"textures/entity/horse/horse_markings_whitedots.png",
		"textures/entity/horse/horse_markings_blackdots.png"
	};
	private static final String[] HORSE_MARKING_TEX_ID = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
	@Nullable
	private String textureLocation;
	private final String[] textureLayers = new String[2];

	public HorseEntity(EntityType<? extends HorseEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
		if (!this.items.getInvStack(1).isEmpty()) {
			compoundTag.put("ArmorItem", this.items.getInvStack(1).toTag(new CompoundTag()));
		}
	}

	public ItemStack getArmorType() {
		return this.getEquippedStack(EquipmentSlot.CHEST);
	}

	private void equipArmor(ItemStack itemStack) {
		this.equipStack(EquipmentSlot.CHEST, itemStack);
		this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setVariant(compoundTag.getInt("Variant"));
		if (compoundTag.contains("ArmorItem", 10)) {
			ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("ArmorItem"));
			if (!itemStack.isEmpty() && this.canEquip(itemStack)) {
				this.items.setInvStack(1, itemStack);
			}
		}

		this.updateSaddle();
	}

	public void setVariant(int i) {
		this.dataTracker.set(VARIANT, i);
		this.clearTextureInfo();
	}

	public int getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	private void clearTextureInfo() {
		this.textureLocation = null;
	}

	@Environment(EnvType.CLIENT)
	private void initTextureInfo() {
		int i = this.getVariant();
		int j = (i & 0xFF) % 7;
		int k = ((i & 0xFF00) >> 8) % 5;
		this.textureLayers[0] = HORSE_TEX[j];
		this.textureLayers[1] = HORSE_MARKING_TEX[k];
		this.textureLocation = "horse/" + HORSE_TEX_ID[j] + HORSE_MARKING_TEX_ID[k];
	}

	@Environment(EnvType.CLIENT)
	public String getTextureLocation() {
		if (this.textureLocation == null) {
			this.initTextureInfo();
		}

		return this.textureLocation;
	}

	@Environment(EnvType.CLIENT)
	public String[] getTextureLayers() {
		if (this.textureLocation == null) {
			this.initTextureInfo();
		}

		return this.textureLayers;
	}

	@Override
	protected void updateSaddle() {
		super.updateSaddle();
		this.setArmorTypeFromStack(this.items.getInvStack(1));
		this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
	}

	private void setArmorTypeFromStack(ItemStack itemStack) {
		this.equipArmor(itemStack);
		if (!this.world.isClient) {
			this.getAttributeInstance(EntityAttributes.ARMOR).removeModifier(HORSE_ARMOR_BONUS_UUID);
			if (this.canEquip(itemStack)) {
				int i = ((HorseArmorItem)itemStack.getItem()).getBonus();
				if (i != 0) {
					this.getAttributeInstance(EntityAttributes.ARMOR)
						.addModifier(
							new EntityAttributeModifier(HORSE_ARMOR_BONUS_UUID, "Horse armor bonus", (double)i, EntityAttributeModifier.Operation.ADDITION).setSerialize(false)
						);
				}
			}
		}
	}

	@Override
	public void onInvChange(Inventory inventory) {
		ItemStack itemStack = this.getArmorType();
		super.onInvChange(inventory);
		ItemStack itemStack2 = this.getArmorType();
		if (this.age > 20 && this.canEquip(itemStack2) && itemStack != itemStack2) {
			this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
		}
	}

	@Override
	protected void playWalkSound(BlockSoundGroup blockSoundGroup) {
		super.playWalkSound(blockSoundGroup);
		if (this.random.nextInt(10) == 0) {
			this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, blockSoundGroup.getVolume() * 0.6F, blockSoundGroup.getPitch());
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue((double)this.getChildHealthBonus());
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(this.getChildMovementSpeedBonus());
		this.getAttributeInstance(JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && this.dataTracker.isDirty()) {
			this.dataTracker.clearDirty();
			this.clearTextureInfo();
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
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return SoundEvents.ENTITY_HORSE_HURT;
	}

	@Override
	protected SoundEvent getAngrySound() {
		super.getAngrySound();
		return SoundEvents.ENTITY_HORSE_ANGRY;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		boolean bl = !itemStack.isEmpty();
		if (bl && itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(playerEntity, hand);
		} else {
			if (!this.isBaby()) {
				if (this.isTame() && playerEntity.shouldCancelInteraction()) {
					this.openInventory(playerEntity);
					return true;
				}

				if (this.hasPassengers()) {
					return super.interactMob(playerEntity, hand);
				}
			}

			if (bl) {
				if (this.receiveFood(playerEntity, itemStack)) {
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					return true;
				}

				if (itemStack.useOnEntity(playerEntity, this, hand)) {
					return true;
				}

				if (!this.isTame()) {
					this.playAngrySound();
					return true;
				}

				boolean bl2 = !this.isBaby() && !this.isSaddled() && itemStack.getItem() == Items.SADDLE;
				if (this.canEquip(itemStack) || bl2) {
					this.openInventory(playerEntity);
					return true;
				}
			}

			if (this.isBaby()) {
				return super.interactMob(playerEntity, hand);
			} else {
				this.putPlayerOnBack(playerEntity);
				return true;
			}
		}
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		if (animalEntity == this) {
			return false;
		} else {
			return !(animalEntity instanceof DonkeyEntity) && !(animalEntity instanceof HorseEntity)
				? false
				: this.canBreed() && ((HorseBaseEntity)animalEntity).canBreed();
		}
	}

	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		HorseBaseEntity horseBaseEntity;
		if (passiveEntity instanceof DonkeyEntity) {
			horseBaseEntity = EntityType.MULE.create(this.world);
		} else {
			HorseEntity horseEntity = (HorseEntity)passiveEntity;
			horseBaseEntity = EntityType.HORSE.create(this.world);
			int i = this.random.nextInt(9);
			int j;
			if (i < 4) {
				j = this.getVariant() & 0xFF;
			} else if (i < 8) {
				j = horseEntity.getVariant() & 0xFF;
			} else {
				j = this.random.nextInt(7);
			}

			int k = this.random.nextInt(5);
			if (k < 2) {
				j |= this.getVariant() & 0xFF00;
			} else if (k < 4) {
				j |= horseEntity.getVariant() & 0xFF00;
			} else {
				j |= this.random.nextInt(5) << 8 & 0xFF00;
			}

			((HorseEntity)horseBaseEntity).setVariant(j);
		}

		this.setChildAttributes(passiveEntity, horseBaseEntity);
		return horseBaseEntity;
	}

	@Override
	public boolean canEquip() {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack itemStack) {
		return itemStack.getItem() instanceof HorseArmorItem;
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		int i;
		if (entityData instanceof HorseEntity.class_1499) {
			i = ((HorseEntity.class_1499)entityData).field_6994;
		} else {
			i = this.random.nextInt(7);
			entityData = new HorseEntity.class_1499(i);
		}

		this.setVariant(i | this.random.nextInt(5) << 8);
		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	public static class class_1499 extends PassiveEntity.class_4697 {
		public final int field_6994;

		public class_1499(int i) {
			this.field_6994 = i;
		}
	}
}
