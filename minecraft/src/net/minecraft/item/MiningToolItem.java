package net.minecraft.item;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningToolItem extends ToolItem {
	private final TagKey<Block> effectiveBlocks;
	protected final float miningSpeed;

	protected MiningToolItem(ToolMaterial material, TagKey<Block> effectiveBlocks, Item.Settings settings) {
		super(material, settings);
		this.effectiveBlocks = effectiveBlocks;
		this.miningSpeed = material.getMiningSpeedMultiplier();
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
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return state.isIn(this.effectiveBlocks) ? this.miningSpeed : 1.0F;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(2, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
			stack.damage(1, miner, EquipmentSlot.MAINHAND);
		}

		return true;
	}

	@Override
	public boolean isSuitableFor(BlockState state) {
		int i = this.getMaterial().getMiningLevel();
		if (i < MiningLevels.DIAMOND && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
			return false;
		} else if (i < MiningLevels.IRON && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
			return false;
		} else {
			return i < MiningLevels.STONE && state.isIn(BlockTags.NEEDS_STONE_TOOL) ? false : state.isIn(this.effectiveBlocks);
		}
	}
}
