package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.context.ContextParameter;

public class KilledByPlayerLootCondition implements LootCondition {
	private static final KilledByPlayerLootCondition INSTANCE = new KilledByPlayerLootCondition();
	public static final MapCodec<KilledByPlayerLootCondition> CODEC = MapCodec.unit(INSTANCE);

	private KilledByPlayerLootCondition() {
	}

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.KILLED_BY_PLAYER;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(LootContextParameters.LAST_DAMAGE_PLAYER);
	}

	public boolean test(LootContext lootContext) {
		return lootContext.hasParameter(LootContextParameters.LAST_DAMAGE_PLAYER);
	}

	public static LootCondition.Builder builder() {
		return () -> INSTANCE;
	}
}
