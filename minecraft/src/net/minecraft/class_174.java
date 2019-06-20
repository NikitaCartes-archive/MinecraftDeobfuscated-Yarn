package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

public class class_174 {
	private static final Map<class_2960, class_179<?>> field_1205 = Maps.<class_2960, class_179<?>>newHashMap();
	public static final class_2062 field_1184 = method_767(new class_2062());
	public static final class_2080 field_1192 = method_767(new class_2080(new class_2960("player_killed_entity")));
	public static final class_2080 field_1188 = method_767(new class_2080(new class_2960("entity_killed_player")));
	public static final class_2037 field_1180 = method_767(new class_2037());
	public static final class_2066 field_1195 = method_767(new class_2066());
	public static final class_2119 field_1207 = method_767(new class_2119());
	public static final class_2115 field_1199 = method_767(new class_2115());
	public static final class_2044 field_1209 = method_767(new class_2044());
	public static final class_2030 field_1181 = method_767(new class_2030());
	public static final class_2054 field_1208 = method_767(new class_2054());
	public static final class_1996 field_1213 = method_767(new class_1996());
	public static final class_2006 field_1189 = method_767(new class_2006());
	public static final class_2143 field_1186 = method_767(new class_2143());
	public static final class_2128 field_1182 = method_767(new class_2128());
	public static final class_196 field_1190 = method_767(new class_196());
	public static final class_2092 field_1194 = method_767(new class_2092(new class_2960("location")));
	public static final class_2092 field_1212 = method_767(new class_2092(new class_2960("slept_in_bed")));
	public static final class_2014 field_1210 = method_767(new class_2014());
	public static final class_2140 field_1206 = method_767(new class_2140());
	public static final class_2069 field_1185 = method_767(new class_2069());
	public static final class_2085 field_1200 = method_767(new class_2085());
	public static final class_1999 field_1183 = method_767(new class_1999());
	public static final class_2135 field_1187 = method_767(new class_2135());
	public static final class_2131 field_1201 = method_767(new class_2131());
	public static final class_2111 field_1191 = method_767(new class_2111());
	public static final class_2010 field_1198 = method_767(new class_2010());
	public static final class_2027 field_1193 = method_767(new class_2027());
	public static final class_2148 field_1204 = method_767(new class_2148());
	public static final class_2108 field_1211 = method_767(new class_2108());
	public static final class_2058 field_1203 = method_767(new class_2058());
	public static final class_2002 field_1202 = method_767(new class_2002());
	public static final class_2123 field_1196 = method_767(new class_2123());
	public static final class_2076 field_1197 = method_767(new class_2076());
	public static final class_2092 field_19250 = method_767(new class_2092(new class_2960("hero_of_the_village")));
	public static final class_2092 field_19251 = method_767(new class_2092(new class_2960("voluntary_exile")));

	private static <T extends class_179<?>> T method_767(T arg) {
		if (field_1205.containsKey(arg.method_794())) {
			throw new IllegalArgumentException("Duplicate criterion id " + arg.method_794());
		} else {
			field_1205.put(arg.method_794(), arg);
			return arg;
		}
	}

	@Nullable
	public static <T extends class_184> class_179<T> method_765(class_2960 arg) {
		return (class_179<T>)field_1205.get(arg);
	}

	public static Iterable<? extends class_179<?>> method_766() {
		return field_1205.values();
	}
}
