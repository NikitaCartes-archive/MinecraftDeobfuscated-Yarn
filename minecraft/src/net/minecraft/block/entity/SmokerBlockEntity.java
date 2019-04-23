package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.recipe.RecipeType;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity {
	public SmokerBlockEntity() {
		super(BlockEntityType.field_16414, RecipeType.SMOKING);
	}

	@Override
	protected Component getContainerName() {
		return new TranslatableComponent("container.smoker");
	}

	@Override
	protected int getFuelTime(ItemStack itemStack) {
		return super.getFuelTime(itemStack) / 2;
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return new SmokerContainer(i, playerInventory, this, this.propertyDelegate);
	}
}
