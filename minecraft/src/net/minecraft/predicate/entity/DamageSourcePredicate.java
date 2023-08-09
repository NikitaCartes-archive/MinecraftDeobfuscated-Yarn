package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record DamageSourcePredicate(List<TagPredicate<DamageType>> tagPredicates, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity) {
	public static final Codec<DamageSourcePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(TagPredicate.createCodec(RegistryKeys.DAMAGE_TYPE).listOf(), "tags", List.of())
						.forGetter(DamageSourcePredicate::tagPredicates),
					Codecs.createStrictOptionalFieldCodec(EntityPredicate.CODEC, "direct_entity").forGetter(DamageSourcePredicate::directEntity),
					Codecs.createStrictOptionalFieldCodec(EntityPredicate.CODEC, "source_entity").forGetter(DamageSourcePredicate::sourceEntity)
				)
				.apply(instance, DamageSourcePredicate::new)
	);

	static Optional<DamageSourcePredicate> create(
		List<TagPredicate<DamageType>> tagPredicates, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity
	) {
		return tagPredicates.isEmpty() && directEntity.isEmpty() && sourceEntity.isEmpty()
			? Optional.empty()
			: Optional.of(new DamageSourcePredicate(tagPredicates, directEntity, sourceEntity));
	}

	public boolean test(ServerPlayerEntity player, DamageSource damageSource) {
		return this.test(player.getServerWorld(), player.getPos(), damageSource);
	}

	public boolean test(ServerWorld world, Vec3d pos, DamageSource damageSource) {
		for (TagPredicate<DamageType> tagPredicate : this.tagPredicates) {
			if (!tagPredicate.test(damageSource.getTypeRegistryEntry())) {
				return false;
			}
		}

		return this.directEntity.isPresent() && !((EntityPredicate)this.directEntity.get()).test(world, pos, damageSource.getSource())
			? false
			: !this.sourceEntity.isPresent() || ((EntityPredicate)this.sourceEntity.get()).test(world, pos, damageSource.getAttacker());
	}

	public static Optional<DamageSourcePredicate> fromJson(@Nullable JsonElement json) {
		return json != null && !json.isJsonNull() ? Optional.of(Util.getResult(CODEC.parse(JsonOps.INSTANCE, json), JsonParseException::new)) : Optional.empty();
	}

	public JsonElement toJson() {
		return Util.getResult(CODEC.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new);
	}

	public static class Builder {
		private final ImmutableList.Builder<TagPredicate<DamageType>> tagPredicates = ImmutableList.builder();
		private Optional<EntityPredicate> directEntity = Optional.empty();
		private Optional<EntityPredicate> sourceEntity = Optional.empty();

		public static DamageSourcePredicate.Builder create() {
			return new DamageSourcePredicate.Builder();
		}

		public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> tagPredicate) {
			this.tagPredicates.add(tagPredicate);
			return this;
		}

		public DamageSourcePredicate.Builder directEntity(EntityPredicate.Builder entity) {
			this.directEntity = entity.build();
			return this;
		}

		public DamageSourcePredicate.Builder sourceEntity(EntityPredicate.Builder entity) {
			this.sourceEntity = entity.build();
			return this;
		}

		public Optional<DamageSourcePredicate> build() {
			return DamageSourcePredicate.create(this.tagPredicates.build(), this.directEntity, this.sourceEntity);
		}
	}
}
