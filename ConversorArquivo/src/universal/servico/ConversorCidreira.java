/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.servico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import universal.entidade.Movimento;
import universal.entidade.Servidor;

/**
 *
 * @author caio.mota
 */
@Integracao(id = "CD")
public class ConversorCidreira extends Integrador {

    @Override
    public void converterArquivo(File file, String caminhoSaida) throws Exception {
        gerarRelatorio(file);

        String[][] linhasServidor = getLinhasServidor();
        String[][] linhasMovimento = getLinhas();
        gerarRelatorioExcel(linhasMovimento, caminhoSaida + "/" + "verba");
        gerarRelatorioExcel(linhasServidor, caminhoSaida + "/" + "servidor");
    }

    public void gerarRelatorio(File file) throws IOException, ParseException {
        int i = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String sCurrentLine = br.readLine();

        while (sCurrentLine != null) {
            if (sCurrentLine.substring(0, 2).equals("01")) {
                Servidor servidor = new Servidor();
                servidor.setMatricula(Integer.parseInt(sCurrentLine.substring(2, 11).trim()) + "");
                servidor.setNome(sCurrentLine.substring(31, 70).trim());

                Pattern pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{3})(\\d{2})");
                Matcher matcher = pattern.matcher(sCurrentLine.substring(70, 81).trim());

                if (matcher.find()) {
                    servidor.setCpf(matcher.replaceAll("$1.$2.$3-$4"));
                }

                pattern = Pattern.compile("(\\d{2})(\\d{2})(\\d{4})");
                matcher = pattern.matcher(sCurrentLine.substring(81, 89).trim());
                if (matcher.find()) {
                    servidor.setDataNascimento(matcher.replaceAll("$1/$2/$3"));
                }
                sCurrentLine = br.readLine();
                servidor.setSetor(sCurrentLine.substring(54, 57));
                servidor.setCargo(sCurrentLine.substring(21, 48).trim());

                pattern = Pattern.compile("(\\d{2})(\\d{2})(\\d{4})");
                matcher = pattern.matcher(sCurrentLine.substring(13, 21).trim());
                if (matcher.find()) {
                    servidor.setDataAdmissao(matcher.replaceAll("$1/$2/$3"));
                }
                sCurrentLine = br.readLine();
                while (sCurrentLine.substring(0, 2).equals("03")) {
                    Movimento movimento = new Movimento();
                    if (sCurrentLine.contains("+")) {
                        movimento.setTipo("C");
                    } else {
                        movimento.setTipo("D");
                    }

                    movimento.setCodigo(Integer.parseInt(sCurrentLine.substring(13, 16).trim()) + "");
                    movimento.setDescricao(sCurrentLine.substring(16, 40).trim());
                    movimento.setValor(sCurrentLine.substring(50, 60).trim());
                    movimento.setMatricula(servidor.getMatricula());
                    listaVerba.add(movimento);
                    sCurrentLine = br.readLine();

                }
                servidores.add(servidor);
            }
            sCurrentLine = br.readLine();
        }
    }

    @Override
    public void gerarRelatorio(String[] lines) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
