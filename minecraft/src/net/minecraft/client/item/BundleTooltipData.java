package net.minecraft.client.item;

import net.minecraft.component.type.BundleContentsComponent;

public record BundleTooltipData(BundleContentsComponent contents) implements TooltipData {
}
