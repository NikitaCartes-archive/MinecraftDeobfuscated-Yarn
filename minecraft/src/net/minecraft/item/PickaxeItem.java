package net.minecraft.item;

import net.minecraft.registry.tag.BlockTags;

public class PickaxeItem extends MiningToolItem {
	public PickaxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(material, BlockTags.PICKAXE_MINEABLE, attackDamage, attackSpeed, settings);
	}
}
