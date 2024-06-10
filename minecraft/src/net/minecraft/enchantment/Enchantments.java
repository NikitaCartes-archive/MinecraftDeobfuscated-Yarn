package net.minecraft.enchantment;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.effect.AllOfEnchantmentEffects;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.enchantment.effect.DamageImmunityEnchantmentEffect;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.entity.ApplyMobEffectEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.DamageEntityEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.DamageItemEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ExplodeEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.PlaySoundEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ReplaceDiskEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.SummonEntityEnchantmentEffect;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.enchantment.effect.value.MultiplyEnchantmentEffect;
import net.minecraft.enchantment.effect.value.RemoveBinomialEnchantmentEffect;
import net.minecraft.enchantment.effect.value.SetEnchantmentEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.AllOfLootCondition;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EnchantmentActiveCheckLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.EnchantmentLevelLootNumberProvider;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.MovementPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class Enchantments {
	public static final RegistryKey<Enchantment> PROTECTION = of("protection");
	public static final RegistryKey<Enchantment> FIRE_PROTECTION = of("fire_protection");
	public static final RegistryKey<Enchantment> FEATHER_FALLING = of("feather_falling");
	public static final RegistryKey<Enchantment> BLAST_PROTECTION = of("blast_protection");
	public static final RegistryKey<Enchantment> PROJECTILE_PROTECTION = of("projectile_protection");
	public static final RegistryKey<Enchantment> RESPIRATION = of("respiration");
	public static final RegistryKey<Enchantment> AQUA_AFFINITY = of("aqua_affinity");
	public static final RegistryKey<Enchantment> THORNS = of("thorns");
	public static final RegistryKey<Enchantment> DEPTH_STRIDER = of("depth_strider");
	public static final RegistryKey<Enchantment> FROST_WALKER = of("frost_walker");
	public static final RegistryKey<Enchantment> BINDING_CURSE = of("binding_curse");
	public static final RegistryKey<Enchantment> SOUL_SPEED = of("soul_speed");
	public static final RegistryKey<Enchantment> SWIFT_SNEAK = of("swift_sneak");
	public static final RegistryKey<Enchantment> SHARPNESS = of("sharpness");
	public static final RegistryKey<Enchantment> SMITE = of("smite");
	public static final RegistryKey<Enchantment> BANE_OF_ARTHROPODS = of("bane_of_arthropods");
	public static final RegistryKey<Enchantment> KNOCKBACK = of("knockback");
	public static final RegistryKey<Enchantment> FIRE_ASPECT = of("fire_aspect");
	public static final RegistryKey<Enchantment> LOOTING = of("looting");
	public static final RegistryKey<Enchantment> SWEEPING_EDGE = of("sweeping_edge");
	public static final RegistryKey<Enchantment> EFFICIENCY = of("efficiency");
	public static final RegistryKey<Enchantment> SILK_TOUCH = of("silk_touch");
	public static final RegistryKey<Enchantment> UNBREAKING = of("unbreaking");
	public static final RegistryKey<Enchantment> FORTUNE = of("fortune");
	public static final RegistryKey<Enchantment> POWER = of("power");
	public static final RegistryKey<Enchantment> PUNCH = of("punch");
	public static final RegistryKey<Enchantment> FLAME = of("flame");
	public static final RegistryKey<Enchantment> INFINITY = of("infinity");
	public static final RegistryKey<Enchantment> LUCK_OF_THE_SEA = of("luck_of_the_sea");
	public static final RegistryKey<Enchantment> LURE = of("lure");
	public static final RegistryKey<Enchantment> LOYALTY = of("loyalty");
	public static final RegistryKey<Enchantment> IMPALING = of("impaling");
	public static final RegistryKey<Enchantment> RIPTIDE = of("riptide");
	public static final RegistryKey<Enchantment> CHANNELING = of("channeling");
	public static final RegistryKey<Enchantment> MULTISHOT = of("multishot");
	public static final RegistryKey<Enchantment> QUICK_CHARGE = of("quick_charge");
	public static final RegistryKey<Enchantment> PIERCING = of("piercing");
	public static final RegistryKey<Enchantment> DENSITY = of("density");
	public static final RegistryKey<Enchantment> BREACH = of("breach");
	public static final RegistryKey<Enchantment> WIND_BURST = of("wind_burst");
	public static final RegistryKey<Enchantment> MENDING = of("mending");
	public static final RegistryKey<Enchantment> VANISHING_CURSE = of("vanishing_curse");

	public static void bootstrap(Registerable<Enchantment> registry) {
		RegistryEntryLookup<DamageType> registryEntryLookup = registry.getRegistryLookup(RegistryKeys.DAMAGE_TYPE);
		RegistryEntryLookup<Enchantment> registryEntryLookup2 = registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
		RegistryEntryLookup<Item> registryEntryLookup3 = registry.getRegistryLookup(RegistryKeys.ITEM);
		RegistryEntryLookup<Block> registryEntryLookup4 = registry.getRegistryLookup(RegistryKeys.BLOCK);
		register(
			registry,
			PROTECTION,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
						10,
						4,
						Enchantment.leveledCost(1, 11),
						Enchantment.leveledCost(12, 11),
						1,
						AttributeModifierSlot.ARMOR
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE_PROTECTION,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F)),
					DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY)))
				)
		);
		register(
			registry,
			FIRE_PROTECTION,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
						5,
						4,
						Enchantment.leveledCost(10, 8),
						Enchantment.leveledCost(18, 8),
						2,
						AttributeModifierSlot.ARMOR
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE_PROTECTION,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.0F)),
					AllOfLootCondition.builder(
						DamageSourcePropertiesLootCondition.builder(
							DamageSourcePredicate.Builder.create()
								.tag(TagPredicate.expected(DamageTypeTags.IS_FIRE))
								.tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY))
						)
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.fire_protection"),
						EntityAttributes.GENERIC_BURNING_TIME,
						EnchantmentLevelBasedValue.linear(-0.15F),
						EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
					)
				)
		);
		register(
			registry,
			FEATHER_FALLING,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
						5,
						4,
						Enchantment.leveledCost(5, 6),
						Enchantment.leveledCost(11, 6),
						2,
						AttributeModifierSlot.ARMOR
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE_PROTECTION,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(3.0F)),
					DamageSourcePropertiesLootCondition.builder(
						DamageSourcePredicate.Builder.create()
							.tag(TagPredicate.expected(DamageTypeTags.IS_FALL))
							.tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY))
					)
				)
		);
		register(
			registry,
			BLAST_PROTECTION,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
						2,
						4,
						Enchantment.leveledCost(5, 8),
						Enchantment.leveledCost(13, 8),
						4,
						AttributeModifierSlot.ARMOR
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE_PROTECTION,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.0F)),
					DamageSourcePropertiesLootCondition.builder(
						DamageSourcePredicate.Builder.create()
							.tag(TagPredicate.expected(DamageTypeTags.IS_EXPLOSION))
							.tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY))
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.blast_protection"),
						EntityAttributes.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE,
						EnchantmentLevelBasedValue.linear(0.15F),
						EntityAttributeModifier.Operation.ADD_VALUE
					)
				)
		);
		register(
			registry,
			PROJECTILE_PROTECTION,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
						5,
						4,
						Enchantment.leveledCost(3, 6),
						Enchantment.leveledCost(9, 6),
						2,
						AttributeModifierSlot.ARMOR
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.ARMOR_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE_PROTECTION,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.0F)),
					DamageSourcePropertiesLootCondition.builder(
						DamageSourcePredicate.Builder.create()
							.tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
							.tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY))
					)
				)
		);
		register(
			registry,
			RESPIRATION,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(10, 10),
						Enchantment.leveledCost(40, 10),
						4,
						AttributeModifierSlot.HEAD
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.respiration"),
						EntityAttributes.GENERIC_OXYGEN_BONUS,
						EnchantmentLevelBasedValue.linear(1.0F),
						EntityAttributeModifier.Operation.ADD_VALUE
					)
				)
		);
		register(
			registry,
			AQUA_AFFINITY,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
						2,
						1,
						Enchantment.constantCost(1),
						Enchantment.constantCost(41),
						4,
						AttributeModifierSlot.HEAD
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.aqua_affinity"),
						EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED,
						EnchantmentLevelBasedValue.linear(4.0F),
						EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
					)
				)
		);
		register(
			registry,
			THORNS,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
						registryEntryLookup3.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
						1,
						3,
						Enchantment.leveledCost(10, 20),
						Enchantment.leveledCost(60, 20),
						8,
						AttributeModifierSlot.ANY
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.POST_ATTACK,
					EnchantmentEffectTarget.VICTIM,
					EnchantmentEffectTarget.ATTACKER,
					AllOfEnchantmentEffects.allOf(
						new DamageEntityEnchantmentEffect(
							EnchantmentLevelBasedValue.constant(1.0F), EnchantmentLevelBasedValue.constant(5.0F), registryEntryLookup.getOrThrow(DamageTypes.THORNS)
						),
						new DamageItemEnchantmentEffect(EnchantmentLevelBasedValue.constant(2.0F))
					),
					RandomChanceLootCondition.builder(EnchantmentLevelLootNumberProvider.create(EnchantmentLevelBasedValue.linear(0.15F)))
				)
		);
		register(
			registry,
			DEPTH_STRIDER,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(10, 10),
						Enchantment.leveledCost(25, 10),
						4,
						AttributeModifierSlot.FEET
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.depth_strider"),
						EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY,
						EnchantmentLevelBasedValue.linear(0.33333334F),
						EntityAttributeModifier.Operation.ADD_VALUE
					)
				)
		);
		register(
			registry,
			FROST_WALKER,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
						2,
						2,
						Enchantment.leveledCost(10, 10),
						Enchantment.leveledCost(25, 10),
						4,
						AttributeModifierSlot.FEET
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE_IMMUNITY,
					DamageImmunityEnchantmentEffect.INSTANCE,
					DamageSourcePropertiesLootCondition.builder(
						DamageSourcePredicate.Builder.create()
							.tag(TagPredicate.expected(DamageTypeTags.BURN_FROM_STEPPING))
							.tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY))
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.LOCATION_CHANGED,
					new ReplaceDiskEnchantmentEffect(
						new EnchantmentLevelBasedValue.Clamped(EnchantmentLevelBasedValue.linear(3.0F, 1.0F), 0.0F, 16.0F),
						EnchantmentLevelBasedValue.constant(1.0F),
						new Vec3i(0, -1, 0),
						Optional.of(
							BlockPredicate.allOf(
								BlockPredicate.matchingBlockTag(new Vec3i(0, 1, 0), BlockTags.AIR),
								BlockPredicate.matchingBlocks(Blocks.WATER),
								BlockPredicate.matchingFluids(Fluids.WATER),
								BlockPredicate.unobstructed()
							)
						),
						BlockStateProvider.of(Blocks.FROSTED_ICE),
						Optional.of(GameEvent.BLOCK_PLACE)
					),
					EntityPropertiesLootCondition.builder(
						LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onGround(true))
					)
				)
		);
		register(
			registry,
			BINDING_CURSE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.EQUIPPABLE_ENCHANTABLE),
						1,
						1,
						Enchantment.constantCost(25),
						Enchantment.constantCost(50),
						8,
						AttributeModifierSlot.ARMOR
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)
		);
		EntityPredicate.Builder builder = EntityPredicate.Builder.create()
			.periodicTick(5)
			.flags(EntityFlagsPredicate.Builder.create().flying(false).onGround(true))
			.movement(MovementPredicate.horizontalSpeed(NumberRange.DoubleRange.atLeast(1.0E-5F)))
			.movementAffectedBy(LocationPredicate.Builder.create().block(net.minecraft.predicate.BlockPredicate.Builder.create().tag(BlockTags.SOUL_SPEED_BLOCKS)));
		register(
			registry,
			SOUL_SPEED,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
						1,
						3,
						Enchantment.leveledCost(10, 10),
						Enchantment.leveledCost(25, 10),
						8,
						AttributeModifierSlot.FEET
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.LOCATION_CHANGED,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.soul_speed"),
						EntityAttributes.GENERIC_MOVEMENT_SPEED,
						EnchantmentLevelBasedValue.linear(0.0405F, 0.0105F),
						EntityAttributeModifier.Operation.ADD_VALUE
					),
					AllOfLootCondition.builder(
						InvertedLootCondition.builder(
							EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().vehicle(EntityPredicate.Builder.create()))
						),
						AnyOfLootCondition.builder(
							AllOfLootCondition.builder(
								EnchantmentActiveCheckLootCondition.requireActive(),
								EntityPropertiesLootCondition.builder(
									LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().flying(false))
								),
								AnyOfLootCondition.builder(
									EntityPropertiesLootCondition.builder(
										LootContext.EntityTarget.THIS,
										EntityPredicate.Builder.create()
											.movementAffectedBy(
												LocationPredicate.Builder.create().block(net.minecraft.predicate.BlockPredicate.Builder.create().tag(BlockTags.SOUL_SPEED_BLOCKS))
											)
									),
									EntityPropertiesLootCondition.builder(
										LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onGround(false)).build()
									)
								)
							),
							AllOfLootCondition.builder(
								EnchantmentActiveCheckLootCondition.requireInactive(),
								EntityPropertiesLootCondition.builder(
									LootContext.EntityTarget.THIS,
									EntityPredicate.Builder.create()
										.movementAffectedBy(
											LocationPredicate.Builder.create().block(net.minecraft.predicate.BlockPredicate.Builder.create().tag(BlockTags.SOUL_SPEED_BLOCKS))
										)
										.flags(EntityFlagsPredicate.Builder.create().flying(false))
								)
							)
						)
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.LOCATION_CHANGED,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.soul_speed"),
						EntityAttributes.GENERIC_MOVEMENT_EFFICIENCY,
						EnchantmentLevelBasedValue.constant(1.0F),
						EntityAttributeModifier.Operation.ADD_VALUE
					),
					EntityPropertiesLootCondition.builder(
						LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.create()
							.movementAffectedBy(LocationPredicate.Builder.create().block(net.minecraft.predicate.BlockPredicate.Builder.create().tag(BlockTags.SOUL_SPEED_BLOCKS)))
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.LOCATION_CHANGED,
					new DamageItemEnchantmentEffect(EnchantmentLevelBasedValue.constant(1.0F)),
					AllOfLootCondition.builder(
						RandomChanceLootCondition.builder(EnchantmentLevelLootNumberProvider.create(EnchantmentLevelBasedValue.constant(0.04F))),
						EntityPropertiesLootCondition.builder(
							LootContext.EntityTarget.THIS,
							EntityPredicate.Builder.create()
								.flags(EntityFlagsPredicate.Builder.create().onGround(true))
								.movementAffectedBy(LocationPredicate.Builder.create().block(net.minecraft.predicate.BlockPredicate.Builder.create().tag(BlockTags.SOUL_SPEED_BLOCKS)))
						)
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.TICK,
					new SpawnParticlesEnchantmentEffect(
						ParticleTypes.SOUL,
						SpawnParticlesEnchantmentEffect.withinBoundingBox(),
						SpawnParticlesEnchantmentEffect.entityPosition(0.1F),
						SpawnParticlesEnchantmentEffect.scaledVelocity(-0.2F),
						SpawnParticlesEnchantmentEffect.fixedVelocity(ConstantFloatProvider.create(0.1F)),
						ConstantFloatProvider.create(1.0F)
					),
					EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, builder)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.TICK,
					new PlaySoundEnchantmentEffect(SoundEvents.PARTICLE_SOUL_ESCAPE, ConstantFloatProvider.create(0.6F), UniformFloatProvider.create(0.6F, 1.0F)),
					AllOfLootCondition.builder(RandomChanceLootCondition.builder(0.35F), EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, builder))
				)
		);
		register(
			registry,
			SWIFT_SNEAK,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
						1,
						3,
						Enchantment.leveledCost(25, 25),
						Enchantment.leveledCost(75, 25),
						8,
						AttributeModifierSlot.LEGS
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.swift_sneak"),
						EntityAttributes.PLAYER_SNEAKING_SPEED,
						EnchantmentLevelBasedValue.linear(0.15F),
						EntityAttributeModifier.Operation.ADD_VALUE
					)
				)
		);
		register(
			registry,
			SHARPNESS,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
						registryEntryLookup3.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						10,
						5,
						Enchantment.leveledCost(1, 11),
						Enchantment.leveledCost(21, 11),
						1,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
				.addEffect(EnchantmentEffectComponentTypes.DAMAGE, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F, 0.5F)))
		);
		register(
			registry,
			SMITE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
						registryEntryLookup3.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						5,
						5,
						Enchantment.leveledCost(5, 8),
						Enchantment.leveledCost(25, 8),
						2,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.5F)),
					EntityPropertiesLootCondition.builder(
						LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().type(EntityTypePredicate.create(EntityTypeTags.SENSITIVE_TO_SMITE))
					)
				)
		);
		register(
			registry,
			BANE_OF_ARTHROPODS,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
						registryEntryLookup3.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						5,
						5,
						Enchantment.leveledCost(5, 8),
						Enchantment.leveledCost(25, 8),
						2,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.5F)),
					EntityPropertiesLootCondition.builder(
						LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().type(EntityTypePredicate.create(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS))
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.POST_ATTACK,
					EnchantmentEffectTarget.ATTACKER,
					EnchantmentEffectTarget.VICTIM,
					new ApplyMobEffectEnchantmentEffect(
						RegistryEntryList.of(StatusEffects.SLOWNESS),
						EnchantmentLevelBasedValue.constant(1.5F),
						EnchantmentLevelBasedValue.linear(1.5F, 0.5F),
						EnchantmentLevelBasedValue.constant(3.0F),
						EnchantmentLevelBasedValue.constant(3.0F)
					),
					EntityPropertiesLootCondition.builder(
							LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().type(EntityTypePredicate.create(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS))
						)
						.and(DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().isDirect(true)))
				)
		);
		register(
			registry,
			KNOCKBACK,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						5,
						2,
						Enchantment.leveledCost(5, 20),
						Enchantment.leveledCost(55, 20),
						2,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.KNOCKBACK, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F)))
		);
		register(
			registry,
			FIRE_ASPECT,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.FIRE_ASPECT_ENCHANTABLE),
						registryEntryLookup3.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						2,
						2,
						Enchantment.leveledCost(10, 20),
						Enchantment.leveledCost(60, 20),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.POST_ATTACK,
					EnchantmentEffectTarget.ATTACKER,
					EnchantmentEffectTarget.VICTIM,
					new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.linear(4.0F)),
					DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().isDirect(true))
				)
		);
		register(
			registry,
			LOOTING,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(15, 9),
						Enchantment.leveledCost(65, 9),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.EQUIPMENT_DROPS,
					EnchantmentEffectTarget.ATTACKER,
					EnchantmentEffectTarget.VICTIM,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.01F)),
					EntityPropertiesLootCondition.builder(
						LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.create().type(EntityTypePredicate.create(EntityType.PLAYER))
					)
				)
		);
		register(
			registry,
			SWEEPING_EDGE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(5, 9),
						Enchantment.leveledCost(20, 9),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.sweeping_edge"),
						EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO,
						new EnchantmentLevelBasedValue.Fraction(EnchantmentLevelBasedValue.linear(1.0F), EnchantmentLevelBasedValue.linear(2.0F, 1.0F)),
						EntityAttributeModifier.Operation.ADD_VALUE
					)
				)
		);
		register(
			registry,
			EFFICIENCY,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.MINING_ENCHANTABLE),
						10,
						5,
						Enchantment.leveledCost(1, 10),
						Enchantment.leveledCost(51, 10),
						1,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ATTRIBUTES,
					new AttributeEnchantmentEffect(
						Identifier.ofVanilla("enchantment.efficiency"),
						EntityAttributes.PLAYER_MINING_EFFICIENCY,
						new EnchantmentLevelBasedValue.LevelsSquared(1.0F),
						EntityAttributeModifier.Operation.ADD_VALUE
					)
				)
		);
		register(
			registry,
			SILK_TOUCH,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
						1,
						1,
						Enchantment.constantCost(15),
						Enchantment.constantCost(65),
						8,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE_SET))
				.addEffect(EnchantmentEffectComponentTypes.BLOCK_EXPERIENCE, new SetEnchantmentEffect(EnchantmentLevelBasedValue.constant(0.0F)))
		);
		register(
			registry,
			UNBREAKING,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
						5,
						3,
						Enchantment.leveledCost(5, 8),
						Enchantment.leveledCost(55, 8),
						2,
						AttributeModifierSlot.ANY
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ITEM_DAMAGE,
					new RemoveBinomialEnchantmentEffect(
						new EnchantmentLevelBasedValue.Fraction(EnchantmentLevelBasedValue.linear(2.0F), EnchantmentLevelBasedValue.linear(10.0F, 5.0F))
					),
					MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.ARMOR_ENCHANTABLE))
				)
				.addEffect(
					EnchantmentEffectComponentTypes.ITEM_DAMAGE,
					new RemoveBinomialEnchantmentEffect(
						new EnchantmentLevelBasedValue.Fraction(EnchantmentLevelBasedValue.linear(1.0F), EnchantmentLevelBasedValue.linear(2.0F, 1.0F))
					),
					InvertedLootCondition.builder(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.ARMOR_ENCHANTABLE)))
				)
		);
		register(
			registry,
			FORTUNE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.MINING_LOOT_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(15, 9),
						Enchantment.leveledCost(65, 9),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE_SET))
		);
		register(
			registry,
			POWER,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.BOW_ENCHANTABLE),
						10,
						5,
						Enchantment.leveledCost(1, 10),
						Enchantment.leveledCost(16, 10),
						1,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.5F)),
					EntityPropertiesLootCondition.builder(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.create().type(EntityTypeTags.ARROWS).build())
				)
		);
		register(
			registry,
			PUNCH,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.BOW_ENCHANTABLE),
						2,
						2,
						Enchantment.leveledCost(12, 20),
						Enchantment.leveledCost(37, 20),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.KNOCKBACK,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F)),
					EntityPropertiesLootCondition.builder(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.create().type(EntityTypeTags.ARROWS).build())
				)
		);
		register(
			registry,
			FLAME,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.BOW_ENCHANTABLE),
						2,
						1,
						Enchantment.constantCost(20),
						Enchantment.constantCost(50),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.PROJECTILE_SPAWNED, new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.constant(100.0F)))
		);
		register(
			registry,
			INFINITY,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.BOW_ENCHANTABLE),
						1,
						1,
						Enchantment.constantCost(20),
						Enchantment.constantCost(50),
						8,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.BOW_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.AMMO_USE,
					new SetEnchantmentEffect(EnchantmentLevelBasedValue.constant(0.0F)),
					MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(Items.ARROW))
				)
		);
		register(
			registry,
			LUCK_OF_THE_SEA,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(15, 9),
						Enchantment.leveledCost(65, 9),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.FISHING_LUCK_BONUS, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F)))
		);
		register(
			registry,
			LURE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(15, 9),
						Enchantment.leveledCost(65, 9),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.FISHING_TIME_REDUCTION, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(5.0F)))
		);
		register(
			registry,
			LOYALTY,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
						5,
						3,
						Enchantment.leveledCost(12, 7),
						Enchantment.constantCost(50),
						2,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.TRIDENT_RETURN_ACCELERATION, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F)))
		);
		register(
			registry,
			IMPALING,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
						2,
						5,
						Enchantment.leveledCost(1, 8),
						Enchantment.leveledCost(21, 8),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
				.addEffect(
					EnchantmentEffectComponentTypes.DAMAGE,
					new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.5F)),
					EntityPropertiesLootCondition.builder(
						LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().type(EntityTypePredicate.create(EntityTypeTags.SENSITIVE_TO_IMPALING)).build()
					)
				)
		);
		register(
			registry,
			RIPTIDE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(17, 7),
						Enchantment.constantCost(50),
						4,
						AttributeModifierSlot.HAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.RIPTIDE_EXCLUSIVE_SET))
				.addNonListEffect(EnchantmentEffectComponentTypes.TRIDENT_SPIN_ATTACK_STRENGTH, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.5F, 0.75F)))
				.addNonListEffect(
					EnchantmentEffectComponentTypes.TRIDENT_SOUND,
					List.of(SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundEvents.ITEM_TRIDENT_RIPTIDE_2, SoundEvents.ITEM_TRIDENT_RIPTIDE_3)
				)
		);
		register(
			registry,
			CHANNELING,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
						1,
						1,
						Enchantment.constantCost(25),
						Enchantment.constantCost(50),
						8,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.POST_ATTACK,
					EnchantmentEffectTarget.ATTACKER,
					EnchantmentEffectTarget.VICTIM,
					AllOfEnchantmentEffects.allOf(
						new SummonEntityEnchantmentEffect(RegistryEntryList.of(EntityType.LIGHTNING_BOLT.getRegistryEntry()), false),
						new PlaySoundEnchantmentEffect(SoundEvents.ITEM_TRIDENT_THUNDER, ConstantFloatProvider.create(5.0F), ConstantFloatProvider.create(1.0F))
					),
					AllOfLootCondition.builder(
						WeatherCheckLootCondition.create().thundering(true),
						EntityPropertiesLootCondition.builder(
							LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().location(LocationPredicate.Builder.create().canSeeSky(true))
						),
						EntityPropertiesLootCondition.builder(LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.create().type(EntityType.TRIDENT))
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.HIT_BLOCK,
					AllOfEnchantmentEffects.allOf(
						new SummonEntityEnchantmentEffect(RegistryEntryList.of(EntityType.LIGHTNING_BOLT.getRegistryEntry()), false),
						new PlaySoundEnchantmentEffect(SoundEvents.ITEM_TRIDENT_THUNDER, ConstantFloatProvider.create(5.0F), ConstantFloatProvider.create(1.0F))
					),
					AllOfLootCondition.builder(
						WeatherCheckLootCondition.create().thundering(true),
						EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().type(EntityType.TRIDENT)),
						LocationCheckLootCondition.builder(LocationPredicate.Builder.create().canSeeSky(true)),
						BlockStatePropertyLootCondition.builder(Blocks.LIGHTNING_ROD)
					)
				)
		);
		register(
			registry,
			MULTISHOT,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
						2,
						1,
						Enchantment.constantCost(20),
						Enchantment.constantCost(50),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE_SET))
				.addEffect(EnchantmentEffectComponentTypes.PROJECTILE_COUNT, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.0F)))
				.addEffect(EnchantmentEffectComponentTypes.PROJECTILE_SPREAD, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(10.0F)))
		);
		register(
			registry,
			QUICK_CHARGE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
						5,
						3,
						Enchantment.leveledCost(12, 20),
						Enchantment.constantCost(50),
						2,
						AttributeModifierSlot.MAINHAND,
						AttributeModifierSlot.OFFHAND
					)
				)
				.addNonListEffect(EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(-0.25F)))
				.addNonListEffect(
					EnchantmentEffectComponentTypes.CROSSBOW_CHARGING_SOUNDS,
					List.of(
						new CrossbowItem.LoadingSounds(
							Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
						),
						new CrossbowItem.LoadingSounds(
							Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
						),
						new CrossbowItem.LoadingSounds(
							Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
						)
					)
				)
		);
		register(
			registry,
			PIERCING,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
						10,
						4,
						Enchantment.leveledCost(1, 10),
						Enchantment.constantCost(50),
						1,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.CROSSBOW_EXCLUSIVE_SET))
				.addEffect(EnchantmentEffectComponentTypes.PROJECTILE_PIERCING, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F)))
		);
		register(
			registry,
			DENSITY,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.MACE_ENCHANTABLE),
						5,
						5,
						Enchantment.leveledCost(5, 8),
						Enchantment.leveledCost(25, 8),
						2,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
				.addEffect(EnchantmentEffectComponentTypes.SMASH_DAMAGE_PER_FALLEN_BLOCK, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.5F)))
		);
		register(
			registry,
			BREACH,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.MACE_ENCHANTABLE),
						2,
						4,
						Enchantment.leveledCost(15, 9),
						Enchantment.leveledCost(65, 9),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE_SET))
				.addEffect(EnchantmentEffectComponentTypes.ARMOR_EFFECTIVENESS, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(-0.15F)))
		);
		register(
			registry,
			WIND_BURST,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.MACE_ENCHANTABLE),
						2,
						3,
						Enchantment.leveledCost(15, 9),
						Enchantment.leveledCost(65, 9),
						4,
						AttributeModifierSlot.MAINHAND
					)
				)
				.addEffect(
					EnchantmentEffectComponentTypes.POST_ATTACK,
					EnchantmentEffectTarget.ATTACKER,
					EnchantmentEffectTarget.ATTACKER,
					new ExplodeEnchantmentEffect(
						false,
						Optional.empty(),
						Optional.of(EnchantmentLevelBasedValue.lookup(List.of(1.2F, 1.75F, 2.2F), EnchantmentLevelBasedValue.linear(1.5F, 0.35F))),
						registryEntryLookup4.getOptional(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()),
						Vec3d.ZERO,
						EnchantmentLevelBasedValue.constant(3.5F),
						false,
						World.ExplosionSourceType.TRIGGER,
						ParticleTypes.GUST_EMITTER_SMALL,
						ParticleTypes.GUST_EMITTER_LARGE,
						SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
					),
					EntityPropertiesLootCondition.builder(
						LootContext.EntityTarget.DIRECT_ATTACKER,
						EntityPredicate.Builder.create()
							.flags(EntityFlagsPredicate.Builder.create().flying(false))
							.movement(MovementPredicate.fallDistance(NumberRange.DoubleRange.atLeast(1.5)))
					)
				)
		);
		register(
			registry,
			MENDING,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
						2,
						1,
						Enchantment.leveledCost(25, 25),
						Enchantment.leveledCost(75, 25),
						4,
						AttributeModifierSlot.ANY
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, new MultiplyEnchantmentEffect(EnchantmentLevelBasedValue.constant(2.0F)))
		);
		register(
			registry,
			VANISHING_CURSE,
			Enchantment.builder(
					Enchantment.definition(
						registryEntryLookup3.getOrThrow(ItemTags.VANISHING_ENCHANTABLE),
						1,
						1,
						Enchantment.constantCost(25),
						Enchantment.constantCost(50),
						8,
						AttributeModifierSlot.ANY
					)
				)
				.addEffect(EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP)
		);
	}

	private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
		registry.register(key, builder.build(key.getValue()));
	}

	private static RegistryKey<Enchantment> of(String id) {
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.ofVanilla(id));
	}
}
