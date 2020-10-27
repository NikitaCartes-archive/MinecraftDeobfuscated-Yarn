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
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.util.Language;
import net.minecraft.util.logging.DebugLoggerPrintStream;
import net.minecraft.util.logging.LoggerPrintStream;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
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
					RequiredTagListRegistry.validateRegistrations();
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
		collectMissingTranslations(Registry.CUSTOM_STAT, identifier -> "stat." + identifier.toString().replace(':', '.'), set);
		collectMissingGameRuleTranslations(set);
		return set;
	}

	public static void logMissing() {
		if (!initialized) {
			throw new IllegalArgumentException("Not bootstrapped");
		} else {
			if (SharedConstants.isDevelopment) {
				getMissingTranslations().forEach(string -> LOGGER.error("Missing translations: " + string));
				CommandManager.checkMissing();
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
