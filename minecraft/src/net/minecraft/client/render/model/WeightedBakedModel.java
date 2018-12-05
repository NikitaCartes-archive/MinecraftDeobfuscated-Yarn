package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class WeightedBakedModel implements BakedModel {
	private final int totalWeight;
	private final List<WeightedBakedModel.ModelEntry> models;
	private final BakedModel defaultModel;

	public WeightedBakedModel(List<WeightedBakedModel.ModelEntry> list) {
		this.models = list;
		this.totalWeight = WeightedPicker.getWeightSum(list);
		this.defaultModel = ((WeightedBakedModel.ModelEntry)list.get(0)).model;
	}

	@Override
	public List<BakedQuad> method_4707(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		return WeightedPicker.getAt(this.models, Math.abs((int)random.nextLong()) % this.totalWeight).model.method_4707(blockState, direction, random);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.defaultModel.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepthInGui() {
		return this.defaultModel.hasDepthInGui();
	}

	@Override
	public boolean isBuiltin() {
		return this.defaultModel.isBuiltin();
	}

	@Override
	public Sprite getSprite() {
		return this.defaultModel.getSprite();
	}

	@Override
	public ModelTransformations getTransformations() {
		return this.defaultModel.getTransformations();
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return this.defaultModel.getItemPropertyOverrides();
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final List<WeightedBakedModel.ModelEntry> models = Lists.<WeightedBakedModel.ModelEntry>newArrayList();

		public WeightedBakedModel.Builder add(@Nullable BakedModel bakedModel, int i) {
			if (bakedModel != null) {
				this.models.add(new WeightedBakedModel.ModelEntry(bakedModel, i));
			}

			return this;
		}

		@Nullable
		public BakedModel getFirst() {
			if (this.models.isEmpty()) {
				return null;
			} else {
				return (BakedModel)(this.models.size() == 1 ? ((WeightedBakedModel.ModelEntry)this.models.get(0)).model : new WeightedBakedModel(this.models));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class ModelEntry extends WeightedPicker.Entry {
		protected final BakedModel model;

		public ModelEntry(BakedModel bakedModel, int i) {
			super(i);
			this.model = bakedModel;
		}
	}
}
