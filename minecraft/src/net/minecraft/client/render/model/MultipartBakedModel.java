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
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;

@Environment(EnvType.CLIENT)
public class MultipartBakedModel implements BakedModel {
	private final List<Pair<Predicate<BlockState>, BakedModel>> components;
	protected final boolean ambientOcclusion;
	protected final boolean depthGui;
	protected final Sprite sprite;
	protected final ModelTransformation transformations;
	protected final ModelItemPropertyOverrideList itemPropertyOverrides;
	private final Map<BlockState, BitSet> field_5431 = new Object2ObjectOpenCustomHashMap<>(SystemUtil.identityHashStrategy());

	public MultipartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> list) {
		this.components = list;
		BakedModel bakedModel = (BakedModel)((Pair)list.iterator().next()).getRight();
		this.ambientOcclusion = bakedModel.useAmbientOcclusion();
		this.depthGui = bakedModel.hasDepthInGui();
		this.sprite = bakedModel.getSprite();
		this.transformations = bakedModel.getTransformation();
		this.itemPropertyOverrides = bakedModel.getItemPropertyOverrides();
	}

	@Override
	public List<BakedQuad> method_4707(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
		if (blockState == null) {
			return Collections.emptyList();
		} else {
			BitSet bitSet = (BitSet)this.field_5431.get(blockState);
			if (bitSet == null) {
				bitSet = new BitSet();

				for (int i = 0; i < this.components.size(); i++) {
					Pair<Predicate<BlockState>, BakedModel> pair = (Pair<Predicate<BlockState>, BakedModel>)this.components.get(i);
					if (pair.getLeft().test(blockState)) {
						bitSet.set(i);
					}
				}

				this.field_5431.put(blockState, bitSet);
			}

			List<BakedQuad> list = Lists.<BakedQuad>newArrayList();
			long l = random.nextLong();

			for (int j = 0; j < bitSet.length(); j++) {
				if (bitSet.get(j)) {
					list.addAll(((BakedModel)((Pair)this.components.get(j)).getRight()).method_4707(blockState, direction, new Random(l)));
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
	public boolean hasDepthInGui() {
		return this.depthGui;
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
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return this.itemPropertyOverrides;
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final List<Pair<Predicate<BlockState>, BakedModel>> components = Lists.<Pair<Predicate<BlockState>, BakedModel>>newArrayList();

		public void addComponent(Predicate<BlockState> predicate, BakedModel bakedModel) {
			this.components.add(Pair.of(predicate, bakedModel));
		}

		public BakedModel build() {
			return new MultipartBakedModel(this.components);
		}
	}
}
