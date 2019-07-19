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
	private static final EntityPredicate.Builder field_11344 = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
	private static final Set<EntityType<?>> field_19339 = ImmutableSet.of(
		EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER
	);
	private final Map<Identifier, LootTable.Builder> field_16543 = Maps.<Identifier, LootTable.Builder>newHashMap();

	private static LootTable.Builder method_10401(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)))
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(LootTableEntry.builder(EntityType.SHEEP.getLootTableId())));
	}

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.method_16368(EntityType.ARMOR_STAND, LootTable.builder());
		this.method_16368(EntityType.BAT, LootTable.builder());
		this.method_16368(
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
		this.method_16368(
			EntityType.CAT,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.STRING).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
		);
		this.method_16368(
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
		this.method_16368(
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
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.COD,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
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
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
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
		this.method_16368(
			EntityType.DOLPHIN,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
		);
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
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
		this.method_16368(EntityType.ENDER_DRAGON, LootTable.builder());
		this.method_16368(
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
		this.method_16368(EntityType.ENDERMITE, LootTable.builder());
		this.method_16368(
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
		this.method_16368(EntityType.FOX, LootTable.builder());
		this.method_16368(
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
		this.method_16368(EntityType.GIANT, LootTable.builder());
		this.method_16368(
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
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
			EntityType.RAVAGER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SADDLE).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(EntityType.ILLUSIONER, LootTable.builder());
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.OCELOT, LootTable.builder());
		this.method_16368(
			EntityType.PANDA,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.BAMBOO).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
			EntityType.PIG,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PORKCHOP)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.PILLAGER, LootTable.builder());
		this.method_16368(EntityType.PLAYER, LootTable.builder());
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
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
		this.method_16368(
			EntityType.SALMON,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SALMON)
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.withCondition(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.SHEEP,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.MUTTON)
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.withFunction(FurnaceSmeltLootFunction.builder().withCondition(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16369(LootTables.BLACK_SHEEP_ENTITY, method_10401(Blocks.BLACK_WOOL));
		this.method_16369(LootTables.BLUE_SHEEP_ENTITY, method_10401(Blocks.BLUE_WOOL));
		this.method_16369(LootTables.BROWN_SHEEP_ENTITY, method_10401(Blocks.BROWN_WOOL));
		this.method_16369(LootTables.CYAN_SHEEP_ENTITY, method_10401(Blocks.CYAN_WOOL));
		this.method_16369(LootTables.GRAY_SHEEP_ENTITY, method_10401(Blocks.GRAY_WOOL));
		this.method_16369(LootTables.GREEN_SHEEP_ENTITY, method_10401(Blocks.GREEN_WOOL));
		this.method_16369(LootTables.LIGHT_BLUE_SHEEP_ENTITY, method_10401(Blocks.LIGHT_BLUE_WOOL));
		this.method_16369(LootTables.LIGHT_GRAY_SHEEP_ENTITY, method_10401(Blocks.LIGHT_GRAY_WOOL));
		this.method_16369(LootTables.LIME_SHEEP_ENTITY, method_10401(Blocks.LIME_WOOL));
		this.method_16369(LootTables.MAGENTA_SHEEP_ENTITY, method_10401(Blocks.MAGENTA_WOOL));
		this.method_16369(LootTables.ORANGE_SHEEP_ENTITY, method_10401(Blocks.ORANGE_WOOL));
		this.method_16369(LootTables.PINK_SHEEP_ENTITY, method_10401(Blocks.PINK_WOOL));
		this.method_16369(LootTables.PURPLE_SHEEP_ENTITY, method_10401(Blocks.PURPLE_WOOL));
		this.method_16369(LootTables.RED_SHEEP_ENTITY, method_10401(Blocks.RED_WOOL));
		this.method_16369(LootTables.WHITE_SHEEP_ENTITY, method_10401(Blocks.WHITE_WOOL));
		this.method_16369(LootTables.YELLOW_SHEEP_ENTITY, method_10401(Blocks.YELLOW_WOOL));
		this.method_16368(
			EntityType.SHULKER,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SHULKER_SHELL))
						.withCondition(RandomChanceWithLootingLootCondition.builder(0.5F, 0.0625F))
				)
		);
		this.method_16368(EntityType.SILVERFISH, LootTable.builder());
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
			EntityType.SNOW_GOLEM,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SNOWBALL).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 15.0F))))
				)
		);
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(EntityType.VEX, LootTable.builder());
		this.method_16368(EntityType.VILLAGER, LootTable.builder());
		this.method_16368(EntityType.WANDERING_TRADER, LootTable.builder());
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(EntityType.WITHER, LootTable.builder());
		this.method_16368(
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
		this.method_16368(EntityType.WOLF, LootTable.builder());
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
		this.method_16368(
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
			if (!field_19339.contains(entityType) && entityType.getCategory() == EntityCategory.MISC) {
				if (identifier != LootTables.EMPTY && this.field_16543.remove(identifier) != null) {
					throw new IllegalStateException(
						String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", identifier, Registry.ENTITY_TYPE.getId(entityType))
					);
				}
			} else if (identifier != LootTables.EMPTY && set.add(identifier)) {
				LootTable.Builder builder = (LootTable.Builder)this.field_16543.remove(identifier);
				if (builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.ENTITY_TYPE.getId(entityType)));
				}

				biConsumer.accept(identifier, builder);
			}
		}

		this.field_16543.forEach(biConsumer::accept);
	}

	private void method_16368(EntityType<?> entityType, LootTable.Builder builder) {
		this.method_16369(entityType.getLootTableId(), builder);
	}

	private void method_16369(Identifier identifier, LootTable.Builder builder) {
		this.field_16543.put(identifier, builder);
	}
}
