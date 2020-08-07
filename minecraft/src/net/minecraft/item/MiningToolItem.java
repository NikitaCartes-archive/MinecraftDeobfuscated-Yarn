package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.Set;
import net.minecraft.class_5508;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MiningToolItem extends ToolItem implements Vanishable {
	private final Set<Block> effectiveBlocks;
	protected final float miningSpeed;
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	protected MiningToolItem(ToolMaterial toolMaterial, Set<Block> set, Item.Settings settings) {
		super(toolMaterial, settings);
		this.effectiveBlocks = set;
		this.miningSpeed = toolMaterial.getMiningSpeedMultiplier();
		Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		this.method_31212().method_31214(this.getMaterial(), builder);
		this.attributeModifiers = builder.build();
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return this.effectiveBlocks.contains(state.getBlock()) ? this.miningSpeed : 1.0F;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(2, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
			stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		}

		return true;
	}

	protected abstract class_5508 method_31212();

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.field_6173 ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	public float getAttackDamage() {
		return this.method_31212().method_31213(this.getMaterial());
	}
}
