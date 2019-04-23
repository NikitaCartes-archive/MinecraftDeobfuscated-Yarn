package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class ModelTransformation {
	public static final ModelTransformation NONE = new ModelTransformation();
	public static float globalTranslationX;
	public static float globalTranslationY;
	public static float globalTranslationZ;
	public static float globalRotationX;
	public static float globalRotationY;
	public static float globalRotationZ;
	public static float globalScaleOffsetX;
	public static float globalScaleOffsetY;
	public static float globalScaleOffsetZ;
	public final Transformation thirdPersonLeftHand;
	public final Transformation thirdPersonRightHand;
	public final Transformation firstPersonLeftHand;
	public final Transformation firstPersonRightHand;
	public final Transformation head;
	public final Transformation gui;
	public final Transformation ground;
	public final Transformation fixed;

	private ModelTransformation() {
		this(
			Transformation.NONE,
			Transformation.NONE,
			Transformation.NONE,
			Transformation.NONE,
			Transformation.NONE,
			Transformation.NONE,
			Transformation.NONE,
			Transformation.NONE
		);
	}

	public ModelTransformation(ModelTransformation modelTransformation) {
		this.thirdPersonLeftHand = modelTransformation.thirdPersonLeftHand;
		this.thirdPersonRightHand = modelTransformation.thirdPersonRightHand;
		this.firstPersonLeftHand = modelTransformation.firstPersonLeftHand;
		this.firstPersonRightHand = modelTransformation.firstPersonRightHand;
		this.head = modelTransformation.head;
		this.gui = modelTransformation.gui;
		this.ground = modelTransformation.ground;
		this.fixed = modelTransformation.fixed;
	}

	public ModelTransformation(
		Transformation transformation,
		Transformation transformation2,
		Transformation transformation3,
		Transformation transformation4,
		Transformation transformation5,
		Transformation transformation6,
		Transformation transformation7,
		Transformation transformation8
	) {
		this.thirdPersonLeftHand = transformation;
		this.thirdPersonRightHand = transformation2;
		this.firstPersonLeftHand = transformation3;
		this.firstPersonRightHand = transformation4;
		this.head = transformation5;
		this.gui = transformation6;
		this.ground = transformation7;
		this.fixed = transformation8;
	}

	public void applyGl(ModelTransformation.Type type) {
		applyGl(this.getTransformation(type), false);
	}

	public static void applyGl(Transformation transformation, boolean bl) {
		if (transformation != Transformation.NONE) {
			int i = bl ? -1 : 1;
			GlStateManager.translatef(
				(float)i * (globalTranslationX + transformation.translation.x()),
				globalTranslationY + transformation.translation.y(),
				globalTranslationZ + transformation.translation.z()
			);
			float f = globalRotationX + transformation.rotation.x();
			float g = globalRotationY + transformation.rotation.y();
			float h = globalRotationZ + transformation.rotation.z();
			if (bl) {
				g = -g;
				h = -h;
			}

			GlStateManager.multMatrix(new Matrix4f(new Quaternion(f, g, h, true)));
			GlStateManager.scalef(
				globalScaleOffsetX + transformation.scale.x(), globalScaleOffsetY + transformation.scale.y(), globalScaleOffsetZ + transformation.scale.z()
			);
		}
	}

	public Transformation getTransformation(ModelTransformation.Type type) {
		switch (type) {
			case field_4323:
				return this.thirdPersonLeftHand;
			case field_4320:
				return this.thirdPersonRightHand;
			case field_4321:
				return this.firstPersonLeftHand;
			case field_4322:
				return this.firstPersonRightHand;
			case field_4316:
				return this.head;
			case field_4317:
				return this.gui;
			case field_4318:
				return this.ground;
			case field_4319:
				return this.fixed;
			default:
				return Transformation.NONE;
		}
	}

	public boolean isTransformationDefined(ModelTransformation.Type type) {
		return this.getTransformation(type) != Transformation.NONE;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelTransformation> {
		protected Deserializer() {
		}

		public ModelTransformation method_3505(JsonElement jsonElement, java.lang.reflect.Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Transformation transformation = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_righthand");
			Transformation transformation2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "thirdperson_lefthand");
			if (transformation2 == Transformation.NONE) {
				transformation2 = transformation;
			}

			Transformation transformation3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_righthand");
			Transformation transformation4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "firstperson_lefthand");
			if (transformation4 == Transformation.NONE) {
				transformation4 = transformation3;
			}

			Transformation transformation5 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "head");
			Transformation transformation6 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "gui");
			Transformation transformation7 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "ground");
			Transformation transformation8 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, "fixed");
			return new ModelTransformation(
				transformation2, transformation, transformation4, transformation3, transformation5, transformation6, transformation7, transformation8
			);
		}

		private Transformation parseModelTransformation(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string) {
			return jsonObject.has(string) ? jsonDeserializationContext.deserialize(jsonObject.get(string), Transformation.class) : Transformation.NONE;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		field_4315,
		field_4323,
		field_4320,
		field_4321,
		field_4322,
		field_4316,
		field_4317,
		field_4318,
		field_4319;
	}
}
