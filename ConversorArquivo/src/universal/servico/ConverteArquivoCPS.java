/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.servico;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import universal.entidade.Movimento;
import universal.entidade.Servidor;

/**
 *
 * @author caio.mota
 */
@Integracao(id = "CPS")
public class ConverteArquivoCPS extends Integrador {

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
        Servidor servidor = null;
        Movimento arquivoVerba = null;
        String linha;
        int i = 0;
        while (i < lines.length - 1) {
            linha = lines[i];
            if (linha.contains("Adm.") && linha.contains(" Dem.")) {
                servidor = new Servidor();
                while (!linha.contains("Ev.   Seq.     Descrição                                      Ref.")) {
                    if (linha.contains("Adm.") && linha.contains(" Dem.")) {
                        String[] nomeMatricula = linha.substring(6, 64).split("-");
                        servidor.setNome(nomeMatricula[1].trim());
                        String texto = nomeMatricula[0];
                        String matricula = texto.split("\\[")[0].trim();
                        servidor.setMatricula(matricula);
                        Pattern pattern = Pattern.compile("Adm. \\d{2}\\/\\d{2}\\/\\d{4}");
                        // String[] split = sCurrentLine.split("\\d{1,3} -
                        // \\d{1,3}-\\d{1,3}-\\d{1,3}");
                        Matcher matcher = pattern.matcher(linha);
                        if (matcher.find()) {
                            servidor.setDataAdmissao(matcher.group().replace("Adm.", "").trim());
                        }
                        pattern = Pattern.compile("Dem. \\d{2}\\/\\d{2}\\/\\d{4}");
                        // String[] split = sCurrentLine.split("\\d{1,3} -
                        // \\d{1,3}-\\d{1,3}-\\d{1,3}");
                        matcher = pattern.matcher(linha);
                        if (matcher.find()) {
                            servidor.setDataDemissao(matcher.group().replace("Dem.", "").trim());
                        }
                        linha = lines[++i];
                    }
                    if (linha.contains(" Local na Data:")) {
                        String[] setor = linha.substring(7, 64).split("-");
                        servidor.setLocal(setor[1].trim());
                        String[] situacao = linha.substring(70, 130).split("-");
                        servidor.setSituacaoMatricula(situacao[1].trim());
                        String[] cargo = linha.substring(132, linha.length() - 1).split("-");
                        servidor.setCargo(cargo[1].trim());
                    }
                    linha = lines[++i];
                }
                if (linha.contains("Ev.   Seq.     Descrição                                      Ref.")) {
                    linha = lines[++i];
                    while (!linha.contains("C/C:")) {
                        arquivoVerba = new Movimento();
                        // arquivoVerba.setCpo(servidor.getCpo());
                        arquivoVerba.setMatricula(servidor.getMatricula());
                        arquivoVerba.setCodigo(linha.substring(6, 12).trim());
                        arquivoVerba.setDescricao(linha.substring(25, 68).trim());
                        arquivoVerba.setTipo(linha.substring(22, 23).trim());
                        if (linha.length() >= 96) {
                            arquivoVerba.setValor(linha.substring(83, 96).trim());
                        } else {
                            arquivoVerba.setValor(linha.substring(83, 95).trim());
                        }
                        listaVerba.add(arquivoVerba);
                        if (linha.length() > 96) {
                            arquivoVerba = new Movimento();
                            arquivoVerba.setMatricula(servidor.getMatricula());
                            arquivoVerba.setCodigo(linha.substring(99, 106).trim());
                            arquivoVerba.setDescricao(linha.substring(118, 161).trim());
                            arquivoVerba.setTipo(linha.substring(115, 116).trim());
                            arquivoVerba.setValor(linha.substring(176, linha.length()).trim());
                            listaVerba.add(arquivoVerba);
                        }
                        linha = lines[++i];
                    }
                    servidores.add(servidor);
                    linha = lines[++i];
                }
            } else {
                i++;
            }
        }
    }

}
