package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_1741;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.World;

public class ArmorItem extends Item {
	private static final UUID[] MODIFIERS = new UUID[]{
		UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
		UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
		UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
		UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};
	public static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior() {
		@Override
		protected ItemStack method_10135(BlockPointer blockPointer, ItemStack itemStack) {
			ItemStack itemStack2 = ArmorItem.dispenseArmor(blockPointer, itemStack);
			return itemStack2.isEmpty() ? super.method_10135(blockPointer, itemStack) : itemStack2;
		}
	};
	protected final EquipmentSlot field_7880;
	protected final int protection;
	protected final float toughness;
	protected final class_1741 type;

	public static ItemStack dispenseArmor(BlockPointer blockPointer, ItemStack itemStack) {
		BlockPos blockPos = blockPointer.getBlockPos().method_10093(blockPointer.getBlockState().get(DispenserBlock.field_10918));
		List<LivingEntity> list = blockPointer.getWorld()
			.getEntities(LivingEntity.class, new BoundingBox(blockPos), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.CanPickup(itemStack)));
		if (list.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			LivingEntity livingEntity = (LivingEntity)list.get(0);
			EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
			ItemStack itemStack2 = itemStack.split(1);
			livingEntity.setEquippedStack(equipmentSlot, itemStack2);
			if (livingEntity instanceof MobEntity) {
				((MobEntity)livingEntity).setEquipmentDropChance(equipmentSlot, 2.0F);
				((MobEntity)livingEntity).setPersistent();
			}

			return itemStack;
		}
	}

	public ArmorItem(class_1741 arg, EquipmentSlot equipmentSlot, Item.Settings settings) {
		super(settings.durabilityIfNotSet(arg.method_7696(equipmentSlot)));
		this.type = arg;
		this.field_7880 = equipmentSlot;
		this.protection = arg.method_7697(equipmentSlot);
		this.toughness = arg.method_7700();
		DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
	}

	public EquipmentSlot getSlotType() {
		return this.field_7880;
	}

	@Override
	public int getEnchantability() {
		return this.type.method_7699();
	}

	public class_1741 getMaterial() {
		return this.type;
	}

	@Override
	public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
		return this.type.method_7695().matches(itemStack2) || super.canRepair(itemStack, itemStack2);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		ItemStack itemStack2 = playerEntity.getEquippedStack(equipmentSlot);
		if (itemStack2.isEmpty()) {
			playerEntity.setEquippedStack(equipmentSlot, itemStack.copy());
			itemStack.setAmount(0);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
		}
	}

	@Override
	public Multimap<String, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
		Multimap<String, EntityAttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
		if (equipmentSlot == this.field_7880) {
			multimap.put(
				EntityAttributes.ARMOR.getId(),
				new EntityAttributeModifier(
					MODIFIERS[equipmentSlot.getEntitySlotId()], "Armor modifier", (double)this.protection, EntityAttributeModifier.Operation.field_6328
				)
			);
			multimap.put(
				EntityAttributes.ARMOR_TOUGHNESS.getId(),
				new EntityAttributeModifier(
					MODIFIERS[equipmentSlot.getEntitySlotId()], "Armor toughness", (double)this.toughness, EntityAttributeModifier.Operation.field_6328
				)
			);
		}

		return multimap;
	}

	public int method_7687() {
		return this.protection;
	}
}
