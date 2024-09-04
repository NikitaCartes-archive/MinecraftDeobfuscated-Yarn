package net.minecraft.client.render.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class WeightedBakedModel extends WrapperBakedModel {
	private final DataPool<BakedModel> models;

	public WeightedBakedModel(DataPool<BakedModel> models) {
		super((BakedModel)((Weighted.Present)models.getEntries().getFirst()).data());
		this.models = models;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return (List<BakedQuad>)this.models.getDataOrEmpty(random).map(model -> model.getQuads(state, face, random)).orElse(Collections.emptyList());
	}
}
