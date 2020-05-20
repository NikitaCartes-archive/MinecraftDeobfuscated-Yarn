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
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.Language;
import net.minecraft.util.logging.DebugLoggerPrintStream;
import net.minecraft.util.logging.LoggerPrintStream;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
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
					ArgumentTypes.register();
					setOutputStreams();
				}
			}
		}
	}

	private static <T> void collectMissingTranslations(Iterable<T> iterable, Function<T, String> keyExtractor, Set<String> translationKeys) {
		Language language = Language.getInstance();
		iterable.forEach(object -> {
			String string = (String)keyExtractor.apply(object);
			if (!language.hasTranslation(string)) {
				translationKeys.add(string);
			}
		});
	}

	private static void method_27732(Set<String> set) {
		final Language language = Language.getInstance();
		GameRules.forEachType(new GameRules.RuleTypeConsumer() {
			@Override
			public <T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> key, GameRules.RuleType<T> type) {
				if (!language.hasTranslation(key.getTranslationKey())) {
					set.add(key.getName());
				}
			}
		});
	}

	public static Set<String> getMissingTranslations() {
		Set<String> set = new TreeSet();
		collectMissingTranslations(Registry.ATTRIBUTES, EntityAttribute::getTranslationKey, set);
		collectMissingTranslations(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
		collectMissingTranslations(Registry.STATUS_EFFECT, StatusEffect::getTranslationKey, set);
		collectMissingTranslations(Registry.ITEM, Item::getTranslationKey, set);
		collectMissingTranslations(Registry.ENCHANTMENT, Enchantment::getTranslationKey, set);
		collectMissingTranslations(Registry.BIOME, Biome::getTranslationKey, set);
		collectMissingTranslations(Registry.BLOCK, Block::getTranslationKey, set);
		collectMissingTranslations(Registry.CUSTOM_STAT, identifier -> "stat." + identifier.toString().replace(':', '.'), set);
		method_27732(set);
		return set;
	}

	public static void logMissing() {
		if (!initialized) {
			throw new IllegalArgumentException("Not bootstrapped");
		} else {
			if (SharedConstants.isDevelopment) {
				getMissingTranslations().forEach(string -> LOGGER.error("Missing translations: " + string));
			}

			DefaultAttributeRegistry.checkMissing();
		}
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
