package net.minecraft.enchantment.effect;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3d;

public record AttributeEnchantmentEffect(
	Identifier id, RegistryEntry<EntityAttribute> attribute, EnchantmentLevelBasedValue amount, EntityAttributeModifier.Operation operation
) implements EnchantmentLocationBasedEffect {
	public static final MapCodec<AttributeEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("id").forGetter(AttributeEnchantmentEffect::id),
					EntityAttribute.CODEC.fieldOf("attribute").forGetter(AttributeEnchantmentEffect::attribute),
					EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(AttributeEnchantmentEffect::amount),
					EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeEnchantmentEffect::operation)
				)
				.apply(instance, AttributeEnchantmentEffect::new)
	);

	private Identifier getModifierId(StringIdentifiable suffix) {
		return this.id.withSuffixedPath("/" + suffix.asString());
	}

	public EntityAttributeModifier createAttributeModifier(int value, StringIdentifiable suffix) {
		return new EntityAttributeModifier(this.getModifierId(suffix), (double)this.amount().getValue(value), this.operation());
	}

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean newlyApplied) {
		if (newlyApplied && user instanceof LivingEntity livingEntity) {
			livingEntity.getAttributes().addTemporaryModifiers(this.getModifiers(level, context.slot()));
		}
	}

	@Override
	public void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
		if (user instanceof LivingEntity livingEntity) {
			livingEntity.getAttributes().removeModifiers(this.getModifiers(level, context.slot()));
		}
	}

	private HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(int level, EquipmentSlot slot) {
		HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> hashMultimap = HashMultimap.create();
		hashMultimap.put(this.attribute, this.createAttributeModifier(level, slot));
		return hashMultimap;
	}

	@Override
	public MapCodec<AttributeEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
