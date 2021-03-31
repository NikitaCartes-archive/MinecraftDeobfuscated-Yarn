package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class FireworkChargeItem extends Item {
	public FireworkChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		NbtCompound nbtCompound = stack.getSubTag("Explosion");
		if (nbtCompound != null) {
			appendFireworkTooltip(nbtCompound, tooltip);
		}
	}

	public static void appendFireworkTooltip(NbtCompound nbt, List<Text> tooltip) {
		FireworkItem.Type type = FireworkItem.Type.byId(nbt.getByte("Type"));
		tooltip.add(new TranslatableText("item.minecraft.firework_star.shape." + type.getName()).formatted(Formatting.GRAY));
		int[] is = nbt.getIntArray("Colors");
		if (is.length > 0) {
			tooltip.add(appendColors(new LiteralText("").formatted(Formatting.GRAY), is));
		}

		int[] js = nbt.getIntArray("FadeColors");
		if (js.length > 0) {
			tooltip.add(appendColors(new TranslatableText("item.minecraft.firework_star.fade_to").append(" ").formatted(Formatting.GRAY), js));
		}

		if (nbt.getBoolean("Trail")) {
			tooltip.add(new TranslatableText("item.minecraft.firework_star.trail").formatted(Formatting.GRAY));
		}

		if (nbt.getBoolean("Flicker")) {
			tooltip.add(new TranslatableText("item.minecraft.firework_star.flicker").formatted(Formatting.GRAY));
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
			? new TranslatableText("item.minecraft.firework_star.custom_color")
			: new TranslatableText("item.minecraft.firework_star." + dyeColor.getName());
	}
}
