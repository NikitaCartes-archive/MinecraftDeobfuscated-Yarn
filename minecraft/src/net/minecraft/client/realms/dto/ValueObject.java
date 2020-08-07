package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ValueObject {
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");

		for (Field field : this.getClass().getFields()) {
			if (!isStatic(field)) {
				try {
					stringBuilder.append(getName(field)).append("=").append(field.get(this)).append(" ");
				} catch (IllegalAccessException var7) {
				}
			}
		}

		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append('}');
		return stringBuilder.toString();
	}

	private static String getName(Field f) {
		SerializedName serializedName = (SerializedName)f.getAnnotation(SerializedName.class);
		return serializedName != null ? serializedName.value() : f.getName();
	}

	private static boolean isStatic(Field f) {
		return Modifier.isStatic(f.getModifiers());
	}
}
