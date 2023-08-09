package net.minecraft.loot.condition;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public record ReferenceLootCondition(Identifier id) implements LootCondition {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<ReferenceLootCondition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Identifier.CODEC.fieldOf("name").forGetter(ReferenceLootCondition::id)).apply(instance, ReferenceLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.REFERENCE;
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootDataKey<LootCondition> lootDataKey = new LootDataKey<>(LootDataType.PREDICATES, this.id);
		if (reporter.isInStack(lootDataKey)) {
			reporter.report("Condition " + this.id + " is recursively called");
		} else {
			LootCondition.super.validate(reporter);
			reporter.getDataLookup()
				.getElementOptional(lootDataKey)
				.ifPresentOrElse(
					predicate -> predicate.validate(reporter.makeChild(".{" + this.id + "}", lootDataKey)), () -> reporter.report("Unknown condition table called " + this.id)
				);
		}
	}

	public boolean test(LootContext lootContext) {
		LootCondition lootCondition = lootContext.getDataLookup().getElement(LootDataType.PREDICATES, this.id);
		if (lootCondition == null) {
			LOGGER.warn("Tried using unknown condition table called {}", this.id);
			return false;
		} else {
			LootContext.Entry<?> entry = LootContext.predicate(lootCondition);
			if (lootContext.markActive(entry)) {
				boolean var4;
				try {
					var4 = lootCondition.test(lootContext);
				} finally {
					lootContext.markInactive(entry);
				}

				return var4;
			} else {
				LOGGER.warn("Detected infinite loop in loot tables");
				return false;
			}
		}
	}

	public static LootCondition.Builder builder(Identifier id) {
		return () -> new ReferenceLootCondition(id);
	}
}
