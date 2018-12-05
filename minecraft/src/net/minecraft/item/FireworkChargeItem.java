package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class FireworkChargeItem extends Item {
	public FireworkChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("Explosion");
		if (compoundTag != null) {
			method_7809(compoundTag, list);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void method_7809(CompoundTag compoundTag, List<TextComponent> list) {
		FireworksItem.class_1782 lv = FireworksItem.class_1782.method_7813(compoundTag.getByte("Type"));
		list.add(new TranslatableTextComponent("item.minecraft.firework_star.shape." + lv.method_7812()).applyFormat(TextFormat.GRAY));
		int[] is = compoundTag.getIntArray("Colors");
		if (is.length > 0) {
			list.add(method_7811(new StringTextComponent("").applyFormat(TextFormat.GRAY), is));
		}

		int[] js = compoundTag.getIntArray("FadeColors");
		if (js.length > 0) {
			list.add(method_7811(new TranslatableTextComponent("item.minecraft.firework_star.fade_to").append(" ").applyFormat(TextFormat.GRAY), js));
		}

		if (compoundTag.getBoolean("Trail")) {
			list.add(new TranslatableTextComponent("item.minecraft.firework_star.trail").applyFormat(TextFormat.GRAY));
		}

		if (compoundTag.getBoolean("Flicker")) {
			list.add(new TranslatableTextComponent("item.minecraft.firework_star.flicker").applyFormat(TextFormat.GRAY));
		}
	}

	@Environment(EnvType.CLIENT)
	private static TextComponent method_7811(TextComponent textComponent, int[] is) {
		for (int i = 0; i < is.length; i++) {
			if (i > 0) {
				textComponent.append(", ");
			}

			textComponent.append(getColorTextComponent(is[i]));
		}

		return textComponent;
	}

	@Environment(EnvType.CLIENT)
	private static TextComponent getColorTextComponent(int i) {
		DyeColor dyeColor = DyeColor.byFireworkColor(i);
		return dyeColor == null
			? new TranslatableTextComponent("item.minecraft.firework_star.custom_color")
			: new TranslatableTextComponent("item.minecraft.firework_star." + dyeColor.getName());
	}
}
