/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.Nullable;

/**
 * A functional interface to create a new screen handler (menu) on the server.
 * 
 * <p>This interface itself is not used directly. Instead, the subinterface
 * {@link NamedScreenHandlerFactory} is passed to {@link
 * net.minecraft.entity.player.PlayerEntity#openHandledScreen}. In vanilla,
 * block entity instances implement that interface, allowing them to be passed.
 * {@link SimpleNamedScreenHandlerFactory} is a screen handler factory implementation
 * for use cases that do not involve a block entity.
 * 
 * <p>The factory should create a new instance of a screen handler with the server-side
 * constructor (one that takes inventories, etc). If the screen handler requires
 * a property delegate or a context, create an instance and pass it here.
 * 
 * @see ScreenHandler
 * @see NamedScreenHandlerFactory
 */
@FunctionalInterface
public interface ScreenHandlerFactory {
    @Nullable
    public ScreenHandler createMenu(int var1, PlayerInventory var2, PlayerEntity var3);
}

