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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import universal.entidade.Movimento;
import universal.entidade.Servidor;

/**
 *
 * @author caio.mota
 */
@Integracao(id = "PL")
public class ConversorPilar extends Integrador {

    private static final String LINHA_VERBA = "2";
    private static final String LINHA_SERVIDOR = "4";
    private static final String LINHA_VALOR_VERBA = "5";
    private static final String LINHA_SETOR = "1";
    private static final String LINHA_CARGO = "3";
    private String[] linha;
    private String linhaAtual;

    @Override
    public void converterArquivo(File file, String caminhoSaida) throws Exception {
        gerarRelatorio(file);

        String[][] linhasServidor = getLinhasServidor();
        String[][] linhasMovimento = getLinhas();
        gerarRelatorioExcel(linhasMovimento, caminhoSaida + "/" + "verba");
        gerarRelatorioExcel(linhasServidor, caminhoSaida + "/" + "servidor");
    }

    public void gerarRelatorio(File file) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        linhaAtual = br.readLine();

        while (linhaAtual != null) {
            linha = linhaAtual.split("\\|");
            Map<String, Movimento> mapVerba = new HashMap<>();

            if (LINHA_VERBA.equals(linha[1])) {

                while (LINHA_VERBA.equals(linha[1])) {
                    preencherMovimento(mapVerba, br);
                }

                while (!LINHA_SERVIDOR.equals(linha[1])) {
                    pularLinha(br);
                }

                Servidor servidor = new Servidor();
                if (LINHA_SERVIDOR.equals(linha[1])) {
                    preencherServidor(servidor);
                }
                if (LINHA_SETOR.equals(linha[1])) {
                    preencherServidor(servidor);
                }

                while (!LINHA_VALOR_VERBA.equals(linha[1])) {
                    pularLinha(br);
                }

                while (linhaAtual != null && LINHA_VALOR_VERBA.equals(linha[1])) {
                    preencherDadosVerba(mapVerba, servidor, br);
                }

            }
            if (br.readLine() != null) {
                linhaAtual = br.readLine();
            }
        }
    }

    public void pularLinha(BufferedReader br) throws IOException {
        linhaAtual = br.readLine();
        linha = linhaAtual.split("\\|");
    }

    public void preencherDadosVerba(Map<String, Movimento> mapVerba, Servidor servidor, BufferedReader br) throws IOException {
        String nroVerba = linha[3];

        BigDecimal valor = new BigDecimal(linha[5]);

        valor = valor.divide(new BigDecimal(100d), 2, BigDecimal.ROUND_HALF_UP);

        Movimento movimento = mapVerba.get(nroVerba);
        movimento.setMatricula(linha[2]);
        movimento.setValor(nroVerba);
        movimento.setValor(valor.toString());
        movimento.setReferencia(linha[4]);

        listaVerba.add(movimento);
        linhaAtual = br.readLine();
        if (linhaAtual != null) {
            linha = linhaAtual.split("\\|");
        }
    }

    public void preencherServidor(Servidor servidor) {
        servidor.setMatricula(linha[2]);
        servidor.setNome(linha[4]);
        servidor.setCpf(linha[5]);
        servidor.setDataNascimento(linha[16]);
        //servidor.setCargo(linha[3]);
        servidor.setDataDemissao(linha[3]);
        System.out.println(servidor.getNome());
        servidores.add(servidor);
    }

    public void preencherMovimento(Map<String, Movimento> mapVerba, BufferedReader br) throws IOException {
        Movimento movimento = new Movimento();
        movimento.setDescricao(linha[3]);
        movimento.setCodigo(linha[2]);
        movimento.setTipo(linha[4]);
        movimento.setReferencia(linha[4]);
        mapVerba.put(linha[2], movimento);

        pularLinha(br);
    }

    @Override
    public void gerarRelatorio(String[] lines) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
