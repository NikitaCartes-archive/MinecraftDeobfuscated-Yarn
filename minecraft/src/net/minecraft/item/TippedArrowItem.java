package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.TextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TippedArrowItem extends ArrowItem {
	public TippedArrowItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_7854() {
		return PotionUtil.setPotion(super.method_7854(), Potions.field_8982);
	}

	@Override
	public void method_7850(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (this.isInItemGroup(itemGroup)) {
			for (Potion potion : Registry.POTION) {
				if (!potion.getEffects().isEmpty()) {
					defaultedList.add(PotionUtil.setPotion(new ItemStack(this), potion));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		PotionUtil.buildTooltip(itemStack, list, 0.125F);
	}

	@Override
	public String method_7866(ItemStack itemStack) {
		return PotionUtil.getPotion(itemStack).getName(this.getTranslationKey() + ".effect.");
	}
}
