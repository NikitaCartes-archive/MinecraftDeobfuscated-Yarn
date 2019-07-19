package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BasicBakedModel implements BakedModel {
	protected final List<BakedQuad> quads;
	protected final Map<Direction, List<BakedQuad>> faceQuads;
	protected final boolean usesAo;
	protected final boolean hasDepth;
	protected final Sprite sprite;
	protected final ModelTransformation transformation;
	protected final ModelItemPropertyOverrideList itemPropertyOverrides;

	public BasicBakedModel(
		List<BakedQuad> quads,
		Map<Direction, List<BakedQuad>> faceQuads,
		boolean usesAo,
		boolean is3dInGui,
		Sprite sprite,
		ModelTransformation transformation,
		ModelItemPropertyOverrideList itemPropertyOverrides
	) {
		this.quads = quads;
		this.faceQuads = faceQuads;
		this.usesAo = usesAo;
		this.hasDepth = is3dInGui;
		this.sprite = sprite;
		this.transformation = transformation;
		this.itemPropertyOverrides = itemPropertyOverrides;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return face == null ? this.quads : (List)this.faceQuads.get(face);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.usesAo;
	}

	@Override
	public boolean hasDepth() {
		return this.hasDepth;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.transformation;
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return this.itemPropertyOverrides;
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final List<BakedQuad> quads = Lists.<BakedQuad>newArrayList();
		private final Map<Direction, List<BakedQuad>> faceQuads = Maps.newEnumMap(Direction.class);
		private final ModelItemPropertyOverrideList itemPropertyOverrides;
		private final boolean usesAo;
		private Sprite particleTexture;
		private final boolean depthInGui;
		private final ModelTransformation transformation;

		public Builder(JsonUnbakedModel unbakedModel, ModelItemPropertyOverrideList itemPropertyOverrides) {
			this(unbakedModel.useAmbientOcclusion(), unbakedModel.hasDepthInGui(), unbakedModel.getTransformations(), itemPropertyOverrides);
		}

		public Builder(BlockState state, BakedModel bakedModel, Sprite sprite, Random random, long randomSeed) {
			this(bakedModel.useAmbientOcclusion(), bakedModel.hasDepth(), bakedModel.getTransformation(), bakedModel.getItemPropertyOverrides());
			this.particleTexture = bakedModel.getSprite();

			for (Direction direction : Direction.values()) {
				random.setSeed(randomSeed);

				for (BakedQuad bakedQuad : bakedModel.getQuads(state, direction, random)) {
					this.addQuad(direction, new RetexturedBakedQuad(bakedQuad, sprite));
				}
			}

			random.setSeed(randomSeed);

			for (BakedQuad bakedQuad2 : bakedModel.getQuads(state, null, random)) {
				this.addQuad(new RetexturedBakedQuad(bakedQuad2, sprite));
			}
		}

		private Builder(boolean usesAo, boolean depthInGui, ModelTransformation transformation, ModelItemPropertyOverrideList itemPropertyOverrides) {
			for (Direction direction : Direction.values()) {
				this.faceQuads.put(direction, Lists.newArrayList());
			}

			this.itemPropertyOverrides = itemPropertyOverrides;
			this.usesAo = usesAo;
			this.depthInGui = depthInGui;
			this.transformation = transformation;
		}

		public BasicBakedModel.Builder addQuad(Direction side, BakedQuad quad) {
			((List)this.faceQuads.get(side)).add(quad);
			return this;
		}

		public BasicBakedModel.Builder addQuad(BakedQuad quad) {
			this.quads.add(quad);
			return this;
		}

		public BasicBakedModel.Builder setParticle(Sprite sprite) {
			this.particleTexture = sprite;
			return this;
		}

		public BakedModel build() {
			if (this.particleTexture == null) {
				throw new RuntimeException("Missing particle!");
			} else {
				return new BasicBakedModel(this.quads, this.faceQuads, this.usesAo, this.depthInGui, this.particleTexture, this.transformation, this.itemPropertyOverrides);
			}
		}
	}
}
