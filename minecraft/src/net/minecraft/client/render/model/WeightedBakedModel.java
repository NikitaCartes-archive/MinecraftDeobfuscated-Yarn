package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class WeightedBakedModel implements BakedModel {
	private final int totalWeight;
	private final List<Weighted.Present<BakedModel>> models;
	private final BakedModel defaultModel;

	public WeightedBakedModel(List<Weighted.Present<BakedModel>> models) {
		this.models = models;
		this.totalWeight = Weighting.getWeightSum(models);
		this.defaultModel = (BakedModel)((Weighted.Present)models.get(0)).data();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return (List<BakedQuad>)Weighting.getAt(this.models, Math.abs((int)random.nextLong()) % this.totalWeight)
			.map(present -> ((BakedModel)present.data()).getQuads(state, face, random))
			.orElse(Collections.emptyList());
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.defaultModel.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return this.defaultModel.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return this.defaultModel.isSideLit();
	}

	@Override
	public boolean isBuiltin() {
		return this.defaultModel.isBuiltin();
	}

	@Override
	public Sprite getParticleSprite() {
		return this.defaultModel.getParticleSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.defaultModel.getTransformation();
	}

	@Override
	public ModelOverrideList getOverrides() {
		return this.defaultModel.getOverrides();
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final ImmutableList.Builder<Weighted.Present<BakedModel>> models = ImmutableList.builder();

		public WeightedBakedModel.Builder add(@Nullable BakedModel model, int weight) {
			if (model != null) {
				this.models.add(Weighted.of(model, weight));
			}

			return this;
		}

		@Nullable
		public BakedModel build() {
			List<Weighted.Present<BakedModel>> list = this.models.build();
			if (list.isEmpty()) {
				return null;
			} else {
				return (BakedModel)(list.size() == 1 ? (BakedModel)((Weighted.Present)list.getFirst()).data() : new WeightedBakedModel(list));
			}
		}
	}
}
