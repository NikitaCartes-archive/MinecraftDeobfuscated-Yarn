package net.minecraft.client.render.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class BuiltinBakedModel implements BakedModel {
	private final ModelTransformation transformation;
	private final ModelOverrideList itemPropertyOverrides;
	private final Sprite sprite;
	private final boolean sideLit;

	public BuiltinBakedModel(ModelTransformation transformation, ModelOverrideList itemPropertyOverrides, Sprite sprite, boolean sideLit) {
		this.transformation = transformation;
		this.itemPropertyOverrides = itemPropertyOverrides;
		this.sprite = sprite;
		this.sideLit = sideLit;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return Collections.emptyList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean hasDepth() {
		return true;
	}

	@Override
	public boolean isSideLit() {
		return this.sideLit;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public Sprite getParticleSprite() {
		return this.sprite;
	}

	@Override
	public ModelTransformation getTransformation() {
		return this.transformation;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return this.itemPropertyOverrides;
	}
}
