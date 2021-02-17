package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CommandItemSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class AbstractDonkeyEntity extends HorseBaseEntity {
	private static final TrackedData<Boolean> CHEST = DataTracker.registerData(AbstractDonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	protected AbstractDonkeyEntity(EntityType<? extends AbstractDonkeyEntity> entityType, World world) {
		super(entityType, world);
		this.playExtraHorseSounds = false;
	}

	@Override
	protected void initAttributes() {
		this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)this.getChildHealthBonus());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CHEST, false);
	}

	public static DefaultAttributeContainer.Builder createAbstractDonkeyAttributes() {
		return createBaseHorseAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.175F).add(EntityAttributes.HORSE_JUMP_STRENGTH, 0.5);
	}

	public boolean hasChest() {
		return this.dataTracker.get(CHEST);
	}

	public void setHasChest(boolean hasChest) {
		this.dataTracker.set(CHEST, hasChest);
	}

	@Override
	protected int getInventorySize() {
		return this.hasChest() ? 17 : super.getInventorySize();
	}

	@Override
	public double getMountedHeightOffset() {
		return super.getMountedHeightOffset() - 0.25;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.hasChest()) {
			if (!this.world.isClient) {
				this.dropItem(Blocks.CHEST);
			}

			this.setHasChest(false);
		}
	}

	@Override
	public void writeCustomDataToNbt(CompoundTag tag) {
		super.writeCustomDataToNbt(tag);
		tag.putBoolean("ChestedHorse", this.hasChest());
		if (this.hasChest()) {
			ListTag listTag = new ListTag();

			for (int i = 2; i < this.items.size(); i++) {
				ItemStack itemStack = this.items.getStack(i);
				if (!itemStack.isEmpty()) {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.putByte("Slot", (byte)i);
					itemStack.writeNbt(compoundTag);
					listTag.add(compoundTag);
				}
			}

			tag.put("Items", listTag);
		}
	}

	@Override
	public void readCustomDataFromNbt(CompoundTag tag) {
		super.readCustomDataFromNbt(tag);
		this.setHasChest(tag.getBoolean("ChestedHorse"));
		this.onChestedStatusChanged();
		if (this.hasChest()) {
			ListTag listTag = tag.getList("Items", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompound(i);
				int j = compoundTag.getByte("Slot") & 255;
				if (j >= 2 && j < this.items.size()) {
					this.items.setStack(j, ItemStack.fromNbt(compoundTag));
				}
			}
		}

		this.updateSaddle();
	}

	@Override
	public CommandItemSlot getCommandItemSlot(int mappedIndex) {
		return mappedIndex == 499 ? new CommandItemSlot() {
			@Override
			public ItemStack get() {
				return AbstractDonkeyEntity.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
			}

			@Override
			public boolean set(ItemStack stack) {
				if (stack.isEmpty()) {
					if (AbstractDonkeyEntity.this.hasChest()) {
						AbstractDonkeyEntity.this.setHasChest(false);
						AbstractDonkeyEntity.this.onChestedStatusChanged();
					}

					return true;
				} else if (stack.isOf(Items.CHEST)) {
					if (!AbstractDonkeyEntity.this.hasChest()) {
						AbstractDonkeyEntity.this.setHasChest(true);
						AbstractDonkeyEntity.this.onChestedStatusChanged();
					}

					return true;
				} else {
					return false;
				}
			}
		} : super.getCommandItemSlot(mappedIndex);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!this.isBaby()) {
			if (this.isTame() && player.shouldCancelInteraction()) {
				this.openInventory(player);
				return ActionResult.success(this.world.isClient);
			}

			if (this.hasPassengers()) {
				return super.interactMob(player, hand);
			}
		}

		if (!itemStack.isEmpty()) {
			if (this.isBreedingItem(itemStack)) {
				return this.interactHorse(player, itemStack);
			}

			if (!this.isTame()) {
				this.playAngrySound();
				return ActionResult.success(this.world.isClient);
			}

			if (!this.hasChest() && itemStack.isOf(Blocks.CHEST.asItem())) {
				this.setHasChest(true);
				this.playAddChestSound();
				if (!player.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}

				this.onChestedStatusChanged();
				return ActionResult.success(this.world.isClient);
			}

			if (!this.isBaby() && !this.isSaddled() && itemStack.isOf(Items.SADDLE)) {
				this.openInventory(player);
				return ActionResult.success(this.world.isClient);
			}
		}

		if (this.isBaby()) {
			return super.interactMob(player, hand);
		} else {
			this.putPlayerOnBack(player);
			return ActionResult.success(this.world.isClient);
		}
	}

	protected void playAddChestSound() {
		this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}

	public int getInventoryColumns() {
		return 5;
	}
}
