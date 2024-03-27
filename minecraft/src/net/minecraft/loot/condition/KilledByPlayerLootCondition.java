package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;

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
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.LAST_DAMAGE_PLAYER);
	}

	public boolean test(LootContext lootContext) {
		return lootContext.hasParameter(LootContextParameters.LAST_DAMAGE_PLAYER);
	}

	public static LootCondition.Builder builder() {
		return () -> INSTANCE;
	}
}
