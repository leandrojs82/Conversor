/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.servico;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import universal.entidade.Movimento;
import universal.entidade.Servidor;

/**
 *
 * @author caio.mota
 */
@Integracao(id = "CASM")
public class ConversorConceicaoAraguaiaServidor extends Integrador {

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
    public void gerarRelatorio(String[] linhas) {
        int i = 0;
        String sCurrentLine = linhas[i];

        while (i < linhas.length - 1) {
            Servidor servidor = new Servidor();
            if (sCurrentLine.contains("Matrícula:")) {
                String[] split = sCurrentLine.split("\\s+");
                servidor.setMatricula(split[1]);
                if (split.length > 5) {
                    servidor.setMatriculaOrigem(split[3]);
                    servidor.setPis(split[5]);
                } else {
                    servidor.setPis(split[4]);
                }

                sCurrentLine = linhas[++i].trim();

                split = sCurrentLine.split("\\s{3,}");

                servidor.setSetor(split[1].split(" - ")[1].trim());

                sCurrentLine = linhas[++i].trim();
                split = sCurrentLine.split(":");

                servidor.setNome(split[1].trim().split("\\s{2,}")[0].trim());
                if (split.length > 2) {
                    servidor.setCpf(split[2].trim().split("\\s{2,}")[0].trim());
                } else {
                    servidor.setCpf(split[2].trim());
                }

                sCurrentLine = linhas[++i].trim();
                split = sCurrentLine.split("\\s{2,}");

                servidor.setDataAdmissao(split[3].trim());

                sCurrentLine = linhas[++i].trim();
                split = sCurrentLine.split("\\s{3,}");

                servidor.setSituacaoMatricula(split[1].split(" - ")[1].trim());

                sCurrentLine = linhas[++i].trim();
                split = sCurrentLine.split("\\s{2,}");

                if (split.length == 4) {
                    servidor.setCargo(split[1].split(" - ")[1].trim());
                } else if (split.length == 3) {
                    Pattern pattern = Pattern.compile("\\d{1,} -");
                    Matcher matcher = pattern.matcher(split[1]);

                    if (matcher.find()) {
                        servidor.setCargo(split[1].split(" - ")[1].trim());
                    }
                }

                sCurrentLine = linhas[++i].trim();
                split = sCurrentLine.split("\\s{2,}");

                servidor.setAgencia(split[1]);
                servidor.setConta(split[3]);

                while (!sCurrentLine.contains("CÓD.        DESCRIÇÃO DO EVENTO")) {
                    sCurrentLine = linhas[++i].trim();
                }

                sCurrentLine = linhas[++i].trim();

                while (!sCurrentLine.contains("Líquido por Funcionário:")) {
                    if (sCurrentLine != null) {
                        split = sCurrentLine.split("\\s{2,}");
                        int tamanho = split.length;
                        String provento = split[tamanho - 2];
                        String desconto = split[tamanho - 1];

                        Movimento arquivoMovimento = new Movimento();
                        arquivoMovimento.setCodigo(split[0].trim());
                        arquivoMovimento.setDescricao(split[1]);
                        arquivoMovimento.setTipo(desconto.equals("0,00") ? "C" : "D");
                        arquivoMovimento.setValor(desconto.equals("0,00") ? provento : desconto);
                        arquivoMovimento.setMatricula(servidor.getMatricula());
                        listaVerba.add(arquivoMovimento);

                        sCurrentLine = linhas[++i].trim();
                    }
                }

                servidores.add(servidor);
            } else {
                sCurrentLine = linhas[++i].trim();
            }
        }
    }

}
