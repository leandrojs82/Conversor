/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.servico;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import universal.LayoutTextStripper;
import universal.entidade.Movimento;
import universal.entidade.Servidor;

/**
 *
 * @author caio.mota
 */
public abstract class Integrador {

    protected List<Servidor> servidores = new ArrayList<>();
    protected List<Movimento> listaVerba = new ArrayList<>();

    abstract public void converterArquivo(File file, String caminhoSaida) throws Exception;

    abstract public void gerarRelatorio(String lines[]);

    public String[] getLinhas(File file) {
        String lines[] = null;

        String text = getTxt(file);
        lines = text.split("\\r?\\n");

        return lines;
    }

    public String getTxt(File file) {
        String text = null;
        try (PDDocument document = PDDocument.load(file)) {
            document.getClass();
            if (!document.isEncrypted()) {

                LayoutTextStripper stripper = new LayoutTextStripper();
                stripper.setSortByPosition(true);

                text = stripper.getText(document);
            }
        } catch (IOException ex) {
            Logger.getLogger(ConverteArquivoCPS.class.getName()).log(Level.SEVERE, null, ex);

        }

        return text;
    }

    public String getTxtSemEspaco(File input) {
        String txt = "";
        try {
            PDDocument pd;
            BufferedWriter wr;
            // The text file where you are going to store the extracted data
            pd = PDDocument.load(input);
            pd.save("CopyOfInvoice.pdf"); // Creates a copy called

            PDFTextStripper stripper = new PDFTextStripper();
            txt = stripper.getText(pd);

            pd.close();

        } catch (IOException ex) {
            Logger.getLogger(Integrador.class.getName()).log(Level.SEVERE, null, ex);

        }
        return txt;
    }

    public void gerarRelatorioExcel(String[][] texto, String nomeArquivo) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet("tab1");
        for (int i = 0; i < texto.length; i++) {
            HSSFRow row = firstSheet.createRow(i);
            int j = 0;
            for (String campo : texto[i]) {
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(campo);
                j++;
            }
        }
        // FileOutputStream out = new FileOutputStream(new File(
        // "C:\\Users\\Desenvolvimento
        // 2\\Documents\\FECHAMENTO\\2017-01\\Fazenda\\Asmuf\\verba 652.xls"));
        FileOutputStream out = new FileOutputStream(new File(nomeArquivo + ".xls"));
        workbook.write(out);
        // "C:\\Users\\Desenvolvimento 2\\Desktop\\fechamento
        // fazenda\\163071.xls"));
        // workbook.write(out);
        out.close();
    }

    public String[][] getLinhas() {
        String[][] texto = new String[listaVerba.size() + 1][6];
        texto[0][0] = "Matrícula";
        texto[0][1] = "Código";
        texto[0][2] = "Verba";
        texto[0][3] = "Tipo";
        texto[0][4] = "Valor";
        texto[0][5] = "Referencia";
        for (int i = 1; i <= listaVerba.size(); i++) {
            Movimento movimento = listaVerba.get(i - 1);
            for (int j = 0; j < 6; j++) {
                switch (j) {
                    case 0:
                        texto[i][j] = movimento.getMatricula();
                        break;
                    case 1:
                        texto[i][j] = movimento.getCodigo();
                        break;
                    case 2:
                        texto[i][j] = movimento.getDescricao();
                        break;
                    case 3:
                        texto[i][j] = movimento.getTipo();
                        break;
                    case 4:
                        texto[i][j] = movimento.getValor();
                        break;
                    case 5:
                        texto[i][j] = movimento.getReferencia();
                        break;
                }
            }
        }
        return texto;
    }

    public String[][] getLinhasServidor() {
        String[][] texto = new String[servidores.size() + 1][16];

        texto[0][0] = "NOME";
        texto[0][1] = "CPF";
        texto[0][2] = "MATRÍCULA";
        texto[0][3] = "MATRÍCULA ORIGEM";
        texto[0][4] = "CARGO";
        texto[0][5] = "SETOR";
        texto[0][6] = "LOCAL";
        texto[0][7] = "ADMISSÃO";
        texto[0][8] = "DEMISSÃO";
        texto[0][9] = "DATA APOSENTADORIA";
        texto[0][10] = "NASCIMENTO";
        texto[0][11] = "SITUACAO MATRÍCULA";
        texto[0][12] = "PIS";
        texto[0][13] = "BANCO";
        texto[0][14] = "CONTA";
        texto[0][15] = "AGÊNCIA";

        for (int i = 1; i <= servidores.size(); i++) {
            Servidor servidor = servidores.get(i - 1);
            for (int j = 0; j < 16; j++) {
                switch (j) {
                    case 0:
                        texto[i][j] = servidor.getNome();
                        break;
                    case 1:
                        texto[i][j] = servidor.getCpf();
                        break;
                    case 2:
                        texto[i][j] = servidor.getMatricula();
                        break;
                    case 3:
                        texto[i][j] = servidor.getMatriculaOrigem();
                        break;
                    case 4:
                        texto[i][j] = servidor.getCargo();
                        break;
                    case 5:
                        texto[i][j] = servidor.getSetor();
                        break;
                    case 6:
                        texto[i][j] = servidor.getLocal();
                        break;
                    case 7:
                        texto[i][j] = servidor.getDataAdmissao();
                        break;
                    case 8:
                        texto[i][j] = servidor.getDataDemissao();
                        break;
                    case 9:
                        texto[i][j] = servidor.getDataAposentadoria();
                        break;
                    case 10:
                        texto[i][j] = servidor.getDataNascimento();
                        break;
                    case 11:
                        texto[i][j] = servidor.getSituacaoMatricula();
                        break;
                    case 12:
                        texto[i][j] = servidor.getPis();
                        break;
                    case 13:
                        texto[i][j] = servidor.getBanco() != null ? servidor.getBanco().toString() : "";
                        break;
                    case 14:
                        texto[i][j] = servidor.getConta();
                        break;
                    case 15:
                        texto[i][j] = servidor.getAgencia();
                        break;
                }
            }
        }
        return texto;
    }

    public void gerarRelatorioTexto(File fileOrigem, String caminho) {
        File fileDestino = new File(caminho + "//arquivo.txt");
        String texto = getTxt(fileOrigem);
        try { // Cria o arquivo
            if (!fileDestino.exists()) {
                fileDestino.createNewFile();
            } else {
                fileDestino.delete();
            }
            FileWriter writer = new FileWriter(fileDestino, true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(texto);
            bw.newLine();
            bw.close();
            writer.close();
        } catch (IOException err) {
        }
    }

    public void gerarRelatorioTextoSemEspaco(File fileOrigem, String caminho) {
        File fileDestino = new File(caminho + "//texto.txt");
        String texto = getTxtSemEspaco(fileOrigem);
        try { // Cria o arquivo
            if (!fileDestino.exists()) {
                fileDestino.createNewFile();
            } else {
                fileDestino.delete();
            }
            FileWriter writer = new FileWriter(fileDestino, true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(texto);
            bw.newLine();
            bw.close();
            writer.close();
        } catch (IOException err) {
        }
    }

}
