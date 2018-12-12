package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.SmokerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.smelting.SmokingRecipe;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity {
	public SmokerBlockEntity() {
		super(BlockEntityType.SMOKER);
	}

	@Override
	protected TextComponent getDefaultName() {
		return new TranslatableTextComponent("container.smoker");
	}

	@Override
	protected int getCookTime() {
		SmokingRecipe smokingRecipe = (SmokingRecipe)this.world.getRecipeManager().get(this, this.world);
		return smokingRecipe != null ? smokingRecipe.getCookTime() : super.getCookTime();
	}

	@Override
	protected int getItemBurnTime(ItemStack itemStack) {
		return super.getItemBurnTime(itemStack) / 2;
	}

	@Override
	public String getContainerId() {
		return "minecraft:smoker";
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this ? false : super.canPlayerUseInv(playerEntity);
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new SmokerContainer(playerInventory, this);
	}
}
