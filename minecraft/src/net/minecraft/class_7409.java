package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class class_7409 extends Item {
	public class_7409(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(this.method_43408().formatted(Formatting.GRAY));
	}

	public MutableText method_43408() {
		return Text.method_43471(this.getTranslationKey() + ".desc");
	}
}
