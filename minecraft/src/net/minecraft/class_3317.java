package net.minecraft;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class class_3317 extends class_3331<String, class_3320> {
	public class_3317(File file) {
		super(file);
	}

	@Override
	protected class_3330<String> method_14642(JsonObject jsonObject) {
		return new class_3320(jsonObject);
	}

	public boolean method_14527(SocketAddress socketAddress) {
		String string = this.method_14526(socketAddress);
		return this.method_14644(string);
	}

	public boolean method_14529(String string) {
		return this.method_14644(string);
	}

	public class_3320 method_14528(SocketAddress socketAddress) {
		String string = this.method_14526(socketAddress);
		return this.method_14640(string);
	}

	private String method_14526(SocketAddress socketAddress) {
		String string = socketAddress.toString();
		if (string.contains("/")) {
			string = string.substring(string.indexOf(47) + 1);
		}

		if (string.contains(":")) {
			string = string.substring(0, string.indexOf(58));
		}

		return string;
	}
}
