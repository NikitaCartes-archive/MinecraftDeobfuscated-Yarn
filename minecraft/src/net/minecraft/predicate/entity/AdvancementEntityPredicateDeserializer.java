package net.minecraft.predicate.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mojang.logging.LogUtils;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class AdvancementEntityPredicateDeserializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Identifier advancementId;
	private final LootManager lootManager;
	private final Gson gson = LootGsons.getConditionGsonBuilder().create();

	public AdvancementEntityPredicateDeserializer(Identifier advancementId, LootManager lootManager) {
		this.advancementId = advancementId;
		this.lootManager = lootManager;
	}

	public final LootCondition[] loadConditions(JsonArray array, String key, LootContextType contextType) {
		LootCondition[] lootConditions = this.gson.fromJson(array, LootCondition[].class);
		LootTableReporter lootTableReporter = new LootTableReporter(contextType, this.lootManager);

		for (LootCondition lootCondition : lootConditions) {
			lootCondition.validate(lootTableReporter);
			lootTableReporter.getMessages().forEach((name, message) -> LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", key, name, message));
		}

		return lootConditions;
	}

	public Identifier getAdvancementId() {
		return this.advancementId;
	}
}
