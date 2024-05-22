package net.minecraft.item.tooltip;

import net.minecraft.component.type.BundleContentsComponent;

public record BundleTooltipData(BundleContentsComponent contents) implements TooltipData {
}
