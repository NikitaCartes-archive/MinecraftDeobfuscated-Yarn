package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
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

	public ModelOverrideList(Baker baker, List<ModelOverride> overrides) {
		this.conditionTypes = (Identifier[])overrides.stream()
			.flatMap(modelOverridex -> modelOverridex.conditions().stream())
			.map(ModelOverride.Condition::type)
			.distinct()
			.toArray(Identifier[]::new);
		Object2IntMap<Identifier> object2IntMap = new Object2IntOpenHashMap<>();

		for (int i = 0; i < this.conditionTypes.length; i++) {
			object2IntMap.put(this.conditionTypes[i], i);
		}

		List<ModelOverrideList.BakedOverride> list = Lists.<ModelOverrideList.BakedOverride>newArrayList();

		for (int j = overrides.size() - 1; j >= 0; j--) {
			ModelOverride modelOverride = (ModelOverride)overrides.get(j);
			BakedModel bakedModel = baker.bake(modelOverride.modelId(), net.minecraft.client.render.model.ModelRotation.X0_Y0);
			ModelOverrideList.InlinedCondition[] inlinedConditions = (ModelOverrideList.InlinedCondition[])modelOverride.conditions().stream().map(condition -> {
				int i = object2IntMap.getInt(condition.type());
				return new ModelOverrideList.InlinedCondition(i, condition.threshold());
			}).toArray(ModelOverrideList.InlinedCondition[]::new);
			list.add(new ModelOverrideList.BakedOverride(inlinedConditions, bakedModel));
		}

		this.overrides = (ModelOverrideList.BakedOverride[])list.toArray(new ModelOverrideList.BakedOverride[0]);
	}

	@Nullable
	public BakedModel getModel(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
		int i = this.conditionTypes.length;
		if (i != 0) {
			float[] fs = new float[i];

			for (int j = 0; j < i; j++) {
				Identifier identifier = this.conditionTypes[j];
				ModelPredicateProvider modelPredicateProvider = ModelPredicateProviderRegistry.get(stack, identifier);
				if (modelPredicateProvider != null) {
					fs[j] = modelPredicateProvider.call(stack, world, entity, seed);
				} else {
					fs[j] = Float.NEGATIVE_INFINITY;
				}
			}

			for (ModelOverrideList.BakedOverride bakedOverride : this.overrides) {
				if (bakedOverride.test(fs)) {
					return bakedOverride.model;
				}
			}
		}

		return null;
	}

	@Environment(EnvType.CLIENT)
	static record BakedOverride(ModelOverrideList.InlinedCondition[] conditions, @Nullable BakedModel model) {

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
	static record InlinedCondition(int index, float threshold) {
	}
}
