package net.minecraft;

import com.mojang.logging.LogUtils;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Language;
import net.minecraft.util.Util;
import net.minecraft.util.logging.DebugLoggerPrintStream;
import net.minecraft.util.logging.LoggerPrintStream;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import org.slf4j.Logger;

public class Bootstrap {
	public static final PrintStream SYSOUT = System.out;
	private static volatile boolean initialized;
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void initialize() {
		if (!initialized) {
			initialized = true;
			if (Registry.REGISTRIES.getIds().isEmpty()) {
				throw new IllegalStateException("Unable to load registries");
			} else {
				FireBlock.registerDefaultFlammables();
				ComposterBlock.registerDefaultCompostableItems();
				if (EntityType.getId(EntityType.PLAYER) == null) {
					throw new IllegalStateException("Failed loading EntityTypes");
				} else {
					BrewingRecipeRegistry.registerDefaults();
					EntitySelectorOptions.register();
					DispenserBehavior.registerDefaults();
					CauldronBehavior.registerBehavior();
					Registry.freezeRegistries();
					setOutputStreams();
				}
			}
		}
	}

	private static <T> void collectMissingTranslations(Iterable<T> registry, Function<T, String> keyExtractor, Set<String> translationKeys) {
		Language language = Language.getInstance();
		registry.forEach(object -> {
			String string = (String)keyExtractor.apply(object);
			if (!language.hasTranslation(string)) {
				translationKeys.add(string);
			}
		});
	}

	private static void collectMissingGameRuleTranslations(Set<String> translations) {
		final Language language = Language.getInstance();
		GameRules.accept(new GameRules.Visitor() {
			@Override
			public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
				if (!language.hasTranslation(key.getTranslationKey())) {
					translations.add(key.getName());
				}
			}
		});
	}

	public static Set<String> getMissingTranslations() {
		Set<String> set = new TreeSet();
		collectMissingTranslations(Registry.ATTRIBUTE, EntityAttribute::getTranslationKey, set);
		collectMissingTranslations(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
		collectMissingTranslations(Registry.STATUS_EFFECT, StatusEffect::getTranslationKey, set);
		collectMissingTranslations(Registry.ITEM, Item::getTranslationKey, set);
		collectMissingTranslations(Registry.ENCHANTMENT, Enchantment::getTranslationKey, set);
		collectMissingTranslations(Registry.BLOCK, Block::getTranslationKey, set);
		collectMissingTranslations(Registry.CUSTOM_STAT, stat -> "stat." + stat.toString().replace(':', '.'), set);
		collectMissingGameRuleTranslations(set);
		return set;
	}

	public static void ensureBootstrapped(Supplier<String> callerGetter) {
		if (!initialized) {
			throw createNotBootstrappedException(callerGetter);
		}
	}

	private static RuntimeException createNotBootstrappedException(Supplier<String> callerGetter) {
		try {
			String string = (String)callerGetter.get();
			return new IllegalArgumentException("Not bootstrapped (called from " + string + ")");
		} catch (Exception var3) {
			RuntimeException runtimeException = new IllegalArgumentException("Not bootstrapped (failed to resolve location)");
			runtimeException.addSuppressed(var3);
			return runtimeException;
		}
	}

	public static void logMissing() {
		ensureBootstrapped(() -> "validate");
		if (SharedConstants.isDevelopment) {
			getMissingTranslations().forEach(key -> LOGGER.error("Missing translations: {}", key));
			CommandManager.checkMissing();
			logMissingBiomePlacementModifier();
		}

		DefaultAttributeRegistry.checkMissing();
	}

	private static void logMissingBiomePlacementModifier() {
		BuiltinRegistries.BIOME.stream().forEach(biome -> {
			List<RegistryEntryList<PlacedFeature>> list = biome.getGenerationSettings().getFeatures();
			list.stream().flatMap(RegistryEntryList::stream).forEach(registryEntry -> {
				if (!((PlacedFeature)registryEntry.value()).placementModifiers().contains(BiomePlacementModifier.of())) {
					Util.error("Placed feature " + BuiltinRegistries.PLACED_FEATURE.getKey((PlacedFeature)registryEntry.value()) + " is missing BiomeFilter.biome()");
				}
			});
		});
	}

	private static void setOutputStreams() {
		if (LOGGER.isDebugEnabled()) {
			System.setErr(new DebugLoggerPrintStream("STDERR", System.err));
			System.setOut(new DebugLoggerPrintStream("STDOUT", SYSOUT));
		} else {
			System.setErr(new LoggerPrintStream("STDERR", System.err));
			System.setOut(new LoggerPrintStream("STDOUT", SYSOUT));
		}
	}

	public static void println(String str) {
		SYSOUT.println(str);
	}
}
