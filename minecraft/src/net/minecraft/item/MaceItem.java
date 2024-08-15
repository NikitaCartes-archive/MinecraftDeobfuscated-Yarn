package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
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
	private static final int ATTACK_DAMAGE_MODIFIER_VALUE = 5;
	private static final float ATTACK_SPEED_MODIFIER_VALUE = -3.4F;
	public static final float MINING_SPEED_MULTIPLIER = 1.5F;
	private static final float HEAVY_SMASH_SOUND_FALL_DISTANCE_THRESHOLD = 5.0F;
	public static final float KNOCKBACK_RANGE = 3.5F;
	private static final float KNOCKBACK_POWER = 0.7F;

	public MaceItem(Item.Settings settings) {
		super(settings);
	}

	public static AttributeModifiersComponent createAttributeModifiers() {
		return AttributeModifiersComponent.builder()
			.add(
				EntityAttributes.ATTACK_DAMAGE,
				new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.0, EntityAttributeModifier.Operation.ADD_VALUE),
				AttributeModifierSlot.MAINHAND
			)
			.add(
				EntityAttributes.ATTACK_SPEED,
				new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3.4F, EntityAttributeModifier.Operation.ADD_VALUE),
				AttributeModifierSlot.MAINHAND
			)
			.build();
	}

	public static ToolComponent createToolComponent() {
		return new ToolComponent(List.of(), 1.0F, 2);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (shouldDealAdditionalDamage(attacker)) {
			ServerWorld serverWorld = (ServerWorld)attacker.getWorld();
			attacker.setVelocity(attacker.getVelocity().withAxis(Direction.Axis.Y, 0.01F));
			if (attacker instanceof ServerPlayerEntity serverPlayerEntity) {
				serverPlayerEntity.currentExplosionImpactPos = this.getCurrentExplosionImpactPos(serverPlayerEntity);
				serverPlayerEntity.setIgnoreFallDamageFromCurrentExplosion(true);
				serverPlayerEntity.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayerEntity));
			}

			if (target.isOnGround()) {
				if (attacker instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.setSpawnExtraParticlesOnFall(true);
				}

				SoundEvent soundEvent = attacker.fallDistance > 5.0F ? SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY : SoundEvents.ITEM_MACE_SMASH_GROUND;
				serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), soundEvent, attacker.getSoundCategory(), 1.0F, 1.0F);
			} else {
				serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ITEM_MACE_SMASH_AIR, attacker.getSoundCategory(), 1.0F, 1.0F);
			}

			knockbackNearbyEntities(serverWorld, attacker, target);
		}

		return true;
	}

	private Vec3d getCurrentExplosionImpactPos(ServerPlayerEntity player) {
		return player.shouldIgnoreFallDamageFromCurrentExplosion()
				&& player.currentExplosionImpactPos != null
				&& player.currentExplosionImpactPos.y <= player.getPos().y
			? player.currentExplosionImpactPos
			: player.getPos();
	}

	@Override
	public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
		if (shouldDealAdditionalDamage(attacker)) {
			attacker.onLanding();
		}
	}

	@Override
	public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
		if (damageSource.getSource() instanceof LivingEntity livingEntity) {
			if (!shouldDealAdditionalDamage(livingEntity)) {
				return 0.0F;
			} else {
				float f = 3.0F;
				float g = 8.0F;
				float h = livingEntity.fallDistance;
				float i;
				if (h <= 3.0F) {
					i = 4.0F * h;
				} else if (h <= 8.0F) {
					i = 12.0F + 2.0F * (h - 3.0F);
				} else {
					i = 22.0F + h - 8.0F;
				}

				return livingEntity.getWorld() instanceof ServerWorld serverWorld
					? i + EnchantmentHelper.getSmashDamagePerFallenBlock(serverWorld, livingEntity.getWeaponStack(), target, damageSource, 0.0F) * h
					: i;
			}
		} else {
			return 0.0F;
		}
	}

	private static void knockbackNearbyEntities(World world, Entity attacker, Entity attacked) {
		world.syncWorldEvent(WorldEvents.SMASH_ATTACK, attacked.getSteppingPos(), 750);
		world.getEntitiesByClass(LivingEntity.class, attacked.getBoundingBox().expand(3.5), getKnockbackPredicate(attacker, attacked)).forEach(entity -> {
			Vec3d vec3d = entity.getPos().subtract(attacked.getPos());
			double d = getKnockback(attacker, entity, vec3d);
			Vec3d vec3d2 = vec3d.normalize().multiply(d);
			if (d > 0.0) {
				entity.addVelocity(vec3d2.x, 0.7F, vec3d2.z);
				if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayerEntity));
				}
			}
		});
	}

	private static Predicate<LivingEntity> getKnockbackPredicate(Entity attacker, Entity attacked) {
		return entity -> {
			boolean bl;
			boolean bl2;
			boolean bl3;
			boolean var10000;
			label62: {
				bl = !entity.isSpectator();
				bl2 = entity != attacker && entity != attacked;
				bl3 = !attacker.isTeammate(entity);
				if (entity instanceof TameableEntity tameableEntity && tameableEntity.isTamed() && attacker.getUuid().equals(tameableEntity.getOwnerUuid())) {
					var10000 = true;
					break label62;
				}

				var10000 = false;
			}

			boolean bl4;
			label55: {
				bl4 = !var10000;
				if (entity instanceof ArmorStandEntity armorStandEntity && armorStandEntity.isMarker()) {
					var10000 = false;
					break label55;
				}

				var10000 = true;
			}

			boolean bl5 = var10000;
			boolean bl6 = attacked.squaredDistanceTo(entity) <= Math.pow(3.5, 2.0);
			return bl && bl2 && bl3 && bl4 && bl5 && bl6;
		};
	}

	private static double getKnockback(Entity attacker, LivingEntity attacked, Vec3d distance) {
		return (3.5 - distance.length())
			* 0.7F
			* (double)(attacker.fallDistance > 5.0F ? 2 : 1)
			* (1.0 - attacked.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
	}

	public static boolean shouldDealAdditionalDamage(LivingEntity attacker) {
		return attacker.fallDistance > 1.5F && !attacker.isFallFlying();
	}
}
