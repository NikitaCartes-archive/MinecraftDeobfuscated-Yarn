package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
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
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("Explosion");
		if (compoundTag != null) {
			buildTooltip(compoundTag, list);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void buildTooltip(CompoundTag compoundTag, List<TextComponent> list) {
		FireworksItem.Type type = FireworksItem.Type.fromId(compoundTag.getByte("Type"));
		list.add(new TranslatableTextComponent("item.minecraft.firework_star.shape." + type.getName()).applyFormat(TextFormat.field_1080));
		int[] is = compoundTag.getIntArray("Colors");
		if (is.length > 0) {
			list.add(appendColorNames(new StringTextComponent("").applyFormat(TextFormat.field_1080), is));
		}

		int[] js = compoundTag.getIntArray("FadeColors");
		if (js.length > 0) {
			list.add(appendColorNames(new TranslatableTextComponent("item.minecraft.firework_star.fade_to").append(" ").applyFormat(TextFormat.field_1080), js));
		}

		if (compoundTag.getBoolean("Trail")) {
			list.add(new TranslatableTextComponent("item.minecraft.firework_star.trail").applyFormat(TextFormat.field_1080));
		}

		if (compoundTag.getBoolean("Flicker")) {
			list.add(new TranslatableTextComponent("item.minecraft.firework_star.flicker").applyFormat(TextFormat.field_1080));
		}
	}

	@Environment(EnvType.CLIENT)
	private static TextComponent appendColorNames(TextComponent textComponent, int[] is) {
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
