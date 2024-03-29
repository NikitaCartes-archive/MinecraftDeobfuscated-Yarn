package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record DamageSourcePropertiesLootCondition(Optional<DamageSourcePredicate> predicate) implements LootCondition {
	public static final Codec<DamageSourcePropertiesLootCondition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(DamageSourcePredicate.CODEC, "predicate").forGetter(DamageSourcePropertiesLootCondition::predicate)
				)
				.apply(instance, DamageSourcePropertiesLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.DAMAGE_SOURCE_PROPERTIES;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.ORIGIN, LootContextParameters.DAMAGE_SOURCE);
	}

	public boolean test(LootContext lootContext) {
		DamageSource damageSource = lootContext.get(LootContextParameters.DAMAGE_SOURCE);
		Vec3d vec3d = lootContext.get(LootContextParameters.ORIGIN);
		if (vec3d != null && damageSource != null) {
			return this.predicate.isEmpty() || ((DamageSourcePredicate)this.predicate.get()).test(lootContext.getWorld(), vec3d, damageSource);
		} else {
			return false;
		}
	}

	public static LootCondition.Builder builder(DamageSourcePredicate.Builder builder) {
		return () -> new DamageSourcePropertiesLootCondition(Optional.of(builder.build()));
	}
}
