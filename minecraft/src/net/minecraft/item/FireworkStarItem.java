package net.minecraft.item;

import java.util.List;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.text.Text;

public class FireworkStarItem extends Item {
	public FireworkStarItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		FireworkExplosionComponent fireworkExplosionComponent = stack.get(DataComponentTypes.FIREWORK_EXPLOSION);
		if (fireworkExplosionComponent != null) {
			fireworkExplosionComponent.appendTooltip(tooltip::add, type);
		}
	}
}
