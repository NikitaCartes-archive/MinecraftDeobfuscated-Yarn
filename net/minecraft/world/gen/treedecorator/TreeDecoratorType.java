/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.AttachedToLeavesTreeDecorator;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;

public class TreeDecoratorType<P extends TreeDecorator> {
    public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = TreeDecoratorType.register("trunk_vine", TrunkVineTreeDecorator.CODEC);
    public static final TreeDecoratorType<LeavesVineTreeDecorator> LEAVE_VINE = TreeDecoratorType.register("leave_vine", LeavesVineTreeDecorator.CODEC);
    public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = TreeDecoratorType.register("cocoa", CocoaBeansTreeDecorator.CODEC);
    public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = TreeDecoratorType.register("beehive", BeehiveTreeDecorator.CODEC);
    public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = TreeDecoratorType.register("alter_ground", AlterGroundTreeDecorator.CODEC);
    public static final TreeDecoratorType<AttachedToLeavesTreeDecorator> ATTACHED_TO_LEAVES = TreeDecoratorType.register("attached_to_leaves", AttachedToLeavesTreeDecorator.CODEC);
    private final Codec<P> codec;

    private static <P extends TreeDecorator> TreeDecoratorType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.TREE_DECORATOR_TYPE, id, new TreeDecoratorType<P>(codec));
    }

    private TreeDecoratorType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}

