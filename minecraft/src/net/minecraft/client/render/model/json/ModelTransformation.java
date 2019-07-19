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
import net.minecraft.util.math.Quaternion;

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
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY,
			Transformation.IDENTITY
		);
	}

	public ModelTransformation(ModelTransformation other) {
		this.thirdPersonLeftHand = other.thirdPersonLeftHand;
		this.thirdPersonRightHand = other.thirdPersonRightHand;
		this.firstPersonLeftHand = other.firstPersonLeftHand;
		this.firstPersonRightHand = other.firstPersonRightHand;
		this.head = other.head;
		this.gui = other.gui;
		this.ground = other.ground;
		this.fixed = other.fixed;
	}

	public ModelTransformation(
		Transformation thirdPersonLeftHand,
		Transformation thirdPersonRightHand,
		Transformation firstPersonLeftHand,
		Transformation firstPersonRightHand,
		Transformation head,
		Transformation gui,
		Transformation ground,
		Transformation fixed
	) {
		this.thirdPersonLeftHand = thirdPersonLeftHand;
		this.thirdPersonRightHand = thirdPersonRightHand;
		this.firstPersonLeftHand = firstPersonLeftHand;
		this.firstPersonRightHand = firstPersonRightHand;
		this.head = head;
		this.gui = gui;
		this.ground = ground;
		this.fixed = fixed;
	}

	public void applyGl(ModelTransformation.Type type) {
		applyGl(this.getTransformation(type), false);
	}

	public static void applyGl(Transformation transform, boolean leftyFlip) {
		if (transform != Transformation.IDENTITY) {
			int i = leftyFlip ? -1 : 1;
			GlStateManager.translatef(
				(float)i * (globalTranslationX + transform.translation.getX()),
				globalTranslationY + transform.translation.getY(),
				globalTranslationZ + transform.translation.getZ()
			);
			float f = globalRotationX + transform.rotation.getX();
			float g = globalRotationY + transform.rotation.getY();
			float h = globalRotationZ + transform.rotation.getZ();
			if (leftyFlip) {
				g = -g;
				h = -h;
			}

			GlStateManager.multMatrix(new Matrix4f(new Quaternion(f, g, h, true)));
			GlStateManager.scalef(globalScaleOffsetX + transform.scale.getX(), globalScaleOffsetY + transform.scale.getY(), globalScaleOffsetZ + transform.scale.getZ());
		}
	}

	public Transformation getTransformation(ModelTransformation.Type renderMode) {
		switch (renderMode) {
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
				return Transformation.IDENTITY;
		}
	}

	public boolean isTransformationDefined(ModelTransformation.Type renderMode) {
		return this.getTransformation(renderMode) != Transformation.IDENTITY;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelTransformation> {
		protected Deserializer() {
		}

		public ModelTransformation deserialize(JsonElement element, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = element.getAsJsonObject();
			Transformation transformation = this.parseModelTransformation(context, jsonObject, "thirdperson_righthand");
			Transformation transformation2 = this.parseModelTransformation(context, jsonObject, "thirdperson_lefthand");
			if (transformation2 == Transformation.IDENTITY) {
				transformation2 = transformation;
			}

			Transformation transformation3 = this.parseModelTransformation(context, jsonObject, "firstperson_righthand");
			Transformation transformation4 = this.parseModelTransformation(context, jsonObject, "firstperson_lefthand");
			if (transformation4 == Transformation.IDENTITY) {
				transformation4 = transformation3;
			}

			Transformation transformation5 = this.parseModelTransformation(context, jsonObject, "head");
			Transformation transformation6 = this.parseModelTransformation(context, jsonObject, "gui");
			Transformation transformation7 = this.parseModelTransformation(context, jsonObject, "ground");
			Transformation transformation8 = this.parseModelTransformation(context, jsonObject, "fixed");
			return new ModelTransformation(
				transformation2, transformation, transformation4, transformation3, transformation5, transformation6, transformation7, transformation8
			);
		}

		private Transformation parseModelTransformation(JsonDeserializationContext ctx, JsonObject json, String key) {
			return json.has(key) ? ctx.deserialize(json.get(key), Transformation.class) : Transformation.IDENTITY;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		NONE,
		THIRD_PERSON_LEFT_HAND,
		THIRD_PERSON_RIGHT_HAND,
		FIRST_PERSON_LEFT_HAND,
		FIRST_PERSON_RIGHT_HAND,
		HEAD,
		GUI,
		GROUND,
		FIXED;
	}
}
