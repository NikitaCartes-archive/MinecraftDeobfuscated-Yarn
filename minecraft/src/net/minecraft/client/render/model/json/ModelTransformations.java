package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Quaternion;
import net.minecraft.sortme.Matrix4f;

@Environment(EnvType.CLIENT)
public class ModelTransformations {
	public static final ModelTransformations ORIGIN = new ModelTransformations();
	public static float globalTranslationX;
	public static float globalTranslationY;
	public static float globalTranslationZ;
	public static float globalRotationX;
	public static float globalRotationY;
	public static float globalRotationZ;
	public static float globalScaleOffsetX;
	public static float globalScaleOffsetY;
	public static float globalScaleOffsetZ;
	public final ModelTransformation thirdPersonLeftHand;
	public final ModelTransformation thirdPersonRightHand;
	public final ModelTransformation firstPersonLeftHand;
	public final ModelTransformation firstPersonRightHand;
	public final ModelTransformation head;
	public final ModelTransformation gui;
	public final ModelTransformation ground;
	public final ModelTransformation fixed;

	private ModelTransformations() {
		this(
			ModelTransformation.ORIGIN,
			ModelTransformation.ORIGIN,
			ModelTransformation.ORIGIN,
			ModelTransformation.ORIGIN,
			ModelTransformation.ORIGIN,
			ModelTransformation.ORIGIN,
			ModelTransformation.ORIGIN,
			ModelTransformation.ORIGIN
		);
	}

	public ModelTransformations(ModelTransformations modelTransformations) {
		this.thirdPersonLeftHand = modelTransformations.thirdPersonLeftHand;
		this.thirdPersonRightHand = modelTransformations.thirdPersonRightHand;
		this.firstPersonLeftHand = modelTransformations.firstPersonLeftHand;
		this.firstPersonRightHand = modelTransformations.firstPersonRightHand;
		this.head = modelTransformations.head;
		this.gui = modelTransformations.gui;
		this.ground = modelTransformations.ground;
		this.fixed = modelTransformations.fixed;
	}

	public ModelTransformations(
		ModelTransformation modelTransformation,
		ModelTransformation modelTransformation2,
		ModelTransformation modelTransformation3,
		ModelTransformation modelTransformation4,
		ModelTransformation modelTransformation5,
		ModelTransformation modelTransformation6,
		ModelTransformation modelTransformation7,
		ModelTransformation modelTransformation8
	) {
		this.thirdPersonLeftHand = modelTransformation;
		this.thirdPersonRightHand = modelTransformation2;
		this.firstPersonLeftHand = modelTransformation3;
		this.firstPersonRightHand = modelTransformation4;
		this.head = modelTransformation5;
		this.gui = modelTransformation6;
		this.ground = modelTransformation7;
		this.fixed = modelTransformation8;
	}

	public void applyGl(ModelTransformations.Type type) {
		applyGl(this.getTransformation(type), false);
	}

	public static void applyGl(ModelTransformation modelTransformation, boolean bl) {
		if (modelTransformation != ModelTransformation.ORIGIN) {
			int i = bl ? -1 : 1;
			GlStateManager.translatef(
				(float)i * (globalTranslationX + modelTransformation.field_4286.x()),
				globalTranslationY + modelTransformation.field_4286.y(),
				globalTranslationZ + modelTransformation.field_4286.z()
			);
			float f = globalRotationX + modelTransformation.field_4287.x();
			float g = globalRotationY + modelTransformation.field_4287.y();
			float h = globalRotationZ + modelTransformation.field_4287.z();
			if (bl) {
				g = -g;
				h = -h;
			}

			GlStateManager.multMatrix(new Matrix4f(new Quaternion(f, g, h, true)));
			GlStateManager.scalef(
				globalScaleOffsetX + modelTransformation.field_4285.x(),
				globalScaleOffsetY + modelTransformation.field_4285.y(),
				globalScaleOffsetZ + modelTransformation.field_4285.z()
			);
		}
	}

	public ModelTransformation getTransformation(ModelTransformations.Type type) {
		switch (type) {
			case THIRD_PERSON_LEFT_HAND:
				return this.thirdPersonLeftHand;
			case THIRD_PERSON_RIGHT_HAND:
				return this.thirdPersonRightHand;
			case FIRST_PERSON_LEFT_HAND:
				return this.firstPersonLeftHand;
			case FIRST_PERSON_RIGHT_HAND:
				return this.firstPersonRightHand;
			case HEAD:
				return this.head;
			case GUI:
				return this.gui;
			case GROUND:
				return this.ground;
			case FIXED:
				return this.fixed;
			default:
				return ModelTransformation.ORIGIN;
		}
	}

	public boolean isTransformationDefined(ModelTransformations.Type type) {
		return this.getTransformation(type) != ModelTransformation.ORIGIN;
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		ORIGIN,
		THIRD_PERSON_LEFT_HAND,
		THIRD_PERSON_RIGHT_HAND,
		FIRST_PERSON_LEFT_HAND,
		FIRST_PERSON_RIGHT_HAND,
		HEAD,
		GUI,
		GROUND,
		FIXED;
	}

	@Environment(EnvType.CLIENT)
	static class class_810 implements JsonDeserializer<ModelTransformations> {
		public ModelTransformations method_3505(JsonElement jsonElement, java.lang.reflect.Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			ModelTransformation modelTransformation = this.method_3504(jsonDeserializationContext, jsonObject, "thirdperson_righthand");
			ModelTransformation modelTransformation2 = this.method_3504(jsonDeserializationContext, jsonObject, "thirdperson_lefthand");
			if (modelTransformation2 == ModelTransformation.ORIGIN) {
				modelTransformation2 = modelTransformation;
			}

			ModelTransformation modelTransformation3 = this.method_3504(jsonDeserializationContext, jsonObject, "firstperson_righthand");
			ModelTransformation modelTransformation4 = this.method_3504(jsonDeserializationContext, jsonObject, "firstperson_lefthand");
			if (modelTransformation4 == ModelTransformation.ORIGIN) {
				modelTransformation4 = modelTransformation3;
			}

			ModelTransformation modelTransformation5 = this.method_3504(jsonDeserializationContext, jsonObject, "head");
			ModelTransformation modelTransformation6 = this.method_3504(jsonDeserializationContext, jsonObject, "gui");
			ModelTransformation modelTransformation7 = this.method_3504(jsonDeserializationContext, jsonObject, "ground");
			ModelTransformation modelTransformation8 = this.method_3504(jsonDeserializationContext, jsonObject, "fixed");
			return new ModelTransformations(
				modelTransformation2,
				modelTransformation,
				modelTransformation4,
				modelTransformation3,
				modelTransformation5,
				modelTransformation6,
				modelTransformation7,
				modelTransformation8
			);
		}

		private ModelTransformation method_3504(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string) {
			return jsonObject.has(string) ? jsonDeserializationContext.deserialize(jsonObject.get(string), ModelTransformation.class) : ModelTransformation.ORIGIN;
		}
	}
}
