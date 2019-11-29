package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.class_4741;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MiningToolItem extends ToolItem {
	private final Set<Block> effectiveBlocks;
	protected final float miningSpeed;

	protected MiningToolItem(ToolMaterial toolMaterial, Set<Block> set, Item.Settings settings) {
		super(toolMaterial, settings);
		this.effectiveBlocks = set;
		this.miningSpeed = toolMaterial.getMiningSpeed();
	}

	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		return this.effectiveBlocks.contains(state.getBlock()) ? this.miningSpeed : 1.0F;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(2, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (!world.isClient && state.getHardness(world, pos) != 0.0F) {
			stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return true;
	}

	protected abstract class_4741 method_24225();

	@Override
	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot slot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(slot);
		if (slot == EquipmentSlot.MAINHAND) {
			this.method_24225().method_24227(this.getMaterial(), multimap);
		}

		return multimap;
	}
}
