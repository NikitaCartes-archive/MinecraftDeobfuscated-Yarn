package net.minecraft.data.server.loottable.vanilla;

import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.EntityLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.SlimePredicate;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;

public class VanillaEntityLootTableGenerator extends EntityLootTableGenerator {
	public VanillaEntityLootTableGenerator() {
		super(FeatureFlags.FEATURE_MANAGER.getFeatureSet());
	}

	@Override
	public void generate() {
		this.register(EntityType.ALLAY, LootTable.builder());
		this.register(EntityType.ARMOR_STAND, LootTable.builder());
		this.register(EntityType.AXOLOTL, LootTable.builder());
		this.register(EntityType.BAT, LootTable.builder());
		this.register(EntityType.BEE, LootTable.builder());
		this.register(
			EntityType.BLAZE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BLAZE_ROD)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.CAT,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F))))
				)
		);
		this.register(EntityType.CAMEL, LootTable.builder());
		this.register(
			EntityType.CAVE_SPIDER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.STRING)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.SPIDER_EYE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.CHICKEN,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.FEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.CHICKEN)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.COD,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.COD)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BONE_MEAL))
						.conditionally(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.COW,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BEEF)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.CREEPER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.GUNPOWDER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.with(TagEntry.expandBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS))
						.conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.create().type(EntityTypeTags.SKELETONS)))
				)
		);
		this.register(
			EntityType.DOLPHIN,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.COD)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
				)
		);
		this.register(
			EntityType.DONKEY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.DROWNED,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.COPPER_INGOT))
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.11F, 0.02F))
				)
		);
		this.register(EntityType.ELDER_GUARDIAN, createElderGuardianTableBuilder());
		this.register(EntityType.ENDER_DRAGON, LootTable.builder());
		this.register(
			EntityType.ENDERMAN,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ENDER_PEARL)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.ENDERMITE, LootTable.builder());
		this.register(
			EntityType.EVOKER,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.TOTEM_OF_UNDYING)))
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.EMERALD)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(EntityType.FOX, LootTable.builder());
		this.register(EntityType.FROG, LootTable.builder());
		this.register(
			EntityType.GHAST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.GHAST_TEAR)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.GUNPOWDER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.GIANT, LootTable.builder());
		this.register(
			EntityType.GLOW_SQUID,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.GLOW_INK_SAC)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.GOAT, LootTable.builder());
		this.register(
			EntityType.GUARDIAN,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.PRISMARINE_SHARD)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.COD)
								.weight(2)
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
						.with(ItemEntry.builder(Items.PRISMARINE_CRYSTALS).weight(2).apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F))))
						.with(EmptyEntry.builder())
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.HORSE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.HUSK,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT))
						.with(ItemEntry.builder(Items.CARROT))
						.with(
							ItemEntry.builder(Items.POTATO)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.RAVAGER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.SADDLE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
		);
		this.register(EntityType.ILLUSIONER, LootTable.builder());
		this.register(
			EntityType.IRON_GOLEM,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Blocks.POPPY).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 5.0F))))
				)
		);
		this.register(
			EntityType.LLAMA,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.MAGMA_CUBE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.MAGMA_CREAM)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-2.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.conditionally(this.killedByFrog().invert())
								.conditionally(
									EntityPropertiesLootCondition.builder(
										LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().typeSpecific(SlimePredicate.of(NumberRange.IntRange.atLeast(2)))
									)
								)
						)
						.with(
							ItemEntry.builder(Items.PEARLESCENT_FROGLIGHT)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.conditionally(this.killedByFrog(FrogVariant.WARM))
						)
						.with(
							ItemEntry.builder(Items.VERDANT_FROGLIGHT)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.conditionally(this.killedByFrog(FrogVariant.COLD))
						)
						.with(
							ItemEntry.builder(Items.OCHRE_FROGLIGHT)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.conditionally(this.killedByFrog(FrogVariant.TEMPERATE))
						)
				)
		);
		this.register(
			EntityType.MULE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.MOOSHROOM,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BEEF)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.OCELOT, LootTable.builder());
		this.register(
			EntityType.PANDA,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Blocks.BAMBOO).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
		);
		this.register(
			EntityType.PARROT,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.FEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.PHANTOM,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.PHANTOM_MEMBRANE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.PIG,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.PORKCHOP)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.PILLAGER, LootTable.builder());
		this.register(EntityType.PLAYER, LootTable.builder());
		this.register(
			EntityType.POLAR_BEAR,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.COD)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.weight(3)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(
							ItemEntry.builder(Items.SALMON)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.PUFFERFISH,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.PUFFERFISH).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BONE_MEAL))
						.conditionally(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.RABBIT,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.RABBIT_HIDE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.RABBIT)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.RABBIT_FOOT))
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.1F, 0.03F))
				)
		);
		this.register(
			EntityType.SALMON,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.SALMON)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BONE_MEAL))
						.conditionally(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.SHEEP,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.MUTTON)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.SHEEP, LootTables.BLACK_SHEEP_ENTITY, createForSheep(Blocks.BLACK_WOOL));
		this.register(EntityType.SHEEP, LootTables.BLUE_SHEEP_ENTITY, createForSheep(Blocks.BLUE_WOOL));
		this.register(EntityType.SHEEP, LootTables.BROWN_SHEEP_ENTITY, createForSheep(Blocks.BROWN_WOOL));
		this.register(EntityType.SHEEP, LootTables.CYAN_SHEEP_ENTITY, createForSheep(Blocks.CYAN_WOOL));
		this.register(EntityType.SHEEP, LootTables.GRAY_SHEEP_ENTITY, createForSheep(Blocks.GRAY_WOOL));
		this.register(EntityType.SHEEP, LootTables.GREEN_SHEEP_ENTITY, createForSheep(Blocks.GREEN_WOOL));
		this.register(EntityType.SHEEP, LootTables.LIGHT_BLUE_SHEEP_ENTITY, createForSheep(Blocks.LIGHT_BLUE_WOOL));
		this.register(EntityType.SHEEP, LootTables.LIGHT_GRAY_SHEEP_ENTITY, createForSheep(Blocks.LIGHT_GRAY_WOOL));
		this.register(EntityType.SHEEP, LootTables.LIME_SHEEP_ENTITY, createForSheep(Blocks.LIME_WOOL));
		this.register(EntityType.SHEEP, LootTables.MAGENTA_SHEEP_ENTITY, createForSheep(Blocks.MAGENTA_WOOL));
		this.register(EntityType.SHEEP, LootTables.ORANGE_SHEEP_ENTITY, createForSheep(Blocks.ORANGE_WOOL));
		this.register(EntityType.SHEEP, LootTables.PINK_SHEEP_ENTITY, createForSheep(Blocks.PINK_WOOL));
		this.register(EntityType.SHEEP, LootTables.PURPLE_SHEEP_ENTITY, createForSheep(Blocks.PURPLE_WOOL));
		this.register(EntityType.SHEEP, LootTables.RED_SHEEP_ENTITY, createForSheep(Blocks.RED_WOOL));
		this.register(EntityType.SHEEP, LootTables.WHITE_SHEEP_ENTITY, createForSheep(Blocks.WHITE_WOOL));
		this.register(EntityType.SHEEP, LootTables.YELLOW_SHEEP_ENTITY, createForSheep(Blocks.YELLOW_WOOL));
		this.register(
			EntityType.SHULKER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.SHULKER_SHELL))
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.5F, 0.0625F))
				)
		);
		this.register(EntityType.SILVERFISH, LootTable.builder());
		this.register(
			EntityType.SKELETON,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BONE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.SKELETON_HORSE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BONE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.SLIME,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.SLIME_BALL)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.conditionally(this.killedByFrog().invert())
						)
						.with(ItemEntry.builder(Items.SLIME_BALL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).conditionally(this.killedByFrog()))
						.conditionally(
							EntityPropertiesLootCondition.builder(
								LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().typeSpecific(SlimePredicate.of(NumberRange.IntRange.exactly(1)))
							)
						)
				)
		);
		this.register(EntityType.SNIFFER, LootTable.builder());
		this.register(
			EntityType.SNOW_GOLEM,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.SNOWBALL).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 15.0F))))
				)
		);
		this.register(
			EntityType.SPIDER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.STRING)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.SPIDER_EYE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.SQUID,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.INK_SAC)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.STRAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BONE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)).withLimit(1))
								.apply(SetPotionLootFunction.builder(Potions.SLOWNESS))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.STRIDER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.STRING)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.TADPOLE, LootTable.builder());
		this.register(
			EntityType.TRADER_LLAMA,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.TROPICAL_FISH,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.TROPICAL_FISH).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BONE_MEAL))
						.conditionally(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.TURTLE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Blocks.SEAGRASS)
								.weight(3)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BOWL))
						.conditionally(
							DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().tag(TagPredicate.expected(DamageTypeTags.IS_LIGHTNING)))
						)
				)
		);
		this.register(EntityType.VEX, LootTable.builder());
		this.register(EntityType.VILLAGER, LootTable.builder());
		this.register(
			EntityType.WARDEN, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.SCULK_CATALYST)))
		);
		this.register(EntityType.WANDERING_TRADER, LootTable.builder());
		this.register(
			EntityType.VINDICATOR,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.EMERALD)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.WITCH,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(
							ItemEntry.builder(Items.GLOWSTONE_DUST)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(
							ItemEntry.builder(Items.SUGAR)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(
							ItemEntry.builder(Items.REDSTONE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(
							ItemEntry.builder(Items.SPIDER_EYE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(
							ItemEntry.builder(Items.GLASS_BOTTLE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(
							ItemEntry.builder(Items.GUNPOWDER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(
							ItemEntry.builder(Items.STICK)
								.weight(2)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.WITHER, LootTable.builder());
		this.register(
			EntityType.WITHER_SKELETON,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.COAL)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.BONE)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Blocks.WITHER_SKELETON_SKULL))
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(EntityType.WOLF, LootTable.builder());
		this.register(
			EntityType.ZOGLIN,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.ZOMBIE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT))
						.with(ItemEntry.builder(Items.CARROT))
						.with(
							ItemEntry.builder(Items.POTATO)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.ZOMBIE_HORSE,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.ZOMBIFIED_PIGLIN,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.GOLD_NUGGET)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.GOLD_INGOT))
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.HOGLIN,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.PORKCHOP)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F)))
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LEATHER)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.PIGLIN, LootTable.builder());
		this.register(EntityType.PIGLIN_BRUTE, LootTable.builder());
		this.register(
			EntityType.ZOMBIE_VILLAGER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
								.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT))
						.with(ItemEntry.builder(Items.CARROT))
						.with(
							ItemEntry.builder(Items.POTATO)
								.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
						)
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
	}

	public static LootTable.Builder createElderGuardianTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.PRISMARINE_SHARD)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
							.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
					)
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.COD)
							.weight(3)
							.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
							.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
					)
					.with(ItemEntry.builder(Items.PRISMARINE_CRYSTALS).weight(2).apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F))))
					.with(EmptyEntry.builder())
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(ItemEntry.builder(Blocks.WET_SPONGE))
					.conditionally(KilledByPlayerLootCondition.builder())
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY)
							.apply(FurnaceSmeltLootFunction.builder().conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE)))
					)
					.conditionally(KilledByPlayerLootCondition.builder())
					.conditionally(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(4))
					.with(ItemEntry.builder(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}
}
