package net.minecraft.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
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
		uuidMap.put(ArmorItem.Type.BODY, UUID.fromString("C1C72771-8B8E-BA4A-ACE0-81A93C8928B2"));
	});
	public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			return ArmorItem.dispenseArmor(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
		}
	};
	protected final ArmorItem.Type type;
	protected final RegistryEntry<ArmorMaterial> material;
	private final Supplier<Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> attributeModifiers;

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

	public ArmorItem(RegistryEntry<ArmorMaterial> material, ArmorItem.Type type, Item.Settings settings) {
		super(settings);
		this.material = material;
		this.type = type;
		DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
		this.attributeModifiers = Suppliers.memoize(
			() -> {
				int i = ((ArmorMaterial)material.value()).getProtection(type);
				float f = ((ArmorMaterial)material.value()).toughness();
				Builder<RegistryEntry<EntityAttribute>, EntityAttributeModifier> builder = ImmutableMultimap.builder();
				UUID uUID = (UUID)MODIFIERS.get(type);
				builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uUID, "Armor modifier", (double)i, EntityAttributeModifier.Operation.ADD_VALUE));
				builder.put(
					EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uUID, "Armor toughness", (double)f, EntityAttributeModifier.Operation.ADD_VALUE)
				);
				float g = ((ArmorMaterial)material.value()).knockbackResistance();
				if (g > 0.0F) {
					builder.put(
						EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
						new EntityAttributeModifier(uUID, "Armor knockback resistance", (double)g, EntityAttributeModifier.Operation.ADD_VALUE)
					);
				}
	
				return builder.build();
			}
		);
	}

	public ArmorItem.Type getType() {
		return this.type;
	}

	@Override
	public int getEnchantability() {
		return ((ArmorMaterial)this.material.value()).enchantability();
	}

	public RegistryEntry<ArmorMaterial> getMaterial() {
		return this.material;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ((Ingredient)((ArmorMaterial)this.material.value()).repairIngredient().get()).test(ingredient) || super.canRepair(stack, ingredient);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return this.equipAndSwap(this, world, user, hand);
	}

	@Override
	public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == this.type.getEquipmentSlot() ? (Multimap)this.attributeModifiers.get() : super.getAttributeModifiers(slot);
	}

	public int getProtection() {
		return ((ArmorMaterial)this.material.value()).getProtection(this.type);
	}

	public float getToughness() {
		return ((ArmorMaterial)this.material.value()).toughness();
	}

	@Override
	public EquipmentSlot getSlotType() {
		return this.type.getEquipmentSlot();
	}

	@Override
	public RegistryEntry<SoundEvent> getEquipSound() {
		return ((ArmorMaterial)this.getMaterial().value()).equipSound();
	}

	public static enum Type implements StringIdentifiable {
		HELMET(EquipmentSlot.HEAD, 11, "helmet"),
		CHESTPLATE(EquipmentSlot.CHEST, 16, "chestplate"),
		LEGGINGS(EquipmentSlot.LEGS, 15, "leggings"),
		BOOTS(EquipmentSlot.FEET, 13, "boots"),
		BODY(EquipmentSlot.BODY, 16, "body");

		public static final Codec<ArmorItem.Type> CODEC = StringIdentifiable.createBasicCodec(ArmorItem.Type::values);
		private final EquipmentSlot equipmentSlot;
		private final String name;
		private final int baseMaxDamage;

		private Type(EquipmentSlot equipmentSlot, int baseMaxDamage, String name) {
			this.equipmentSlot = equipmentSlot;
			this.name = name;
			this.baseMaxDamage = baseMaxDamage;
		}

		public int getMaxDamage(int multiplier) {
			return this.baseMaxDamage * multiplier;
		}

		public EquipmentSlot getEquipmentSlot() {
			return this.equipmentSlot;
		}

		public String getName() {
			return this.name;
		}

		public boolean isTrimmable() {
			return this == HELMET || this == CHESTPLATE || this == LEGGINGS || this == BOOTS;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
