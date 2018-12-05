package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemContainer;
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

public class class_2434 implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	private static final EntityPredicate.Builder field_11344 = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());
	private final Map<Identifier, LootSupplier.Builder> field_16543 = Maps.<Identifier, LootSupplier.Builder>newHashMap();

	private static LootSupplier.Builder method_10401(ItemContainer itemContainer) {
		return LootSupplier.create()
			.withPool(LootPool.create().withRolls(ConstantLootTableRange.create(1)).method_351(ItemEntry.method_411(itemContainer)))
			.withPool(LootPool.create().withRolls(ConstantLootTableRange.create(1)).method_351(LootTableEntry.method_428(EntityType.SHEEP.getLootTableId())));
	}

	public void method_10400(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		this.method_16368(EntityType.ARMOR_STAND, LootSupplier.create());
		this.method_16368(EntityType.BAT, LootSupplier.create());
		this.method_16368(
			EntityType.BLAZE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8894)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.CAT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8276).withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F))))
				)
		);
		this.method_16368(
			EntityType.CAVE_SPIDER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8276)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8680)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.CHICKEN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8153)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8726)
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.COD,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.withCondition(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.COW,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8046)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(1.0F, 3.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.CREEPER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8054)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.method_351(TagEntry.create(ItemTags.field_15541))
						.withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.create().type(EntityTags.field_15507)))
				)
		);
		this.method_16368(
			EntityType.DOLPHIN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
		);
		this.method_16368(
			EntityType.DONKEY,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.DROWNED,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8695))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.05F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ELDER_GUARDIAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8662)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.setWeight(3)
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8434).setWeight(2).withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10562))
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(LootTableEntry.method_428(LootTables.field_795))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.ENDER_DRAGON, LootSupplier.create());
		this.method_16368(
			EntityType.ENDERMAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8634)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.ENDERMITE, LootSupplier.create());
		this.method_16368(
			EntityType.EVOKER,
			LootSupplier.create()
				.withPool(LootPool.create().withRolls(ConstantLootTableRange.create(1)).method_351(ItemEntry.method_411(Items.field_8288)))
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8687)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.GHAST,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8070)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8054)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.GIANT, LootSupplier.create());
		this.method_16368(
			EntityType.GUARDIAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8662)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.setWeight(2)
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8434).setWeight(2).withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(EmptyEntry.Serializer())
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(LootTableEntry.method_428(LootTables.field_795))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.HORSE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.HUSK,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620))
						.method_351(ItemEntry.method_411(Items.field_8179))
						.method_351(ItemEntry.method_411(Items.field_8567))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ILLAGER_BEAST,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8175).withFunction(SetCountLootFunction.method_621(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(EntityType.ILLUSIONER, LootSupplier.create());
		this.method_16368(
			EntityType.IRON_GOLEM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10449).withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620).withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(3.0F, 5.0F))))
				)
		);
		this.method_16368(
			EntityType.LLAMA,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MAGMA_CUBE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8135)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(-2.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MULE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.MOOSHROOM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8745)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8046)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(1.0F, 3.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.OCELOT, LootSupplier.create());
		this.method_16368(
			EntityType.PANDA,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10211).withFunction(SetCountLootFunction.method_621(ConstantLootTableRange.create(1))))
				)
		);
		this.method_16368(
			EntityType.PARROT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8153)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(1.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.PHANTOM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8614)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.PIG,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8389)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(1.0F, 3.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
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
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8429)
								.setWeight(3)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8209)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.PUFFERFISH,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8323).withFunction(SetCountLootFunction.method_621(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.withCondition(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.RABBIT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8245)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8504)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8073))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.1F, 0.03F))
				)
		);
		this.method_16368(
			EntityType.SALMON,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8209)
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.withCondition(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.SHEEP,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8748)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(1.0F, 2.0F)))
								.withFunction(FurnaceSmeltLootFunction.method_724().withCondition(EntityPropertiesLootCondition.method_917(LootContext.EntityTarget.THIS, field_11344)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16369(LootTables.ENTITY_SHEEP_BLACK, method_10401(Blocks.field_10146));
		this.method_16369(LootTables.ENTITY_SHEEP_BLUE, method_10401(Blocks.field_10514));
		this.method_16369(LootTables.ENTITY_SHEEP_BROWN, method_10401(Blocks.field_10113));
		this.method_16369(LootTables.ENTITY_SHEEP_CYAN, method_10401(Blocks.field_10619));
		this.method_16369(LootTables.ENTITY_SHEEP_GRAY, method_10401(Blocks.field_10423));
		this.method_16369(LootTables.ENTITY_SHEEP_GREEN, method_10401(Blocks.field_10170));
		this.method_16369(LootTables.ENTITY_SHEEP_LIGHT_BLUE, method_10401(Blocks.field_10294));
		this.method_16369(LootTables.field_806, method_10401(Blocks.field_10222));
		this.method_16369(LootTables.ENTITY_SHEEP_LIME, method_10401(Blocks.field_10028));
		this.method_16369(LootTables.ENTITY_SHEEP_MAGENTA, method_10401(Blocks.field_10215));
		this.method_16369(LootTables.ENTITY_SHEEP_ORANGE, method_10401(Blocks.field_10095));
		this.method_16369(LootTables.ENTITY_SHEEP_PINK, method_10401(Blocks.field_10459));
		this.method_16369(LootTables.ENTITY_SHEEP_PURPLE, method_10401(Blocks.field_10259));
		this.method_16369(LootTables.ENTITY_SHEEP_RED, method_10401(Blocks.field_10314));
		this.method_16369(LootTables.ENTITY_SHEEP_WHITE, method_10401(Blocks.field_10446));
		this.method_16369(LootTables.ENTITY_SHEEP_YELLOW, method_10401(Blocks.field_10490));
		this.method_16368(
			EntityType.SHULKER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8815))
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.5F, 0.0625F))
				)
		);
		this.method_16368(EntityType.SILVERFISH, LootSupplier.create());
		this.method_16368(
			EntityType.SKELETON,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8107)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SKELETON_HORSE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SLIME,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8777)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.SNOW_GOLEM,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8543).withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 15.0F))))
				)
		);
		this.method_16368(
			EntityType.SPIDER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8276)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8680)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.SQUID,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8794)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(1.0F, 3.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.STRAY,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8107)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)).method_551(1))
								.withFunction(
									SetTagLootFunction.method_677(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness")))
								)
						)
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.TROPICAL_FISH,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8846).withFunction(SetCountLootFunction.method_621(ConstantLootTableRange.create(1))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8324))
						.withCondition(RandomChanceLootCondition.method_932(0.05F))
				)
		);
		this.method_16368(
			EntityType.TURTLE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Blocks.field_10376)
								.setWeight(3)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8428))
						.withCondition(DamageSourcePropertiesLootCondition.method_837(DamageSourcePredicate.Builder.create().lightning(true)))
				)
		);
		this.method_16368(EntityType.VEX, LootSupplier.create());
		this.method_16368(EntityType.VILLAGER, LootSupplier.create());
		this.method_16368(
			EntityType.VINDICATOR,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8687)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.withCondition(KilledByPlayerLootCondition.method_939())
				)
		);
		this.method_16368(
			EntityType.WITCH,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.method_377(1.0F, 3.0F))
						.method_351(
							ItemEntry.method_411(Items.field_8601)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8479)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8725)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8680)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8469)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8054)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8600)
								.setWeight(2)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(EntityType.WITHER, LootSupplier.create());
		this.method_16368(
			EntityType.WITHER_SKELETON,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8713)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(-1.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8606)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Blocks.field_10177))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(EntityType.WOLF, LootSupplier.create());
		this.method_16368(
			EntityType.ZOMBIE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620))
						.method_351(ItemEntry.method_411(Items.field_8179))
						.method_351(ItemEntry.method_411(Items.field_8567))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_HORSE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_PIGMAN,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8397)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 1.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8695))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		this.method_16368(
			EntityType.ZOMBIE_VILLAGER,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(
							ItemEntry.method_411(Items.field_8511)
								.withFunction(SetCountLootFunction.method_621(UniformLootTableRange.method_377(0.0F, 2.0F)))
								.withFunction(LootingEnchantLootFunction.method_547(UniformLootTableRange.method_377(0.0F, 1.0F)))
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8620))
						.method_351(ItemEntry.method_411(Items.field_8179))
						.method_351(ItemEntry.method_411(Items.field_8567))
						.withCondition(KilledByPlayerLootCondition.method_939())
						.withCondition(RandomChanceWithLootingLootCondition.method_953(0.025F, 0.01F))
				)
		);
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
			Identifier identifier = entityType.getLootTableId();
			if (LivingEntity.class.isAssignableFrom(entityType.getEntityClass())) {
				if (identifier != LootTables.EMPTY && set.add(identifier)) {
					LootSupplier.Builder builder = (LootSupplier.Builder)this.field_16543.remove(identifier);
					if (builder == null) {
						throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.ENTITY_TYPE.getId(entityType)));
					}

					biConsumer.accept(identifier, builder);
				}
			} else if (identifier != LootTables.EMPTY && this.field_16543.remove(identifier) != null) {
				throw new IllegalStateException(
					String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", identifier, Registry.ENTITY_TYPE.getId(entityType))
				);
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
