package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;

@Environment(EnvType.CLIENT)
public class MultipartBakedModel implements BakedModel {
	private final List<Pair<Predicate<BlockState>, BakedModel>> components;
	protected final boolean ambientOcclusion;
	protected final boolean depthGui;
	protected final boolean field_21863;
	protected final Sprite sprite;
	protected final ModelTransformation transformations;
	protected final ModelOverrideList itemPropertyOverrides;
	private final Map<BlockState, BitSet> stateCache = new Object2ObjectOpenCustomHashMap<>(Util.identityHashStrategy());

	public MultipartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> components) {
		this.components = components;
		BakedModel bakedModel = (BakedModel)((Pair)components.iterator().next()).getRight();
		this.ambientOcclusion = bakedModel.useAmbientOcclusion();
		this.depthGui = bakedModel.hasDepth();
		this.field_21863 = bakedModel.isSideLit();
		this.sprite = bakedModel.getSprite();
		this.transformations = bakedModel.getTransformation();
		this.itemPropertyOverrides = bakedModel.getOverrides();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		if (state == null) {
			return Collections.emptyList();
		} else {
			BitSet bitSet = (BitSet)this.stateCache.get(state);
			if (bitSet == null) {
				bitSet = new BitSet();

				for (int i = 0; i < this.components.size(); i++) {
					Pair<Predicate<BlockState>, BakedModel> pair = (Pair<Predicate<BlockState>, BakedModel>)this.components.get(i);
					if (pair.getLeft().test(state)) {
						bitSet.set(i);
					}
				}

				this.stateCache.put(state, bitSet);
			}

			List<BakedQuad> list = Lists.<BakedQuad>newArrayList();
			long l = random.nextLong();

			for (int j = 0; j < bitSet.length(); j++) {
				if (bitSet.get(j)) {
					list.addAll(((BakedModel)((Pair)this.components.get(j)).getRight()).getQuads(state, face, new Random(l)));
				}
			}

			return list;
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.ambientOcclusion;
	}

	@Override
	public boolean hasDepth() {
		return this.depthGui;
	}

	@Override
	public boolean isSideLit() {
		return this.field_21863;
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
		return this.transformations;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return this.itemPropertyOverrides;
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final List<Pair<Predicate<BlockState>, BakedModel>> components = Lists.<Pair<Predicate<BlockState>, BakedModel>>newArrayList();

		public void addComponent(Predicate<BlockState> predicate, BakedModel model) {
			this.components.add(Pair.of(predicate, model));
		}

		public BakedModel build() {
			return new MultipartBakedModel(this.components);
		}
	}
}
