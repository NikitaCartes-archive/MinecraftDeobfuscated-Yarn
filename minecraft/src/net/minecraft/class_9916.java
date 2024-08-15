package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.Handle;

@Environment(EnvType.CLIENT)
public interface class_9916 {
	<T> Handle<T> addResource(String name, ClosableFactory<T> factory);

	<T> void method_61928(Handle<T> handle);

	<T> Handle<T> method_61933(Handle<T> handle);

	void addChild(class_9916 child);

	void method_61924();

	void method_61929(Runnable runnable);
}
