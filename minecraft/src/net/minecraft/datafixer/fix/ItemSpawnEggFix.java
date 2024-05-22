package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Util;

public class ItemSpawnEggFix extends DataFix {
	private static final String[] DAMAGE_TO_ENTITY_IDS = DataFixUtils.make(new String[256], ids -> {
		ids[1] = "Item";
		ids[2] = "XPOrb";
		ids[7] = "ThrownEgg";
		ids[8] = "LeashKnot";
		ids[9] = "Painting";
		ids[10] = "Arrow";
		ids[11] = "Snowball";
		ids[12] = "Fireball";
		ids[13] = "SmallFireball";
		ids[14] = "ThrownEnderpearl";
		ids[15] = "EyeOfEnderSignal";
		ids[16] = "ThrownPotion";
		ids[17] = "ThrownExpBottle";
		ids[18] = "ItemFrame";
		ids[19] = "WitherSkull";
		ids[20] = "PrimedTnt";
		ids[21] = "FallingSand";
		ids[22] = "FireworksRocketEntity";
		ids[23] = "TippedArrow";
		ids[24] = "SpectralArrow";
		ids[25] = "ShulkerBullet";
		ids[26] = "DragonFireball";
		ids[30] = "ArmorStand";
		ids[41] = "Boat";
		ids[42] = "MinecartRideable";
		ids[43] = "MinecartChest";
		ids[44] = "MinecartFurnace";
		ids[45] = "MinecartTNT";
		ids[46] = "MinecartHopper";
		ids[47] = "MinecartSpawner";
		ids[40] = "MinecartCommandBlock";
		ids[50] = "Creeper";
		ids[51] = "Skeleton";
		ids[52] = "Spider";
		ids[53] = "Giant";
		ids[54] = "Zombie";
		ids[55] = "Slime";
		ids[56] = "Ghast";
		ids[57] = "PigZombie";
		ids[58] = "Enderman";
		ids[59] = "CaveSpider";
		ids[60] = "Silverfish";
		ids[61] = "Blaze";
		ids[62] = "LavaSlime";
		ids[63] = "EnderDragon";
		ids[64] = "WitherBoss";
		ids[65] = "Bat";
		ids[66] = "Witch";
		ids[67] = "Endermite";
		ids[68] = "Guardian";
		ids[69] = "Shulker";
		ids[90] = "Pig";
		ids[91] = "Sheep";
		ids[92] = "Cow";
		ids[93] = "Chicken";
		ids[94] = "Squid";
		ids[95] = "Wolf";
		ids[96] = "MushroomCow";
		ids[97] = "SnowMan";
		ids[98] = "Ozelot";
		ids[99] = "VillagerGolem";
		ids[100] = "EntityHorse";
		ids[101] = "Rabbit";
		ids[120] = "Villager";
		ids[200] = "EnderCrystal";
	});

	public ItemSpawnEggFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	@Override
	public TypeRewriteRule makeRule() {
		Schema schema = this.getInputSchema();
		Type<?> type = schema.getType(TypeReferences.ITEM_STACK);
		OpticFinder<Pair<String, String>> opticFinder = DSL.fieldFinder(
			"id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType())
		);
		OpticFinder<String> opticFinder2 = DSL.fieldFinder("id", DSL.string());
		OpticFinder<?> opticFinder3 = type.findField("tag");
		OpticFinder<?> opticFinder4 = opticFinder3.type().findField("EntityTag");
		OpticFinder<?> opticFinder5 = DSL.typeFinder(schema.getTypeRaw(TypeReferences.ENTITY));
		Type<?> type2 = this.getOutputSchema().getTypeRaw(TypeReferences.ENTITY);
		return this.fixTypeEverywhereTyped("ItemSpawnEggFix", type, itemStack -> {
			Optional<Pair<String, String>> optional = itemStack.getOptional(opticFinder);
			if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:spawn_egg")) {
				Dynamic<?> dynamic = itemStack.get(DSL.remainderFinder());
				short s = dynamic.get("Damage").asShort((short)0);
				Optional<? extends Typed<?>> optional2 = itemStack.getOptionalTyped(opticFinder3);
				Optional<? extends Typed<?>> optional3 = optional2.flatMap(tagTyped -> tagTyped.getOptionalTyped(opticFinder4));
				Optional<? extends Typed<?>> optional4 = optional3.flatMap(entityTagTyped -> entityTagTyped.getOptionalTyped(opticFinder5));
				Optional<String> optional5 = optional4.flatMap(entityTyped -> entityTyped.getOptional(opticFinder2));
				Typed<?> typed = itemStack;
				String string = DAMAGE_TO_ENTITY_IDS[s & 255];
				if (string != null && (optional5.isEmpty() || !Objects.equals(optional5.get(), string))) {
					Typed<?> typed2 = itemStack.getOrCreateTyped(opticFinder3);
					Typed<?> typed3 = typed2.getOrCreateTyped(opticFinder4);
					Typed<?> typed4 = typed3.getOrCreateTyped(opticFinder5);
					Typed<?> typed5 = Util.apply(typed4, type2, dynamic2 -> dynamic2.set("id", dynamic.createString(string)));
					typed = itemStack.set(opticFinder3, typed2.set(opticFinder4, typed3.set(opticFinder5, typed5)));
				}

				if (s != 0) {
					dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
					typed = typed.set(DSL.remainderFinder(), dynamic);
				}

				return typed;
			} else {
				return itemStack;
			}
		});
	}
}
