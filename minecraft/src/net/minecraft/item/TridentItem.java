package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentItem extends Item implements Vanishable {
	public static final int field_30926 = 10;
	public static final float field_30927 = 8.0F;
	public static final float field_30928 = 2.5F;
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	public TridentItem(Item.Settings settings) {
		super(settings);
		Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 8.0, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			EntityAttributes.GENERIC_ATTACK_SPEED,
			new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.9F, EntityAttributeModifier.Operation.ADDITION)
		);
		this.attributeModifiers = builder.build();
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.isCreative();
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 72000;
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntity playerEntity) {
			int i = this.getMaxUseTime(stack) - remainingUseTicks;
			if (i >= 10) {
				int j = EnchantmentHelper.getRiptide(stack);
				if (j <= 0 || playerEntity.isTouchingWaterOrRain()) {
					if (!world.isClient) {
						stack.damage(1, playerEntity, p -> p.sendToolBreakStatus(user.getActiveHand()));
						if (j == 0) {
							TridentEntity tridentEntity = new TridentEntity(world, playerEntity, stack);
							tridentEntity.setProperties(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
							if (playerEntity.getAbilities().creativeMode) {
								tridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
							}

							world.spawnEntity(tridentEntity);
							world.playSoundFromEntity(null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
							if (!playerEntity.getAbilities().creativeMode) {
								playerEntity.getInventory().removeOne(stack);
							}
						}
					}

					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
					if (j > 0) {
						float f = playerEntity.getYaw();
						float g = playerEntity.getPitch();
						float h = -MathHelper.sin(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
						float k = -MathHelper.sin(g * (float) (Math.PI / 180.0));
						float l = MathHelper.cos(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
						float m = MathHelper.sqrt(h * h + k * k + l * l);
						float n = 3.0F * ((1.0F + (float)j) / 4.0F);
						h *= n / m;
						k *= n / m;
						l *= n / m;
						playerEntity.addVelocity((double)h, (double)k, (double)l);
						playerEntity.setRiptideTicks(20);
						if (playerEntity.isOnGround()) {
							float o = 1.1999999F;
							playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999F, 0.0));
						}

						SoundEvent soundEvent;
						if (j >= 3) {
							soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
						} else if (j == 2) {
							soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
						} else {
							soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
						}

						world.playSoundFromEntity(null, playerEntity, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
					}
				}
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
			return TypedActionResult.fail(itemStack);
		} else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isTouchingWaterOrRain()) {
			return TypedActionResult.fail(itemStack);
		} else {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack);
		}
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if ((double)state.getHardness(world, pos) != 0.0) {
			stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return true;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
