package com.mojang.realmsclient.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextRenderingUtils {
	static List<String> lineBreak(String text) {
		return Arrays.asList(text.split("\\n"));
	}

	public static List<TextRenderingUtils.Line> decompose(String text, TextRenderingUtils.LineSegment... links) {
		return decompose(text, Arrays.asList(links));
	}

	private static List<TextRenderingUtils.Line> decompose(String text, List<TextRenderingUtils.LineSegment> links) {
		List<String> list = lineBreak(text);
		return insertLinks(list, links);
	}

	private static List<TextRenderingUtils.Line> insertLinks(List<String> lines, List<TextRenderingUtils.LineSegment> links) {
		int i = 0;
		ArrayList<TextRenderingUtils.Line> arrayList = new ArrayList();

		for (String string : lines) {
			List<TextRenderingUtils.LineSegment> list = new ArrayList();

			for (String string2 : split(string, "%link")) {
				if (string2.equals("%link")) {
					list.add(links.get(i++));
				} else {
					list.add(TextRenderingUtils.LineSegment.text(string2));
				}
			}

			arrayList.add(new TextRenderingUtils.Line(list));
		}

		return arrayList;
	}

	public static List<String> split(String line, String delimiter) {
		if (delimiter.isEmpty()) {
			throw new IllegalArgumentException("Delimiter cannot be the empty string");
		} else {
			List<String> list = new ArrayList();
			int i = 0;

			int j;
			while ((j = line.indexOf(delimiter, i)) != -1) {
				if (j > i) {
					list.add(line.substring(i, j));
				}

				list.add(delimiter);
				i = j + delimiter.length();
			}

			if (i < line.length()) {
				list.add(line.substring(i));
			}

			return list;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Line {
		public final List<TextRenderingUtils.LineSegment> segments;

		Line(List<TextRenderingUtils.LineSegment> segments) {
			this.segments = segments;
		}

		public String toString() {
			return "Line{segments=" + this.segments + '}';
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				TextRenderingUtils.Line line = (TextRenderingUtils.Line)o;
				return Objects.equals(this.segments, line.segments);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.segments});
		}
	}

	@Environment(EnvType.CLIENT)
	public static class LineSegment {
		final String fullText;
		final String linkTitle;
		final String linkUrl;

		private LineSegment(String fullText) {
			this.fullText = fullText;
			this.linkTitle = null;
			this.linkUrl = null;
		}

		private LineSegment(String fullText, String linkTitle, String linkUrl) {
			this.fullText = fullText;
			this.linkTitle = linkTitle;
			this.linkUrl = linkUrl;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				TextRenderingUtils.LineSegment lineSegment = (TextRenderingUtils.LineSegment)o;
				return Objects.equals(this.fullText, lineSegment.fullText)
					&& Objects.equals(this.linkTitle, lineSegment.linkTitle)
					&& Objects.equals(this.linkUrl, lineSegment.linkUrl);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.fullText, this.linkTitle, this.linkUrl});
		}

		public String toString() {
			return "Segment{fullText='" + this.fullText + '\'' + ", linkTitle='" + this.linkTitle + '\'' + ", linkUrl='" + this.linkUrl + '\'' + '}';
		}

		public String renderedText() {
			return this.isLink() ? this.linkTitle : this.fullText;
		}

		public boolean isLink() {
			return this.linkTitle != null;
		}

		public String getLinkUrl() {
			if (!this.isLink()) {
				throw new IllegalStateException("Not a link: " + this);
			} else {
				return this.linkUrl;
			}
		}

		public static TextRenderingUtils.LineSegment link(String linkTitle, String linkUrl) {
			return new TextRenderingUtils.LineSegment(null, linkTitle, linkUrl);
		}

		static TextRenderingUtils.LineSegment text(String fullText) {
			return new TextRenderingUtils.LineSegment(fullText);
		}
	}
}
