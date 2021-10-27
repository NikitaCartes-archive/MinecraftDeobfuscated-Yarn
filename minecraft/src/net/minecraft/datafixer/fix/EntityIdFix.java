package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class EntityIdFix extends DataFix {
	private static final Map<String, String> RENAMED_ENTITIES = DataFixUtils.make(Maps.<String, String>newHashMap(), map -> {
		map.put("AreaEffectCloud", "minecraft:area_effect_cloud");
		map.put("ArmorStand", "minecraft:armor_stand");
		map.put("Arrow", "minecraft:arrow");
		map.put("Bat", "minecraft:bat");
		map.put("Blaze", "minecraft:blaze");
		map.put("Boat", "minecraft:boat");
		map.put("CaveSpider", "minecraft:cave_spider");
		map.put("Chicken", "minecraft:chicken");
		map.put("Cow", "minecraft:cow");
		map.put("Creeper", "minecraft:creeper");
		map.put("Donkey", "minecraft:donkey");
		map.put("DragonFireball", "minecraft:dragon_fireball");
		map.put("ElderGuardian", "minecraft:elder_guardian");
		map.put("EnderCrystal", "minecraft:ender_crystal");
		map.put("EnderDragon", "minecraft:ender_dragon");
		map.put("Enderman", "minecraft:enderman");
		map.put("Endermite", "minecraft:endermite");
		map.put("EyeOfEnderSignal", "minecraft:eye_of_ender_signal");
		map.put("FallingSand", "minecraft:falling_block");
		map.put("Fireball", "minecraft:fireball");
		map.put("FireworksRocketEntity", "minecraft:fireworks_rocket");
		map.put("Ghast", "minecraft:ghast");
		map.put("Giant", "minecraft:giant");
		map.put("Guardian", "minecraft:guardian");
		map.put("Horse", "minecraft:horse");
		map.put("Husk", "minecraft:husk");
		map.put("Item", "minecraft:item");
		map.put("ItemFrame", "minecraft:item_frame");
		map.put("LavaSlime", "minecraft:magma_cube");
		map.put("LeashKnot", "minecraft:leash_knot");
		map.put("MinecartChest", "minecraft:chest_minecart");
		map.put("MinecartCommandBlock", "minecraft:commandblock_minecart");
		map.put("MinecartFurnace", "minecraft:furnace_minecart");
		map.put("MinecartHopper", "minecraft:hopper_minecart");
		map.put("MinecartRideable", "minecraft:minecart");
		map.put("MinecartSpawner", "minecraft:spawner_minecart");
		map.put("MinecartTNT", "minecraft:tnt_minecart");
		map.put("Mule", "minecraft:mule");
		map.put("MushroomCow", "minecraft:mooshroom");
		map.put("Ozelot", "minecraft:ocelot");
		map.put("Painting", "minecraft:painting");
		map.put("Pig", "minecraft:pig");
		map.put("PigZombie", "minecraft:zombie_pigman");
		map.put("PolarBear", "minecraft:polar_bear");
		map.put("PrimedTnt", "minecraft:tnt");
		map.put("Rabbit", "minecraft:rabbit");
		map.put("Sheep", "minecraft:sheep");
		map.put("Shulker", "minecraft:shulker");
		map.put("ShulkerBullet", "minecraft:shulker_bullet");
		map.put("Silverfish", "minecraft:silverfish");
		map.put("Skeleton", "minecraft:skeleton");
		map.put("SkeletonHorse", "minecraft:skeleton_horse");
		map.put("Slime", "minecraft:slime");
		map.put("SmallFireball", "minecraft:small_fireball");
		map.put("SnowMan", "minecraft:snowman");
		map.put("Snowball", "minecraft:snowball");
		map.put("SpectralArrow", "minecraft:spectral_arrow");
		map.put("Spider", "minecraft:spider");
		map.put("Squid", "minecraft:squid");
		map.put("Stray", "minecraft:stray");
		map.put("ThrownEgg", "minecraft:egg");
		map.put("ThrownEnderpearl", "minecraft:ender_pearl");
		map.put("ThrownExpBottle", "minecraft:xp_bottle");
		map.put("ThrownPotion", "minecraft:potion");
		map.put("Villager", "minecraft:villager");
		map.put("VillagerGolem", "minecraft:villager_golem");
		map.put("Witch", "minecraft:witch");
		map.put("WitherBoss", "minecraft:wither");
		map.put("WitherSkeleton", "minecraft:wither_skeleton");
		map.put("WitherSkull", "minecraft:wither_skull");
		map.put("Wolf", "minecraft:wolf");
		map.put("XPOrb", "minecraft:xp_orb");
		map.put("Zombie", "minecraft:zombie");
		map.put("ZombieHorse", "minecraft:zombie_horse");
		map.put("ZombieVillager", "minecraft:zombie_villager");
	});

	public EntityIdFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		TaggedChoiceType<String> taggedChoiceType = (TaggedChoiceType<String>)this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
		TaggedChoiceType<String> taggedChoiceType2 = (TaggedChoiceType<String>)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		Type<?> type2 = this.getOutputSchema().getType(TypeReferences.ITEM_STACK);
		return TypeRewriteRule.seq(
			this.convertUnchecked("item stack entity name hook converter", type, type2),
			this.fixTypeEverywhere(
				"EntityIdFix", taggedChoiceType, taggedChoiceType2, dynamicOps -> pair -> pair.mapFirst(string -> (String)RENAMED_ENTITIES.getOrDefault(string, string))
			)
		);
	}
}
