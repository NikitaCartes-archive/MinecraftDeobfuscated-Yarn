package net.minecraft.entity.ai.goal;

import net.minecraft.class_1358;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class VillagerBreedingGoal extends class_1358 {
	private int delay;
	private final VillagerEntity villager;

	public VillagerBreedingGoal(VillagerEntity villagerEntity) {
		super(villagerEntity, VillagerEntity.class, 3.0F, 0.02F);
		this.villager = villagerEntity;
	}

	@Override
	public void start() {
		super.start();
		if (this.villager.wantsToStartBreeding() && this.target instanceof VillagerEntity && ((VillagerEntity)this.target).canBreed()) {
			this.delay = 10;
		} else {
			this.delay = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.delay > 0) {
			this.delay--;
			if (this.delay == 0) {
				BasicInventory basicInventory = this.villager.getInventory();

				for (int i = 0; i < basicInventory.getInvSize(); i++) {
					ItemStack itemStack = basicInventory.getInvStack(i);
					ItemStack itemStack2 = ItemStack.EMPTY;
					if (!itemStack.isEmpty()) {
						Item item = itemStack.getItem();
						if ((item == Items.field_8229 || item == Items.field_8567 || item == Items.field_8179 || item == Items.field_8186) && itemStack.getAmount() > 3) {
							int j = itemStack.getAmount() / 2;
							itemStack.subtractAmount(j);
							itemStack2 = new ItemStack(item, j);
						} else if (item == Items.field_8861 && itemStack.getAmount() > 5) {
							int j = itemStack.getAmount() / 2 / 3 * 3;
							int k = j / 3;
							itemStack.subtractAmount(j);
							itemStack2 = new ItemStack(Items.field_8229, k);
						}

						if (itemStack.isEmpty()) {
							basicInventory.setInvStack(i, ItemStack.EMPTY);
						}
					}

					if (!itemStack2.isEmpty()) {
						double d = this.villager.y - 0.3F + (double)this.villager.getEyeHeight();
						ItemEntity itemEntity = new ItemEntity(this.villager.world, this.villager.x, d, this.villager.z, itemStack2);
						float f = 0.3F;
						float g = this.villager.headYaw;
						float h = this.villager.pitch;
						itemEntity.velocityX = (double)(-MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0)) * 0.3F);
						itemEntity.velocityZ = (double)(MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0)) * 0.3F);
						itemEntity.velocityY = (double)(-MathHelper.sin(h * (float) (Math.PI / 180.0)) * 0.3F + 0.1F);
						itemEntity.setToDefaultPickupDelay();
						this.villager.world.spawnEntity(itemEntity);
						break;
					}
				}
			}
		}
	}
}
