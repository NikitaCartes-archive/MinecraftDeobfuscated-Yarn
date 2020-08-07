package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.CompoundTag;
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

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		CompoundTag compoundTag = stack.getSubTag("Explosion");
		if (compoundTag != null) {
			appendFireworkTooltip(compoundTag, tooltip);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void appendFireworkTooltip(CompoundTag tag, List<Text> tooltip) {
		FireworkItem.Type type = FireworkItem.Type.byId(tag.getByte("Type"));
		tooltip.add(new TranslatableText("item.minecraft.firework_star.shape." + type.getName()).formatted(Formatting.field_1080));
		int[] is = tag.getIntArray("Colors");
		if (is.length > 0) {
			tooltip.add(appendColors(new LiteralText("").formatted(Formatting.field_1080), is));
		}

		int[] js = tag.getIntArray("FadeColors");
		if (js.length > 0) {
			tooltip.add(appendColors(new TranslatableText("item.minecraft.firework_star.fade_to").append(" ").formatted(Formatting.field_1080), js));
		}

		if (tag.getBoolean("Trail")) {
			tooltip.add(new TranslatableText("item.minecraft.firework_star.trail").formatted(Formatting.field_1080));
		}

		if (tag.getBoolean("Flicker")) {
			tooltip.add(new TranslatableText("item.minecraft.firework_star.flicker").formatted(Formatting.field_1080));
		}
	}

	@Environment(EnvType.CLIENT)
	private static Text appendColors(MutableText line, int[] colors) {
		for (int i = 0; i < colors.length; i++) {
			if (i > 0) {
				line.append(", ");
			}

			line.append(getColorText(colors[i]));
		}

		return line;
	}

	@Environment(EnvType.CLIENT)
	private static Text getColorText(int color) {
		DyeColor dyeColor = DyeColor.byFireworkColor(color);
		return dyeColor == null
			? new TranslatableText("item.minecraft.firework_star.custom_color")
			: new TranslatableText("item.minecraft.firework_star." + dyeColor.getName());
	}
}
