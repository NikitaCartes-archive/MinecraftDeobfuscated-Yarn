package net.minecraft;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.command.EntitySelectorOptions;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.DebugPrintStreamLogger;
import net.minecraft.util.Language;
import net.minecraft.util.PrintStreamLogger;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
	public static final PrintStream SYSOUT = System.out;
	private static boolean initialized;
	private static final Logger LOGGER = LogManager.getLogger();

	public static void initialize() {
		if (!initialized) {
			initialized = true;
			if (Registry.REGISTRIES.isEmpty()) {
				throw new IllegalStateException("Unable to load registries");
			} else {
				FireBlock.registerDefaultFlammables();
				ComposterBlock.registerDefaultCompostableItems();
				if (EntityType.getId(EntityType.field_6097) == null) {
					throw new IllegalStateException("Failed loading EntityTypes");
				} else {
					BrewingRecipeRegistry.registerDefaults();
					EntitySelectorOptions.register();
					DispenserBehavior.registerDefaults();
					ArgumentTypes.register();
					setOutputStreams();
				}
			}
		}
	}

	private static <T> void collectMissingTranslations(Registry<T> registry, Function<T, String> function, Set<String> set) {
		Language language = Language.getInstance();
		registry.iterator().forEachRemaining(object -> {
			String string = (String)function.apply(object);
			if (!language.hasTranslation(string)) {
				set.add(string);
			}
		});
	}

	public static Set<String> getMissingTranslations() {
		Set<String> set = new TreeSet();
		collectMissingTranslations(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
		collectMissingTranslations(Registry.STATUS_EFFECT, StatusEffect::getTranslationKey, set);
		collectMissingTranslations(Registry.ITEM, Item::getTranslationKey, set);
		collectMissingTranslations(Registry.ENCHANTMENT, Enchantment::getTranslationKey, set);
		collectMissingTranslations(Registry.BIOME, Biome::getTranslationKey, set);
		collectMissingTranslations(Registry.BLOCK, Block::getTranslationKey, set);
		collectMissingTranslations(Registry.CUSTOM_STAT, identifier -> "stat." + identifier.toString().replace(':', '.'), set);
		return set;
	}

	public static void logMissingTranslations() {
		if (!initialized) {
			throw new IllegalArgumentException("Not bootstrapped");
		} else if (!SharedConstants.isDevelopment) {
			getMissingTranslations().forEach(string -> LOGGER.error("Missing translations: " + string));
		}
	}

	private static void setOutputStreams() {
		if (LOGGER.isDebugEnabled()) {
			System.setErr(new DebugPrintStreamLogger("STDERR", System.err));
			System.setOut(new DebugPrintStreamLogger("STDOUT", SYSOUT));
		} else {
			System.setErr(new PrintStreamLogger("STDERR", System.err));
			System.setOut(new PrintStreamLogger("STDOUT", SYSOUT));
		}
	}

	public static void println(String string) {
		SYSOUT.println(string);
	}
}
