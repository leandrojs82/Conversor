package universal.servico;


import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import universal.LayoutTextStripper;

public class ConvertePdf {

    public static String[] buscarLinhaPDF(String caminhoArquivo) throws IOException {

        String lines[] = null;

        try (PDDocument document = PDDocument.load(new File(caminhoArquivo))) {
            document.getClass();
            if (!document.isEncrypted()) {

                LayoutTextStripper stripper = new LayoutTextStripper();
                stripper.setSortByPosition(true);

                String text = stripper.getText(document);

                lines = text.split("\\r?\\n");
            }

        }
        return lines;

    }
}
