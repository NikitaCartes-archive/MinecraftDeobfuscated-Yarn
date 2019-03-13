package net.minecraft.item.block;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.world.World;

public class AirBlockItem extends Item {
	private final Block field_7882;

	public AirBlockItem(Block block, Item.Settings settings) {
		super(settings);
		this.field_7882 = block;
	}

	@Override
	public String getTranslationKey() {
		return this.field_7882.getTranslationKey();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		super.method_7851(itemStack, world, list, tooltipContext);
		this.field_7882.buildTooltip(itemStack, world, list, tooltipContext);
	}
}
