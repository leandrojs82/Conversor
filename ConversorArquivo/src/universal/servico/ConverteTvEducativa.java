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
@Integracao(id = "TV")
public class ConverteTvEducativa extends Integrador {

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
        Movimento movimento = null;
        String linha;
        int i = 0;
        while (i < lines.length - 1) {
            linha = lines[i];
            if (linha.contains("CÓD.      DESCRIÇÃO")) {
                while (!linha.contains(
                        "PROVENTOS                                                                                DESCONTOS")) {
                    linha = lines[i];
                    servidor = new Servidor();
                    if (linha.contains("Admitido em")) {
                        servidor.setMatricula(linha.substring(0, 16).trim());
                        System.out.println("SERVIDOR: " + (linha.substring(0, 16)));
                        servidor.setNome(linha.substring(16, 80).trim());
                        Pattern pattern = Pattern.compile("Adm. \\d{2}\\/\\d{2}\\/\\d{4}");
                        Matcher matcher = pattern.matcher(linha);
                        if (matcher.find()) {
                            servidor.setDataAdmissao((matcher.group()));
                        }
                        linha = lines[++i];
                        if (linha.contains("Demitido em")) {
                            pattern = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{4}");
                            matcher = pattern.matcher(linha);
                            if (matcher.find()) {
                                servidor.setDataDemissao(matcher.group());
                            }
                            linha = lines[++i];
                        }
                        if (linha.contains("***")) {
                            linha = lines[++i];
                        }
                        while (!linha.contains("Total de proventos ->")) {
                            // arquivoVerba.setCpo(servidor.getCpo());
                            if (!linha.substring(0, 37).trim().equals("")) {
                                movimento = new Movimento();
                                movimento.setMatricula(servidor.getMatricula());
                                movimento.setCodigo(linha.substring(0, 16).replace(".", "").trim());
                                movimento.setDescricao(linha.substring(16, 56).trim());
                                movimento.setTipo("C");
                                int tamanhoMax = linha.length() > 84 ? linha.length() >= 86 ? 86 : 85 : 84;
                                movimento.setValor(linha.substring(75, tamanhoMax).replace(".", "").trim());
                                listaVerba.add(movimento);
                            }
                            if (linha.length() > 87) {
                                movimento = new Movimento();
                                movimento.setMatricula(servidor.getMatricula());
                                movimento.setCodigo(linha.substring(90, 99).replace(".", "").trim());
                                movimento.setDescricao(linha.substring(99, 140).trim());
                                movimento.setTipo("D");
                                movimento.setValor(linha.substring(153, linha.length() > 164 ? linha.length() : 164)
                                        .replace(".", "").trim());
                                listaVerba.add(movimento);
                            }

                            linha = lines[++i];
                        }
                        servidores.add(servidor);
                        i++;
                    }
                    i++;
                }
            } else {
                i++;
            }
        }
    }

}
