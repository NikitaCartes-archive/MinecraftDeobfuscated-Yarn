package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.tag.TagKey;

public class MiningToolItem extends ToolItem {
	protected MiningToolItem(ToolMaterial material, TagKey<Block> effectiveBlocks, Item.Settings settings) {
		super(material, settings.component(DataComponentTypes.TOOL, material.createComponent(effectiveBlocks)));
	}

	public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
		return AttributeModifiersComponent.builder()
			.add(
				EntityAttributes.GENERIC_ATTACK_DAMAGE,
				new EntityAttributeModifier(
					ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", (double)(baseAttackDamage + material.getAttackDamage()), EntityAttributeModifier.Operation.ADD_VALUE
				),
				AttributeModifierSlot.MAINHAND
			)
			.add(
				EntityAttributes.GENERIC_ATTACK_SPEED,
				new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
				AttributeModifierSlot.MAINHAND
			)
			.build();
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
