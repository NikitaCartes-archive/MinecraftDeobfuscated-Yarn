package net.minecraft.client.render.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public interface BakedModel {
	List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random);

	boolean useAmbientOcclusion();

	boolean hasDepthInGui();

	boolean method_24304();

	boolean isBuiltin();

	Sprite getSprite();

	ModelTransformation getTransformation();

	ModelItemPropertyOverrideList getItemPropertyOverrides();
}
