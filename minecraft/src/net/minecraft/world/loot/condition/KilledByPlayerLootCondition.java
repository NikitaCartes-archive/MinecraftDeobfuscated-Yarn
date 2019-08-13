package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class KilledByPlayerLootCondition implements LootCondition {
	private static final KilledByPlayerLootCondition INSTANCE = new KilledByPlayerLootCondition();

	private KilledByPlayerLootCondition() {
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.field_1233);
	}

	public boolean method_938(LootContext lootContext) {
		return lootContext.hasParameter(LootContextParameters.field_1233);
	}

	public static LootCondition.Builder builder() {
		return () -> INSTANCE;
	}

	public static class Factory extends LootCondition.Factory<KilledByPlayerLootCondition> {
		protected Factory() {
			super(new Identifier("killed_by_player"), KilledByPlayerLootCondition.class);
		}

		public void method_942(JsonObject jsonObject, KilledByPlayerLootCondition killedByPlayerLootCondition, JsonSerializationContext jsonSerializationContext) {
		}

		public KilledByPlayerLootCondition method_943(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			return KilledByPlayerLootCondition.INSTANCE;
		}
	}
}
