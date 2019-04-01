package net.minecraft;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface class_2520 {
	String[] field_11592 = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]"};
	class_124 field_11591 = class_124.field_1075;
	class_124 field_11594 = class_124.field_1060;
	class_124 field_11593 = class_124.field_1065;
	class_124 field_11595 = class_124.field_1061;

	void method_10713(DataOutput dataOutput) throws IOException;

	void method_10709(DataInput dataInput, int i, class_2505 arg) throws IOException;

	String toString();

	byte method_10711();

	static class_2520 method_10708(byte b) {
		switch (b) {
			case 0:
				return new class_2491();
			case 1:
				return new class_2481();
			case 2:
				return new class_2516();
			case 3:
				return new class_2497();
			case 4:
				return new class_2503();
			case 5:
				return new class_2494();
			case 6:
				return new class_2489();
			case 7:
				return new class_2479();
			case 8:
				return new class_2519();
			case 9:
				return new class_2499();
			case 10:
				return new class_2487();
			case 11:
				return new class_2495();
			case 12:
				return new class_2501();
			default:
				return null;
		}
	}

	static String method_10712(int i) {
		switch (i) {
			case 0:
				return "TAG_End";
			case 1:
				return "TAG_Byte";
			case 2:
				return "TAG_Short";
			case 3:
				return "TAG_Int";
			case 4:
				return "TAG_Long";
			case 5:
				return "TAG_Float";
			case 6:
				return "TAG_Double";
			case 7:
				return "TAG_Byte_Array";
			case 8:
				return "TAG_String";
			case 9:
				return "TAG_List";
			case 10:
				return "TAG_Compound";
			case 11:
				return "TAG_Int_Array";
			case 12:
				return "TAG_Long_Array";
			case 99:
				return "Any Numeric Tag";
			default:
				return "UNKNOWN";
		}
	}

	class_2520 method_10707();

	default String method_10714() {
		return this.toString();
	}

	default class_2561 method_10715() {
		return this.method_10710("", 0);
	}

	class_2561 method_10710(String string, int i);
}
