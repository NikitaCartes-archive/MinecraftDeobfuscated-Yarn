package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MaceItem extends Item {
	private static final int ATTACK_DAMAGE_MODIFIER_VALUE = 6;
	private static final float ATTACK_SPEED_MODIFIER_VALUE = -2.4F;
	public static final float MINING_SPEED_MULTIPLIER = 1.5F;
	private static final ImmutableMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> ATTRIBUTE_MODIFIERS = ImmutableMultimap.<RegistryEntry<EntityAttribute>, EntityAttributeModifier>builder()
		.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 6.0, EntityAttributeModifier.Operation.ADD_VALUE)
		)
		.put(
			EntityAttributes.GENERIC_ATTACK_SPEED,
			new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4F, EntityAttributeModifier.Operation.ADD_VALUE)
		)
		.build();

	public MaceItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return state.isOf(Blocks.COBWEB) ? 15.0F : 1.5F;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
		if (attacker instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.fallDistance > 1.5F) {
			ServerWorld serverWorld = (ServerWorld)attacker.getWorld();
			if (serverPlayerEntity.ignoreFallDamageAboveY == null || serverPlayerEntity.ignoreFallDamageAboveY > serverPlayerEntity.getY()) {
				serverPlayerEntity.ignoreFallDamageAboveY = serverPlayerEntity.getY();
			}

			if (target.isOnGround()) {
				serverPlayerEntity.setSpawnExtraParticlesOnFall(true);
				serverWorld.playSound(
					null,
					serverPlayerEntity.getX(),
					serverPlayerEntity.getY(),
					serverPlayerEntity.getZ(),
					SoundEvents.ITEM_MACE_SMASH_GROUND,
					SoundCategory.NEUTRAL,
					1.0F,
					1.0F
				);
			} else {
				serverWorld.playSound(
					null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 1.0F, 1.0F
				);
			}
		}

		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (state.getHardness(world, pos) != 0.0F) {
			stack.damage(2, miner, EquipmentSlot.MAINHAND);
		}

		return true;
	}

	@Override
	public boolean isSuitableFor(BlockState state) {
		return state.isOf(Blocks.COBWEB);
	}

	@Override
	public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return (Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier>)(slot == EquipmentSlot.MAINHAND
			? ATTRIBUTE_MODIFIERS
			: super.getAttributeModifiers(slot));
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(Items.BREEZE_ROD);
	}
}
