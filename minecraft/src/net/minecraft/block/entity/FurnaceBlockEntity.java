package net.minecraft.block.entity;

import net.minecraft.container.Container;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.smelting.SmeltingRecipe;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public FurnaceBlockEntity() {
		super(BlockEntityType.FURNACE);
	}

	@Override
	protected TextComponent getDefaultName() {
		return new TranslatableTextComponent("container.furnace");
	}

	@Override
	protected int getCookTime() {
		SmeltingRecipe smeltingRecipe = (SmeltingRecipe)this.world.getRecipeManager().get(this, this.world);
		return smeltingRecipe != null ? smeltingRecipe.getCookTime() : super.getCookTime();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this ? false : super.canPlayerUseInv(playerEntity);
	}

	@Override
	public String getContainerId() {
		return "minecraft:furnace";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new FurnaceContainer(playerInventory, this);
	}
}
