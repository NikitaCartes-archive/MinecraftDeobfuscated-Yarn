package net.minecraft.stat;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stat<T> extends ScoreboardCriterion {
	private final StatFormatter field_15319;
	private final T value;
	private final StatType<T> field_15321;

	protected Stat(StatType<T> statType, T object, StatFormatter statFormatter) {
		super(method_14950(statType, object));
		this.field_15321 = statType;
		this.field_15319 = statFormatter;
		this.value = object;
	}

	public static <T> String method_14950(StatType<T> statType, T object) {
		return method_14952(Registry.STAT_TYPE.method_10221(statType)) + ":" + method_14952(statType.getRegistry().method_10221(object));
	}

	private static <T> String method_14952(@Nullable Identifier identifier) {
		return identifier.toString().replace(':', '.');
	}

	public StatType<T> method_14949() {
		return this.field_15321;
	}

	public T getValue() {
		return this.value;
	}

	@Environment(EnvType.CLIENT)
	public String format(int i) {
		return this.field_15319.format(i);
	}

	public boolean equals(Object object) {
		return this == object || object instanceof Stat && Objects.equals(this.getName(), ((Stat)object).getName());
	}

	public int hashCode() {
		return this.getName().hashCode();
	}

	public String toString() {
		return "Stat{name=" + this.getName() + ", formatter=" + this.field_15319 + '}';
	}
}
