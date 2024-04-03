package net.minecraft.item;

import java.util.function.Consumer;
import net.minecraft.client.item.TooltipType;
import net.minecraft.text.Text;

public interface TooltipAppender {
	void appendTooltip(Consumer<Text> textConsumer, TooltipType context);
}
