/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.SharedConstants;
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
    private static final Logger LOGGER;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (Registry.REGISTRIES.isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
        }
        FireBlock.registerDefaultFlammables();
        ComposterBlock.registerDefaultCompostableItems();
        if (EntityType.getId(EntityType.PLAYER) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
        }
        BrewingRecipeRegistry.registerDefaults();
        EntitySelectorOptions.register();
        DispenserBehavior.registerDefaults();
        ArgumentTypes.register();
        Bootstrap.setOutputStreams();
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
        TreeSet<String> set = new TreeSet<String>();
        Bootstrap.collectMissingTranslations(Registry.ENTITY_TYPE, EntityType::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.STATUS_EFFECT, StatusEffect::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.ITEM, Item::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.ENCHANTMENT, Enchantment::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.BIOME, Biome::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.BLOCK, Block::getTranslationKey, set);
        Bootstrap.collectMissingTranslations(Registry.CUSTOM_STAT, identifier -> "stat." + identifier.toString().replace(':', '.'), set);
        return set;
    }

    public static void logMissingTranslations() {
        if (!initialized) {
            throw new IllegalArgumentException("Not bootstrapped");
        }
        if (SharedConstants.isDevelopment) {
            return;
        }
        Bootstrap.getMissingTranslations().forEach(string -> LOGGER.error("Missing translations: " + string));
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

    static {
        LOGGER = LogManager.getLogger();
    }
}

