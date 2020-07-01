/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;

public class TreeDecoratorType<P extends TreeDecorator> {
    public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = TreeDecoratorType.register("trunk_vine", TrunkVineTreeDecorator.CODEC);
    public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = TreeDecoratorType.register("leave_vine", LeaveVineTreeDecorator.CODEC);
    public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = TreeDecoratorType.register("cocoa", CocoaBeansTreeDecorator.CODEC);
    public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = TreeDecoratorType.register("beehive", BeehiveTreeDecorator.CODEC);
    public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = TreeDecoratorType.register("alter_ground", AlterGroundTreeDecorator.CODEC);
    private final Codec<P> codec;

    private static <P extends TreeDecorator> TreeDecoratorType<P> register(String type, Codec<P> codec) {
        return Registry.register(Registry.TREE_DECORATOR_TYPE, type, new TreeDecoratorType<P>(codec));
    }

    private TreeDecoratorType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}

