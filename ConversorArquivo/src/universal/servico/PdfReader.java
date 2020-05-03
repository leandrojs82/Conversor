package universal.servico;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Integracao(id = "IPREV")
public class PdfReader extends Integrador {

    private static Iterator<String> iterator;

    public static void main(String[] args) throws IOException {
        
        String caminhoSaida = null;
        String linhas[] = ConvertePdf.buscarLinhaPDF(caminhoSaida);

        iterator = Arrays.asList(linhas).iterator();

        String linha = buscarProximaLinha();

        while (linha != null) {
            Pattern pattern = Pattern.compile("\\d{1,10}-\\d{1,2}");
            Matcher matcher = pattern.matcher(linha);

            if (matcher.find()) {
                String matricula = matcher.group();
                String servidor = linha.substring(13, 60).trim();
                String dataAdmissao = "";
                String dataDemissao = "";
                pattern = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{4}");
                matcher = pattern.matcher(linha);
                if (matcher.find()) {
                    dataAdmissao = matcher.group();
                }
                if (matcher.find()) {
                    dataDemissao = matcher.group();
                }

                System.out.println(matricula + ";" + servidor + ";" + dataAdmissao + ";" + dataDemissao);

            }
            linha = buscarProximaLinha();
        }
    }

    public static String buscarProximaLinha() {
        String linha = null;
        if (iterator.hasNext()) {
            linha = iterator.next();
            if (linha.replace(" ", "").equals("")) {
                return buscarProximaLinha();
            }
        }
        return linha;
    }

    public static void gerarRelatorioTexto(String lines[]) {
        String caminhoSaida = null;
        File arquivoTxt = new File(caminhoSaida);
        try { // Cria o arquivo
            if (!arquivoTxt.exists()) {
                arquivoTxt.createNewFile();
            }
            FileWriter writer = new FileWriter(arquivoTxt, true);
            BufferedWriter bw = new BufferedWriter(writer);
            for (String line : lines) {
                Matcher matcher = Pattern.compile("\\d{16}").matcher(line);
                Matcher matcherValor = Pattern.compile("\\d{16}").matcher(line);
                if (matcher.find() && line.contains("R$")) {
                    bw.write(line);
                    bw.newLine();
                }
                // System.out.println(line);
            }
            bw.close();
            writer.close();
        } catch (IOException err) {
        }
    }

    @Override
    public void converterArquivo(File file, String caminhoSaida) throws Exception {
        String[] linhas = getLinhas(file);
        gerarRelatorio(linhas);

        String[][] linhasServidor = getLinhasServidor();
        String[][] linhasMovimento = getLinhas();
        gerarRelatorioExcel(linhasMovimento, caminhoSaida + "/" + "verba");
        gerarRelatorioExcel(linhasServidor, caminhoSaida + "/" + "servidor");
    }

    @Override
    public void gerarRelatorio(String[] lines) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
