/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.servico;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import universal.entidade.Movimento;
import universal.entidade.Servidor;

/**
 *
 * @author caio.mota
 */
@Integracao(id = "IN")
public class ConverteIndiaroba extends Integrador {

    @Override
    public void converterArquivo(File file, String caminhoSaida) throws Exception {
        converterArquivo(file);

        String[][] linhasServidor = getLinhasServidor();
        String[][] linhasMovimento = getLinhas();
        gerarRelatorioExcel(linhasMovimento, caminhoSaida + "/" + "verba");
        gerarRelatorioExcel(linhasServidor, caminhoSaida + "/" + "servidor");
    }

    private static final String LINHA_SERVIDOR = "CPF;RG;PIS/PASEP";

    private static final String LINHA_MOVIMENTO = "Total Vencimentos;Total Descontos";

    private void converterArquivo(File file) {

        try {
            Excel excel = new Excel(file);
            String linha = excel.buscarProximaLinha();
            while (linha != null) {
                if (linha.contains(LINHA_SERVIDOR)) {
                    Servidor servidor = new Servidor();
                    linha = excel.buscarProximaLinha();
                    if (linha != null) {
                        String[] colunas = linha.split(";");
                        String matricula = colunas[0];
                        String nome = colunas[1];
                        String cpf = colunas[2];
                        String dataNascimento = colunas[5];

                        servidor.setMatricula(matricula);
                        servidor.setNome(nome);
                        servidor.setCpf(cpf);
                        servidor.setDataNascimento(dataNascimento);
                    }
                    linha = excel.buscarProximaLinha();
                    if (linha != null) {
                        linha = excel.buscarProximaLinha();
                        String[] colunas = linha.split(";");
                        String cargo = colunas[0];
                        String dataAdmissao = colunas[6];

                        servidor.setCargo(cargo);
                        servidor.setDataAdmissao(dataAdmissao);

                    }
                    linha = excel.buscarProximaLinha();
                    if (linha != null) {
                        linha = excel.buscarProximaLinha();
                        String[] colunas = linha.split(";");
                        String situacaoMatricula = colunas[3];
                        String setor = colunas[2];

                        servidor.setSituacaoMatricula(situacaoMatricula);
                        servidor.setSetor(setor);
                    }
                    servidores.add(servidor);

                    excel.buscarProximaLinha();
                    linha = excel.buscarProximaLinha();

                    if (linha != null) {

                        while (!linha.contains(LINHA_MOVIMENTO)) {
                            String[] colunas = linha.split(";");
                            Movimento movimento = new Movimento();
                            String valor;
                            String tipo;

                            if (!colunas[4].equals("")) {
                                valor = colunas[4];
                                tipo = "C";
                            } else {
                                valor = colunas[5];
                                tipo = "D";
                            }
                            String codigo = colunas[0];
                            String descricao = colunas[1];

                            movimento.setValor(valor);
                            movimento.setCodigo(codigo);
                            movimento.setDescricao(descricao);
                            movimento.setTipo(tipo);
                            movimento.setMatricula(servidor.getMatricula());

                            listaVerba.add(movimento);

                            linha = excel.buscarProximaLinha();
                        }
                    }
                }
                linha = excel.buscarProximaLinha();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void gerarRelatorio(String[] lines) {

    }
}