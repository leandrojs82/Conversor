package universal;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class LayoutTextStripper extends PDFTextStripper {

	public float fixedCharWidth = 3;

	boolean endsWithWS = true;
	boolean needsWS = false;
	int chars = 0;
	float recentEnd = 0;

	PDRectangle cropBox = null;
	float pageLeft = 0;

	public LayoutTextStripper() throws IOException {
		super();
	}

	@Override
	protected void startPage(PDPage page) throws IOException {
		super.startPage(page);
		cropBox = page.getArtBox();
		pageLeft = cropBox.getLowerLeftX();
		beginLine();
	}

	@Override
	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

		// for (TextPosition textPosition : textPositions) {
		TextPosition textPosition = textPositions.get(0);
		String textHere = textPosition.getUnicode();
		if (text.trim().length() == 0)
			return;

		float start = textPosition.getXDirAdj();
		boolean spacePresent = endsWithWS | textHere.startsWith(" ");

		if (needsWS | spacePresent | Math.abs(start - recentEnd) > 1) {
			int spacesToInsert = insertSpaces(chars, start, needsWS & !spacePresent);

			for (; spacesToInsert > 0; spacesToInsert--) {
				writeString(" ");
				chars++;
			}
		}

		writeString(text);
		chars += text.length();

		needsWS = false;
		endsWithWS = textHere.endsWith(" ");
		try {
			recentEnd = getEndX(textPosition);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new IOException("Failure retrieving endX of TextPosition", e);
		}
	}
	// }

	@Override
	protected void writeLineSeparator() throws IOException {
		super.writeLineSeparator();
		beginLine();
	}

	@Override
	protected void writeWordSeparator() throws IOException {
		needsWS = true;
	}

	void beginLine() {
		endsWithWS = true;
		needsWS = false;
		chars = 0;
	}

	int insertSpaces(int charsInLineAlready, float chunkStart, boolean spaceRequired) {
		int indexNow = charsInLineAlready;
		int indexToBe = (int) ((chunkStart - pageLeft) / fixedCharWidth);
		int spacesToInsert = indexToBe - indexNow;
		if (spacesToInsert < 1 && spaceRequired)
			spacesToInsert = 1;

		return spacesToInsert;
	}

	float getEndX(TextPosition textPosition)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		java.lang.reflect.Field field = textPosition.getClass().getDeclaredField("endX");
		field.setAccessible(true);
		return field.getFloat(textPosition);
	}

}