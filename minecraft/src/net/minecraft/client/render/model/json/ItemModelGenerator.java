package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ItemModelGenerator {
	public static final List<String> LAYERS = Lists.<String>newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

	public JsonUnbakedModel create(Function<Identifier, Sprite> function, JsonUnbakedModel jsonUnbakedModel) {
		Map<String, String> map = Maps.<String, String>newHashMap();
		List<ModelElement> list = Lists.<ModelElement>newArrayList();

		for (int i = 0; i < LAYERS.size(); i++) {
			String string = (String)LAYERS.get(i);
			if (!jsonUnbakedModel.textureExists(string)) {
				break;
			}

			String string2 = jsonUnbakedModel.resolveTexture(string);
			map.put(string, string2);
			Sprite sprite = (Sprite)function.apply(new Identifier(string2));
			list.addAll(this.method_3480(i, string, sprite));
		}

		map.put("particle", jsonUnbakedModel.textureExists("particle") ? jsonUnbakedModel.resolveTexture("particle") : (String)map.get("layer0"));
		JsonUnbakedModel jsonUnbakedModel2 = new JsonUnbakedModel(
			null, list, map, false, false, jsonUnbakedModel.getTransformations(), jsonUnbakedModel.getOverrides()
		);
		jsonUnbakedModel2.id = jsonUnbakedModel.id;
		return jsonUnbakedModel2;
	}

	private List<ModelElement> method_3480(int i, String string, Sprite sprite) {
		Map<Direction, ModelElementFace> map = Maps.<Direction, ModelElementFace>newHashMap();
		map.put(Direction.field_11035, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
		map.put(Direction.field_11043, new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
		List<ModelElement> list = Lists.<ModelElement>newArrayList();
		list.add(new ModelElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, null, true));
		list.addAll(this.method_3481(sprite, string, i));
		return list;
	}

	private List<ModelElement> method_3481(Sprite sprite, String string, int i) {
		float f = (float)sprite.getWidth();
		float g = (float)sprite.getHeight();
		List<ModelElement> list = Lists.<ModelElement>newArrayList();

		for (ItemModelGenerator.class_802 lv : this.method_3478(sprite)) {
			float h = 0.0F;
			float j = 0.0F;
			float k = 0.0F;
			float l = 0.0F;
			float m = 0.0F;
			float n = 0.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 16.0F / f;
			float r = 16.0F / g;
			float s = (float)lv.method_3487();
			float t = (float)lv.method_3485();
			float u = (float)lv.method_3486();
			ItemModelGenerator.class_803 lv2 = lv.method_3484();
			switch (lv2) {
				case field_4281:
					m = s;
					h = s;
					k = n = t + 1.0F;
					o = u;
					j = u;
					l = u;
					p = u + 1.0F;
					break;
				case field_4277:
					o = u;
					p = u + 1.0F;
					m = s;
					h = s;
					k = n = t + 1.0F;
					j = u + 1.0F;
					l = u + 1.0F;
					break;
				case field_4278:
					m = u;
					h = u;
					k = u;
					n = u + 1.0F;
					p = s;
					j = s;
					l = o = t + 1.0F;
					break;
				case field_4283:
					m = u;
					n = u + 1.0F;
					h = u + 1.0F;
					k = u + 1.0F;
					p = s;
					j = s;
					l = o = t + 1.0F;
			}

			h *= q;
			k *= q;
			j *= r;
			l *= r;
			j = 16.0F - j;
			l = 16.0F - l;
			m *= q;
			n *= q;
			o *= r;
			p *= r;
			Map<Direction, ModelElementFace> map = Maps.<Direction, ModelElementFace>newHashMap();
			map.put(lv2.method_3488(), new ModelElementFace(null, i, string, new ModelElementTexture(new float[]{m, o, n, p}, 0)));
			switch (lv2) {
				case field_4281:
					list.add(new ModelElement(new Vector3f(h, j, 7.5F), new Vector3f(k, j, 8.5F), map, null, true));
					break;
				case field_4277:
					list.add(new ModelElement(new Vector3f(h, l, 7.5F), new Vector3f(k, l, 8.5F), map, null, true));
					break;
				case field_4278:
					list.add(new ModelElement(new Vector3f(h, j, 7.5F), new Vector3f(h, l, 8.5F), map, null, true));
					break;
				case field_4283:
					list.add(new ModelElement(new Vector3f(k, j, 7.5F), new Vector3f(k, l, 8.5F), map, null, true));
			}
		}

		return list;
	}

	private List<ItemModelGenerator.class_802> method_3478(Sprite sprite) {
		int i = sprite.getWidth();
		int j = sprite.getHeight();
		List<ItemModelGenerator.class_802> list = Lists.<ItemModelGenerator.class_802>newArrayList();

		for (int k = 0; k < sprite.getFrameCount(); k++) {
			for (int l = 0; l < j; l++) {
				for (int m = 0; m < i; m++) {
					boolean bl = !this.method_3477(sprite, k, m, l, i, j);
					this.method_3476(ItemModelGenerator.class_803.field_4281, list, sprite, k, m, l, i, j, bl);
					this.method_3476(ItemModelGenerator.class_803.field_4277, list, sprite, k, m, l, i, j, bl);
					this.method_3476(ItemModelGenerator.class_803.field_4278, list, sprite, k, m, l, i, j, bl);
					this.method_3476(ItemModelGenerator.class_803.field_4283, list, sprite, k, m, l, i, j, bl);
				}
			}
		}

		return list;
	}

	private void method_3476(
		ItemModelGenerator.class_803 arg, List<ItemModelGenerator.class_802> list, Sprite sprite, int i, int j, int k, int l, int m, boolean bl
	) {
		boolean bl2 = this.method_3477(sprite, i, j + arg.method_3490(), k + arg.method_3489(), l, m) && bl;
		if (bl2) {
			this.method_3482(list, arg, j, k);
		}
	}

	private void method_3482(List<ItemModelGenerator.class_802> list, ItemModelGenerator.class_803 arg, int i, int j) {
		ItemModelGenerator.class_802 lv = null;

		for (ItemModelGenerator.class_802 lv2 : list) {
			if (lv2.method_3484() == arg) {
				int k = arg.method_3491() ? j : i;
				if (lv2.method_3486() == k) {
					lv = lv2;
					break;
				}
			}
		}

		int l = arg.method_3491() ? j : i;
		int m = arg.method_3491() ? i : j;
		if (lv == null) {
			list.add(new ItemModelGenerator.class_802(arg, m, l));
		} else {
			lv.method_3483(m);
		}
	}

	private boolean method_3477(Sprite sprite, int i, int j, int k, int l, int m) {
		return j >= 0 && k >= 0 && j < l && k < m ? sprite.isPixelTransparent(i, j, k) : true;
	}

	@Environment(EnvType.CLIENT)
	static class class_802 {
		private final ItemModelGenerator.class_803 field_4271;
		private int field_4274;
		private int field_4273;
		private final int field_4272;

		public class_802(ItemModelGenerator.class_803 arg, int i, int j) {
			this.field_4271 = arg;
			this.field_4274 = i;
			this.field_4273 = i;
			this.field_4272 = j;
		}

		public void method_3483(int i) {
			if (i < this.field_4274) {
				this.field_4274 = i;
			} else if (i > this.field_4273) {
				this.field_4273 = i;
			}
		}

		public ItemModelGenerator.class_803 method_3484() {
			return this.field_4271;
		}

		public int method_3487() {
			return this.field_4274;
		}

		public int method_3485() {
			return this.field_4273;
		}

		public int method_3486() {
			return this.field_4272;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum class_803 {
		field_4281(Direction.field_11036, 0, -1),
		field_4277(Direction.field_11033, 0, 1),
		field_4278(Direction.field_11034, -1, 0),
		field_4283(Direction.field_11039, 1, 0);

		private final Direction field_4276;
		private final int field_4280;
		private final int field_4279;

		private class_803(Direction direction, int j, int k) {
			this.field_4276 = direction;
			this.field_4280 = j;
			this.field_4279 = k;
		}

		public Direction method_3488() {
			return this.field_4276;
		}

		public int method_3490() {
			return this.field_4280;
		}

		public int method_3489() {
			return this.field_4279;
		}

		private boolean method_3491() {
			return this == field_4277 || this == field_4281;
		}
	}
}
