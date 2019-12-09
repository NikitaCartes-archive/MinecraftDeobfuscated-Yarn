/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.Transformation;

@Environment(value=EnvType.CLIENT)
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
        this(Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE, Transformation.NONE);
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

    public ModelTransformation(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed) {
        this.thirdPersonLeftHand = thirdPersonLeftHand;
        this.thirdPersonRightHand = thirdPersonRightHand;
        this.firstPersonLeftHand = firstPersonLeftHand;
        this.firstPersonRightHand = firstPersonRightHand;
        this.head = head;
        this.gui = gui;
        this.ground = ground;
        this.fixed = fixed;
    }

    public Transformation getTransformation(Type type) {
        switch (type) {
            case THIRD_PERSON_LEFT_HAND: {
                return this.thirdPersonLeftHand;
            }
            case THIRD_PERSON_RIGHT_HAND: {
                return this.thirdPersonRightHand;
            }
            case FIRST_PERSON_LEFT_HAND: {
                return this.firstPersonLeftHand;
            }
            case FIRST_PERSON_RIGHT_HAND: {
                return this.firstPersonRightHand;
            }
            case HEAD: {
                return this.head;
            }
            case GUI: {
                return this.gui;
            }
            case GROUND: {
                return this.ground;
            }
            case FIXED: {
                return this.fixed;
            }
        }
        return Transformation.NONE;
    }

    public boolean isTransformationDefined(Type type) {
        return this.getTransformation(type) != Transformation.NONE;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelTransformation> {
        protected Deserializer() {
        }

        @Override
        public ModelTransformation deserialize(JsonElement element, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = element.getAsJsonObject();
            Transformation transformation = this.parseModelTransformation(context, jsonObject, "thirdperson_righthand");
            Transformation transformation2 = this.parseModelTransformation(context, jsonObject, "thirdperson_lefthand");
            if (transformation2 == Transformation.NONE) {
                transformation2 = transformation;
            }
            Transformation transformation3 = this.parseModelTransformation(context, jsonObject, "firstperson_righthand");
            Transformation transformation4 = this.parseModelTransformation(context, jsonObject, "firstperson_lefthand");
            if (transformation4 == Transformation.NONE) {
                transformation4 = transformation3;
            }
            Transformation transformation5 = this.parseModelTransformation(context, jsonObject, "head");
            Transformation transformation6 = this.parseModelTransformation(context, jsonObject, "gui");
            Transformation transformation7 = this.parseModelTransformation(context, jsonObject, "ground");
            Transformation transformation8 = this.parseModelTransformation(context, jsonObject, "fixed");
            return new ModelTransformation(transformation2, transformation, transformation4, transformation3, transformation5, transformation6, transformation7, transformation8);
        }

        private Transformation parseModelTransformation(JsonDeserializationContext ctx, JsonObject json, String key) {
            if (json.has(key)) {
                return (Transformation)ctx.deserialize(json.get(key), (java.lang.reflect.Type)((Object)Transformation.class));
            }
            return Transformation.NONE;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, java.lang.reflect.Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }

    @Environment(value=EnvType.CLIENT)
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

