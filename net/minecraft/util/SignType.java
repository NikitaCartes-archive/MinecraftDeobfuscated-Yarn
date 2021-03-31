/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class SignType {
    private static final Set<SignType> VALUES = new ObjectArraySet<SignType>();
    public static final SignType OAK = SignType.register(new SignType("oak"));
    public static final SignType SPRUCE = SignType.register(new SignType("spruce"));
    public static final SignType BIRCH = SignType.register(new SignType("birch"));
    public static final SignType ACACIA = SignType.register(new SignType("acacia"));
    public static final SignType JUNGLE = SignType.register(new SignType("jungle"));
    public static final SignType DARK_OAK = SignType.register(new SignType("dark_oak"));
    public static final SignType CRIMSON = SignType.register(new SignType("crimson"));
    public static final SignType WARPED = SignType.register(new SignType("warped"));
    private final String name;

    protected SignType(String name) {
        this.name = name;
    }

    private static SignType register(SignType type) {
        VALUES.add(type);
        return type;
    }

    public static Stream<SignType> stream() {
        return VALUES.stream();
    }

    public String getName() {
        return this.name;
    }
}

