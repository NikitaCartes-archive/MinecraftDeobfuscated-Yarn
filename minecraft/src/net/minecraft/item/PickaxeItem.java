package net.minecraft.item;

import net.minecraft.registry.tag.BlockTags;

public class PickaxeItem extends MiningToolItem {
	public PickaxeItem(ToolMaterial material, Item.Settings settings) {
		super(material, BlockTags.PICKAXE_MINEABLE, settings);
	}
}
