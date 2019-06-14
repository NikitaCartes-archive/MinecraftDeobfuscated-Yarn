package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
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
	public void method_7851(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		CompoundTag compoundTag = itemStack.getSubTag("Explosion");
		if (compoundTag != null) {
			appendFireworkTooltip(compoundTag, list);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void appendFireworkTooltip(CompoundTag compoundTag, List<Text> list) {
		FireworkItem.Type type = FireworkItem.Type.byId(compoundTag.getByte("Type"));
		list.add(new TranslatableText("item.minecraft.firework_star.shape." + type.getName()).formatted(Formatting.field_1080));
		int[] is = compoundTag.getIntArray("Colors");
		if (is.length > 0) {
			list.add(appendColors(new LiteralText("").formatted(Formatting.field_1080), is));
		}

		int[] js = compoundTag.getIntArray("FadeColors");
		if (js.length > 0) {
			list.add(appendColors(new TranslatableText("item.minecraft.firework_star.fade_to").append(" ").formatted(Formatting.field_1080), js));
		}

		if (compoundTag.getBoolean("Trail")) {
			list.add(new TranslatableText("item.minecraft.firework_star.trail").formatted(Formatting.field_1080));
		}

		if (compoundTag.getBoolean("Flicker")) {
			list.add(new TranslatableText("item.minecraft.firework_star.flicker").formatted(Formatting.field_1080));
		}
	}

	@Environment(EnvType.CLIENT)
	private static Text appendColors(Text text, int[] is) {
		for (int i = 0; i < is.length; i++) {
			if (i > 0) {
				text.append(", ");
			}

			text.append(getColorText(is[i]));
		}

		return text;
	}

	@Environment(EnvType.CLIENT)
	private static Text getColorText(int i) {
		DyeColor dyeColor = DyeColor.byFireworkColor(i);
		return dyeColor == null
			? new TranslatableText("item.minecraft.firework_star.custom_color")
			: new TranslatableText("item.minecraft.firework_star." + dyeColor.getName());
	}
}
