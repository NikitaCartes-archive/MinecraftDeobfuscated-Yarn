package net.minecraft.client.render.model;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public abstract class WrapperBakedModel implements BakedModel {
	protected final BakedModel wrapped;

	public WrapperBakedModel(BakedModel wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return this.wrapped.getQuads(state, face, random);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.wrapped.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return this.wrapped.hasDepth();
	}

	@Override
	public boolean isSideLit() {
		return this.wrapped.isSideLit();
	}

	@Override
	public boolean isBuiltin() {
		return this.wrapped.isBuiltin();
	}

	@Override
	public Sprite getParticleSprite() {
		return this.wrapped.getParticleSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.wrapped.getTransformation();
	}
}
