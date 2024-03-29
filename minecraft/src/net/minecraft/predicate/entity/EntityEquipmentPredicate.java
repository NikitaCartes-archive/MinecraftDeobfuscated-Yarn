package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.village.raid.Raid;

public record EntityEquipmentPredicate(
	Optional<ItemPredicate> head,
	Optional<ItemPredicate> chest,
	Optional<ItemPredicate> legs,
	Optional<ItemPredicate> feet,
	Optional<ItemPredicate> mainhand,
	Optional<ItemPredicate> offhand
) {
	public static final Codec<EntityEquipmentPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(ItemPredicate.CODEC, "head").forGetter(EntityEquipmentPredicate::head),
					Codecs.createStrictOptionalFieldCodec(ItemPredicate.CODEC, "chest").forGetter(EntityEquipmentPredicate::chest),
					Codecs.createStrictOptionalFieldCodec(ItemPredicate.CODEC, "legs").forGetter(EntityEquipmentPredicate::legs),
					Codecs.createStrictOptionalFieldCodec(ItemPredicate.CODEC, "feet").forGetter(EntityEquipmentPredicate::feet),
					Codecs.createStrictOptionalFieldCodec(ItemPredicate.CODEC, "mainhand").forGetter(EntityEquipmentPredicate::mainhand),
					Codecs.createStrictOptionalFieldCodec(ItemPredicate.CODEC, "offhand").forGetter(EntityEquipmentPredicate::offhand)
				)
				.apply(instance, EntityEquipmentPredicate::new)
	);

	public static EntityEquipmentPredicate ominousBannerOnHead(RegistryEntryLookup<BannerPattern> bannerPatternLookup) {
		return EntityEquipmentPredicate.Builder.create()
			.head(ItemPredicate.Builder.create().items(Items.WHITE_BANNER).component(ComponentPredicate.of(Raid.getOminousBanner(bannerPatternLookup).getComponents())))
			.build();
	}

	public boolean test(@Nullable Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			if (this.head.isPresent() && !((ItemPredicate)this.head.get()).test(livingEntity.getEquippedStack(EquipmentSlot.HEAD))) {
				return false;
			} else if (this.chest.isPresent() && !((ItemPredicate)this.chest.get()).test(livingEntity.getEquippedStack(EquipmentSlot.CHEST))) {
				return false;
			} else if (this.legs.isPresent() && !((ItemPredicate)this.legs.get()).test(livingEntity.getEquippedStack(EquipmentSlot.LEGS))) {
				return false;
			} else if (this.feet.isPresent() && !((ItemPredicate)this.feet.get()).test(livingEntity.getEquippedStack(EquipmentSlot.FEET))) {
				return false;
			} else if (this.mainhand.isPresent() && !((ItemPredicate)this.mainhand.get()).test(livingEntity.getEquippedStack(EquipmentSlot.MAINHAND))) {
				return false;
			} else {
				return !this.offhand.isPresent() || ((ItemPredicate)this.offhand.get()).test(livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
			}
		} else {
			return false;
		}
	}

	public static class Builder {
		private Optional<ItemPredicate> head = Optional.empty();
		private Optional<ItemPredicate> chest = Optional.empty();
		private Optional<ItemPredicate> legs = Optional.empty();
		private Optional<ItemPredicate> feet = Optional.empty();
		private Optional<ItemPredicate> mainhand = Optional.empty();
		private Optional<ItemPredicate> offhand = Optional.empty();

		public static EntityEquipmentPredicate.Builder create() {
			return new EntityEquipmentPredicate.Builder();
		}

		public EntityEquipmentPredicate.Builder head(ItemPredicate.Builder item) {
			this.head = Optional.of(item.build());
			return this;
		}

		public EntityEquipmentPredicate.Builder chest(ItemPredicate.Builder item) {
			this.chest = Optional.of(item.build());
			return this;
		}

		public EntityEquipmentPredicate.Builder legs(ItemPredicate.Builder item) {
			this.legs = Optional.of(item.build());
			return this;
		}

		public EntityEquipmentPredicate.Builder feet(ItemPredicate.Builder item) {
			this.feet = Optional.of(item.build());
			return this;
		}

		public EntityEquipmentPredicate.Builder mainhand(ItemPredicate.Builder item) {
			this.mainhand = Optional.of(item.build());
			return this;
		}

		public EntityEquipmentPredicate.Builder offhand(ItemPredicate.Builder item) {
			this.offhand = Optional.of(item.build());
			return this;
		}

		public EntityEquipmentPredicate build() {
			return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
		}
	}
}
