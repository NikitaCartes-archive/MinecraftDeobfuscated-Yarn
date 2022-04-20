package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class FireworkStarItem extends Item {
	public FireworkStarItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		NbtCompound nbtCompound = stack.getSubNbt("Explosion");
		if (nbtCompound != null) {
			appendFireworkTooltip(nbtCompound, tooltip);
		}
	}

	public static void appendFireworkTooltip(NbtCompound nbt, List<Text> tooltip) {
		FireworkRocketItem.Type type = FireworkRocketItem.Type.byId(nbt.getByte("Type"));
		tooltip.add(Text.method_43471("item.minecraft.firework_star.shape." + type.getName()).formatted(Formatting.GRAY));
		int[] is = nbt.getIntArray("Colors");
		if (is.length > 0) {
			tooltip.add(appendColors(Text.method_43473().formatted(Formatting.GRAY), is));
		}

		int[] js = nbt.getIntArray("FadeColors");
		if (js.length > 0) {
			tooltip.add(appendColors(Text.method_43471("item.minecraft.firework_star.fade_to").append(" ").formatted(Formatting.GRAY), js));
		}

		if (nbt.getBoolean("Trail")) {
			tooltip.add(Text.method_43471("item.minecraft.firework_star.trail").formatted(Formatting.GRAY));
		}

		if (nbt.getBoolean("Flicker")) {
			tooltip.add(Text.method_43471("item.minecraft.firework_star.flicker").formatted(Formatting.GRAY));
		}
	}

	private static Text appendColors(MutableText line, int[] colors) {
		for (int i = 0; i < colors.length; i++) {
			if (i > 0) {
				line.append(", ");
			}

			line.append(getColorText(colors[i]));
		}

		return line;
	}

	private static Text getColorText(int color) {
		DyeColor dyeColor = DyeColor.byFireworkColor(color);
		return dyeColor == null
			? Text.method_43471("item.minecraft.firework_star.custom_color")
			: Text.method_43471("item.minecraft.firework_star." + dyeColor.getName());
	}
}
