/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;

@Environment(value=EnvType.CLIENT)
public class EntityModelLayers {
    private static final String MAIN = "main";
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();
    public static final EntityModelLayer ARMOR_STAND = EntityModelLayers.registerMain("armor_stand");
    public static final EntityModelLayer ARMOR_STAND_INNER_ARMOR = EntityModelLayers.createInnerArmor("armor_stand");
    public static final EntityModelLayer ARMOR_STAND_OUTER_ARMOR = EntityModelLayers.createOuterArmor("armor_stand");
    public static final EntityModelLayer AXOLOTL = EntityModelLayers.registerMain("axolotl");
    public static final EntityModelLayer BANNER = EntityModelLayers.registerMain("banner");
    public static final EntityModelLayer BAT = EntityModelLayers.registerMain("bat");
    public static final EntityModelLayer BED_FOOT = EntityModelLayers.registerMain("bed_foot");
    public static final EntityModelLayer BED_HEAD = EntityModelLayers.registerMain("bed_head");
    public static final EntityModelLayer BEE = EntityModelLayers.registerMain("bee");
    public static final EntityModelLayer BELL = EntityModelLayers.registerMain("bell");
    public static final EntityModelLayer BLAZE = EntityModelLayers.registerMain("blaze");
    public static final EntityModelLayer BOOK = EntityModelLayers.registerMain("book");
    public static final EntityModelLayer CAT = EntityModelLayers.registerMain("cat");
    public static final EntityModelLayer CAT_COLLAR = EntityModelLayers.register("cat", "collar");
    public static final EntityModelLayer CAVE_SPIDER = EntityModelLayers.registerMain("cave_spider");
    public static final EntityModelLayer CHEST = EntityModelLayers.registerMain("chest");
    public static final EntityModelLayer CHEST_MINECART = EntityModelLayers.registerMain("chest_minecart");
    public static final EntityModelLayer CHICKEN = EntityModelLayers.registerMain("chicken");
    public static final EntityModelLayer COD = EntityModelLayers.registerMain("cod");
    public static final EntityModelLayer COMMAND_BLOCK_MINECART = EntityModelLayers.registerMain("command_block_minecart");
    public static final EntityModelLayer CONDUIT = EntityModelLayers.register("conduit", "cage");
    public static final EntityModelLayer CONDUIT_EYE = EntityModelLayers.register("conduit", "eye");
    public static final EntityModelLayer CONDUIT_SHELL = EntityModelLayers.register("conduit", "shell");
    public static final EntityModelLayer CONDUIT_WIND = EntityModelLayers.register("conduit", "wind");
    public static final EntityModelLayer COW = EntityModelLayers.registerMain("cow");
    public static final EntityModelLayer CREEPER = EntityModelLayers.registerMain("creeper");
    public static final EntityModelLayer CREEPER_ARMOR = EntityModelLayers.register("creeper", "armor");
    public static final EntityModelLayer CREEPER_HEAD = EntityModelLayers.registerMain("creeper_head");
    public static final EntityModelLayer DOLPHIN = EntityModelLayers.registerMain("dolphin");
    public static final EntityModelLayer DONKEY = EntityModelLayers.registerMain("donkey");
    public static final EntityModelLayer DOUBLE_CHEST_LEFT = EntityModelLayers.registerMain("double_chest_left");
    public static final EntityModelLayer DOUBLE_CHEST_RIGHT = EntityModelLayers.registerMain("double_chest_right");
    public static final EntityModelLayer DRAGON_SKULL = EntityModelLayers.registerMain("dragon_skull");
    public static final EntityModelLayer DROWNED = EntityModelLayers.registerMain("drowned");
    public static final EntityModelLayer DROWNED_INNER_ARMOR = EntityModelLayers.createInnerArmor("drowned");
    public static final EntityModelLayer DROWNED_OUTER_ARMOR = EntityModelLayers.createOuterArmor("drowned");
    public static final EntityModelLayer DROWNED_OUTER = EntityModelLayers.register("drowned", "outer");
    public static final EntityModelLayer ELDER_GUARDIAN = EntityModelLayers.registerMain("elder_guardian");
    public static final EntityModelLayer ELYTRA = EntityModelLayers.registerMain("elytra");
    public static final EntityModelLayer ENDERMAN = EntityModelLayers.registerMain("enderman");
    public static final EntityModelLayer ENDERMITE = EntityModelLayers.registerMain("endermite");
    public static final EntityModelLayer ENDER_DRAGON = EntityModelLayers.registerMain("ender_dragon");
    public static final EntityModelLayer END_CRYSTAL = EntityModelLayers.registerMain("end_crystal");
    public static final EntityModelLayer EVOKER = EntityModelLayers.registerMain("evoker");
    public static final EntityModelLayer EVOKER_FANGS = EntityModelLayers.registerMain("evoker_fangs");
    public static final EntityModelLayer FOX = EntityModelLayers.registerMain("fox");
    public static final EntityModelLayer FROG = EntityModelLayers.registerMain("frog");
    public static final EntityModelLayer FURNACE_MINECART = EntityModelLayers.registerMain("furnace_minecart");
    public static final EntityModelLayer GHAST = EntityModelLayers.registerMain("ghast");
    public static final EntityModelLayer GIANT = EntityModelLayers.registerMain("giant");
    public static final EntityModelLayer GIANT_INNER_ARMOR = EntityModelLayers.createInnerArmor("giant");
    public static final EntityModelLayer GIANT_OUTER_ARMOR = EntityModelLayers.createOuterArmor("giant");
    public static final EntityModelLayer GLOW_SQUID = EntityModelLayers.registerMain("glow_squid");
    public static final EntityModelLayer GOAT = EntityModelLayers.registerMain("goat");
    public static final EntityModelLayer GUARDIAN = EntityModelLayers.registerMain("guardian");
    public static final EntityModelLayer HOGLIN = EntityModelLayers.registerMain("hoglin");
    public static final EntityModelLayer HOPPER_MINECART = EntityModelLayers.registerMain("hopper_minecart");
    public static final EntityModelLayer HORSE = EntityModelLayers.registerMain("horse");
    public static final EntityModelLayer HORSE_ARMOR = EntityModelLayers.registerMain("horse_armor");
    public static final EntityModelLayer HUSK = EntityModelLayers.registerMain("husk");
    public static final EntityModelLayer HUSK_INNER_ARMOR = EntityModelLayers.createInnerArmor("husk");
    public static final EntityModelLayer HUSK_OUTER_ARMOR = EntityModelLayers.createOuterArmor("husk");
    public static final EntityModelLayer ILLUSIONER = EntityModelLayers.registerMain("illusioner");
    public static final EntityModelLayer IRON_GOLEM = EntityModelLayers.registerMain("iron_golem");
    public static final EntityModelLayer LEASH_KNOT = EntityModelLayers.registerMain("leash_knot");
    public static final EntityModelLayer LLAMA = EntityModelLayers.registerMain("llama");
    public static final EntityModelLayer LLAMA_DECOR = EntityModelLayers.register("llama", "decor");
    public static final EntityModelLayer LLAMA_SPIT = EntityModelLayers.registerMain("llama_spit");
    public static final EntityModelLayer MAGMA_CUBE = EntityModelLayers.registerMain("magma_cube");
    public static final EntityModelLayer MINECART = EntityModelLayers.registerMain("minecart");
    public static final EntityModelLayer MOOSHROOM = EntityModelLayers.registerMain("mooshroom");
    public static final EntityModelLayer MULE = EntityModelLayers.registerMain("mule");
    public static final EntityModelLayer OCELOT = EntityModelLayers.registerMain("ocelot");
    public static final EntityModelLayer PANDA = EntityModelLayers.registerMain("panda");
    public static final EntityModelLayer PARROT = EntityModelLayers.registerMain("parrot");
    public static final EntityModelLayer PHANTOM = EntityModelLayers.registerMain("phantom");
    public static final EntityModelLayer PIG = EntityModelLayers.registerMain("pig");
    public static final EntityModelLayer PIGLIN = EntityModelLayers.registerMain("piglin");
    public static final EntityModelLayer PIGLIN_BRUTE = EntityModelLayers.registerMain("piglin_brute");
    public static final EntityModelLayer PIGLIN_BRUTE_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin_brute");
    public static final EntityModelLayer PIGLIN_BRUTE_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin_brute");
    public static final EntityModelLayer PIGLIN_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin");
    public static final EntityModelLayer PIGLIN_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin");
    public static final EntityModelLayer PIG_SADDLE = EntityModelLayers.register("pig", "saddle");
    public static final EntityModelLayer PILLAGER = EntityModelLayers.registerMain("pillager");
    public static final EntityModelLayer PLAYER = EntityModelLayers.registerMain("player");
    public static final EntityModelLayer PLAYER_HEAD = EntityModelLayers.registerMain("player_head");
    public static final EntityModelLayer PLAYER_INNER_ARMOR = EntityModelLayers.createInnerArmor("player");
    public static final EntityModelLayer PLAYER_OUTER_ARMOR = EntityModelLayers.createOuterArmor("player");
    public static final EntityModelLayer PLAYER_SLIM = EntityModelLayers.registerMain("player_slim");
    public static final EntityModelLayer PLAYER_SLIM_INNER_ARMOR = EntityModelLayers.createInnerArmor("player_slim");
    public static final EntityModelLayer PLAYER_SLIM_OUTER_ARMOR = EntityModelLayers.createOuterArmor("player_slim");
    public static final EntityModelLayer SPIN_ATTACK = EntityModelLayers.registerMain("spin_attack");
    public static final EntityModelLayer POLAR_BEAR = EntityModelLayers.registerMain("polar_bear");
    public static final EntityModelLayer PUFFERFISH_BIG = EntityModelLayers.registerMain("pufferfish_big");
    public static final EntityModelLayer PUFFERFISH_MEDIUM = EntityModelLayers.registerMain("pufferfish_medium");
    public static final EntityModelLayer PUFFERFISH_SMALL = EntityModelLayers.registerMain("pufferfish_small");
    public static final EntityModelLayer RABBIT = EntityModelLayers.registerMain("rabbit");
    public static final EntityModelLayer RAVAGER = EntityModelLayers.registerMain("ravager");
    public static final EntityModelLayer SALMON = EntityModelLayers.registerMain("salmon");
    public static final EntityModelLayer SHEEP = EntityModelLayers.registerMain("sheep");
    public static final EntityModelLayer SHEEP_FUR = EntityModelLayers.register("sheep", "fur");
    public static final EntityModelLayer SHIELD = EntityModelLayers.registerMain("shield");
    public static final EntityModelLayer SHULKER = EntityModelLayers.registerMain("shulker");
    public static final EntityModelLayer SHULKER_BULLET = EntityModelLayers.registerMain("shulker_bullet");
    public static final EntityModelLayer SILVERFISH = EntityModelLayers.registerMain("silverfish");
    public static final EntityModelLayer SKELETON = EntityModelLayers.registerMain("skeleton");
    public static final EntityModelLayer SKELETON_HORSE = EntityModelLayers.registerMain("skeleton_horse");
    public static final EntityModelLayer SKELETON_INNER_ARMOR = EntityModelLayers.createInnerArmor("skeleton");
    public static final EntityModelLayer SKELETON_OUTER_ARMOR = EntityModelLayers.createOuterArmor("skeleton");
    public static final EntityModelLayer SKELETON_SKULL = EntityModelLayers.registerMain("skeleton_skull");
    public static final EntityModelLayer SLIME = EntityModelLayers.registerMain("slime");
    public static final EntityModelLayer SLIME_OUTER = EntityModelLayers.register("slime", "outer");
    public static final EntityModelLayer SNOW_GOLEM = EntityModelLayers.registerMain("snow_golem");
    public static final EntityModelLayer SPAWNER_MINECART = EntityModelLayers.registerMain("spawner_minecart");
    public static final EntityModelLayer SPIDER = EntityModelLayers.registerMain("spider");
    public static final EntityModelLayer SQUID = EntityModelLayers.registerMain("squid");
    public static final EntityModelLayer STRAY = EntityModelLayers.registerMain("stray");
    public static final EntityModelLayer STRAY_INNER_ARMOR = EntityModelLayers.createInnerArmor("stray");
    public static final EntityModelLayer STRAY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("stray");
    public static final EntityModelLayer STRAY_OUTER = EntityModelLayers.register("stray", "outer");
    public static final EntityModelLayer STRIDER = EntityModelLayers.registerMain("strider");
    public static final EntityModelLayer STRIDER_SADDLE = EntityModelLayers.register("strider", "saddle");
    public static final EntityModelLayer TADPOLE = EntityModelLayers.registerMain("tadpole");
    public static final EntityModelLayer TNT_MINECART = EntityModelLayers.registerMain("tnt_minecart");
    public static final EntityModelLayer TRADER_LLAMA = EntityModelLayers.registerMain("trader_llama");
    public static final EntityModelLayer TRIDENT = EntityModelLayers.registerMain("trident");
    public static final EntityModelLayer TROPICAL_FISH_LARGE = EntityModelLayers.registerMain("tropical_fish_large");
    public static final EntityModelLayer TROPICAL_FISH_LARGE_PATTERN = EntityModelLayers.register("tropical_fish_large", "pattern");
    public static final EntityModelLayer TROPICAL_FISH_SMALL = EntityModelLayers.registerMain("tropical_fish_small");
    public static final EntityModelLayer TROPICAL_FISH_SMALL_PATTERN = EntityModelLayers.register("tropical_fish_small", "pattern");
    public static final EntityModelLayer TURTLE = EntityModelLayers.registerMain("turtle");
    public static final EntityModelLayer VEX = EntityModelLayers.registerMain("vex");
    public static final EntityModelLayer VILLAGER = EntityModelLayers.registerMain("villager");
    public static final EntityModelLayer VINDICATOR = EntityModelLayers.registerMain("vindicator");
    public static final EntityModelLayer WANDERING_TRADER = EntityModelLayers.registerMain("wandering_trader");
    public static final EntityModelLayer WITCH = EntityModelLayers.registerMain("witch");
    public static final EntityModelLayer WITHER = EntityModelLayers.registerMain("wither");
    public static final EntityModelLayer WITHER_ARMOR = EntityModelLayers.register("wither", "armor");
    public static final EntityModelLayer WITHER_SKELETON = EntityModelLayers.registerMain("wither_skeleton");
    public static final EntityModelLayer WITHER_SKELETON_INNER_ARMOR = EntityModelLayers.createInnerArmor("wither_skeleton");
    public static final EntityModelLayer WITHER_SKELETON_OUTER_ARMOR = EntityModelLayers.createOuterArmor("wither_skeleton");
    public static final EntityModelLayer WITHER_SKELETON_SKULL = EntityModelLayers.registerMain("wither_skeleton_skull");
    public static final EntityModelLayer WITHER_SKULL = EntityModelLayers.registerMain("wither_skull");
    public static final EntityModelLayer WOLF = EntityModelLayers.registerMain("wolf");
    public static final EntityModelLayer ZOGLIN = EntityModelLayers.registerMain("zoglin");
    public static final EntityModelLayer ZOMBIE = EntityModelLayers.registerMain("zombie");
    public static final EntityModelLayer ZOMBIE_HEAD = EntityModelLayers.registerMain("zombie_head");
    public static final EntityModelLayer ZOMBIE_HORSE = EntityModelLayers.registerMain("zombie_horse");
    public static final EntityModelLayer ZOMBIE_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie");
    public static final EntityModelLayer ZOMBIE_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie");
    public static final EntityModelLayer ZOMBIE_VILLAGER = EntityModelLayers.registerMain("zombie_villager");
    public static final EntityModelLayer ZOMBIE_VILLAGER_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie_villager");
    public static final EntityModelLayer ZOMBIE_VILLAGER_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie_villager");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN = EntityModelLayers.registerMain("zombified_piglin");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombified_piglin");
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombified_piglin");

    private static EntityModelLayer registerMain(String id) {
        return EntityModelLayers.register(id, MAIN);
    }

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = EntityModelLayers.create(id, layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + entityModelLayer);
        }
        return entityModelLayer;
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(new Identifier("minecraft", id), layer);
    }

    private static EntityModelLayer createInnerArmor(String id) {
        return EntityModelLayers.register(id, "inner_armor");
    }

    private static EntityModelLayer createOuterArmor(String id) {
        return EntityModelLayers.register(id, "outer_armor");
    }

    public static EntityModelLayer createBoat(BoatEntity.Type type) {
        return EntityModelLayers.create("boat/" + type.getName(), MAIN);
    }

    public static EntityModelLayer createSign(SignType type) {
        return EntityModelLayers.create("sign/" + type.getName(), MAIN);
    }

    public static Stream<EntityModelLayer> getLayers() {
        return LAYERS.stream();
    }
}

