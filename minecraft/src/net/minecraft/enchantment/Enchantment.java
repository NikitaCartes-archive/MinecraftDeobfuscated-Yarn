package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.enchantment.effect.DamageImmunityEnchantmentEffect;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffect;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableFloat;

public record Enchantment(Text description, Enchantment.Definition definition, RegistryEntryList<Enchantment> exclusiveSet, ComponentMap effects) {
	public static final int MAX_LEVEL = 255;
	public static final Codec<Enchantment> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					TextCodecs.CODEC.fieldOf("description").forGetter(Enchantment::description),
					Enchantment.Definition.CODEC.forGetter(Enchantment::definition),
					RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("exclusive_set", RegistryEntryList.of()).forGetter(Enchantment::exclusiveSet),
					EnchantmentEffectComponentTypes.COMPONENT_MAP_CODEC.optionalFieldOf("effects", ComponentMap.EMPTY).forGetter(Enchantment::effects)
				)
				.apply(instance, Enchantment::new)
	);
	public static final Codec<RegistryEntry<Enchantment>> ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.ENCHANTMENT);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<Enchantment>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.ENCHANTMENT);

	public static Enchantment.Cost constantCost(int base) {
		return new Enchantment.Cost(base, 0);
	}

	public static Enchantment.Cost leveledCost(int base, int perLevel) {
		return new Enchantment.Cost(base, perLevel);
	}

	public static Enchantment.Definition definition(
		RegistryEntryList<Item> supportedItems,
		RegistryEntryList<Item> primaryItems,
		int weight,
		int maxLevel,
		Enchantment.Cost minCost,
		Enchantment.Cost maxCost,
		int anvilCost,
		AttributeModifierSlot... slots
	) {
		return new Enchantment.Definition(supportedItems, Optional.of(primaryItems), weight, maxLevel, minCost, maxCost, anvilCost, List.of(slots));
	}

	public static Enchantment.Definition definition(
		RegistryEntryList<Item> supportedItems,
		int weight,
		int maxLevel,
		Enchantment.Cost minCost,
		Enchantment.Cost maxCost,
		int anvilCost,
		AttributeModifierSlot... slots
	) {
		return new Enchantment.Definition(supportedItems, Optional.empty(), weight, maxLevel, minCost, maxCost, anvilCost, List.of(slots));
	}

	public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity entity) {
		Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (this.slotMatches(equipmentSlot)) {
				ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
				if (!itemStack.isEmpty()) {
					map.put(equipmentSlot, itemStack);
				}
			}
		}

		return map;
	}

	public RegistryEntryList<Item> getApplicableItems() {
		return this.definition.supportedItems();
	}

	public boolean slotMatches(EquipmentSlot slot) {
		return this.definition.slots().stream().anyMatch(slotx -> slotx.matches(slot));
	}

	public boolean isPrimaryItem(ItemStack stack) {
		return this.isSupportedItem(stack) && (this.definition.primaryItems.isEmpty() || stack.isIn((RegistryEntryList<Item>)this.definition.primaryItems.get()));
	}

	public boolean isSupportedItem(ItemStack stack) {
		return stack.isIn(this.definition.supportedItems);
	}

	public int getWeight() {
		return this.definition.weight();
	}

	public int getAnvilCost() {
		return this.definition.anvilCost();
	}

	public int getMinLevel() {
		return 1;
	}

	public int getMaxLevel() {
		return this.definition.maxLevel();
	}

	public int getMinPower(int level) {
		return this.definition.minCost().forLevel(level);
	}

	public int getMaxPower(int level) {
		return this.definition.maxCost().forLevel(level);
	}

	public String toString() {
		return "Enchantment " + this.description.getString();
	}

	public static boolean canBeCombined(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second) {
		return !first.equals(second) && !first.value().exclusiveSet.contains(second) && !second.value().exclusiveSet.contains(first);
	}

	public static Text getName(RegistryEntry<Enchantment> enchantment, int level) {
		MutableText mutableText = enchantment.value().description.copy();
		if (enchantment.isIn(EnchantmentTags.CURSE)) {
			Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.RED));
		} else {
			Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
		}

		if (level != 1 || enchantment.value().getMaxLevel() != 1) {
			mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + level));
		}

		return mutableText;
	}

	public boolean isAcceptableItem(ItemStack stack) {
		return this.definition.supportedItems().contains(stack.getRegistryEntry());
	}

	public <T> List<T> getEffect(ComponentType<List<T>> type) {
		return this.effects.getOrDefault(type, List.of());
	}

	public boolean hasDamageImmunityTo(ServerWorld world, int level, Entity user, DamageSource damageSource) {
		LootContext lootContext = createEnchantedDamageLootContext(world, level, user, damageSource);

		for (EnchantmentEffectEntry<DamageImmunityEnchantmentEffect> enchantmentEffectEntry : this.getEffect(EnchantmentEffectComponentTypes.DAMAGE_IMMUNITY)) {
			if (enchantmentEffectEntry.test(lootContext)) {
				return true;
			}
		}

		return false;
	}

	public void modifyDamageProtection(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damageProtection) {
		LootContext lootContext = createEnchantedDamageLootContext(world, level, user, damageSource);

		for (EnchantmentEffectEntry<EnchantmentValueEffect> enchantmentEffectEntry : this.getEffect(EnchantmentEffectComponentTypes.DAMAGE_PROTECTION)) {
			if (enchantmentEffectEntry.test(lootContext)) {
				damageProtection.setValue(enchantmentEffectEntry.effect().apply(level, user.getRandom(), damageProtection.floatValue()));
			}
		}
	}

	public void modifyItemDamage(ServerWorld world, int level, ItemStack stack, MutableFloat itemDamage) {
		this.modifyValue(EnchantmentEffectComponentTypes.ITEM_DAMAGE, world, level, stack, itemDamage);
	}

	public void modifyAmmoUse(ServerWorld world, int level, ItemStack projectileStack, MutableFloat ammoUse) {
		this.modifyValue(EnchantmentEffectComponentTypes.AMMO_USE, world, level, projectileStack, ammoUse);
	}

	public void modifyProjectilePiercing(ServerWorld world, int level, ItemStack stack, MutableFloat projectilePiercing) {
		this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_PIERCING, world, level, stack, projectilePiercing);
	}

	public void modifyBlockExperience(ServerWorld world, int level, ItemStack stack, MutableFloat blockExperience) {
		this.modifyValue(EnchantmentEffectComponentTypes.BLOCK_EXPERIENCE, world, level, stack, blockExperience);
	}

	public void modifyMobExperience(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat mobExperience) {
		this.modifyValue(EnchantmentEffectComponentTypes.MOB_EXPERIENCE, world, level, stack, user, mobExperience);
	}

	public void modifyRepairWithXp(ServerWorld world, int level, ItemStack stack, MutableFloat repairWithXp) {
		this.modifyValue(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, world, level, stack, repairWithXp);
	}

	public void modifyTridentReturnAcceleration(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat tridentReturnAcceleration) {
		this.modifyValue(EnchantmentEffectComponentTypes.TRIDENT_RETURN_ACCELERATION, world, level, stack, user, tridentReturnAcceleration);
	}

	public void modifyTridentSpinAttackStrength(Random random, int level, MutableFloat tridentSpinAttackStrength) {
		this.modifyValue(EnchantmentEffectComponentTypes.TRIDENT_SPIN_ATTACK_STRENGTH, random, level, tridentSpinAttackStrength);
	}

	public void modifyFishingTimeReduction(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat fishingTimeReduction) {
		this.modifyValue(EnchantmentEffectComponentTypes.FISHING_TIME_REDUCTION, world, level, stack, user, fishingTimeReduction);
	}

	public void modifyFishingLuckBonus(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat fishingLuckBonus) {
		this.modifyValue(EnchantmentEffectComponentTypes.FISHING_LUCK_BONUS, world, level, stack, user, fishingLuckBonus);
	}

	public void modifyDamage(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damage) {
		this.modifyValue(EnchantmentEffectComponentTypes.DAMAGE, world, level, stack, user, damageSource, damage);
	}

	public void modifySmashDamagePerFallenBlock(
		ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat smashDamagePerFallenBlock
	) {
		this.modifyValue(EnchantmentEffectComponentTypes.SMASH_DAMAGE_PER_FALLEN_BLOCK, world, level, stack, user, damageSource, smashDamagePerFallenBlock);
	}

	public void modifyKnockback(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat knockback) {
		this.modifyValue(EnchantmentEffectComponentTypes.KNOCKBACK, world, level, stack, user, damageSource, knockback);
	}

	public void modifyArmorEffectiveness(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat armorEffectiveness) {
		this.modifyValue(EnchantmentEffectComponentTypes.ARMOR_EFFECTIVENESS, world, level, stack, user, damageSource, armorEffectiveness);
	}

	public static void applyTargetedEffect(
		TargetedEnchantmentEffect<EnchantmentEntityEffect> effect,
		ServerWorld world,
		int level,
		EnchantmentEffectContext context,
		Entity user,
		DamageSource damageSource
	) {
		if (effect.test(createEnchantedDamageLootContext(world, level, user, damageSource))) {
			Entity entity = switch (effect.affected()) {
				case ATTACKER -> damageSource.getAttacker();
				case DAMAGING_ENTITY -> damageSource.getSource();
				case VICTIM -> user;
			};
			if (entity != null) {
				effect.effect().apply(world, level, context, entity, entity.getPos());
			}
		}
	}

	public void onTargetDamaged(
		ServerWorld world, int level, EnchantmentEffectContext context, EnchantmentEffectTarget target, Entity user, DamageSource damageSource
	) {
		for (TargetedEnchantmentEffect<EnchantmentEntityEffect> targetedEnchantmentEffect : this.getEffect(EnchantmentEffectComponentTypes.POST_ATTACK)) {
			if (target == targetedEnchantmentEffect.enchanted()) {
				applyTargetedEffect(targetedEnchantmentEffect, world, level, context, user, damageSource);
			}
		}
	}

	public void modifyProjectileCount(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat projectileCount) {
		this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_COUNT, world, level, stack, user, projectileCount);
	}

	public void modifyProjectileSpread(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat projectileSpread) {
		this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_SPREAD, world, level, stack, user, projectileSpread);
	}

	public void modifyCrossbowChargeTime(Random random, int level, MutableFloat crossbowChargeTime) {
		this.modifyValue(EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, random, level, crossbowChargeTime);
	}

	public void modifyValue(ComponentType<EnchantmentValueEffect> type, Random random, int level, MutableFloat value) {
		EnchantmentValueEffect enchantmentValueEffect = this.effects.get(type);
		if (enchantmentValueEffect != null) {
			value.setValue(enchantmentValueEffect.apply(level, random, value.floatValue()));
		}
	}

	public void onTick(ServerWorld world, int level, EnchantmentEffectContext context, Entity user) {
		applyEffects(
			this.getEffect(EnchantmentEffectComponentTypes.TICK),
			createEnchantedEntityLootContext(world, level, user, user.getPos()),
			effect -> effect.apply(world, level, context, user, user.getPos())
		);
	}

	public void onProjectileSpawned(ServerWorld world, int level, EnchantmentEffectContext context, Entity user) {
		applyEffects(
			this.getEffect(EnchantmentEffectComponentTypes.PROJECTILE_SPAWNED),
			createEnchantedEntityLootContext(world, level, user, user.getPos()),
			effect -> effect.apply(world, level, context, user, user.getPos())
		);
	}

	public void onHitBlock(ServerWorld world, int level, EnchantmentEffectContext context, Entity enchantedEntity, Vec3d pos, BlockState state) {
		applyEffects(
			this.getEffect(EnchantmentEffectComponentTypes.HIT_BLOCK),
			createHitBlockLootContext(world, level, enchantedEntity, pos, state),
			effect -> effect.apply(world, level, context, enchantedEntity, pos)
		);
	}

	private void modifyValue(
		ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type, ServerWorld world, int level, ItemStack stack, MutableFloat value
	) {
		applyEffects(
			this.getEffect(type),
			createEnchantedItemLootContext(world, level, stack),
			effect -> value.setValue(effect.apply(level, world.getRandom(), value.getValue()))
		);
	}

	private void modifyValue(
		ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type, ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat value
	) {
		applyEffects(
			this.getEffect(type),
			createEnchantedEntityLootContext(world, level, user, user.getPos()),
			effect -> value.setValue(effect.apply(level, user.getRandom(), value.floatValue()))
		);
	}

	private void modifyValue(
		ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type,
		ServerWorld world,
		int level,
		ItemStack stack,
		Entity user,
		DamageSource damageSource,
		MutableFloat value
	) {
		applyEffects(
			this.getEffect(type),
			createEnchantedDamageLootContext(world, level, user, damageSource),
			effect -> value.setValue(effect.apply(level, user.getRandom(), value.floatValue()))
		);
	}

	public static LootContext createEnchantedDamageLootContext(ServerWorld world, int level, Entity entity, DamageSource damageSource) {
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
			.add(LootContextParameters.THIS_ENTITY, entity)
			.add(LootContextParameters.ENCHANTMENT_LEVEL, level)
			.add(LootContextParameters.ORIGIN, entity.getPos())
			.add(LootContextParameters.DAMAGE_SOURCE, damageSource)
			.addOptional(LootContextParameters.ATTACKING_ENTITY, damageSource.getAttacker())
			.addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, damageSource.getSource())
			.build(LootContextTypes.ENCHANTED_DAMAGE);
		return new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
	}

	private static LootContext createEnchantedItemLootContext(ServerWorld world, int level, ItemStack stack) {
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
			.add(LootContextParameters.TOOL, stack)
			.add(LootContextParameters.ENCHANTMENT_LEVEL, level)
			.build(LootContextTypes.ENCHANTED_ITEM);
		return new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
	}

	private static LootContext createEnchantedLocationLootContext(ServerWorld world, int level, Entity entity, boolean enchantmentActive) {
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
			.add(LootContextParameters.THIS_ENTITY, entity)
			.add(LootContextParameters.ENCHANTMENT_LEVEL, level)
			.add(LootContextParameters.ORIGIN, entity.getPos())
			.add(LootContextParameters.ENCHANTMENT_ACTIVE, enchantmentActive)
			.build(LootContextTypes.ENCHANTED_LOCATION);
		return new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
	}

	private static LootContext createEnchantedEntityLootContext(ServerWorld world, int level, Entity entity, Vec3d pos) {
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
			.add(LootContextParameters.THIS_ENTITY, entity)
			.add(LootContextParameters.ENCHANTMENT_LEVEL, level)
			.add(LootContextParameters.ORIGIN, pos)
			.build(LootContextTypes.ENCHANTED_ENTITY);
		return new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
	}

	private static LootContext createHitBlockLootContext(ServerWorld world, int level, Entity entity, Vec3d pos, BlockState state) {
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
			.add(LootContextParameters.THIS_ENTITY, entity)
			.add(LootContextParameters.ENCHANTMENT_LEVEL, level)
			.add(LootContextParameters.ORIGIN, pos)
			.add(LootContextParameters.BLOCK_STATE, state)
			.build(LootContextTypes.HIT_BLOCK);
		return new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
	}

	private static <T> void applyEffects(List<EnchantmentEffectEntry<T>> entries, LootContext lootContext, Consumer<T> effectConsumer) {
		for (EnchantmentEffectEntry<T> enchantmentEffectEntry : entries) {
			if (enchantmentEffectEntry.test(lootContext)) {
				effectConsumer.accept(enchantmentEffectEntry.effect());
			}
		}
	}

	public void applyLocationBasedEffects(ServerWorld world, int level, EnchantmentEffectContext context, LivingEntity user) {
		if (context.slot() != null && !this.slotMatches(context.slot())) {
			Set<EnchantmentLocationBasedEffect> set = (Set<EnchantmentLocationBasedEffect>)user.getLocationBasedEnchantmentEffects().remove(this);
			if (set != null) {
				set.forEach(effect -> effect.remove(context, user, user.getPos(), level));
			}
		} else {
			Set<EnchantmentLocationBasedEffect> set = (Set<EnchantmentLocationBasedEffect>)user.getLocationBasedEnchantmentEffects().get(this);

			for (EnchantmentEffectEntry<EnchantmentLocationBasedEffect> enchantmentEffectEntry : this.getEffect(EnchantmentEffectComponentTypes.LOCATION_CHANGED)) {
				EnchantmentLocationBasedEffect enchantmentLocationBasedEffect = enchantmentEffectEntry.effect();
				boolean bl = set != null && set.contains(enchantmentLocationBasedEffect);
				if (enchantmentEffectEntry.test(createEnchantedLocationLootContext(world, level, user, bl))) {
					if (!bl) {
						if (set == null) {
							set = new ObjectArraySet<>();
							user.getLocationBasedEnchantmentEffects().put(this, set);
						}

						set.add(enchantmentLocationBasedEffect);
					}

					enchantmentLocationBasedEffect.apply(world, level, context, user, user.getPos(), !bl);
				} else if (set != null && set.remove(enchantmentLocationBasedEffect)) {
					enchantmentLocationBasedEffect.remove(context, user, user.getPos(), level);
				}
			}

			if (set != null && set.isEmpty()) {
				user.getLocationBasedEnchantmentEffects().remove(this);
			}
		}
	}

	public void removeLocationBasedEffects(int level, EnchantmentEffectContext context, LivingEntity user) {
		Set<EnchantmentLocationBasedEffect> set = (Set<EnchantmentLocationBasedEffect>)user.getLocationBasedEnchantmentEffects().remove(this);
		if (set != null) {
			for (EnchantmentLocationBasedEffect enchantmentLocationBasedEffect : set) {
				enchantmentLocationBasedEffect.remove(context, user, user.getPos(), level);
			}
		}
	}

	public static Enchantment.Builder builder(Enchantment.Definition definition) {
		return new Enchantment.Builder(definition);
	}

	public static class Builder {
		private final Enchantment.Definition definition;
		private RegistryEntryList<Enchantment> exclusiveSet = RegistryEntryList.of();
		private final Map<ComponentType<?>, List<?>> effectLists = new HashMap();
		private final ComponentMap.Builder effectMap = ComponentMap.builder();

		public Builder(Enchantment.Definition properties) {
			this.definition = properties;
		}

		public Enchantment.Builder exclusiveSet(RegistryEntryList<Enchantment> exclusiveSet) {
			this.exclusiveSet = exclusiveSet;
			return this;
		}

		public <E> Enchantment.Builder addEffect(ComponentType<List<EnchantmentEffectEntry<E>>> effectType, E effect, LootCondition.Builder requirements) {
			this.getEffectsList(effectType).add(new EnchantmentEffectEntry<>(effect, Optional.of(requirements.build())));
			return this;
		}

		public <E> Enchantment.Builder addEffect(ComponentType<List<EnchantmentEffectEntry<E>>> effectType, E effect) {
			this.getEffectsList(effectType).add(new EnchantmentEffectEntry<>(effect, Optional.empty()));
			return this;
		}

		public <E> Enchantment.Builder addEffect(
			ComponentType<List<TargetedEnchantmentEffect<E>>> type,
			EnchantmentEffectTarget enchanted,
			EnchantmentEffectTarget affected,
			E effect,
			LootCondition.Builder requirements
		) {
			this.getEffectsList(type).add(new TargetedEnchantmentEffect<>(enchanted, affected, effect, Optional.of(requirements.build())));
			return this;
		}

		public <E> Enchantment.Builder addEffect(
			ComponentType<List<TargetedEnchantmentEffect<E>>> type, EnchantmentEffectTarget enchanted, EnchantmentEffectTarget affected, E effect
		) {
			this.getEffectsList(type).add(new TargetedEnchantmentEffect<>(enchanted, affected, effect, Optional.empty()));
			return this;
		}

		public Enchantment.Builder addEffect(ComponentType<List<AttributeEnchantmentEffect>> type, AttributeEnchantmentEffect effect) {
			this.getEffectsList(type).add(effect);
			return this;
		}

		public <E> Enchantment.Builder addNonListEffect(ComponentType<E> type, E effect) {
			this.effectMap.add(type, effect);
			return this;
		}

		public Enchantment.Builder addEffect(ComponentType<Unit> type) {
			this.effectMap.add(type, Unit.INSTANCE);
			return this;
		}

		private <E> List<E> getEffectsList(ComponentType<List<E>> type) {
			return (List<E>)this.effectLists.computeIfAbsent(type, typex -> {
				ArrayList<E> arrayList = new ArrayList();
				this.effectMap.add(type, arrayList);
				return arrayList;
			});
		}

		public Enchantment build(Identifier id) {
			return new Enchantment(Text.translatable(Util.createTranslationKey("enchantment", id)), this.definition, this.exclusiveSet, this.effectMap.build());
		}
	}

	public static record Cost(int base, int perLevelAboveFirst) {
		public static final Codec<Enchantment.Cost> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("base").forGetter(Enchantment.Cost::base), Codec.INT.fieldOf("per_level_above_first").forGetter(Enchantment.Cost::perLevelAboveFirst)
					)
					.apply(instance, Enchantment.Cost::new)
		);

		public int forLevel(int level) {
			return this.base + this.perLevelAboveFirst * (level - 1);
		}
	}

	public static record Definition(
		RegistryEntryList<Item> supportedItems,
		Optional<RegistryEntryList<Item>> primaryItems,
		int weight,
		int maxLevel,
		Enchantment.Cost minCost,
		Enchantment.Cost maxCost,
		int anvilCost,
		List<AttributeModifierSlot> slots
	) {
		public static final MapCodec<Enchantment.Definition> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("supported_items").forGetter(Enchantment.Definition::supportedItems),
						RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("primary_items").forGetter(Enchantment.Definition::primaryItems),
						Codecs.rangedInt(1, 1024).fieldOf("weight").forGetter(Enchantment.Definition::weight),
						Codecs.rangedInt(1, 255).fieldOf("max_level").forGetter(Enchantment.Definition::maxLevel),
						Enchantment.Cost.CODEC.fieldOf("min_cost").forGetter(Enchantment.Definition::minCost),
						Enchantment.Cost.CODEC.fieldOf("max_cost").forGetter(Enchantment.Definition::maxCost),
						Codecs.NONNEGATIVE_INT.fieldOf("anvil_cost").forGetter(Enchantment.Definition::anvilCost),
						AttributeModifierSlot.CODEC.listOf().fieldOf("slots").forGetter(Enchantment.Definition::slots)
					)
					.apply(instance, Enchantment.Definition::new)
		);
	}
}
