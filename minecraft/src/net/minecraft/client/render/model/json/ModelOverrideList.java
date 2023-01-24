package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelOverrideList {
	public static final ModelOverrideList EMPTY = new ModelOverrideList();
	public static final float field_42072 = Float.NEGATIVE_INFINITY;
	private final ModelOverrideList.BakedOverride[] overrides;
	private final Identifier[] conditionTypes;

	private ModelOverrideList() {
		this.overrides = new ModelOverrideList.BakedOverride[0];
		this.conditionTypes = new Identifier[0];
	}

	public ModelOverrideList(Baker baker, JsonUnbakedModel parent, List<ModelOverride> overrides) {
		this.conditionTypes = (Identifier[])overrides.stream()
			.flatMap(ModelOverride::streamConditions)
			.map(ModelOverride.Condition::getType)
			.distinct()
			.toArray(Identifier[]::new);
		Object2IntMap<Identifier> object2IntMap = new Object2IntOpenHashMap<>();

		for (int i = 0; i < this.conditionTypes.length; i++) {
			object2IntMap.put(this.conditionTypes[i], i);
		}

		List<ModelOverrideList.BakedOverride> list = Lists.<ModelOverrideList.BakedOverride>newArrayList();

		for (int j = overrides.size() - 1; j >= 0; j--) {
			ModelOverride modelOverride = (ModelOverride)overrides.get(j);
			BakedModel bakedModel = this.bakeOverridingModel(baker, parent, modelOverride);
			ModelOverrideList.InlinedCondition[] inlinedConditions = (ModelOverrideList.InlinedCondition[])modelOverride.streamConditions().map(condition -> {
				int i = object2IntMap.getInt(condition.getType());
				return new ModelOverrideList.InlinedCondition(i, condition.getThreshold());
			}).toArray(ModelOverrideList.InlinedCondition[]::new);
			list.add(new ModelOverrideList.BakedOverride(inlinedConditions, bakedModel));
		}

		this.overrides = (ModelOverrideList.BakedOverride[])list.toArray(new ModelOverrideList.BakedOverride[0]);
	}

	@Nullable
	private BakedModel bakeOverridingModel(Baker baker, JsonUnbakedModel parent, ModelOverride override) {
		UnbakedModel unbakedModel = baker.getOrLoadModel(override.getModelId());
		return Objects.equals(unbakedModel, parent) ? null : baker.bake(override.getModelId(), net.minecraft.client.render.model.ModelRotation.X0_Y0);
	}

	@Nullable
	public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
		if (this.overrides.length != 0) {
			Item item = stack.getItem();
			int i = this.conditionTypes.length;
			float[] fs = new float[i];

			for (int j = 0; j < i; j++) {
				Identifier identifier = this.conditionTypes[j];
				ModelPredicateProvider modelPredicateProvider = ModelPredicateProviderRegistry.get(item, identifier);
				if (modelPredicateProvider != null) {
					fs[j] = modelPredicateProvider.call(stack, world, entity, seed);
				} else {
					fs[j] = Float.NEGATIVE_INFINITY;
				}
			}

			for (ModelOverrideList.BakedOverride bakedOverride : this.overrides) {
				if (bakedOverride.test(fs)) {
					BakedModel bakedModel = bakedOverride.model;
					if (bakedModel == null) {
						return model;
					}

					return bakedModel;
				}
			}
		}

		return model;
	}

	@Environment(EnvType.CLIENT)
	static class BakedOverride {
		private final ModelOverrideList.InlinedCondition[] conditions;
		@Nullable
		final BakedModel model;

		BakedOverride(ModelOverrideList.InlinedCondition[] conditions, @Nullable BakedModel model) {
			this.conditions = conditions;
			this.model = model;
		}

		boolean test(float[] values) {
			for (ModelOverrideList.InlinedCondition inlinedCondition : this.conditions) {
				float f = values[inlinedCondition.index];
				if (f < inlinedCondition.threshold) {
					return false;
				}
			}

			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	static class InlinedCondition {
		public final int index;
		public final float threshold;

		InlinedCondition(int index, float threshold) {
			this.index = index;
			this.threshold = threshold;
		}
	}
}
