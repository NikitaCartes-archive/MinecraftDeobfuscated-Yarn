package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ArmorItem extends Item implements Wearable {
	private static final UUID[] MODIFIERS = new UUID[]{
		UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
		UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
		UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
		UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};
	public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			return ArmorItem.dispenseArmor(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
		}
	};
	protected final EquipmentSlot slot;
	private final int protection;
	private final float toughness;
	protected final float knockbackResistance;
	protected final ArmorMaterial type;
	private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

	public static boolean dispenseArmor(BlockPointer pointer, ItemStack armor) {
		BlockPos blockPos = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
		List<LivingEntity> list = pointer.getWorld()
			.getEntitiesByClass(LivingEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.Equipable(armor)));
		if (list.isEmpty()) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)list.get(0);
			EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(armor);
			ItemStack itemStack = armor.split(1);
			livingEntity.equipStack(equipmentSlot, itemStack);
			if (livingEntity instanceof MobEntity) {
				((MobEntity)livingEntity).setEquipmentDropChance(equipmentSlot, 2.0F);
				((MobEntity)livingEntity).setPersistent();
			}

			return true;
		}
	}

	public ArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Settings settings) {
		super(settings.maxDamageIfAbsent(material.getDurability(slot)));
		this.type = material;
		this.slot = slot;
		this.protection = material.getProtectionAmount(slot);
		this.toughness = material.getToughness();
		this.knockbackResistance = material.getKnockbackResistance();
		DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
		Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		UUID uUID = MODIFIERS[slot.getEntitySlotId()];
		builder.put(
			EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uUID, "Armor modifier", (double)this.protection, EntityAttributeModifier.Operation.ADDITION)
		);
		builder.put(
			EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
			new EntityAttributeModifier(uUID, "Armor toughness", (double)this.toughness, EntityAttributeModifier.Operation.ADDITION)
		);
		if (material == ArmorMaterials.NETHERITE) {
			builder.put(
				EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
				new EntityAttributeModifier(uUID, "Armor knockback resistance", (double)this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION)
			);
		}

		this.attributeModifiers = builder.build();
	}

	public EquipmentSlot getSlotType() {
		return this.slot;
	}

	@Override
	public int getEnchantability() {
		return this.type.getEnchantability();
	}

	public ArmorMaterial getMaterial() {
		return this.type;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.type.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		ItemStack itemStack2 = user.getEquippedStack(equipmentSlot);
		if (itemStack2.isEmpty()) {
			user.equipStack(equipmentSlot, itemStack.copy());
			itemStack.setCount(0);
			return TypedActionResult.success(itemStack, world.isClient());
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == this.slot ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	public int getProtection() {
		return this.protection;
	}

	public float method_26353() {
		return this.toughness;
	}
}
