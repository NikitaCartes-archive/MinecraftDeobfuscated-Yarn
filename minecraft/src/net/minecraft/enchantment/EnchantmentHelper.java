package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;

public class EnchantmentHelper {
	/**
	 * Gets the level of an enchantment on an item stack.
	 */
	public static int getLevel(RegistryEntry<Enchantment> enchantment, ItemStack stack) {
		ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
		return itemEnchantmentsComponent.getLevel(enchantment);
	}

	public static ItemEnchantmentsComponent apply(ItemStack stack, java.util.function.Consumer<ItemEnchantmentsComponent.Builder> applier) {
		ComponentType<ItemEnchantmentsComponent> componentType = getEnchantmentsComponentType(stack);
		ItemEnchantmentsComponent itemEnchantmentsComponent = stack.get(componentType);
		if (itemEnchantmentsComponent == null) {
			return ItemEnchantmentsComponent.DEFAULT;
		} else {
			ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(itemEnchantmentsComponent);
			applier.accept(builder);
			ItemEnchantmentsComponent itemEnchantmentsComponent2 = builder.build();
			stack.set(componentType, itemEnchantmentsComponent2);
			return itemEnchantmentsComponent2;
		}
	}

	public static boolean canHaveEnchantments(ItemStack stack) {
		return stack.contains(getEnchantmentsComponentType(stack));
	}

	public static void set(ItemStack stack, ItemEnchantmentsComponent enchantments) {
		stack.set(getEnchantmentsComponentType(stack), enchantments);
	}

	public static ItemEnchantmentsComponent getEnchantments(ItemStack stack) {
		return stack.getOrDefault(getEnchantmentsComponentType(stack), ItemEnchantmentsComponent.DEFAULT);
	}

	private static ComponentType<ItemEnchantmentsComponent> getEnchantmentsComponentType(ItemStack stack) {
		return stack.isOf(Items.ENCHANTED_BOOK) ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS;
	}

