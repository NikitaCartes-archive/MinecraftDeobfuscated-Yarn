package net.minecraft.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.util.dynamic.Codecs;

public record AdvancementCriterion<T extends CriterionConditions>(Criterion<T> trigger, T conditions) {
	private static final MapCodec<AdvancementCriterion<?>> MAP_CODEC = Codecs.parameters(
		"trigger", "conditions", Criteria.CODEC, AdvancementCriterion::trigger, AdvancementCriterion::getCodec
	);
	public static final Codec<AdvancementCriterion<?>> CODEC = MAP_CODEC.codec();

	private static <T extends CriterionConditions> Codec<AdvancementCriterion<T>> getCodec(Criterion<T> criterion) {
		return criterion.getConditionsCodec().xmap(conditions -> new AdvancementCriterion<>(criterion, (T)conditions), AdvancementCriterion::conditions);
	}
}
