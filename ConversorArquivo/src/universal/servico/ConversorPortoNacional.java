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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import universal.entidade.Movimento;
import universal.entidade.Servidor;
import universal.entidade.Total;

/**
 *
 * @author caio.mota
 */
@Integracao(id = "PN")
public class ConversorPortoNacional extends Integrador {

    private List<Total> listTotal;

    @Override
    public void converterArquivo(File file, String caminhoSaida) throws Exception {
        gerarRelatorio(file);

        String[][] linhasServidor = getLinhasServidor();
        String[][] linhasMovimento = getLinhas();
        gerarRelatorioExcel(linhasMovimento, caminhoSaida + "/" + "verba");
        gerarRelatorioExcel(linhasServidor, caminhoSaida + "/" + "servidor");
        gerarRelatorioExcel(getLinhasValidacao(), caminhoSaida + "/" + "total");
    }

    public void gerarRelatorio(File file) throws IOException, ParseException {

        listTotal = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String sCurrentLine = lerLinha(br);

        while (sCurrentLine != null) {
            if (sCurrentLine.contains("PIS/PASEP:")) {
//                sCurrentLine = lerLinha(br);
                //SERVIDOR
                Servidor servidor = new Servidor();

                //MATRÍCULA
                Pattern pattern = Pattern.compile("Matrícula:\\s+\\d{1,8}");
                Matcher matcher = pattern.matcher(sCurrentLine.trim());

                if (matcher.find()) {
                    String[] split = matcher.group().split("\\s+");
                    servidor.setMatricula(split[1]);
                }

                //PIS
                pattern = Pattern.compile("PIS/PASEP:\\s+\\d{1,15}");
                matcher = pattern.matcher(sCurrentLine.trim());

                if (matcher.find()) {
                    servidor.setPis(matcher.group().split("\\s+")[1]);
                }

                sCurrentLine = lerLinha(br);

                //SETOR
                String[] splitSetor = sCurrentLine.split("Gestão:\\s+\\d{1,15}\\s+\\-");
                servidor.setSetor(splitSetor[1].trim());

                //NOME
                sCurrentLine = lerLinha(br);
                String[] nomeCpf = sCurrentLine.split("\\:");

                //CPF
                servidor.setNome(nomeCpf[1].replace("CPF", "").trim());

                pattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}");
                matcher = pattern.matcher(nomeCpf[2].trim());

                if (matcher.find()) {
                    servidor.setCpf(matcher.group());
                }

                //DATA ADMISSÃO
                sCurrentLine = lerLinha(br);
                pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
                matcher = pattern.matcher(sCurrentLine.trim());

                if (matcher.find()) {
                    servidor.setDataAdmissao(matcher.group());
                }
                sCurrentLine = lerLinha(br);

                //SITUAÇÃO DE MATRÍCULA
                String[] splitSituacaoMatricula = sCurrentLine.split("Provimento:\\s+\\d{1,15}\\s+\\-");
                servidor.setSituacaoMatricula(splitSituacaoMatricula[1].trim());

                //CARGO
                sCurrentLine = lerLinha(br);
                String[] splitCargo = sCurrentLine.split("Função:");

                servidor.setCargo(splitCargo[0].split("\\d{1,8}\\s+\\-")[1].replace("-", "").trim());

                while (!sCurrentLine.contains("PZ INICIAL    PZ FINAL")) {
                    sCurrentLine = lerLinha(br);
                }
                sCurrentLine = lerLinha(br);

                while (!sCurrentLine.contains("Líquido por Funcionário:")) {
                    Movimento movimento = new Movimento();

                    movimento.setCodigo(Integer.parseInt(sCurrentLine.substring(1, 15).trim()) + "");

                    String valor = sCurrentLine.substring(109, 123).trim();
                    movimento.setDescricao(sCurrentLine.substring(15, 52).trim());
                    movimento.setMatricula(servidor.getMatricula());

                    if (!valor.equals("0,00")) {
                        movimento.setTipo("C");
                    } else if (valor.equals("0,00")) {
                        movimento.setTipo("D");
                        valor = sCurrentLine.substring(126, sCurrentLine.length()).trim();
                    }

                    if (!valor.equals("0,00")) {
                        movimento.setValor(valor);

                        listaVerba.add(movimento);
                    }
                    sCurrentLine = lerLinha(br);
                }

                String[] totais = sCurrentLine.split("R\\$");
                Total total = new Total(totais[1].trim(), totais[2].trim(), totais[3].trim());
                listTotal.add(total);

                servidores.add(servidor);
            }
            sCurrentLine = lerLinha(br);
        }
    }

    @Override
    public void gerarRelatorio(String[] lines) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String lerLinha(BufferedReader br) throws IOException {
        String linha = br.readLine();
        while (linha != null && linha.trim().equals("")) {
            linha = br.readLine();
        }
        return linha;
    }

    public String[][] getLinhasValidacao() {
        String[][] texto = new String[listTotal.size() + 1][3];

        texto[0][0] = "LIQUIDO";
        texto[0][1] = "CREDITO";
        texto[0][2] = "DEBITO";

        for (int i = 1; i <= servidores.size(); i++) {
            Total total = listTotal.get(i - 1);
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0:
                        texto[i][j] = total.getLiquido();
                        break;
                    case 1:
                        texto[i][j] = total.getCredito();
                        break;
                    case 2:
                        texto[i][j] = total.getDebito();
                        break;
                }
            }
        }
        return texto;
    }

}