	public static boolean hasEnchantments(ItemStack stack) {
		return !stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty()
			|| !stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).isEmpty();
	}

	public static int getItemDamage(ServerWorld world, ItemStack stack, int baseItemDamage) {
		MutableFloat mutableFloat = new MutableFloat((float)baseItemDamage);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyItemDamage(world, level, stack, mutableFloat));
		return mutableFloat.intValue();
	}

	public static int getAmmoUse(ServerWorld world, ItemStack rangedWeaponStack, ItemStack projectileStack, int baseAmmoUse) {
		MutableFloat mutableFloat = new MutableFloat((float)baseAmmoUse);
		forEachEnchantment(rangedWeaponStack, (enchantment, level) -> enchantment.value().modifyAmmoUse(world, level, projectileStack, mutableFloat));
		return mutableFloat.intValue();
	}

	public static int getBlockExperience(ServerWorld world, ItemStack stack, int baseBlockExperience) {
		MutableFloat mutableFloat = new MutableFloat((float)baseBlockExperience);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyBlockExperience(world, level, stack, mutableFloat));
		return mutableFloat.intValue();
	}

	public static int getMobExperience(ServerWorld world, @Nullable Entity attacker, Entity mob, int baseMobExperience) {
		if (attacker instanceof LivingEntity livingEntity) {
			MutableFloat mutableFloat = new MutableFloat((float)baseMobExperience);
			forEachEnchantment(livingEntity, (enchantment, level, context) -> enchantment.value().modifyMobExperience(world, level, context.stack(), mob, mutableFloat));
			return mutableFloat.intValue();
		} else {
			return baseMobExperience;
		}
	}

	private static void forEachEnchantment(ItemStack stack, EnchantmentHelper.Consumer consumer) {
		ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

		for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
			consumer.accept((RegistryEntry<Enchantment>)entry.getKey(), entry.getIntValue());
		}
	}

	private static void forEachEnchantment(ItemStack stack, EquipmentSlot slot, LivingEntity entity, EnchantmentHelper.ContextAwareConsumer contextAwareConsumer) {
		if (!stack.isEmpty()) {
			ItemEnchantmentsComponent itemEnchantmentsComponent = stack.get(DataComponentTypes.ENCHANTMENTS);
			if (itemEnchantmentsComponent != null && !itemEnchantmentsComponent.isEmpty()) {
				EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(stack, slot, entity);

				for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
					RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
					if (registryEntry.value().slotMatches(slot)) {
						contextAwareConsumer.accept(registryEntry, entry.getIntValue(), enchantmentEffectContext);
					}
				}
			}
		}
	}

	private static void forEachEnchantment(LivingEntity entity, EnchantmentHelper.ContextAwareConsumer contextAwareConsumer) {
		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			forEachEnchantment(entity.getEquippedStack(equipmentSlot), equipmentSlot, entity, contextAwareConsumer);
		}
	}

	public static boolean isInvulnerableTo(ServerWorld world, LivingEntity user, DamageSource damageSource) {
		MutableBoolean mutableBoolean = new MutableBoolean();
		forEachEnchantment(
			user,
			(enchantment, level, context) -> mutableBoolean.setValue(
					mutableBoolean.isTrue() || enchantment.value().hasDamageImmunityTo(world, level, user, damageSource)
				)
		);
		return mutableBoolean.isTrue();
	}

	public static float getProtectionAmount(ServerWorld world, LivingEntity user, DamageSource damageSource) {
		MutableFloat mutableFloat = new MutableFloat(0.0F);
		forEachEnchantment(
			user, (enchantment, level, context) -> enchantment.value().modifyDamageProtection(world, level, context.stack(), user, damageSource, mutableFloat)
		);
		return mutableFloat.floatValue();
	}

	public static float getDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseDamage) {
		MutableFloat mutableFloat = new MutableFloat(baseDamage);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyDamage(world, level, stack, target, damageSource, mutableFloat));
		return mutableFloat.floatValue();
	}

	public static float getSmashDamagePerFallenBlock(
		ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseSmashDamagePerFallenBlock
	) {
		MutableFloat mutableFloat = new MutableFloat(baseSmashDamagePerFallenBlock);
		forEachEnchantment(
			stack, (enchantment, level) -> enchantment.value().modifySmashDamagePerFallenBlock(world, level, stack, target, damageSource, mutableFloat)
		);
		return mutableFloat.floatValue();
	}

	public static float getArmorEffectiveness(ServerWorld world, ItemStack stack, Entity user, DamageSource damageSource, float baseArmorEffectiveness) {
		MutableFloat mutableFloat = new MutableFloat(baseArmorEffectiveness);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyArmorEffectiveness(world, level, stack, user, damageSource, mutableFloat));
		return mutableFloat.floatValue();
	}

	public static float modifyKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseKnockback) {
		MutableFloat mutableFloat = new MutableFloat(baseKnockback);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyKnockback(world, level, stack, target, damageSource, mutableFloat));
		return mutableFloat.floatValue();
	}

	public static void onTargetDamaged(ServerWorld world, Entity target, DamageSource damageSource) {
		if (damageSource.getAttacker() instanceof LivingEntity livingEntity) {
			onTargetDamaged(world, target, damageSource, livingEntity.getWeaponStack());
		} else {
			onTargetDamaged(world, target, damageSource, null);
		}
	}

	public static void onTargetDamaged(ServerWorld world, Entity target, DamageSource damageSource, @Nullable ItemStack weapon) {
		if (target instanceof LivingEntity livingEntity) {
			forEachEnchantment(
				livingEntity,
				(enchantment, level, context) -> enchantment.value().onTargetDamaged(world, level, context, EnchantmentEffectTarget.VICTIM, target, damageSource)
			);
		}

		if (weapon != null && damageSource.getAttacker() instanceof LivingEntity livingEntity) {
			forEachEnchantment(
				weapon,
				EquipmentSlot.MAINHAND,
				livingEntity,
				(enchantment, level, context) -> enchantment.value().onTargetDamaged(world, level, context, EnchantmentEffectTarget.ATTACKER, target, damageSource)
			);
		}
	}

	public static void applyLocationBasedEffects(ServerWorld world, LivingEntity user) {
		forEachEnchantment(user, (enchantment, level, context) -> enchantment.value().applyLocationBasedEffects(world, level, context, user));
	}

	public static void applyLocationBasedEffects(ServerWorld world, ItemStack stack, LivingEntity user, EquipmentSlot slot) {
		forEachEnchantment(stack, slot, user, (enchantment, level, context) -> enchantment.value().applyLocationBasedEffects(world, level, context, user));
	}

	public static void removeLocationBasedEffects(LivingEntity user) {
		forEachEnchantment(user, (enchantment, level, context) -> enchantment.value().removeLocationBasedEffects(level, context, user));
	}

	public static void removeLocationBasedEffects(ItemStack stack, LivingEntity user, EquipmentSlot slot) {
		forEachEnchantment(stack, slot, user, (enchantment, level, context) -> enchantment.value().removeLocationBasedEffects(level, context, user));
	}

	public static void onTick(ServerWorld world, LivingEntity user) {
		forEachEnchantment(user, (enchantment, level, context) -> enchantment.value().onTick(world, level, context, user));
	}

	/**
	 * {@return the highest level of the passed enchantment in the enchantment's
	 * applicable equipment slots' item stacks}
	 * 
	 * @param entity the entity whose equipment slots are checked
	 */
	public static int getEquipmentLevel(RegistryEntry<Enchantment> enchantment, LivingEntity entity) {
		Iterable<ItemStack> iterable = enchantment.value().getEquipment(entity).values();
		int i = 0;

		for (ItemStack itemStack : iterable) {
			int j = getLevel(enchantment, itemStack);
			if (j > i) {
				i = j;
			}
		}

		return i;
	}

	public static int getProjectileCount(ServerWorld world, ItemStack stack, Entity user, int baseProjectileCount) {
		MutableFloat mutableFloat = new MutableFloat((float)baseProjectileCount);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyProjectileCount(world, level, stack, user, mutableFloat));
		return Math.max(0, mutableFloat.intValue());
	}

	public static float getProjectileSpread(ServerWorld world, ItemStack stack, Entity user, float baseProjectileSpread) {
		MutableFloat mutableFloat = new MutableFloat(baseProjectileSpread);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyProjectileSpread(world, level, stack, user, mutableFloat));
		return Math.max(0.0F, mutableFloat.floatValue());
	}

	public static int getProjectilePiercing(ServerWorld world, ItemStack weaponStack, ItemStack projectileStack) {
		MutableFloat mutableFloat = new MutableFloat(0.0F);
		forEachEnchantment(weaponStack, (enchantment, level) -> enchantment.value().modifyProjectilePiercing(world, level, projectileStack, mutableFloat));
		return Math.max(0, mutableFloat.intValue());
	}

	public static void onProjectileSpawned(
		ServerWorld world, ItemStack weaponStack, PersistentProjectileEntity projectileEntity, java.util.function.Consumer<Item> onBreak
	) {
		LivingEntity livingEntity2 = projectileEntity.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
		EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(weaponStack, null, livingEntity2, onBreak);
		forEachEnchantment(weaponStack, (enchantment, level) -> enchantment.value().onProjectileSpawned(world, level, enchantmentEffectContext, projectileEntity));
	}

	public static void onHitBlock(
		ServerWorld world,
		ItemStack stack,
		@Nullable LivingEntity user,
		Entity enchantedEntity,
		@Nullable EquipmentSlot slot,
		Vec3d pos,
		BlockState state,
		java.util.function.Consumer<Item> onBreak
	) {
		EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(stack, slot, user, onBreak);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().onHitBlock(world, level, enchantmentEffectContext, enchantedEntity, pos, state));
	}

	public static int getRepairWithXp(ServerWorld world, ItemStack stack, int baseRepairWithXp) {
		MutableFloat mutableFloat = new MutableFloat((float)baseRepairWithXp);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyRepairWithXp(world, level, stack, mutableFloat));
		return Math.max(0, mutableFloat.intValue());
	}

	public static float getEquipmentDropChance(ServerWorld world, LivingEntity attacker, DamageSource damageSource, float baseEquipmentDropChance) {
		MutableFloat mutableFloat = new MutableFloat(baseEquipmentDropChance);
		Random random = attacker.getRandom();
		forEachEnchantment(attacker, (enchantment, level, context) -> {
			LootContext lootContext = Enchantment.createEnchantedDamageLootContext(world, level, attacker, damageSource);
			enchantment.value().getEffect(EnchantmentEffectComponentTypes.EQUIPMENT_DROPS).forEach(effect -> {
				if (effect.enchanted() == EnchantmentEffectTarget.VICTIM && effect.affected() == EnchantmentEffectTarget.VICTIM && effect.test(lootContext)) {
					mutableFloat.setValue(((EnchantmentValueEffect)effect.effect()).apply(level, random, mutableFloat.floatValue()));
				}
			});
		});
		if (damageSource.getAttacker() instanceof LivingEntity livingEntity) {
			forEachEnchantment(livingEntity, (enchantment, level, context) -> {
				LootContext lootContext = Enchantment.createEnchantedDamageLootContext(world, level, attacker, damageSource);
				enchantment.value().getEffect(EnchantmentEffectComponentTypes.EQUIPMENT_DROPS).forEach(effect -> {
					if (effect.enchanted() == EnchantmentEffectTarget.ATTACKER && effect.affected() == EnchantmentEffectTarget.VICTIM && effect.test(lootContext)) {
						mutableFloat.setValue(((EnchantmentValueEffect)effect.effect()).apply(level, random, mutableFloat.floatValue()));
					}
				});
			});
		}

		return mutableFloat.floatValue();
	}

	public static void applyAttributeModifiers(
		ItemStack stack, AttributeModifierSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer
	) {
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().getEffect(EnchantmentEffectComponentTypes.ATTRIBUTES).forEach(effect -> {
				if (((Enchantment)enchantment.value()).definition().slots().contains(slot)) {
					attributeModifierConsumer.accept(effect.attribute(), effect.createAttributeModifier(level, slot));
				}
			}));
	}

	public static void applyAttributeModifiers(
		ItemStack stack, EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer
	) {
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().getEffect(EnchantmentEffectComponentTypes.ATTRIBUTES).forEach(effect -> {
				if (((Enchantment)enchantment.value()).slotMatches(slot)) {
					attributeModifierConsumer.accept(effect.attribute(), effect.createAttributeModifier(level, slot));
				}
			}));
	}

	public static int getFishingLuckBonus(ServerWorld world, ItemStack stack, Entity user) {
		MutableFloat mutableFloat = new MutableFloat(0.0F);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyFishingLuckBonus(world, level, stack, user, mutableFloat));
		return Math.max(0, mutableFloat.intValue());
	}

	public static float getFishingTimeReduction(ServerWorld world, ItemStack stack, Entity user) {
		MutableFloat mutableFloat = new MutableFloat(0.0F);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyFishingTimeReduction(world, level, stack, user, mutableFloat));
		return Math.max(0.0F, mutableFloat.floatValue());
	}

	public static int getTridentReturnAcceleration(ServerWorld world, ItemStack stack, Entity user) {
		MutableFloat mutableFloat = new MutableFloat(0.0F);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyTridentReturnAcceleration(world, level, stack, user, mutableFloat));
		return Math.max(0, mutableFloat.intValue());
	}

	public static float getCrossbowChargeTime(ItemStack stack, LivingEntity user, float baseCrossbowChargeTime) {
		MutableFloat mutableFloat = new MutableFloat(baseCrossbowChargeTime);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyCrossbowChargeTime(user.getRandom(), level, mutableFloat));
		return Math.max(0.0F, mutableFloat.floatValue());
	}

	public static float getTridentSpinAttackStrength(ItemStack stack, LivingEntity user) {
		MutableFloat mutableFloat = new MutableFloat(0.0F);
		forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyTridentSpinAttackStrength(user.getRandom(), level, mutableFloat));
		return mutableFloat.floatValue();
	}

	public static boolean hasAnyEnchantmentsIn(ItemStack stack, TagKey<Enchantment> tag) {
		ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

		for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
			RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
			if (registryEntry.isIn(tag)) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasAnyEnchantmentsWith(ItemStack stack, ComponentType<?> componentType) {
		MutableBoolean mutableBoolean = new MutableBoolean(false);
		forEachEnchantment(stack, (enchantment, level) -> {
			if (enchantment.value().effects().contains(componentType)) {
				mutableBoolean.setTrue();
			}
		});
		return mutableBoolean.booleanValue();
	}

	public static <T> Optional<T> getEffect(ItemStack stack, ComponentType<List<T>> componentType) {
		Pair<List<T>, Integer> pair = getEffectListAndLevel(stack, componentType);
		if (pair != null) {
			List<T> list = pair.getFirst();
			int i = pair.getSecond();
			return Optional.of(list.get(Math.min(i, list.size()) - 1));
		} else {
			return Optional.empty();
		}
	}

	@Nullable
	public static <T> Pair<T, Integer> getEffectListAndLevel(ItemStack stack, ComponentType<T> componentType) {
		MutableObject<Pair<T, Integer>> mutableObject = new MutableObject<>();
		forEachEnchantment(stack, (enchantment, level) -> {
			if (mutableObject.getValue() == null || mutableObject.getValue().getSecond() < level) {
				T object = enchantment.value().effects().get(componentType);
				if (object != null) {
					mutableObject.setValue(Pair.of(object, level));
				}
			}
		});
		return mutableObject.getValue();
	}

	/**
	 * {@return a pair of an equipment slot and the item stack in the supplied
	 * entity's slot} It indicates the item stack has the enchantment supplied.
	 * 
	 * <p>If multiple equipment slots' item stacks are valid, a random pair is
	 * returned.
	 */
	public static Optional<EnchantmentEffectContext> chooseEquipmentWith(ComponentType<?> componentType, LivingEntity entity, Predicate<ItemStack> stackPredicate) {
		List<EnchantmentEffectContext> list = new ArrayList();

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
			if (stackPredicate.test(itemStack)) {
				ItemEnchantmentsComponent itemEnchantmentsComponent = itemStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

				for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
					RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
					if (registryEntry.value().effects().contains(componentType) && registryEntry.value().slotMatches(equipmentSlot)) {
						list.add(new EnchantmentEffectContext(itemStack, equipmentSlot, entity));
					}
				}
			}
		}

		return Util.getRandomOrEmpty(list, entity.getRandom());
	}

	/**
	 * {@return the required experience level for an enchanting option in the
	 * enchanting table's screen, or the enchantment screen}
	 * 
	 * @param bookshelfCount the number of bookshelves
	 * @param stack the item stack to enchant
	 * @param random the random, which guarantees consistent results with the same seed
	 * @param slotIndex the index of the enchanting option
	 */
	public static int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return 0;
		} else {
			if (bookshelfCount > 15) {
				bookshelfCount = 15;
			}

			int j = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
			if (slotIndex == 0) {
				return Math.max(j / 3, 1);
			} else {
				return slotIndex == 1 ? j * 2 / 3 + 1 : Math.max(j, bookshelfCount * 2);
			}
		}
	}

	public static ItemStack enchant(
		Random random, ItemStack stack, int level, DynamicRegistryManager dynamicRegistryManager, Optional<? extends RegistryEntryList<Enchantment>> enchantments
	) {
		return enchant(
			random,
			stack,
			level,
			(Stream<RegistryEntry<Enchantment>>)enchantments.map(RegistryEntryList::stream)
				.orElseGet(() -> dynamicRegistryManager.get(RegistryKeys.ENCHANTMENT).streamEntries().map(reference -> reference))
		);
	}

	/**
	 * Enchants the {@code target} item stack and returns it.
	 */
	public static ItemStack enchant(Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
		List<EnchantmentLevelEntry> list = generateEnchantments(random, stack, level, possibleEnchantments);
		if (stack.isOf(Items.BOOK)) {
			stack = new ItemStack(Items.ENCHANTED_BOOK);
		}

		for (EnchantmentLevelEntry enchantmentLevelEntry : list) {
			stack.addEnchantment(enchantmentLevelEntry.enchantment, enchantmentLevelEntry.level);
		}

		return stack;
	}

	/**
	 * Generate the enchantments for enchanting the {@code stack}.
	 */
	public static List<EnchantmentLevelEntry> generateEnchantments(
		Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments
	) {
		List<EnchantmentLevelEntry> list = Lists.<EnchantmentLevelEntry>newArrayList();
		Item item = stack.getItem();
		int i = item.getEnchantability();
		if (i <= 0) {
			return list;
		} else {
			level += 1 + random.nextInt(i / 4 + 1) + random.nextInt(i / 4 + 1);
			float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
			level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
			List<EnchantmentLevelEntry> list2 = getPossibleEntries(level, stack, possibleEnchantments);
			if (!list2.isEmpty()) {
				Weighting.getRandom(random, list2).ifPresent(list::add);

				while (random.nextInt(50) <= level) {
					if (!list.isEmpty()) {
						removeConflicts(list2, Util.getLast(list));
					}

					if (list2.isEmpty()) {
						break;
					}

					Weighting.getRandom(random, list2).ifPresent(list::add);
					level /= 2;
				}
			}

			return list;
		}
	}

	/**
	 * Remove entries conflicting with the picked entry from the possible
	 * entries.
	 * 
	 * @param possibleEntries the possible entries
	 * @param pickedEntry the picked entry
	 */
	public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
		possibleEntries.removeIf(entry -> !Enchantment.canBeCombined(pickedEntry.enchantment, entry.enchantment));
	}

	/**
	 * {@return whether the {@code candidate} enchantment is compatible with the
	 * {@code existing} enchantments}
	 */
	public static boolean isCompatible(Collection<RegistryEntry<Enchantment>> existing, RegistryEntry<Enchantment> candidate) {
		for (RegistryEntry<Enchantment> registryEntry : existing) {
			if (!Enchantment.canBeCombined(registryEntry, candidate)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Gets all the possible entries for enchanting the {@code stack} at the
	 * given {@code power}.
	 */
	public static List<EnchantmentLevelEntry> getPossibleEntries(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
		List<EnchantmentLevelEntry> list = Lists.<EnchantmentLevelEntry>newArrayList();
		boolean bl = stack.isOf(Items.BOOK);
		possibleEnchantments.filter(enchantment -> ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl).forEach(enchantmentx -> {
			Enchantment enchantment = (Enchantment)enchantmentx.value();

			for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
				if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
					list.add(new EnchantmentLevelEntry(enchantmentx, j));
					break;
				}
			}
		});
		return list;
	}

	public static void applyEnchantmentProvider(
		ItemStack stack, DynamicRegistryManager registryManager, RegistryKey<EnchantmentProvider> providerKey, LocalDifficulty localDifficulty, Random random
	) {
		EnchantmentProvider enchantmentProvider = registryManager.get(RegistryKeys.ENCHANTMENT_PROVIDER).get(providerKey);
		if (enchantmentProvider != null) {
			apply(stack, componentBuilder -> enchantmentProvider.provideEnchantments(stack, componentBuilder, random, localDifficulty));
		}
	}

	@FunctionalInterface
	interface Consumer {
		void accept(RegistryEntry<Enchantment> enchantment, int level);
	}

	@FunctionalInterface
	interface ContextAwareConsumer {
		void accept(RegistryEntry<Enchantment> enchantment, int level, EnchantmentEffectContext context);
	}
}
