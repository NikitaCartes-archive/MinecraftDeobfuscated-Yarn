package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
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
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class EntityLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	private static final EntityPredicate.Builder NEEDS_ENTITY_ON_FIRE = EntityPredicate.Builder.create()
		.flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
	private static final Set<EntityType<?>> ENTITY_TYPES_IN_MISC_CATEGORY_TO_CHECK = ImmutableSet.of(
		EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER
	);
	private final Map<Identifier, LootTable.Builder> lootTables = Maps.<Identifier, LootTable.Builder>newHashMap();

	private static LootTable.Builder createForSheep(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)))
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(LootTableEntry.builder(EntityType.SHEEP.getLootTableId())));
	}

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.register(EntityType.ARMOR_STAND, LootTable.builder());
		this.register(EntityType.BAT, LootTable.builder());
		this.register(EntityType.BEE, LootTable.builder());
		this.register(
			EntityType.BLAZE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BLAZE_ROD)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.CAT,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.STRING).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
		);
		this.register(
			EntityType.CAVE_SPIDER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.STRING)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SPIDER_EYE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.CHICKEN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.FEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.CHICKEN)
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.COD,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.COW,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BEEF)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.CREEPER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GUNPOWDER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withEntry(TagEntry.builder(ItemTags.MUSIC_DISCS))
						.withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.create().type(EntityTypeTags.SKELETONS)))
				)
		);
		this.register(
			EntityType.DOLPHIN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
						)
				)
		);
		this.register(
			EntityType.DONKEY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.DROWNED,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.05F, 0.01F))
				)
		);
		this.register(
			EntityType.ELDER_GUARDIAN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_SHARD)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.setWeight(3)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
						)
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_CRYSTALS).setWeight(2).withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.WET_SPONGE))
						.withCondition(KilledByPlayerLootCondition.builder())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(EntityType.ENDER_DRAGON, LootTable.builder());
		this.register(
			EntityType.ENDERMAN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ENDER_PEARL)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.ENDERMITE, LootTable.builder());
		this.register(
			EntityType.EVOKER,
			LootTable.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.TOTEM_OF_UNDYING)))
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.EMERALD)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(EntityType.FOX, LootTable.builder());
		this.register(
			EntityType.GHAST,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GHAST_TEAR)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GUNPOWDER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.GIANT, LootTable.builder());
		this.register(
			EntityType.GUARDIAN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_SHARD)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.setWeight(2)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
						)
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_CRYSTALS).setWeight(2).withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.HORSE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.HUSK,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT))
						.withEntry(ItemEntry.builder(Items.CARROT))
						.withEntry(ItemEntry.builder(Items.POTATO))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.RAVAGER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SADDLE).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.register(EntityType.ILLUSIONER, LootTable.builder());
		this.register(
			EntityType.IRON_GOLEM,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.POPPY).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
		);
		this.register(
			EntityType.LLAMA,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.MAGMA_CUBE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.MAGMA_CREAM)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-2.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.MULE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.MOOSHROOM,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BEEF)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.OCELOT, LootTable.builder());
		this.register(
			EntityType.PANDA,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.BAMBOO).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.register(
			EntityType.PARROT,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.FEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.PHANTOM,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PHANTOM_MEMBRANE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.PIG,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PORKCHOP)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.PILLAGER, LootTable.builder());
		this.register(EntityType.PLAYER, LootTable.builder());
		this.register(
			EntityType.POLAR_BEAR,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.setWeight(3)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.SALMON)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.PUFFERFISH,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.PUFFERFISH).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.RABBIT,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.RABBIT_HIDE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.RABBIT)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.RABBIT_FOOT))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.1F, 0.03F))
				)
		);
		this.register(
			EntityType.SALMON,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SALMON)
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.SHEEP,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.MUTTON)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(LootTables.BLACK_SHEEP_ENTITY, createForSheep(Blocks.BLACK_WOOL));
		this.register(LootTables.BLUE_SHEEP_ENTITY, createForSheep(Blocks.BLUE_WOOL));
		this.register(LootTables.BROWN_SHEEP_ENTITY, createForSheep(Blocks.BROWN_WOOL));
		this.register(LootTables.CYAN_SHEEP_ENTITY, createForSheep(Blocks.CYAN_WOOL));
		this.register(LootTables.GRAY_SHEEP_ENTITY, createForSheep(Blocks.GRAY_WOOL));
		this.register(LootTables.GREEN_SHEEP_ENTITY, createForSheep(Blocks.GREEN_WOOL));
		this.register(LootTables.LIGHT_BLUE_SHEEP_ENTITY, createForSheep(Blocks.LIGHT_BLUE_WOOL));
		this.register(LootTables.LIGHT_GRAY_SHEEP_ENTITY, createForSheep(Blocks.LIGHT_GRAY_WOOL));
		this.register(LootTables.LIME_SHEEP_ENTITY, createForSheep(Blocks.LIME_WOOL));
		this.register(LootTables.MAGENTA_SHEEP_ENTITY, createForSheep(Blocks.MAGENTA_WOOL));
		this.register(LootTables.ORANGE_SHEEP_ENTITY, createForSheep(Blocks.ORANGE_WOOL));
		this.register(LootTables.PINK_SHEEP_ENTITY, createForSheep(Blocks.PINK_WOOL));
		this.register(LootTables.PURPLE_SHEEP_ENTITY, createForSheep(Blocks.PURPLE_WOOL));
		this.register(LootTables.RED_SHEEP_ENTITY, createForSheep(Blocks.RED_WOOL));
		this.register(LootTables.WHITE_SHEEP_ENTITY, createForSheep(Blocks.WHITE_WOOL));
		this.register(LootTables.YELLOW_SHEEP_ENTITY, createForSheep(Blocks.YELLOW_WOOL));
		this.register(
			EntityType.SHULKER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SHULKER_SHELL))
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.5F, 0.0625F))
				)
		);
		this.register(EntityType.SILVERFISH, LootTable.builder());
		this.register(
			EntityType.SKELETON,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ARROW)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.SKELETON_HORSE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.SLIME,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SLIME_BALL)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.SNOW_GOLEM,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SNOWBALL).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 15.0F))))
				)
		);
		this.register(
			EntityType.SPIDER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.STRING)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SPIDER_EYE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.SQUID,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.INK_SAC)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.STRAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ARROW)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)).withLimit(1))
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.TRADER_LLAMA,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.TROPICAL_FISH,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.TROPICAL_FISH).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.register(
			EntityType.TURTLE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Blocks.SEAGRASS)
								.setWeight(3)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BOWL))
						.withCondition(DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().lightning(true)))
				)
		);
		this.register(EntityType.VEX, LootTable.builder());
		this.register(EntityType.VILLAGER, LootTable.builder());
		this.register(EntityType.WANDERING_TRADER, LootTable.builder());
		this.register(
			EntityType.VINDICATOR,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.EMERALD)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.builder())
				)
		);
		this.register(
			EntityType.WITCH,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(
							ItemEntry.builder(Items.GLOWSTONE_DUST)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.SUGAR)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.REDSTONE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.SPIDER_EYE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.GLASS_BOTTLE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.GUNPOWDER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.STICK)
								.setWeight(2)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.WITHER, LootTable.builder());
		this.register(
			EntityType.WITHER_SKELETON,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COAL)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.WITHER_SKELETON_SKULL))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(EntityType.WOLF, LootTable.builder());
		this.register(
			EntityType.ZOMBIE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT))
						.withEntry(ItemEntry.builder(Items.CARROT))
						.withEntry(ItemEntry.builder(Items.POTATO))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.ZOMBIE_HORSE,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(
			EntityType.ZOMBIE_PIGMAN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GOLD_NUGGET)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.register(
			EntityType.HOGLIN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PORKCHOP)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F)))
								.withFunction(
									FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, NEEDS_ENTITY_ON_FIRE))
								)
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.register(EntityType.PIGLIN, LootTable.builder());
		this.register(
			EntityType.ZOMBIE_VILLAGER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT))
						.withEntry(ItemEntry.builder(Items.CARROT))
						.withEntry(ItemEntry.builder(Items.POTATO))
						.withCondition(KilledByPlayerLootCondition.builder())
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
			Identifier identifier = entityType.getLootTableId();
			if (!ENTITY_TYPES_IN_MISC_CATEGORY_TO_CHECK.contains(entityType) && entityType.getCategory() == EntityCategory.MISC) {
				if (identifier != LootTables.EMPTY && this.lootTables.remove(identifier) != null) {
					throw new IllegalStateException(
						String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", identifier, Registry.ENTITY_TYPE.getId(entityType))
					);
				}
			} else if (identifier != LootTables.EMPTY && set.add(identifier)) {
				LootTable.Builder builder = (LootTable.Builder)this.lootTables.remove(identifier);
				if (builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.ENTITY_TYPE.getId(entityType)));
				}

				biConsumer.accept(identifier, builder);
			}
		}

		this.lootTables.forEach(biConsumer::accept);
	}

	private void register(EntityType<?> entityType, LootTable.Builder builder) {
		this.register(entityType.getLootTableId(), builder);
	}

	private void register(Identifier identifier, LootTable.Builder builder) {
		this.lootTables.put(identifier, builder);
	}
}
