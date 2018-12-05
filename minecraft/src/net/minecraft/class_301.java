package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.Pointer;

@Environment(EnvType.CLIENT)
public class class_301 {
	@Nullable
	private static final MethodHandle field_1643 = GLX.make(() -> {
		try {
			Lookup lookup = MethodHandles.lookup();
			Class<?> class_ = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
			Method method = class_.getDeclaredMethod("untrack", long.class);
			method.setAccessible(true);
			Field field = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
			field.setAccessible(true);
			Object object = field.get(null);
			return class_.isInstance(object) ? lookup.unreflect(method) : null;
		} catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException var5) {
			throw new RuntimeException(var5);
		}
	});

	public static void method_1407(long l) {
		if (field_1643 != null) {
			try {
				field_1643.invoke(l);
			} catch (Throwable var3) {
				throw new RuntimeException(var3);
			}
		}
	}

	public static void method_1406(Pointer pointer) {
		method_1407(pointer.address());
	}
}
