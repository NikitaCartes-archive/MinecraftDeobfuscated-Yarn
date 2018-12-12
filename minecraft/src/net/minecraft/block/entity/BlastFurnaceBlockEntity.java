package net.minecraft.block.entity;

import net.minecraft.container.BlastFurnaceContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.smelting.BlastingRecipe;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class BlastFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public BlastFurnaceBlockEntity() {
		super(BlockEntityType.BLAST_FURNACE);
	}

	@Override
	protected TextComponent getDefaultName() {
		return new TranslatableTextComponent("container.blast_furnace");
	}

	@Override
	protected int getCookTime() {
		BlastingRecipe blastingRecipe = (BlastingRecipe)this.world.getRecipeManager().get(this, this.world);
		return blastingRecipe != null ? blastingRecipe.getCookTime() : super.getCookTime();
	}

	@Override
	protected int getItemBurnTime(ItemStack itemStack) {
		return super.getItemBurnTime(itemStack) / 2;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this ? false : super.canPlayerUseInv(playerEntity);
	}

	@Override
	public String getContainerId() {
		return "minecraft:blast_furnace";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new BlastFurnaceContainer(playerInventory, this);
	}
}
