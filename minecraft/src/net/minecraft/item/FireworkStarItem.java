package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class FireworkStarItem extends Item {
	public FireworkStarItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		FireworkExplosionComponent fireworkExplosionComponent = stack.get(DataComponentTypes.FIREWORK_EXPLOSION);
		if (fireworkExplosionComponent != null) {
			fireworkExplosionComponent.appendTooltip(tooltip::add, context);
		}
	}
}
