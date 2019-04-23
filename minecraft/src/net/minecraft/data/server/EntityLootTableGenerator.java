package net.minecraft.data.server;

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
	private final Map<Identifier, LootSupplier.Builder> field_16543 = Maps.<Identifier, LootSupplier.Builder>newHashMap();

	private static LootSupplier.Builder method_10401(ItemConvertible itemConvertible) {
		return LootSupplier.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)))
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(LootTableEntry.builder(EntityType.field_6115.getLootTableId())));
	}

	public void method_10400(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		this.method_16368(EntityType.field_6131, LootSupplier.builder());
		this.method_16368(EntityType.field_6108, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6099,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8894)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.field_16281,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8276).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
		);
		this.method_16368(
			EntityType.field_6084,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8276)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8680)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.field_6132,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8153)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8726)
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6070,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.field_6085,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8046)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6046,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8054)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withEntry(TagEntry.builder(ItemTags.field_15541))
						.method_356(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_936, EntityPredicate.Builder.create().type(EntityTypeTags.field_15507)))
				)
		);
		this.method_16368(
			EntityType.field_6087,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6067,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6123,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8695))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.05F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.field_6086,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8662)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.setWeight(3)
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
						)
						.withEntry(ItemEntry.builder(Items.field_8434).setWeight(2).method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F))))
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10562))
						.method_356(KilledByPlayerLootCondition.builder())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.field_795))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.field_6116, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6091,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8634)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.field_6128, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6090,
			LootSupplier.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8288)))
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8687)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(EntityType.field_17943, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6107,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8070)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8054)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.field_6095, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6118,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8662)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.setWeight(2)
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
						)
						.withEntry(ItemEntry.builder(Items.field_8434).setWeight(2).method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F))))
						.withEntry(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.field_795))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.field_6139,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6071,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620))
						.withEntry(ItemEntry.builder(Items.field_8179))
						.withEntry(ItemEntry.builder(Items.field_8567))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.field_6134,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8175).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(EntityType.field_6065, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6147,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10449).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
		);
		this.method_16368(
			EntityType.field_6074,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6102,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8135)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-2.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6057,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6143,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8046)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.field_6081, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6146,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10211).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(
			EntityType.field_6104,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8153)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6078,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8614)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.field_6093,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8389)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.field_6105, LootSupplier.builder());
		this.method_16368(EntityType.field_6097, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6042,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8429)
								.setWeight(3)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8209)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6062,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8323).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.field_6140,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8245)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8504)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8073))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.1F, 0.03F))
				)
		);
		this.method_16368(
			EntityType.field_6073,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8209)
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.field_6115,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8748)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_438(FurnaceSmeltLootFunction.builder().method_524(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, field_11344)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16369(LootTables.BLACK_SHEEP_ENTITY, method_10401(Blocks.field_10146));
		this.method_16369(LootTables.BLUE_SHEEP_ENTITY, method_10401(Blocks.field_10514));
		this.method_16369(LootTables.BROWN_SHEEP_ENTITY, method_10401(Blocks.field_10113));
		this.method_16369(LootTables.CYAN_SHEEP_ENTITY, method_10401(Blocks.field_10619));
		this.method_16369(LootTables.GRAY_SHEEP_ENTITY, method_10401(Blocks.field_10423));
		this.method_16369(LootTables.GREEN_SHEEP_ENTITY, method_10401(Blocks.field_10170));
		this.method_16369(LootTables.LIGHT_BLUE_SHEEP_ENTITY, method_10401(Blocks.field_10294));
		this.method_16369(LootTables.LIGHT_GRAY_SHEEP_ENTITY, method_10401(Blocks.field_10222));
		this.method_16369(LootTables.LIME_SHEEP_ENTITY, method_10401(Blocks.field_10028));
		this.method_16369(LootTables.MAGENTA_SHEEP_ENTITY, method_10401(Blocks.field_10215));
		this.method_16369(LootTables.ORANGE_SHEEP_ENTITY, method_10401(Blocks.field_10095));
		this.method_16369(LootTables.PINK_SHEEP_ENTITY, method_10401(Blocks.field_10459));
		this.method_16369(LootTables.PURPLE_SHEEP_ENTITY, method_10401(Blocks.field_10259));
		this.method_16369(LootTables.RED_SHEEP_ENTITY, method_10401(Blocks.field_10314));
		this.method_16369(LootTables.WHITE_SHEEP_ENTITY, method_10401(Blocks.field_10446));
		this.method_16369(LootTables.YELLOW_SHEEP_ENTITY, method_10401(Blocks.field_10490));
		this.method_16368(
			EntityType.field_6109,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8815))
						.method_356(RandomChanceWithLootingLootCondition.builder(0.5F, 0.0625F))
				)
		);
		this.method_16368(EntityType.field_6125, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6137,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8107)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6075,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6069,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8777)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6047,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8543).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 15.0F))))
				)
		);
		this.method_16368(
			EntityType.field_6079,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8276)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8680)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.field_6114,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8794)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6098,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8107)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)).withLimit(1))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.field_17714,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6111,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8846).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8324))
						.method_356(RandomChanceLootCondition.builder(0.05F))
				)
		);
		this.method_16368(
			EntityType.field_6113,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Blocks.field_10376)
								.setWeight(3)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8428))
						.method_356(DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().lightning(true)))
				)
		);
		this.method_16368(EntityType.field_6059, LootSupplier.builder());
		this.method_16368(EntityType.field_6077, LootSupplier.builder());
		this.method_16368(EntityType.field_17713, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6117,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8687)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.builder())
				)
		);
		this.method_16368(
			EntityType.field_6145,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(
							ItemEntry.builder(Items.field_8601)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8479)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8725)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8680)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8469)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8054)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.withEntry(
							ItemEntry.builder(Items.field_8600)
								.setWeight(2)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.field_6119, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6076,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8713)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Blocks.field_10177))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.field_6055, LootSupplier.builder());
		this.method_16368(
			EntityType.field_6051,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620))
						.withEntry(ItemEntry.builder(Items.field_8179))
						.withEntry(ItemEntry.builder(Items.field_8567))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.field_6048,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.field_6050,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8397)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8695))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.field_6054,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8620))
						.withEntry(ItemEntry.builder(Items.field_8179))
						.withEntry(ItemEntry.builder(Items.field_8567))
						.method_356(KilledByPlayerLootCondition.builder())
						.method_356(RandomChanceWithLootingLootCondition.builder(0.025F, 0.01F))
				)
		);
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
			Identifier identifier = entityType.getLootTableId();
			if (entityType != EntityType.field_6097 && entityType != EntityType.field_6131 && entityType.getCategory() == EntityCategory.field_17715) {
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
