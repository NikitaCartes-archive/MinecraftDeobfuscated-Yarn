package net.minecraft.client.util;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.OrderedText;

@Environment(EnvType.CLIENT)
public interface OrderableTooltip {
	List<OrderedText> getOrderedTooltip();
}
