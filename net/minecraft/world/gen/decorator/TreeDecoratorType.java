/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;

public class TreeDecoratorType<P extends TreeDecorator> {
    public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = TreeDecoratorType.register("trunk_vine", TrunkVineTreeDecorator::new);
    public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = TreeDecoratorType.register("leave_vine", LeaveVineTreeDecorator::new);
    public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = TreeDecoratorType.register("cocoa", CocoaBeansTreeDecorator::new);
    public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = TreeDecoratorType.register("beehive", BeehiveTreeDecorator::new);
    public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = TreeDecoratorType.register("alter_ground", AlterGroundTreeDecorator::new);
    private final Function<Dynamic<?>, P> field_21325;

    private static <P extends TreeDecorator> TreeDecoratorType<P> register(String string, Function<Dynamic<?>, P> function) {
        return Registry.register(Registry.TREE_DECORATOR_TYPE, string, new TreeDecoratorType<P>(function));
    }

    private TreeDecoratorType(Function<Dynamic<?>, P> function) {
        this.field_21325 = function;
    }

    public P method_23472(Dynamic<?> dynamic) {
        return (P)((TreeDecorator)this.field_21325.apply(dynamic));
    }
}

