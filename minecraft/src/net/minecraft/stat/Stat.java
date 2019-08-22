package net.minecraft.stat;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stat<T> extends ScoreboardCriterion {
	private final StatFormatter formatter;
	private final T value;
	private final StatType<T> type;

	protected Stat(StatType<T> statType, T object, StatFormatter statFormatter) {
		super(getName(statType, object));
		this.type = statType;
		this.formatter = statFormatter;
		this.value = object;
	}

	public static <T> String getName(StatType<T> statType, T object) {
		return getName(Registry.STAT_TYPE.getId(statType)) + ":" + getName(statType.getRegistry().getId(object));
	}

	private static <T> String getName(@Nullable Identifier identifier) {
		return identifier.toString().replace(':', '.');
	}

	public StatType<T> getType() {
		return this.type;
	}

	public T getValue() {
		return this.value;
	}

	@Environment(EnvType.CLIENT)
	public String format(int i) {
		return this.formatter.format(i);
	}

	public boolean equals(Object object) {
		return this == object || object instanceof Stat && Objects.equals(this.getName(), ((Stat)object).getName());
	}

	public int hashCode() {
		return this.getName().hashCode();
	}

	public String toString() {
		return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + '}';
	}
}
