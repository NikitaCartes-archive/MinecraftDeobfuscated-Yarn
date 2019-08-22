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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.world.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.world.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.world.loot.condition.RandomChanceLootCondition;
import net.minecraft.world.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.EmptyEntry;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.entry.LootTableEntry;
import net.minecraft.world.loot.entry.TagEntry;
import net.minecraft.world.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.world.loot.function.LootingEnchantLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetNbtLootFunction;

public class EntityLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	private static final EntityPredicate.Builder field_11344 = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
	private static final Set<EntityType<?>> field_19339 = ImmutableSet.of(
		EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER
	);
	private final Map<Identifier, LootSupplier.Builder> field_16543 = Maps.<Identifier, LootSupplier.Builder>newHashMap();

	private static LootSupplier.Builder method_10401(ItemConvertible itemConvertible) {
		return LootSupplier.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)))
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(LootTableEntry.builder(EntityType.SHEEP.getLootTableId())));
	}

	public void method_10400(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		this.method_16368(EntityType.ARMOR_STAND, LootSupplier.builder());
		this.method_16368(EntityType.BAT, LootSupplier.builder());
		this.method_16368(EntityType.BEE, LootSupplier.builder());
		this.method_16368(
			EntityType.BLAZE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BLAZE_ROD)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.CAT,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.STRING).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
		);
		this.method_16368(
			EntityType.CAVE_SPIDER,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.STRING)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SPIDER_EYE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.CHICKEN,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.FEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.CHICKEN)
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.COD,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.COW,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BEEF)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.CREEPER,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GUNPOWDER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withEntry(TagEntry.builder(ItemTags.MUSIC_DISCS))
						.method_356(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.create().type(EntityTypeTags.SKELETONS)))
				)
		);
		this.method_16368(
			EntityType.DOLPHIN,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
		);
		this.method_16368(
			EntityType.DONKEY,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.DROWNED,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.05F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ELDER_GUARDIAN,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_SHARD)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.setWeight(3)
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_CRYSTALS).setWeight(2).method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.WET_SPONGE))
						.method_356(KilledByPlayerLootCondition.builder())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.ENDER_DRAGON, LootSupplier.builder());
		this.method_16368(
			EntityType.ENDERMAN,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ENDER_PEARL)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.ENDERMITE, LootSupplier.builder());
		this.method_16368(
			EntityType.EVOKER,
			LootSupplier.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.TOTEM_OF_UNDYING)))
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.EMERALD)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(EntityType.FOX, LootSupplier.builder());
		this.method_16368(
			EntityType.GHAST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GHAST_TEAR)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GUNPOWDER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.GIANT, LootSupplier.builder());
		this.method_16368(
			EntityType.GUARDIAN,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_SHARD)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.setWeight(2)
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
						.withEntry(
							ItemEntry.builder(Items.PRISMARINE_CRYSTALS).setWeight(2).method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.HORSE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.HUSK,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT))
						.withEntry(ItemEntry.builder(Items.CARROT))
						.withEntry(ItemEntry.builder(Items.POTATO))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.RAVAGER,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SADDLE).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(EntityType.ILLUSIONER, LootSupplier.builder());
		this.method_16368(
			EntityType.IRON_GOLEM,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.POPPY).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
		);
		this.method_16368(
			EntityType.LLAMA,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MAGMA_CUBE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.MAGMA_CREAM)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-2.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MULE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MOOSHROOM,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BEEF)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.OCELOT, LootSupplier.builder());
		this.method_16368(
			EntityType.PANDA,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.BAMBOO).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(
			EntityType.PARROT,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.FEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.PHANTOM,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PHANTOM_MEMBRANE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.PIG,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.PORKCHOP)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.PILLAGER, LootSupplier.builder());
		this.method_16368(EntityType.PLAYER, LootSupplier.builder());
		this.method_16368(
			EntityType.POLAR_BEAR,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COD)
								.setWeight(3)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.SALMON)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.PUFFERFISH,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.PUFFERFISH).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.RABBIT,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.RABBIT_HIDE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.RABBIT)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.RABBIT_FOOT))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.1F, 0.03F))
				)
		);
		this.method_16368(
			EntityType.SALMON,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SALMON)
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.SHEEP,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.MUTTON)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16369(LootTables.SHEEP_BLACK_ENTITIE, method_10401(Blocks.BLACK_WOOL));
		this.method_16369(LootTables.SHEEP_BLUE_ENTITIE, method_10401(Blocks.BLUE_WOOL));
		this.method_16369(LootTables.SHEEP_BROWN_ENTITIE, method_10401(Blocks.BROWN_WOOL));
		this.method_16369(LootTables.SHEEP_CYAN_ENTITIE, method_10401(Blocks.CYAN_WOOL));
		this.method_16369(LootTables.SHEEP_GRAY_ENTITIE, method_10401(Blocks.GRAY_WOOL));
		this.method_16369(LootTables.SHEEP_GREEN_ENTITIE, method_10401(Blocks.GREEN_WOOL));
		this.method_16369(LootTables.SHEEP_LIGHT_BLUE_ENTITIE, method_10401(Blocks.LIGHT_BLUE_WOOL));
		this.method_16369(LootTables.SHEEP_LIGHT_GRAY_ENTITIE, method_10401(Blocks.LIGHT_GRAY_WOOL));
		this.method_16369(LootTables.SHEEP_LIME_ENTITIE, method_10401(Blocks.LIME_WOOL));
		this.method_16369(LootTables.SHEEP_MAGENTA_ENTITIE, method_10401(Blocks.MAGENTA_WOOL));
		this.method_16369(LootTables.SHEEP_ORANGE_ENTITIE, method_10401(Blocks.ORANGE_WOOL));
		this.method_16369(LootTables.SHEEP_PINK_ENTITIE, method_10401(Blocks.PINK_WOOL));
		this.method_16369(LootTables.SHEEP_PURPLE_ENTITIE, method_10401(Blocks.PURPLE_WOOL));
		this.method_16369(LootTables.SHEEP_RED_ENTITIE, method_10401(Blocks.RED_WOOL));
		this.method_16369(LootTables.SHEEP_WHITE_ENTITIE, method_10401(Blocks.WHITE_WOOL));
		this.method_16369(LootTables.SHEEP_YELLOW_ENTITIE, method_10401(Blocks.YELLOW_WOOL));
		this.method_16368(
			EntityType.SHULKER,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SHULKER_SHELL))
						.method_356(RandomChanceWithLootingLootCondition.builder(0.5F, 0.0625F))
				)
		);
		this.method_16368(EntityType.SILVERFISH, LootSupplier.builder());
		this.method_16368(
			EntityType.SKELETON,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SKELETON_HORSE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SLIME,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SLIME_BALL)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SNOW_GOLEM,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.SNOWBALL).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 15.0F))))
				)
		);
		this.method_16368(
			EntityType.SPIDER,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.STRING)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.SPIDER_EYE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.SQUID,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.INK_SAC)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.STRAY,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)).withLimit(1))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.TRADER_LLAMA,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.LEATHER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.TROPICAL_FISH,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.TROPICAL_FISH).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BONE_MEAL))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.TURTLE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Blocks.SEAGRASS)
								.setWeight(3)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BOWL))
						.method_356(DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().lightning(true)))
				)
		);
		this.method_16368(EntityType.VEX, LootSupplier.builder());
		this.method_16368(EntityType.VILLAGER, LootSupplier.builder());
		this.method_16368(EntityType.WANDERING_TRADER, LootSupplier.builder());
		this.method_16368(
			EntityType.VINDICATOR,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.EMERALD)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.WITCH,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(
							ItemEntry.builder(Items.GLOWSTONE_DUST)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.SUGAR)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.REDSTONE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.SPIDER_EYE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.GLASS_BOTTLE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.GUNPOWDER)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.STICK)
								.setWeight(2)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.WITHER, LootSupplier.builder());
		this.method_16368(
			EntityType.WITHER_SKELETON,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.COAL)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.BONE)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.WITHER_SKELETON_SKULL))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.WOLF, LootSupplier.builder());
		this.method_16368(
			EntityType.ZOMBIE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT))
						.withEntry(ItemEntry.builder(Items.CARROT))
						.withEntry(ItemEntry.builder(Items.POTATO))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_HORSE,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_PIGMAN,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.GOLD_NUGGET)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_VILLAGER,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.ROTTEN_FLESH)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT))
						.withEntry(ItemEntry.builder(Items.CARROT))
						.withEntry(ItemEntry.builder(Items.POTATO))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
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
				LootSupplier.Builder builder = (LootSupplier.Builder)this.field_16543.remove(identifier);
				if (builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.ENTITY_TYPE.getId(entityType)));
				}

				biConsumer.accept(identifier, builder);
			}
		}

		this.field_16543.forEach(biConsumer::accept);
	}

	private void method_16368(EntityType<?> entityType, LootSupplier.Builder builder) {
		this.method_16369(entityType.getLootTableId(), builder);
	}

	private void method_16369(Identifier identifier, LootSupplier.Builder builder) {
		this.field_16543.put(identifier, builder);
	}
}
