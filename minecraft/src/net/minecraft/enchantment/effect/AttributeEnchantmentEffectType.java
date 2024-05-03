package net.minecraft.enchantment.effect;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValueType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

public record AttributeEnchantmentEffectType(
	String name, RegistryEntry<EntityAttribute> attribute, EnchantmentLevelBasedValueType amount, EntityAttributeModifier.Operation operation, UUID uuid
) implements EnchantmentLocationBasedEffectType {
	public static final MapCodec<AttributeEnchantmentEffectType> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.STRING.fieldOf("name").forGetter(AttributeEnchantmentEffectType::name),
					EntityAttribute.CODEC.fieldOf("attribute").forGetter(AttributeEnchantmentEffectType::attribute),
					EnchantmentLevelBasedValueType.CODEC.fieldOf("amount").forGetter(AttributeEnchantmentEffectType::amount),
					EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeEnchantmentEffectType::operation),
					Uuids.STRING_CODEC.fieldOf("uuid").forGetter(AttributeEnchantmentEffectType::uuid)
				)
				.apply(instance, AttributeEnchantmentEffectType::new)
	);

	public EntityAttributeModifier createAttributeModifier(int value) {
		return new EntityAttributeModifier(this.uuid(), this.name(), (double)this.amount().getValue(value), this.operation());
	}

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean bl) {
		if (bl && user instanceof LivingEntity livingEntity) {
			livingEntity.getAttributes().addTemporaryModifiers(this.getModifiers(level));
		}
	}

	@Override
	public void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
		if (user instanceof LivingEntity livingEntity) {
			livingEntity.getAttributes().removeModifiers(this.getModifiers(level));
		}
	}

	private HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(int level) {
		HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> hashMultimap = HashMultimap.create();
		hashMultimap.put(this.attribute, this.createAttributeModifier(level));
		return hashMultimap;
	}

	@Override
	public MapCodec<AttributeEnchantmentEffectType> getCodec() {
		return CODEC;
	}
}
