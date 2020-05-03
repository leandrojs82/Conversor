package universal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
public class PDFParserTextStripper extends PDFTextStripper {
    private PDDocument document;
    public PDFParserTextStripper(PDDocument document) throws IOException {
        this.document = document;
//      super();
    }
    public void stripPage(int pageNr) throws IOException {
        this.setStartPage(pageNr + 1);
        this.setEndPage(pageNr + 1);
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        writeText(document, dummy); // This call starts the parsing process and
                                    // calls writeString repeatedly.
    }
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
            System.out.println("String[" + text.getXDirAdj() + "," + text.getYDirAdj() + " fs=" + text.getFontSizeInPt()
                    + " xscale=" + text.getXScale() + " height=" + text.getHeightDir() + " space="
                    + text.getWidthOfSpace() + " width=" + text.getWidthDirAdj() + " ] " + text.getUnicode());
        }
    }
}