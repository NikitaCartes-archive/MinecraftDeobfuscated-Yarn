package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.TagKey;

public class MiningToolItem extends Item {
	protected MiningToolItem(ToolMaterial material, TagKey<Block> effectiveBlocks, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(material.applyToolSettings(settings, effectiveBlocks, attackDamage, attackSpeed));
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(2, attacker, EquipmentSlot.MAINHAND);
	}
}
