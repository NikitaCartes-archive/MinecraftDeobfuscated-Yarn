package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MaceItem extends Item {
	private static final int ATTACK_DAMAGE_MODIFIER_VALUE = 6;
	private static final float ATTACK_SPEED_MODIFIER_VALUE = -2.4F;
	private static final float MINING_SPEED_MULTIPLIER = 1.5F;
	private static final float KNOCKBACK_RANGE = 2.5F;
	private static final float KNOCKBACK_POWER = 0.6F;
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
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
		if (attacker instanceof ServerPlayerEntity serverPlayerEntity && method_59050(serverPlayerEntity)) {
			ServerWorld serverWorld = (ServerWorld)attacker.getWorld();
			if (!serverPlayerEntity.ignoreFallDamageFromCurrentExplosion
				|| serverPlayerEntity.currentExplosionImpactPos == null
				|| serverPlayerEntity.currentExplosionImpactPos.getY() > serverPlayerEntity.getY()) {
				serverPlayerEntity.currentExplosionImpactPos = serverPlayerEntity.getPos();
				serverPlayerEntity.ignoreFallDamageFromCurrentExplosion = true;
			}

			if (target.isOnGround()) {
				serverPlayerEntity.setSpawnExtraParticlesOnFall(true);
				SoundEvent soundEvent = serverPlayerEntity.fallDistance > 5.0F ? SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY : SoundEvents.ITEM_MACE_SMASH_GROUND;
				serverWorld.playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), soundEvent, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			} else {
				serverWorld.playSound(
					null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 1.0F, 1.0F
				);
			}

			this.knockbackNearbyEntities(serverWorld, serverPlayerEntity, target);
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
		return method_59050(player) ? baseAttackDamage * 0.5F * player.fallDistance : 0.0F;
	}

	private void knockbackNearbyEntities(World world, PlayerEntity player, Entity target) {
		world.getEntitiesByClass(
				LivingEntity.class,
				target.getBoundingBox().expand(2.5),
				entity -> entity != player
						&& entity != target
						&& !target.isTeammate(entity)
						&& (!(entity instanceof ArmorStandEntity armorStandEntity) || !armorStandEntity.isMarker())
						&& target.squaredDistanceTo(entity) <= Math.pow(2.5, 2.0)
			)
			.forEach(
				entity -> {
					Vec3d vec3d = entity.getPos().subtract(target.getPos());
					double d = (2.5 - vec3d.length()) * 0.6F * (1.0 - entity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
					Vec3d vec3d2 = vec3d.normalize().multiply(d);
					if (d > 0.0) {
						entity.addVelocity(vec3d2.x, 0.6F, vec3d2.z);
						if (world instanceof ServerWorld serverWorld) {
							BlockPos blockPos = entity.getSteppingPos();
							Vec3d vec3d3 = blockPos.toCenterPos().add(0.0, 0.5, 0.0);
							int i = (int)(100.0 * d);
							serverWorld.spawnParticles(
								new BlockStateParticleEffect(ParticleTypes.BLOCK, serverWorld.getBlockState(blockPos)), vec3d3.x, vec3d3.y, vec3d3.z, i, 0.3F, 0.3F, 0.3F, 0.15F
							);
						}
					}
				}
			);
	}

	public static boolean method_59050(PlayerEntity playerEntity) {
		return playerEntity.fallDistance > 1.5F && !playerEntity.isFallFlying();
	}
}
