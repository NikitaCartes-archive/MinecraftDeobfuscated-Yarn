package net.minecraft.item.block;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
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

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		super.buildTooltip(itemStack, world, list, tooltipOptions);
		this.block.addInformation(itemStack, world, list, tooltipOptions);
	}
}
