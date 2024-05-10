package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record DamageSourcePredicate(
	List<TagPredicate<DamageType>> tags, Optional<EntityPredicate> directEntity, Optional<EntityPredicate> sourceEntity, Optional<Boolean> isDirect
) {
	public static final Codec<DamageSourcePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					TagPredicate.createCodec(RegistryKeys.DAMAGE_TYPE).listOf().optionalFieldOf("tags", List.of()).forGetter(DamageSourcePredicate::tags),
					EntityPredicate.CODEC.optionalFieldOf("direct_entity").forGetter(DamageSourcePredicate::directEntity),
					EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamageSourcePredicate::sourceEntity),
					Codec.BOOL.optionalFieldOf("is_direct").forGetter(DamageSourcePredicate::isDirect)
				)
				.apply(instance, DamageSourcePredicate::new)
	);

	public boolean test(ServerPlayerEntity player, DamageSource damageSource) {
		return this.test(player.getServerWorld(), player.getPos(), damageSource);
	}

	public boolean test(ServerWorld world, Vec3d pos, DamageSource damageSource) {
		for (TagPredicate<DamageType> tagPredicate : this.tags) {
			if (!tagPredicate.test(damageSource.getTypeRegistryEntry())) {
				return false;
			}
		}

		if (this.directEntity.isPresent() && !((EntityPredicate)this.directEntity.get()).test(world, pos, damageSource.getSource())) {
			return false;
		} else {
			return this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).test(world, pos, damageSource.getAttacker())
				? false
				: !this.isDirect.isPresent() || (Boolean)this.isDirect.get() == damageSource.isDirect();
		}
	}

	public static class Builder {
		private final ImmutableList.Builder<TagPredicate<DamageType>> tagPredicates = ImmutableList.builder();
		private Optional<EntityPredicate> directEntity = Optional.empty();
		private Optional<EntityPredicate> sourceEntity = Optional.empty();
		private Optional<Boolean> isDirect = Optional.empty();

		public static DamageSourcePredicate.Builder create() {
			return new DamageSourcePredicate.Builder();
		}

		public DamageSourcePredicate.Builder tag(TagPredicate<DamageType> tagPredicate) {
			this.tagPredicates.add(tagPredicate);
			return this;
		}

		public DamageSourcePredicate.Builder directEntity(EntityPredicate.Builder entity) {
			this.directEntity = Optional.of(entity.build());
			return this;
		}

		public DamageSourcePredicate.Builder sourceEntity(EntityPredicate.Builder entity) {
			this.sourceEntity = Optional.of(entity.build());
			return this;
		}

		public DamageSourcePredicate.Builder isDirect(boolean direct) {
			this.isDirect = Optional.of(direct);
			return this;
		}

		public DamageSourcePredicate build() {
			return new DamageSourcePredicate(this.tagPredicates.build(), this.directEntity, this.sourceEntity, this.isDirect);
		}
	}
}
