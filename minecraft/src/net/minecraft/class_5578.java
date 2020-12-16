package net.minecraft;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLike;
import net.minecraft.util.math.Box;

public class class_5578<T extends EntityLike> implements class_5577<T> {
	private final class_5570<T> field_27258;
	private final class_5573<T> field_27259;

	public class_5578(class_5570<T> arg, class_5573<T> arg2) {
		this.field_27258 = arg;
		this.field_27259 = arg2;
	}

	@Nullable
	@Override
	public T getById(int id) {
		return this.field_27258.getEntity(id);
	}

	@Nullable
	@Override
	public T getByUuid(UUID uuid) {
		return this.field_27258.getEntity(uuid);
	}

	@Override
	public Iterable<T> iterate() {
		return this.field_27258.method_31751();
	}

	@Override
	public <U extends T> void method_31806(class_5575<T, U> arg, Consumer<U> consumer) {
		this.field_27258.method_31754(arg, consumer);
	}

	@Override
	public void method_31807(Box box, Consumer<T> consumer) {
		this.field_27259.method_31783(box, consumer);
	}

	@Override
	public <U extends T> void method_31805(class_5575<T, U> arg, Box box, Consumer<U> consumer) {
		this.field_27259.method_31773(arg, box, consumer);
	}
}
