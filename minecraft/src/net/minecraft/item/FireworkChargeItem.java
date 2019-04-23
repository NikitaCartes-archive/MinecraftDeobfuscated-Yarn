package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class FireworkChargeItem extends Item {
	public FireworkChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("Explosion");
		if (compoundTag != null) {
			buildTooltip(compoundTag, list);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void buildTooltip(CompoundTag compoundTag, List<Component> list) {
		FireworkItem.Type type = FireworkItem.Type.fromId(compoundTag.getByte("Type"));
		list.add(new TranslatableComponent("item.minecraft.firework_star.shape." + type.getName()).applyFormat(ChatFormat.field_1080));
		int[] is = compoundTag.getIntArray("Colors");
		if (is.length > 0) {
			list.add(appendColorNames(new TextComponent("").applyFormat(ChatFormat.field_1080), is));
		}

		int[] js = compoundTag.getIntArray("FadeColors");
		if (js.length > 0) {
			list.add(appendColorNames(new TranslatableComponent("item.minecraft.firework_star.fade_to").append(" ").applyFormat(ChatFormat.field_1080), js));
		}

		if (compoundTag.getBoolean("Trail")) {
			list.add(new TranslatableComponent("item.minecraft.firework_star.trail").applyFormat(ChatFormat.field_1080));
		}

		if (compoundTag.getBoolean("Flicker")) {
			list.add(new TranslatableComponent("item.minecraft.firework_star.flicker").applyFormat(ChatFormat.field_1080));
		}
	}

	@Environment(EnvType.CLIENT)
	private static Component appendColorNames(Component component, int[] is) {
		for (int i = 0; i < is.length; i++) {
			if (i > 0) {
				component.append(", ");
			}

			component.append(getColorTextComponent(is[i]));
		}

		return component;
	}

	@Environment(EnvType.CLIENT)
	private static Component getColorTextComponent(int i) {
		DyeColor dyeColor = DyeColor.byFireworkColor(i);
		return dyeColor == null
			? new TranslatableComponent("item.minecraft.firework_star.custom_color")
			: new TranslatableComponent("item.minecraft.firework_star." + dyeColor.getName());
	}
}
