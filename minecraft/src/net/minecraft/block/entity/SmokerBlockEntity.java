package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity {
	public SmokerBlockEntity() {
		super(BlockEntityType.SMOKER, RecipeType.SMOKING);
	}

	@Override
	protected TextComponent method_17823() {
		return new TranslatableTextComponent("container.smoker");
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
