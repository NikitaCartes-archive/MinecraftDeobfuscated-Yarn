package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.DensityEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class MaceItem extends Item {
	private static final int ATTACK_DAMAGE_MODIFIER_VALUE = 6;
	private static final float ATTACK_SPEED_MODIFIER_VALUE = -2.4F;
	private static final float MINING_SPEED_MULTIPLIER = 1.5F;
	private static final float field_50141 = 5.0F;
	public static final float KNOCKBACK_RANGE = 3.5F;
	private static final float KNOCKBACK_POWER = 0.7F;
	private static final float FALL_DISTANCE_MULTIPLIER = 3.0F;
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

	public static ToolComponent createToolComponent() {
		return new ToolComponent(List.of(), 1.0F, 2);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	@Override
	public int getEnchantability() {
		return 15;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
		if (attacker instanceof ServerPlayerEntity serverPlayerEntity && shouldDealAdditionalDamage(serverPlayerEntity)) {
			ServerWorld serverWorld = (ServerWorld)attacker.getWorld();
			if (!serverPlayerEntity.ignoreFallDamageFromCurrentExplosion
				|| serverPlayerEntity.currentExplosionImpactPos == null
				|| serverPlayerEntity.currentExplosionImpactPos.getY() > serverPlayerEntity.getY()) {
				serverPlayerEntity.currentExplosionImpactPos = serverPlayerEntity.getPos();
				serverPlayerEntity.ignoreFallDamageFromCurrentExplosion = true;
			}

			serverPlayerEntity.setVelocity(serverPlayerEntity.getVelocity().withAxis(Direction.Axis.Y, 0.0));
			serverPlayerEntity.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayerEntity));
			if (target.isOnGround()) {
				serverPlayerEntity.setSpawnExtraParticlesOnFall(true);
				SoundEvent soundEvent = serverPlayerEntity.fallDistance > 5.0F ? SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY : SoundEvents.ITEM_MACE_SMASH_GROUND;
				serverWorld.playSound(
					null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), soundEvent, serverPlayerEntity.getSoundCategory(), 1.0F, 1.0F
				);
			} else {
				serverWorld.playSound(
					null,
					serverPlayerEntity.getX(),
					serverPlayerEntity.getY(),
					serverPlayerEntity.getZ(),
					SoundEvents.ITEM_MACE_SMASH_AIR,
					serverPlayerEntity.getSoundCategory(),
					1.0F,
					1.0F
				);
			}

			knockbackNearbyEntities(serverWorld, serverPlayerEntity, target);
		}

		return true;
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

	@Override
	public float getBonusAttackDamage(PlayerEntity player, float baseAttackDamage) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.DENSITY, player);
		float f = DensityEnchantment.getDamage(i, player.fallDistance);
		return shouldDealAdditionalDamage(player) ? 3.0F * player.fallDistance + f : 0.0F;
	}

	private static void knockbackNearbyEntities(World world, PlayerEntity player, Entity attacked) {
		world.syncWorldEvent(WorldEvents.SMASH_ATTACK, attacked.getSteppingPos(), 750);
		world.getEntitiesByClass(LivingEntity.class, attacked.getBoundingBox().expand(3.5), getKnockbackPredicate(player, attacked)).forEach(entity -> {
			Vec3d vec3d = entity.getPos().subtract(attacked.getPos());
			double d = getKnockback(player, entity, vec3d);
			Vec3d vec3d2 = vec3d.normalize().multiply(d);
			if (d > 0.0) {
				entity.addVelocity(vec3d2.x, 0.7F, vec3d2.z);
			}
		});
	}

	private static Predicate<LivingEntity> getKnockbackPredicate(PlayerEntity player, Entity attacked) {
		return entity -> {
			boolean bl;
			boolean bl2;
			boolean bl3;
			boolean var10000;
			label44: {
				bl = !entity.isSpectator();
				bl2 = entity != player && entity != attacked;
				bl3 = !player.isTeammate(entity);
				if (entity instanceof ArmorStandEntity armorStandEntity && armorStandEntity.isMarker()) {
					var10000 = false;
					break label44;
				}

				var10000 = true;
			}

			boolean bl4 = var10000;
			boolean bl5 = attacked.squaredDistanceTo(entity) <= Math.pow(3.5, 2.0);
			return bl && bl2 && bl3 && bl4 && bl5;
		};
	}

	private static double getKnockback(PlayerEntity player, LivingEntity attacked, Vec3d distance) {
		return (3.5 - distance.length())
			* 0.7F
			* (double)(player.fallDistance > 5.0F ? 2 : 1)
			* (1.0 - attacked.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
	}

	public static boolean shouldDealAdditionalDamage(PlayerEntity attacker) {
		return attacker.fallDistance > 1.5F && !attacker.isFallFlying();
	}
}
