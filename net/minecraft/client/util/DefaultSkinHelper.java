/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DefaultSkinHelper {
    private static final Skin[] SKINS = new Skin[]{new Skin("textures/entity/player/slim/alex.png", Model.SLIM), new Skin("textures/entity/player/slim/ari.png", Model.SLIM), new Skin("textures/entity/player/slim/efe.png", Model.SLIM), new Skin("textures/entity/player/slim/kai.png", Model.SLIM), new Skin("textures/entity/player/slim/makena.png", Model.SLIM), new Skin("textures/entity/player/slim/noor.png", Model.SLIM), new Skin("textures/entity/player/slim/steve.png", Model.SLIM), new Skin("textures/entity/player/slim/sunny.png", Model.SLIM), new Skin("textures/entity/player/slim/zuri.png", Model.SLIM), new Skin("textures/entity/player/wide/alex.png", Model.WIDE), new Skin("textures/entity/player/wide/ari.png", Model.WIDE), new Skin("textures/entity/player/wide/efe.png", Model.WIDE), new Skin("textures/entity/player/wide/kai.png", Model.WIDE), new Skin("textures/entity/player/wide/makena.png", Model.WIDE), new Skin("textures/entity/player/wide/noor.png", Model.WIDE), new Skin("textures/entity/player/wide/steve.png", Model.WIDE), new Skin("textures/entity/player/wide/sunny.png", Model.WIDE), new Skin("textures/entity/player/wide/zuri.png", Model.WIDE)};

    public static Identifier getTexture() {
        return SKINS[6].texture();
    }

    public static Identifier getTexture(UUID uuid) {
        return DefaultSkinHelper.getSkin((UUID)uuid).texture;
    }

    public static String getModel(UUID uuid) {
        return DefaultSkinHelper.getSkin((UUID)uuid).model.name;
    }

    private static Skin getSkin(UUID uuid) {
        return SKINS[Math.floorMod(uuid.hashCode(), SKINS.length)];
    }

    @Environment(value=EnvType.CLIENT)
    record Skin(Identifier texture, Model model) {
        public Skin(String texture, Model model) {
            this(new Identifier(texture), model);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Model {
        SLIM("slim"),
        WIDE("default");

        final String name;

        private Model(String name) {
            this.name = name;
        }
    }
}

