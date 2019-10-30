package net.minecraft.datafixers.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixers.TypeReferences;

public class EntityHealthFix extends DataFix {
	private static final Set<String> ENTITIES = Sets.<String>newHashSet(
		"ArmorStand",
		"Bat",
		"Blaze",
		"CaveSpider",
		"Chicken",
		"Cow",
		"Creeper",
		"EnderDragon",
		"Enderman",
		"Endermite",
		"EntityHorse",
		"Ghast",
		"Giant",
		"Guardian",
		"LavaSlime",
		"MushroomCow",
		"Ozelot",
		"Pig",
		"PigZombie",
		"Rabbit",
		"Sheep",
		"Shulker",
		"Silverfish",
		"Skeleton",
		"Slime",
		"SnowMan",
		"Spider",
		"Squid",
		"Villager",
		"VillagerGolem",
		"Witch",
		"WitherBoss",
		"Wolf",
		"Zombie"
	);

	public EntityHealthFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	public Dynamic<?> fixHealth(Dynamic<?> tag) {
		Optional<Number> optional = tag.get("HealF").asNumber();
		Optional<Number> optional2 = tag.get("Health").asNumber();
		float f;
		if (optional.isPresent()) {
			f = ((Number)optional.get()).floatValue();
			tag = tag.remove("HealF");
		} else {
			if (!optional2.isPresent()) {
				return tag;
			}

			f = ((Number)optional2.get()).floatValue();
		}

		return tag.set("Health", tag.createFloat(f));
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityHealthFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), this::fixHealth)
		);
	}
}
