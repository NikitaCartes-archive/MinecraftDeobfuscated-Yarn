package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TippedArrowItem extends ArrowItem {
	public TippedArrowItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		return PotionUtil.setPotion(super.getDefaultStack(), Potions.POISON);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.isIn(group)) {
			for (Potion potion : Registry.POTION) {
				if (!potion.getEffects().isEmpty()) {
					stacks.add(PotionUtil.setPotion(new ItemStack(this), potion));
				}
			}
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		PotionUtil.buildTooltip(stack, tooltip, 0.125F);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return PotionUtil.getPotion(stack).finishTranslationKey(this.getTranslationKey() + ".effect.");
	}
}
