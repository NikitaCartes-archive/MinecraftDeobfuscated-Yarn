package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelTransformation {
	public static final ModelTransformation NONE = new ModelTransformation();
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

	public Transformation getTransformation(ModelTransformation.Type type) {
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
