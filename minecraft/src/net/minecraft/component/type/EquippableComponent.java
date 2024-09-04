package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModels;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record EquippableComponent(
	EquipmentSlot slot,
	RegistryEntry<SoundEvent> equipSound,
	Optional<Identifier> model,
	Optional<RegistryEntryList<EntityType<?>>> allowedEntities,
	boolean dispensable
) {
	public static final Codec<EquippableComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					EquipmentSlot.CODEC.fieldOf("slot").forGetter(EquippableComponent::slot),
					SoundEvent.ENTRY_CODEC.optionalFieldOf("equip_sound", SoundEvents.ITEM_ARMOR_EQUIP_GENERIC).forGetter(EquippableComponent::equipSound),
					Identifier.CODEC.optionalFieldOf("model").forGetter(EquippableComponent::model),
					RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).optionalFieldOf("allowed_entities").forGetter(EquippableComponent::allowedEntities),
					Codec.BOOL.optionalFieldOf("dispensable", Boolean.valueOf(true)).forGetter(EquippableComponent::dispensable)
				)
				.apply(instance, EquippableComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, EquippableComponent> PACKET_CODEC = PacketCodec.tuple(
		EquipmentSlot.PACKET_CODEC,
		EquippableComponent::slot,
		SoundEvent.ENTRY_PACKET_CODEC,
		EquippableComponent::equipSound,
		Identifier.PACKET_CODEC.collect(PacketCodecs::optional),
		EquippableComponent::model,
		PacketCodecs.registryEntryList(RegistryKeys.ENTITY_TYPE).collect(PacketCodecs::optional),
		EquippableComponent::allowedEntities,
		PacketCodecs.BOOL,
		EquippableComponent::dispensable,
		EquippableComponent::new
	);

	public static EquippableComponent ofCarpet(DyeColor color) {
		return new EquippableComponent(
			EquipmentSlot.BODY,
			SoundEvents.ENTITY_LLAMA_SWAG,
			Optional.of((Identifier)EquipmentModels.CARPET_FROM_COLOR.get(color)),
			Optional.of(RegistryEntryList.of(EntityType::getRegistryEntry, EntityType.LLAMA, EntityType.TRADER_LLAMA)),
			true
		);
	}

	public ActionResult equip(ItemStack stack, PlayerEntity player) {
		if (!player.canUseSlot(this.slot)) {
			return ActionResult.PASS;
		} else {
			ItemStack itemStack = player.getEquippedStack(this.slot);
			if ((!EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || player.isCreative())
				&& !ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
				if (!player.getWorld().isClient()) {
					player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
				}

				if (stack.getCount() <= 1) {
					ItemStack itemStack2 = itemStack.isEmpty() ? stack : itemStack.copyAndEmpty();
					ItemStack itemStack3 = player.isCreative() ? stack.copy() : stack.copyAndEmpty();
					player.equipStack(this.slot, itemStack3);
					return ActionResult.SUCCESS.withNewHandStack(itemStack2);
				} else {
					ItemStack itemStack2 = itemStack.copyAndEmpty();
					ItemStack itemStack3 = stack.splitUnlessCreative(1, player);
					player.equipStack(this.slot, itemStack3);
					if (!player.getInventory().insertStack(itemStack2)) {
						player.dropItem(itemStack2, false);
					}

					return ActionResult.SUCCESS.withNewHandStack(stack);
				}
			} else {
				return ActionResult.FAIL;
			}
		}
	}

	public boolean allows(EntityType<?> entityType) {
		return this.allowedEntities.isEmpty() || ((RegistryEntryList)this.allowedEntities.get()).contains(entityType.getRegistryEntry());
	}
}
