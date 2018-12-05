package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotationContainer;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_816 implements UnbakedModel {
	private final StateFactory<Block, BlockState> field_4329;
	private final List<class_819> field_4330;

	public class_816(StateFactory<Block, BlockState> stateFactory, List<class_819> list) {
		this.field_4329 = stateFactory;
		this.field_4330 = list;
	}

	public List<class_819> method_3519() {
		return this.field_4330;
	}

	public Set<WeightedUnbakedModel> method_3520() {
		Set<WeightedUnbakedModel> set = Sets.<WeightedUnbakedModel>newHashSet();

		for (class_819 lv : this.field_4330) {
			set.add(lv.method_3529());
		}

		return set;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_816)) {
			return false;
		} else {
			class_816 lv = (class_816)object;
			return Objects.equals(this.field_4329, lv.field_4329) && Objects.equals(this.field_4330, lv.field_4330);
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_4329, this.field_4330});
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return (Collection<Identifier>)this.method_3519().stream().flatMap(arg -> arg.method_3529().getModelDependencies().stream()).collect(Collectors.toSet());
	}

	@Override
	public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<String> set) {
		return (Collection<Identifier>)this.method_3519()
			.stream()
			.flatMap(arg -> arg.method_3529().getTextureDependencies(function, set).stream())
			.collect(Collectors.toSet());
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader modelLoader, Function<Identifier, Sprite> function, ModelRotationContainer modelRotationContainer) {
		MultipartBakedModel.class_1096 lv = new MultipartBakedModel.class_1096();

		for (class_819 lv2 : this.method_3519()) {
			BakedModel bakedModel = lv2.method_3529().bake(modelLoader, function, modelRotationContainer);
			if (bakedModel != null) {
				lv.method_4749(lv2.method_3530(this.field_4329), bakedModel);
			}
		}

		return lv.method_4750();
	}

	@Environment(EnvType.CLIENT)
	public static class class_817 implements JsonDeserializer<class_816> {
		private final ModelVariantMap.class_791 field_4331;

		public class_817(ModelVariantMap.class_791 arg) {
			this.field_4331 = arg;
		}

		public class_816 method_3523(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new class_816(this.field_4331.method_3425(), this.method_3522(jsonDeserializationContext, jsonElement.getAsJsonArray()));
		}

		private List<class_819> method_3522(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
			List<class_819> list = Lists.<class_819>newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				list.add(jsonDeserializationContext.deserialize(jsonElement, class_819.class));
			}

			return list;
		}
	}
}
