package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public class BlockPosFormatFix extends DataFix {
	private static final List<String> PATROL_TARGET_ENTITY_IDS = List.of(
		"minecraft:witch", "minecraft:ravager", "minecraft:pillager", "minecraft:illusioner", "minecraft:evoker", "minecraft:vindicator"
	);

	public BlockPosFormatFix(Schema outputSchema) {
		super(outputSchema, false);
	}

	private Typed<?> fixOldBlockPosFormat(Typed<?> typed, Map<String, String> oldToNewKey) {
		return typed.update(DSL.remainderFinder(), dynamic -> {
			for (Entry<String, String> entry : oldToNewKey.entrySet()) {
				dynamic = dynamic.renameAndFixField((String)entry.getKey(), (String)entry.getValue(), FixUtil::fixBlockPos);
			}

			return dynamic;
		});
	}

	private <T> Dynamic<T> fixMapItemFrames(Dynamic<T> dynamic) {
		return dynamic.update("frames", frames -> frames.createList(frames.asStream().map(frame -> {
				frame = frame.renameAndFixField("Pos", "pos", FixUtil::fixBlockPos);
				frame = frame.renameField("Rotation", "rotation");
				return frame.renameField("EntityId", "entity_id");
			}))).update("banners", banners -> banners.createList(banners.asStream().map(banner -> {
				banner = banner.renameField("Pos", "pos");
				banner = banner.renameField("Color", "color");
				return banner.renameField("Name", "name");
			})));
	}

	@Override
	public TypeRewriteRule makeRule() {
		List<TypeRewriteRule> list = new ArrayList();
		this.addEntityFixes(list);
		this.addBlockEntityFixes(list);
		list.add(
			this.fixTypeEverywhereTyped(
				"BlockPos format for map frames",
				this.getInputSchema().getType(TypeReferences.SAVED_DATA_MAP_DATA),
				typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("data", this::fixMapItemFrames))
			)
		);
		Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
		list.add(
			this.fixTypeEverywhereTyped(
				"BlockPos format for compass target",
				type,
				ItemNbtFix.fixNbt(type, "minecraft:compass"::equals, tagDynamic -> tagDynamic.update("LodestonePos", FixUtil::fixBlockPos))
			)
		);
		return TypeRewriteRule.seq(list);
	}

	private void addEntityFixes(List<TypeRewriteRule> rules) {
		rules.add(this.createFixRule(TypeReferences.ENTITY, "minecraft:bee", Map.of("HivePos", "hive_pos", "FlowerPos", "flower_pos")));
		rules.add(this.createFixRule(TypeReferences.ENTITY, "minecraft:end_crystal", Map.of("BeamTarget", "beam_target")));
		rules.add(this.createFixRule(TypeReferences.ENTITY, "minecraft:wandering_trader", Map.of("WanderTarget", "wander_target")));

		for (String string : PATROL_TARGET_ENTITY_IDS) {
			rules.add(this.createFixRule(TypeReferences.ENTITY, string, Map.of("PatrolTarget", "patrol_target")));
		}

		rules.add(
			this.fixTypeEverywhereTyped(
				"BlockPos format in Leash for mobs",
				this.getInputSchema().getType(TypeReferences.ENTITY),
				typed -> typed.update(DSL.remainderFinder(), entityDynamic -> entityDynamic.renameAndFixField("Leash", "leash", FixUtil::fixBlockPos))
			)
		);
	}

	private void addBlockEntityFixes(List<TypeRewriteRule> rules) {
		rules.add(this.createFixRule(TypeReferences.BLOCK_ENTITY, "minecraft:beehive", Map.of("FlowerPos", "flower_pos")));
		rules.add(this.createFixRule(TypeReferences.BLOCK_ENTITY, "minecraft:end_gateway", Map.of("ExitPortal", "exit_portal")));
	}

	private TypeRewriteRule createFixRule(TypeReference typeReference, String id, Map<String, String> oldToNewKey) {
		String string = "BlockPos format in " + oldToNewKey.keySet() + " for " + id + " (" + typeReference.typeName() + ")";
		OpticFinder<?> opticFinder = DSL.namedChoice(id, this.getInputSchema().getChoiceType(typeReference, id));
		return this.fixTypeEverywhereTyped(
			string, this.getInputSchema().getType(typeReference), typed -> typed.updateTyped(opticFinder, typedx -> this.fixOldBlockPosFormat(typedx, oldToNewKey))
		);
	}
}
