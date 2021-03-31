package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class AirBlockItem extends Item {
	private final Block block;

	public AirBlockItem(Block block, Item.Settings settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public String getTranslationKey() {
		return this.block.getTranslationKey();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		this.block.appendTooltip(stack, world, tooltip, context);
	}
}
