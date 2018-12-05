package net.minecraft.client.render.model;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BuiltinBakedModel implements BakedModel {
	private final ModelTransformations transformations;
	private final ModelItemPropertyOverrideList itemOverrideList;
	private final Sprite sprite;

	public BuiltinBakedModel(ModelTransformations modelTransformations, ModelItemPropertyOverrideList modelItemPropertyOverrideList, Sprite sprite) {
		this.transformations = modelTransformations;
		this.itemOverrideList = modelItemPropertyOverrideList;
		this.sprite = sprite;
	}

	@Override
	public List<BakedQuad> method_4707(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		return Collections.emptyList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean hasDepthInGui() {
		return true;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public ModelTransformations getTransformations() {
		return this.transformations;
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return this.itemOverrideList;
	}
}
