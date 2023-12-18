package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.EnumMap;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ArmorItem extends Item implements Equipment {
	private static final EnumMap<ArmorItem.Type, UUID> MODIFIERS = Util.make(new EnumMap(ArmorItem.Type.class), uuidMap -> {
		uuidMap.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
		uuidMap.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
		uuidMap.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
		uuidMap.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
	});
	public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			return ArmorItem.dispenseArmor(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
		}
	};
	protected final ArmorItem.Type type;
	private final int protection;
	private final float toughness;
	protected final float knockbackResistance;
	protected final ArmorMaterial material;
	private final Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifiers;

	public static boolean dispenseArmor(BlockPointer pointer, ItemStack armor) {
		BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
		List<LivingEntity> list = pointer.world()
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

	public ArmorItem(ArmorMaterial material, ArmorItem.Type type, Item.Settings settings) {
		super(settings.maxDamageIfAbsent(material.getDurability(type)));
		this.material = material;
		this.type = type;
		this.protection = material.getProtection(type);
		this.toughness = material.getToughness();
		this.knockbackResistance = material.getKnockbackResistance();
		DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
		Builder<RegistryEntry<EntityAttribute>, EntityAttributeModifier> builder = ImmutableMultimap.builder();
		UUID uUID = (UUID)MODIFIERS.get(type);
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

	public ArmorItem.Type getType() {
		return this.type;
	}

	@Override
	public int getEnchantability() {
		return this.material.getEnchantability();
	}

	public ArmorMaterial getMaterial() {
		return this.material;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return this.equipAndSwap(this, world, user, hand);
	}

	@Override
	public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == this.type.getEquipmentSlot() ? this.attributeModifiers : super.getAttributeModifiers(slot);
	}

	public int getProtection() {
		return this.protection;
	}

	public float getToughness() {
		return this.toughness;
	}

	@Override
	public EquipmentSlot getSlotType() {
		return this.type.getEquipmentSlot();
	}

	@Override
	public SoundEvent getEquipSound() {
		return this.getMaterial().getEquipSound();
	}

	public static enum Type {
		HELMET(EquipmentSlot.HEAD, "helmet"),
		CHESTPLATE(EquipmentSlot.CHEST, "chestplate"),
		LEGGINGS(EquipmentSlot.LEGS, "leggings"),
		BOOTS(EquipmentSlot.FEET, "boots");

		private final EquipmentSlot equipmentSlot;
		private final String name;

		private Type(EquipmentSlot equipmentSlot, String name) {
			this.equipmentSlot = equipmentSlot;
			this.name = name;
		}

		public EquipmentSlot getEquipmentSlot() {
			return this.equipmentSlot;
		}

		public String getName() {
			return this.name;
		}
	}
}
