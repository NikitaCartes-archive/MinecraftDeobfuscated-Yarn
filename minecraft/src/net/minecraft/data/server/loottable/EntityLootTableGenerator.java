package net.minecraft.data.server.loottable;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntitySubPredicateTypes;
import net.minecraft.predicate.entity.SheepPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.DyeColor;

public abstract class EntityLootTableGenerator implements LootTableGenerator {
	protected final RegistryWrapper.WrapperLookup registries;
	private final FeatureSet requiredFeatures;
	private final FeatureSet featureSet;
	private final Map<EntityType<?>, Map<RegistryKey<LootTable>, LootTable.Builder>> lootTables = Maps.<EntityType<?>, Map<RegistryKey<LootTable>, LootTable.Builder>>newHashMap();

	protected final AnyOfLootCondition.Builder createSmeltLootCondition() {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return AnyOfLootCondition.builder(
			EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true))
			),
			EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.DIRECT_ATTACKER,
				EntityPredicate.Builder.create()
					.equipment(
						EntityEquipmentPredicate.Builder.create()
							.mainhand(
								ItemPredicate.Builder.create()
									.subPredicate(
										ItemSubPredicateTypes.ENCHANTMENTS,
										EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(EnchantmentTags.SMELTS_LOOT), NumberRange.IntRange.ANY)))
									)
							)
					)
			)
		);
	}

	protected EntityLootTableGenerator(FeatureSet requiredFeatures, RegistryWrapper.WrapperLookup registries) {
		this(requiredFeatures, requiredFeatures, registries);
	}

	protected EntityLootTableGenerator(FeatureSet requiredFeatures, FeatureSet featureSet, RegistryWrapper.WrapperLookup registries) {
		this.requiredFeatures = requiredFeatures;
		this.featureSet = featureSet;
		this.registries = registries;
	}

	public static LootPool.Builder createForSheep(Map<DyeColor, RegistryKey<LootTable>> colorLootTables) {
		AlternativeEntry.Builder builder = AlternativeEntry.builder();

		for (Entry<DyeColor, RegistryKey<LootTable>> entry : colorLootTables.entrySet()) {
			builder = builder.alternatively(
				LootTableEntry.builder((RegistryKey<LootTable>)entry.getValue())
					.conditionally(
						EntityPropertiesLootCondition.builder(
							LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().typeSpecific(SheepPredicate.unsheared((DyeColor)entry.getKey()))
						)
					)
			);
		}

		return LootPool.builder().with(builder);
	}

	public abstract void generate();

	@Override
	public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
		this.generate();
		Set<RegistryKey<LootTable>> set = new HashSet();
		Registries.ENTITY_TYPE
			.streamEntries()
			.forEach(
				entityType -> {
					EntityType<?> entityType2 = (EntityType<?>)entityType.value();
					if (entityType2.isEnabled(this.requiredFeatures)) {
						Optional<RegistryKey<LootTable>> optional = entityType2.getLootTableKey();
						if (optional.isPresent()) {
							Map<RegistryKey<LootTable>, LootTable.Builder> map = (Map<RegistryKey<LootTable>, LootTable.Builder>)this.lootTables.remove(entityType2);
							if (entityType2.isEnabled(this.featureSet) && (map == null || !map.containsKey(optional.get()))) {
								throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", optional.get(), entityType.registryKey().getValue()));
							}

							if (map != null) {
								map.forEach((tableKey, lootTableBuilder) -> {
									if (!set.add(tableKey)) {
										throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", tableKey, entityType.registryKey().getValue()));
									} else {
										lootTableBiConsumer.accept(tableKey, lootTableBuilder);
									}
								});
							}
						} else {
							Map<RegistryKey<LootTable>, LootTable.Builder> mapx = (Map<RegistryKey<LootTable>, LootTable.Builder>)this.lootTables.remove(entityType2);
							if (mapx != null) {
								throw new IllegalStateException(
									String.format(
										Locale.ROOT,
										"Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
										mapx.keySet().stream().map(registryKey -> registryKey.getValue().toString()).collect(Collectors.joining(",")),
										entityType.registryKey().getValue()
									)
								);
							}
						}
					}
				}
			);
		if (!this.lootTables.isEmpty()) {
			throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.lootTables.keySet());
		}
	}

	protected LootCondition.Builder killedByFrog(RegistryEntryLookup<EntityType<?>> registryLookup) {
		return DamageSourcePropertiesLootCondition.builder(
			DamageSourcePredicate.Builder.create().sourceEntity(EntityPredicate.Builder.create().type(registryLookup, EntityType.FROG))
		);
	}

	protected LootCondition.Builder killedByFrog(RegistryEntryLookup<EntityType<?>> registryLookup, RegistryKey<FrogVariant> frogVariant) {
		return DamageSourcePropertiesLootCondition.builder(
			DamageSourcePredicate.Builder.create()
				.sourceEntity(
					EntityPredicate.Builder.create()
						.type(registryLookup, EntityType.FROG)
						.typeSpecific(EntitySubPredicateTypes.frogVariant(Registries.FROG_VARIANT.getOrThrow(frogVariant)))
				)
		);
	}

	protected void register(EntityType<?> entityType, LootTable.Builder lootTable) {
		this.register(
			entityType,
			(RegistryKey<LootTable>)entityType.getLootTableKey().orElseThrow(() -> new IllegalStateException("Entity " + entityType + " has no loot table")),
			lootTable
		);
	}

	protected void register(EntityType<?> entityType, RegistryKey<LootTable> tableKey, LootTable.Builder lootTable) {
		((Map)this.lootTables.computeIfAbsent(entityType, type -> new HashMap())).put(tableKey, lootTable);
	}
}
