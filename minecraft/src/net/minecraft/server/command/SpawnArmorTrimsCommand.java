package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnArmorTrimsCommand {
	private static final List<RegistryKey<ArmorTrimPattern>> PATTERNS = List.of(
		ArmorTrimPatterns.SENTRY,
		ArmorTrimPatterns.DUNE,
		ArmorTrimPatterns.COAST,
		ArmorTrimPatterns.WILD,
		ArmorTrimPatterns.WARD,
		ArmorTrimPatterns.EYE,
		ArmorTrimPatterns.VEX,
		ArmorTrimPatterns.TIDE,
		ArmorTrimPatterns.SNOUT,
		ArmorTrimPatterns.RIB,
		ArmorTrimPatterns.SPIRE,
		ArmorTrimPatterns.WAYFINDER,
		ArmorTrimPatterns.SHAPER,
		ArmorTrimPatterns.SILENCE,
		ArmorTrimPatterns.RAISER,
		ArmorTrimPatterns.HOST,
		ArmorTrimPatterns.FLOW,
		ArmorTrimPatterns.BOLT
	);
	private static final List<RegistryKey<ArmorTrimMaterial>> MATERIALS = List.of(
		ArmorTrimMaterials.QUARTZ,
		ArmorTrimMaterials.IRON,
		ArmorTrimMaterials.NETHERITE,
		ArmorTrimMaterials.REDSTONE,
		ArmorTrimMaterials.COPPER,
		ArmorTrimMaterials.GOLD,
		ArmorTrimMaterials.EMERALD,
		ArmorTrimMaterials.DIAMOND,
		ArmorTrimMaterials.LAPIS,
		ArmorTrimMaterials.AMETHYST
	);
	private static final ToIntFunction<RegistryKey<ArmorTrimPattern>> PATTERN_INDEX_GETTER = Util.lastIndexGetter(PATTERNS);
	private static final ToIntFunction<RegistryKey<ArmorTrimMaterial>> MATERIAL_INDEX_GETTER = Util.lastIndexGetter(MATERIALS);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("spawn_armor_trims")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(context -> execute(context.getSource(), context.getSource().getPlayerOrThrow()))
		);
	}

	private static int execute(ServerCommandSource source, PlayerEntity player) {
		World world = player.getWorld();
		DefaultedList<ArmorTrim> defaultedList = DefaultedList.of();
		Registry<ArmorTrimPattern> registry = world.getRegistryManager().getOrThrow(RegistryKeys.TRIM_PATTERN);
		Registry<ArmorTrimMaterial> registry2 = world.getRegistryManager().getOrThrow(RegistryKeys.TRIM_MATERIAL);
		RegistryWrapper<Item> registryWrapper = world.createCommandRegistryWrapper(RegistryKeys.ITEM);
		Map<Identifier, List<Item>> map = (Map<Identifier, List<Item>>)registryWrapper.streamEntries()
			.map(RegistryEntry.Reference::value)
			.filter(
				itemx -> {
					EquippableComponent equippableComponentx = itemx.getComponents().get(DataComponentTypes.EQUIPPABLE);
					return equippableComponentx != null
						&& equippableComponentx.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR
						&& equippableComponentx.model().isPresent();
				}
			)
			.collect(Collectors.groupingBy(itemx -> (Identifier)itemx.getComponents().get(DataComponentTypes.EQUIPPABLE).model().get()));
		registry.stream()
			.sorted(Comparator.comparing(pattern -> PATTERN_INDEX_GETTER.applyAsInt((RegistryKey)registry.getKey(pattern).orElse(null))))
			.forEachOrdered(
				pattern -> registry2.stream()
						.sorted(Comparator.comparing(material -> MATERIAL_INDEX_GETTER.applyAsInt((RegistryKey)registry2.getKey(material).orElse(null))))
						.forEachOrdered(material -> defaultedList.add(new ArmorTrim(registry2.getEntry(material), registry.getEntry(pattern))))
			);
		BlockPos blockPos = player.getBlockPos().offset(player.getHorizontalFacing(), 5);
		int i = map.size() - 1;
		double d = 3.0;
		int j = 0;
		int k = 0;

		for (ArmorTrim armorTrim : defaultedList) {
			for (List<Item> list : map.values()) {
				double e = (double)blockPos.getX() + 0.5 - (double)(j % registry2.size()) * 3.0;
				double f = (double)blockPos.getY() + 0.5 + (double)(k % i) * 3.0;
				double g = (double)blockPos.getZ() + 0.5 + (double)(j / registry2.size() * 10);
				ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, e, f, g);
				armorStandEntity.setYaw(180.0F);
				armorStandEntity.setNoGravity(true);

				for (Item item : list) {
					EquippableComponent equippableComponent = (EquippableComponent)Objects.requireNonNull(item.getComponents().get(DataComponentTypes.EQUIPPABLE));
					ItemStack itemStack = new ItemStack(item);
					itemStack.set(DataComponentTypes.TRIM, armorTrim);
					armorStandEntity.equipStack(equippableComponent.slot(), itemStack);
					if (itemStack.isOf(Items.TURTLE_HELMET)) {
						armorStandEntity.setCustomName(
							armorTrim.pattern().value().getDescription(armorTrim.material()).copy().append(" ").append(armorTrim.material().value().description())
						);
						armorStandEntity.setCustomNameVisible(true);
					} else {
						armorStandEntity.setInvisible(true);
					}
				}

				world.spawnEntity(armorStandEntity);
				k++;
			}

			j++;
		}

		source.sendFeedback(() -> Text.literal("Armorstands with trimmed armor spawned around you"), true);
		return 1;
	}
}
