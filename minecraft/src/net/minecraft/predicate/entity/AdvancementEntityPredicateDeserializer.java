package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.List;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class AdvancementEntityPredicateDeserializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Identifier advancementId;
	private final LootManager lootManager;

	public AdvancementEntityPredicateDeserializer(Identifier advancementId, LootManager lootManager) {
		this.advancementId = advancementId;
		this.lootManager = lootManager;
	}

	public final List<LootCondition> loadConditions(JsonArray array, String key, LootContextType contextType) {
		List<LootCondition> list = Util.getResult(LootConditionTypes.CODEC.listOf().parse(JsonOps.INSTANCE, array), JsonParseException::new);
		LootTableReporter lootTableReporter = new LootTableReporter(contextType, this.lootManager);

		for (LootCondition lootCondition : list) {
			lootCondition.validate(lootTableReporter);
			lootTableReporter.getMessages().forEach((name, message) -> LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", key, name, message));
		}

		return list;
	}

	public Identifier getAdvancementId() {
		return this.advancementId;
	}
}
