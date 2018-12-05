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
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ModelItemPropertyOverrideList {
	public static final ModelItemPropertyOverrideList ORIGIN = new ModelItemPropertyOverrideList();
	private final List<ModelItemOverride> field_4293 = Lists.<ModelItemOverride>newArrayList();
	private final List<BakedModel> field_4291;

	private ModelItemPropertyOverrideList() {
		this.field_4291 = Collections.emptyList();
	}

	public ModelItemPropertyOverrideList(
		ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel, Function<Identifier, UnbakedModel> function, List<ModelItemOverride> list
	) {
		this.field_4291 = (List<BakedModel>)list.stream().map(modelItemOverride -> {
			UnbakedModel unbakedModel = (UnbakedModel)function.apply(modelItemOverride.getModelId());
			return Objects.equals(unbakedModel, jsonUnbakedModel) ? null : modelLoader.bake(modelItemOverride.getModelId(), ModelRotation.X0_Y0);
		}).collect(Collectors.toList());
		Collections.reverse(this.field_4291);

		for (int i = list.size() - 1; i >= 0; i--) {
			this.field_4293.add(list.get(i));
		}
	}

	@Nullable
	public BakedModel method_3495(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		if (!this.field_4293.isEmpty()) {
			for (int i = 0; i < this.field_4293.size(); i++) {
				ModelItemOverride modelItemOverride = (ModelItemOverride)this.field_4293.get(i);
				if (modelItemOverride.matches(itemStack, world, livingEntity)) {
					BakedModel bakedModel2 = (BakedModel)this.field_4291.get(i);
					if (bakedModel2 == null) {
						return bakedModel;
					}

					return bakedModel2;
				}
			}
		}

		return bakedModel;
	}
}
