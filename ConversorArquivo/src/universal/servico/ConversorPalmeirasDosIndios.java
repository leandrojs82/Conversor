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
@Integracao(id = "PI")
public class ConversorPalmeirasDosIndios extends Integrador {

    private static final String LINHA_SERVIDOR = "F";
    private static final String LINHA_PROVENTO = "P";
    private static final String LINHA_DESCONTO = "D";
    private static final String LINHA_TOTAL = "T";
    private static final String LINHA_EM_BRANCO = "M";

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
        String linhaAtual = br.readLine();

        while (linhaAtual != null) {
            String[] linha = linhaAtual.split(";");
            if (LINHA_SERVIDOR.equals(linha[0])) {
                String nome = linha[5];
                String matricula = linha[4];
                String cpf = linha[3];
                String dataNascimento = linha[9];
                String dataAdmissao = linha[10];
                String cargo = linha[12];
                String setor = linha[8];
                String situacaoMatricula = linha[16];

                Servidor servidor = new Servidor();

                servidor.setMatricula(matricula);
                servidor.setDataAdmissao(dataAdmissao);
                servidor.setNome(nome);
                servidor.setCpf(cpf);
                servidor.setDataNascimento(dataNascimento);
                servidor.setDataAdmissao(dataAdmissao);
                servidor.setCargo(cargo);
                servidor.setSetor(setor);
                servidor.setSituacaoMatricula(situacaoMatricula);

                servidores.add(servidor);
                linhaAtual = br.readLine();
                linha = linhaAtual.split(";");
                while (!LINHA_TOTAL.equals(linha[0])) {

                    String valor = linha[8];
                    String codigo = linha[5];
                    String descricao = linha[6];
                    String tipo = linha[0];

                    Movimento movimento = new Movimento();
                    movimento.setCodigo(codigo);
                    movimento.setDescricao(descricao);
                    movimento.setMatricula(matricula);
                    movimento.setTipo(tipo.replace("P", "C"));
                    movimento.setValor(valor);

                    listaVerba.add(movimento);

                    linhaAtual = br.readLine();
                    linha = linhaAtual.split(";");

                }
            }

            linhaAtual = br.readLine();
        }
    }

    @Override
    public void gerarRelatorio(String[] lines) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
