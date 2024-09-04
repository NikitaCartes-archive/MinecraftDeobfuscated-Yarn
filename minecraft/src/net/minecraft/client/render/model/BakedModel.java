package net.minecraft.client.render.model;

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
public interface BakedModel {
	List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random);

	boolean useAmbientOcclusion();

	boolean hasDepth();

	/**
	 * Allows control of the lighting when rendering a model in a GUI.
	 * <p>
	 * True, the model will be lit from the side, like a block.
	 * <p>
	 * False, the model will be lit from the front, like an item.
	 */
	boolean isSideLit();

	boolean isBuiltin();

	/**
	 * {@return a texture that represents the model}
	 * <p>
	 * This is primarily used in particles. For example, block break particles use this sprite.
	 */
	Sprite getParticleSprite();

	ModelTransformation getTransformation();

	default ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}
}
