package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public abstract class class_8294<T> implements class_8289 {
	private final Set<T> field_43701 = new HashSet();

	protected abstract Codec<T> method_50185();

	protected abstract Text method_50187(T object);

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43701.stream().map(object -> new class_8294.class_8295(object));
	}

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.method_50185().xmap(object -> new class_8294.class_8295(object), arg -> arg.field_43703));
	}

	public boolean method_50256(T object) {
		return this.field_43701.contains(object);
	}

	protected boolean method_50258(T object) {
		return this.field_43701.remove(object);
	}

	protected boolean method_50231(T object) {
		return this.field_43701.add(object);
	}

	public Collection<T> method_50257() {
		return Collections.unmodifiableCollection(this.field_43701);
	}

	protected void method_50254(class_8290 arg, MinecraftServer minecraftServer) {
	}

	protected class class_8295 implements class_8291.class_8292 {
		final T field_43703;

		public class_8295(T object) {
			this.field_43703 = object;
		}

		@Override
		public class_8289 method_50121() {
			return class_8294.this;
		}

		@Override
		public Text method_50123() {
			return class_8294.this.method_50187(this.field_43703);
		}

		@Override
		public void method_50122(class_8290 arg) {
			T object = this.field_43703;
			if (arg == class_8290.APPROVE) {
				class_8294.this.method_50231(object);
			} else {
				class_8294.this.method_50258(object);
			}
		}

		@Override
		public void method_50164(class_8290 arg, MinecraftServer minecraftServer) {
			class_8291.class_8292.super.method_50164(arg, minecraftServer);
			class_8294.this.method_50254(arg, minecraftServer);
		}
	}
}
