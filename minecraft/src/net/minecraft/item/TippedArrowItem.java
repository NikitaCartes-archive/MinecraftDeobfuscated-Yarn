package net.minecraft.item;

import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;

public class TippedArrowItem extends ArrowItem {
	public TippedArrowItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack itemStack = super.getDefaultStack();
		itemStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.POISON));
		return itemStack;
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
		if (potionContentsComponent != null) {
			potionContentsComponent.buildTooltip(tooltip::add, 0.125F, context.getUpdateTickRate());
		}
	}

	@Override
	public Text getName(ItemStack stack) {
		return (Text)stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
			.potion()
			.map(potion -> Text.translatable(this.translationKey + ".effect." + ((Potion)potion.value()).getBaseName()))
			.orElseGet(() -> Text.translatable(this.translationKey + ".effect.empty"));
	}
}
