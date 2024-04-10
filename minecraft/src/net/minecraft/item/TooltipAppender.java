package net.minecraft.item;

import java.util.function.Consumer;
import net.minecraft.client.item.TooltipType;
import net.minecraft.text.Text;

public interface TooltipAppender {
	void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type);
}
