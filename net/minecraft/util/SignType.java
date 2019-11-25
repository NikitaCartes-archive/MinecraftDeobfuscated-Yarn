/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SignType {
    private static final Set<SignType> VALUES = new ObjectArraySet<SignType>();
    public static final SignType OAK = SignType.register(new SignType("oak"));
    public static final SignType SPRUCE = SignType.register(new SignType("spruce"));
    public static final SignType BIRCH = SignType.register(new SignType("birch"));
    public static final SignType ACACIA = SignType.register(new SignType("acacia"));
    public static final SignType JUNGLE = SignType.register(new SignType("jungle"));
    public static final SignType DARK_OAK = SignType.register(new SignType("dark_oak"));
    private final String name;

    protected SignType(String string) {
        this.name = string;
    }

    private static SignType register(SignType signType) {
        VALUES.add(signType);
        return signType;
    }

    @Environment(value=EnvType.CLIENT)
    public static Stream<SignType> stream() {
        return VALUES.stream();
    }

    @Environment(value=EnvType.CLIENT)
    public String getName() {
        return this.name;
    }
}

