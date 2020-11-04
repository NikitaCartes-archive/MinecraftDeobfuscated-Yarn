package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public FurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.FURNACE, blockPos, blockState, RecipeType.SMELTING);
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.furnace");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new FurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}
}
