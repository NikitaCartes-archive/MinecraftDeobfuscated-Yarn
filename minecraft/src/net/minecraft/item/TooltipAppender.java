package net.minecraft.item;

import java.util.function.Consumer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;

public interface TooltipAppender {
	void appendTooltip(Consumer<Text> textConsumer, TooltipContext context);
}
