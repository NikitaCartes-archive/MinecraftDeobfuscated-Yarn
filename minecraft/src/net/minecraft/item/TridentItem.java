package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentItem extends Item {
	public TridentItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(
			new Identifier("throwing"),
			(itemStack, world, livingEntity) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
		);
	}

	@Override
	public boolean canMine(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		return !playerEntity.isCreative();
	}

	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.SPEAR;
	}

	@Override
	public int getMaxUseTime(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
		if (livingEntity instanceof PlayerEntity) {
			PlayerEntity playerEntity = (PlayerEntity)livingEntity;
			int j = this.getMaxUseTime(itemStack) - i;
			if (j >= 10) {
				int k = EnchantmentHelper.getRiptide(itemStack);
				if (k <= 0 || playerEntity.isInsideWaterOrRain()) {
					if (!world.isClient) {
						itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(livingEntity.getActiveHand()));
						if (k == 0) {
							TridentEntity tridentEntity = new TridentEntity(world, playerEntity, itemStack);
							tridentEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 2.5F + (float)k * 0.5F, 1.0F);
							if (playerEntity.abilities.creativeMode) {
								tridentEntity.pickupType = ProjectileEntity.PickupPermission.CREATIVE_ONLY;
							}

							world.spawnEntity(tridentEntity);
							world.playSoundFromEntity(null, tridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
							if (!playerEntity.abilities.creativeMode) {
								playerEntity.inventory.removeOne(itemStack);
							}
						}
					}

					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
					if (k > 0) {
						float f = playerEntity.yaw;
						float g = playerEntity.pitch;
						float h = -MathHelper.sin(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
						float l = -MathHelper.sin(g * (float) (Math.PI / 180.0));
						float m = MathHelper.cos(f * (float) (Math.PI / 180.0)) * MathHelper.cos(g * (float) (Math.PI / 180.0));
						float n = MathHelper.sqrt(h * h + l * l + m * m);
						float o = 3.0F * ((1.0F + (float)k) / 4.0F);
						h *= o / n;
						l *= o / n;
						m *= o / n;
						playerEntity.addVelocity((double)h, (double)l, (double)m);
						playerEntity.method_6018(20);
						if (playerEntity.onGround) {
							float p = 1.1999999F;
							playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999F, 0.0));
						}

						SoundEvent soundEvent;
						if (k >= 3) {
							soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
						} else if (k == 2) {
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
			return TypedActionResult.fail(itemStack);
		} else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !playerEntity.isInsideWaterOrRain()) {
			return TypedActionResult.fail(itemStack);
		} else {
			playerEntity.setCurrentHand(hand);
			return TypedActionResult.successWithoutSwing(itemStack);
		}
	}

	@Override
	public boolean postHit(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
		itemStack.damage(1, livingEntity2, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}

	@Override
	public boolean postMine(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if ((double)blockState.getHardness(world, blockPos) != 0.0) {
			itemStack.damage(2, livingEntity, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return true;
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			multimap.put(
				EntityAttributes.ATTACK_DAMAGE.getId(),
				new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", 8.0, EntityAttributeModifier.Operation.ADDITION)
			);
			multimap.put(
				EntityAttributes.ATTACK_SPEED.getId(),
				new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Tool modifier", -2.9F, EntityAttributeModifier.Operation.ADDITION)
			);
		}

		return multimap;
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
