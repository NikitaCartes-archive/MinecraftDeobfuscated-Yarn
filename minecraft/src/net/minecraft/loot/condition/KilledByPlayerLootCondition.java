package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;

public class KilledByPlayerLootCondition implements LootCondition {
	private static final KilledByPlayerLootCondition INSTANCE = new KilledByPlayerLootCondition();

	private KilledByPlayerLootCondition() {
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.LAST_DAMAGE_PLAYER);
	}

	public boolean test(LootContext lootContext) {
		return lootContext.hasParameter(LootContextParameters.LAST_DAMAGE_PLAYER);
	}

	public static LootCondition.Builder builder() {
		return () -> INSTANCE;
	}

	public static class Factory extends LootCondition.Factory<KilledByPlayerLootCondition> {
		protected Factory() {
			super(new Identifier("killed_by_player"), KilledByPlayerLootCondition.class);
		}

		public void toJson(JsonObject jsonObject, KilledByPlayerLootCondition killedByPlayerLootCondition, JsonSerializationContext jsonSerializationContext) {
		}

		public KilledByPlayerLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return KilledByPlayerLootCondition.INSTANCE;
		}
	}
}
