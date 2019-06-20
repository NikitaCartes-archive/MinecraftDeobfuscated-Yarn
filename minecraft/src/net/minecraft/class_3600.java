package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.Set;

public class class_3600 extends DataFix {
	private static final Set<String> field_15894 = Sets.<String>newHashSet(
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

	public class_3600(Schema schema, boolean bl) {
		super(schema, bl);
	}

	public Dynamic<?> method_15704(Dynamic<?> dynamic) {
		Optional<Number> optional = dynamic.get("HealF").asNumber();
		Optional<Number> optional2 = dynamic.get("Health").asNumber();
		float f;
		if (optional.isPresent()) {
			f = ((Number)optional.get()).floatValue();
			dynamic = dynamic.remove("HealF");
		} else {
			if (!optional2.isPresent()) {
				return dynamic;
			}

			f = ((Number)optional2.get()).floatValue();
		}

		return dynamic.set("Health", dynamic.createFloat(f));
	}

	@Override
	public TypeRewriteRule makeRule() {
		return this.fixTypeEverywhereTyped(
			"EntityHealthFix", this.getInputSchema().getType(class_1208.field_5729), typed -> typed.update(DSL.remainderFinder(), this::method_15704)
		);
	}
}
