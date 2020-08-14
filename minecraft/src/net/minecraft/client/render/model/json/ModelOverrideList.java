package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelOverrideList {
	public static final ModelOverrideList EMPTY = new ModelOverrideList();
	private final List<ModelOverride> overrides = Lists.<ModelOverride>newArrayList();
	private final List<BakedModel> models;

	private ModelOverrideList() {
		this.models = Collections.emptyList();
	}

	public ModelOverrideList(
		ModelLoader modelLoader, JsonUnbakedModel unbakedModel, Function<Identifier, UnbakedModel> unbakedModelGetter, List<ModelOverride> overrides
	) {
		this.models = (List<BakedModel>)overrides.stream()
			.map(
				modelOverride -> {
					UnbakedModel unbakedModelx = (UnbakedModel)unbakedModelGetter.apply(modelOverride.getModelId());
					return Objects.equals(unbakedModelx, unbakedModel)
						? null
						: modelLoader.bake(modelOverride.getModelId(), net.minecraft.client.render.model.ModelRotation.X0_Y0);
				}
			)
			.collect(Collectors.toList());
		Collections.reverse(this.models);

		for (int i = overrides.size() - 1; i >= 0; i--) {
			this.overrides.add(overrides.get(i));
		}
	}

	@Nullable
	public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
		if (!this.overrides.isEmpty()) {
			for (int i = 0; i < this.overrides.size(); i++) {
				ModelOverride modelOverride = (ModelOverride)this.overrides.get(i);
				if (modelOverride.matches(stack, world, entity)) {
					BakedModel bakedModel = (BakedModel)this.models.get(i);
					if (bakedModel == null) {
						return model;
					}

					return bakedModel;
				}
			}
		}

		return model;
	}
}
