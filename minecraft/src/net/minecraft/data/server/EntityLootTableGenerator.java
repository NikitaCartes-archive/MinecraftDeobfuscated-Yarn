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
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.tag.EntityTags;
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
import net.minecraft.world.loot.function.SetTagLootFunction;

public class EntityLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	private static final EntityPredicate.Builder field_11344 = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
	private final Map<Identifier, LootSupplier.Builder> field_16543 = Maps.<Identifier, LootSupplier.Builder>newHashMap();

	private static LootSupplier.Builder method_10401(ItemProvider itemProvider) {
		return LootSupplier.create()
			.withPool(LootPool.create().method_352(ConstantLootTableRange.create(1)).method_351(ItemEntry.method_411(itemProvider)))
			.withPool(LootPool.create().method_352(ConstantLootTableRange.create(1)).method_351(LootTableEntry.method_428(EntityType.SHEEP.method_16351())));
	}

	public void method_10400(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		this.method_16368(EntityType.ARMOR_STAND, LootSupplier.create());
		this.method_16368(EntityType.BAT, LootSupplier.create());
		this.method_16368(
			EntityType.BLAZE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8894)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.CAT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8276).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
		);
		this.method_16368(
			EntityType.CAVE_SPIDER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8276)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8680)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.CHICKEN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8153)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8726)
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.COD,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.method_356(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.COW,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8046)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.CREEPER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8054)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_351(TagEntry.method_445(ItemTags.field_15541))
						.method_356(
							EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.create().method_8922(EntityTags.field_15507))
						)
				)
		);
		this.method_16368(
			EntityType.DOLPHIN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
		);
		this.method_16368(
			EntityType.DONKEY,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.DROWNED,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8695))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.05F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ELDER_GUARDIAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8662)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.setWeight(3)
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8434).setWeight(2).method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(EmptyEntry.method_401())
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10562))
						.method_356(KilledByPlayerLootCondition.method_939())
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(LootTableEntry.method_428(LootTables.field_795))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.ENDER_DRAGON, LootSupplier.create());
		this.method_16368(
			EntityType.ENDERMAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8634)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.ENDERMITE, LootSupplier.create());
		this.method_16368(
			EntityType.EVOKER,
			LootSupplier.create()
				.withPool(LootPool.create().method_352(ConstantLootTableRange.create(1)).method_351(ItemEntry.method_411(Items.field_8288)))
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8687)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(EntityType.field_17943, LootSupplier.create());
		this.method_16368(
			EntityType.GHAST,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8070)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8054)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.GIANT, LootSupplier.create());
		this.method_16368(
			EntityType.GUARDIAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8662)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.setWeight(2)
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8434).setWeight(2).method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(EmptyEntry.method_401())
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(LootTableEntry.method_428(LootTables.field_795))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.HORSE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.HUSK,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620))
						.method_351(ItemEntry.method_411(Items.field_8179))
						.method_351(ItemEntry.method_411(Items.field_8567))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.RAVAGER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8175).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(EntityType.ILLUSIONER, LootSupplier.create());
		this.method_16368(
			EntityType.IRON_GOLEM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10449).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))))
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
		);
		this.method_16368(
			EntityType.LLAMA,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MAGMA_CUBE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8135)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-2.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MULE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MOOSHROOM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8046)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.OCELOT, LootSupplier.create());
		this.method_16368(
			EntityType.PANDA,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10211).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(
			EntityType.PARROT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8153)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.PHANTOM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8614)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.PIG,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8389)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.PILLAGER, LootSupplier.create());
		this.method_16368(EntityType.PLAYER, LootSupplier.create());
		this.method_16368(
			EntityType.POLAR_BEAR,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.setWeight(3)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8209)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.PUFFERFISH,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8323).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.method_356(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.RABBIT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8245)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8504)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8073))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.1F, 0.03F))
				)
		);
		this.method_16368(
			EntityType.SALMON,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8209)
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.method_356(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.SHEEP,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8748)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_438(FurnaceSmeltLootFunction.method_724().method_524(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16369(LootTables.field_778, method_10401(Blocks.field_10146));
		this.method_16369(LootTables.field_394, method_10401(Blocks.field_10514));
		this.method_16369(LootTables.field_489, method_10401(Blocks.field_10113));
		this.method_16369(LootTables.field_365, method_10401(Blocks.field_10619));
		this.method_16369(LootTables.field_878, method_10401(Blocks.field_10423));
		this.method_16369(LootTables.field_607, method_10401(Blocks.field_10170));
		this.method_16369(LootTables.field_461, method_10401(Blocks.field_10294));
		this.method_16369(LootTables.field_806, method_10401(Blocks.field_10222));
		this.method_16369(LootTables.field_702, method_10401(Blocks.field_10028));
		this.method_16369(LootTables.field_224, method_10401(Blocks.field_10215));
		this.method_16369(LootTables.field_814, method_10401(Blocks.field_10095));
		this.method_16369(LootTables.field_629, method_10401(Blocks.field_10459));
		this.method_16369(LootTables.field_285, method_10401(Blocks.field_10259));
		this.method_16369(LootTables.field_716, method_10401(Blocks.field_10314));
		this.method_16369(LootTables.field_869, method_10401(Blocks.field_10446));
		this.method_16369(LootTables.field_385, method_10401(Blocks.field_10490));
		this.method_16368(
			EntityType.SHULKER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8815))
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.5F, 0.0625F))
				)
		);
		this.method_16368(EntityType.SILVERFISH, LootSupplier.create());
		this.method_16368(
			EntityType.SKELETON,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8107)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SKELETON_HORSE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SLIME,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8777)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SNOW_GOLEM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8543).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 15.0F))))
				)
		);
		this.method_16368(
			EntityType.SPIDER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8276)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8680)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.SQUID,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8794)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.STRAY,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8107)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)).method_551(1))
								.method_438(SetTagLootFunction.method_677(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.method_356(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.field_17714,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.TROPICAL_FISH,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8846).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.method_356(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.TURTLE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Blocks.field_10376)
								.setWeight(3)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8428))
						.method_356(DamageSourcePropertiesLootCondition.method_837(DamageSourcePredicate.Builder.create().lightning(true)))
				)
		);
		this.method_16368(EntityType.VEX, LootSupplier.create());
		this.method_16368(EntityType.VILLAGER, LootSupplier.create());
		this.method_16368(EntityType.field_17713, LootSupplier.create());
		this.method_16368(
			EntityType.VINDICATOR,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8687)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_356(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.WITCH,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(UniformLootTableRange.between(1.0F, 3.0F))
						.method_351(
							ItemEntry.method_411(Items.field_8601)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8479)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8725)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8680)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8469)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8054)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8600)
								.setWeight(2)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.WITHER, LootSupplier.create());
		this.method_16368(
			EntityType.WITHER_SKELETON,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8713)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-1.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10177))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.WOLF, LootSupplier.create());
		this.method_16368(
			EntityType.ZOMBIE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620))
						.method_351(ItemEntry.method_411(Items.field_8179))
						.method_351(ItemEntry.method_411(Items.field_8567))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_HORSE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_PIGMAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8397)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8695))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_VILLAGER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
								.method_438(LootingEnchantLootFunction.method_547(UniformLootTableRange.between(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620))
						.method_351(ItemEntry.method_411(Items.field_8179))
						.method_351(ItemEntry.method_411(Items.field_8567))
						.method_356(KilledByPlayerLootCondition.method_939())
						.method_356(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
			Identifier identifier = entityType.method_16351();
			if (entityType != EntityType.PLAYER && entityType != EntityType.ARMOR_STAND && entityType.method_5891() == EntityCategory.field_17715) {
				if (identifier != LootTables.field_844 && this.field_16543.remove(identifier) != null) {
					throw new IllegalStateException(
						String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", identifier, Registry.ENTITY_TYPE.method_10221(entityType))
					);
				}
			} else if (identifier != LootTables.field_844 && set.add(identifier)) {
				LootSupplier.Builder builder = (LootSupplier.Builder)this.field_16543.remove(identifier);
				if (builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.ENTITY_TYPE.method_10221(entityType)));
				}

				biConsumer.accept(identifier, builder);
			}
		}

		this.field_16543.forEach(biConsumer::accept);
	}

	private void method_16368(EntityType<?> entityType, LootSupplier.Builder builder) {
		this.method_16369(entityType.method_16351(), builder);
	}

	private void method_16369(Identifier identifier, LootSupplier.Builder builder) {
		this.field_16543.put(identifier, builder);
	}
}
