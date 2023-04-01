package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_8293;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BedItem extends BlockItem {
	private final Multimap<EntityAttribute, EntityAttributeModifier> field_44151;

	public BedItem(Block block, Item.Settings settings) {
		super(block, settings);
		Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 6.0, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			EntityAttributes.GENERIC_ATTACK_SPEED,
			new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4F, EntityAttributeModifier.Operation.ADDITION)
		);
		this.field_44151 = builder.build();
	}

	@Override
	protected boolean place(ItemPlacementContext context, BlockState state) {
		return context.getWorld().setBlockState(context.getBlockPos(), state, Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD | Block.FORCE_STATE);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (class_8293.field_43665.method_50116()) {
			World world = target.world;
			Vec3d vec3d = target.getPos();
			world.createExplosion(null, world.getDamageSources().badRespawnPoint(vec3d), null, vec3d, 2.0F, true, World.ExplosionSourceType.BLOCK);
			attacker.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
			stack.decrement(1);
		}

		return super.postHit(stack, target, attacker);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("rule.bed_pvp.tooltip"));
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return class_8293.field_43665.method_50116() && slot == EquipmentSlot.MAINHAND ? this.field_44151 : super.getAttributeModifiers(slot);
	}
}
