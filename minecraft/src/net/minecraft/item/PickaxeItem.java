package net.minecraft.item;

import net.minecraft.registry.tag.BlockTags;

public class PickaxeItem extends MiningToolItem {
	protected PickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
		super((float)attackDamage, attackSpeed, material, BlockTags.PICKAXE_MINEABLE, settings);
	}
}
