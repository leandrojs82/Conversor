package universal.servico;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import universal.entidade.Movimento;
import universal.entidade.Servidor;

@Integracao(id = "ARE")
public class ConverteArquivoAreioploes extends Integrador {

    private List<ArquivoTotalVerba> listTotalVerba = new ArrayList<>();

    @Override
    public void converterArquivo(File file, String caminhoSaida) throws Exception {
        String[] linhas = getLinhas(file);
        gerarRelatorio(linhas);

        String[][] linhasServidor = getLinhasServidor();
        String[][] linhasMovimento = getLinhas();

        gerarRelatorioExcel(linhasMovimento, caminhoSaida + "/" + "verba");
        gerarRelatorioExcel(linhasServidor, caminhoSaida + "/" + "servidor");
        gerarRelatorioExcel(getLinhasTotal(listTotalVerba), caminhoSaida + "/" + "total");
    }

    @Override
    public void gerarRelatorio(String lines[]) {
        Servidor servidor = null;
        Movimento movimento = null;
        String linha;
        int i = 0;
        while (i < lines.length - 1) {
            linha = lines[i];
            Pattern pattern = Pattern.compile("Funcionario:\\s+");
            Matcher matcher = pattern.matcher(linha);

            if (matcher.find()) {
                servidor = new Servidor();
                String matriculaNome = linha.split(":")[1];

                pattern = Pattern.compile("\\d+");
                matcher = pattern.matcher(matriculaNome);
                if (matcher.find()) {
                    String matricula = matcher.group();
                    servidor.setMatricula(matricula);
                    servidor.setNome(matriculaNome.replace(matricula, "").trim());
                    linha = lines[++i];
                }
                pattern = Pattern.compile("Função:\\s+\\d+\\s+");
                matcher = pattern.matcher(linha);
                while (!matcher.find()) {
                    linha = lines[i++];
                }
                String[] funcaoAdmissao = linha.split("\\s{2,}");
                servidor.setCargo(funcaoAdmissao[3]);
                servidor.setDataAdmissao(funcaoAdmissao[5]);
                i++;
                while (!linha.contains("Total:")) {
                    linha = lines[i];
                    // pattern = Pattern.compile("\\d{1,8}\\.\\d{4}");
                    pattern = Pattern.compile("(\\s{49,}\\d{1,}(\\.\\d{4}|\\/\\d{3}))|(\\d{1,3}\\/\\d{1,3})\\s{1,}");
                    matcher = pattern.matcher(linha);
                    if (matcher.find()) {
                        String[] verbaSplit = linha.split("\\s{2,}");
                        movimento = new Movimento();
                        if (!linha.substring(149, 162).trim().equals("")) {
                            movimento.setTipo("C");
                        } else {
                            movimento.setTipo("D");
                        }

                        movimento.setCodigo(verbaSplit[1]);
                        movimento.setDescricao(verbaSplit[2]);
                        movimento.setValor(verbaSplit[4]);
                        movimento.setMatricula(servidor.getMatricula());
                        listaVerba.add(movimento);
                    }
                    i++;
                }
                String[] total = linha.split("\\s{2,}");
                ArquivoTotalVerba totalVerba = new ArquivoTotalVerba();
                totalVerba.setTotalCredito(total[2]);
                if (total.length > 3) {
                    totalVerba.setTotalDebito(total[3]);
                }
                totalVerba.setMatricula(servidor.getMatricula());

                listTotalVerba.add(totalVerba);
                servidores.add(servidor);
            }
            i++;
        }

    }

    public void gerarRelatorioValorTotal(String lines[]) {
        String linha;
        int i = 0;
        while (i < lines.length - 1) {
            linha = lines[i];
            Pattern pattern = Pattern.compile("Total:\\s+\\d{1,}");
            Matcher matcher = pattern.matcher(linha);

            if (matcher.find()) {
                String[] total = linha.split("\\s{2,}");
                ArquivoTotalVerba totalVerba = new ArquivoTotalVerba();
                totalVerba.setTotalCredito(total[2]);
                if (total.length > 3) {
                    totalVerba.setTotalDebito(total[3]);
                }
                listTotalVerba.add(totalVerba);
            }
            i++;

        }
    }

    private String[][] getLinhasTotal(List<ArquivoTotalVerba> list) {
        String[][] texto = new String[list.size() + 1][3];
        texto[0][0] = "Matrícula";
        texto[0][1] = "Valor crédito";
        texto[0][2] = "Valor dédito";
        for (int i = 1; i <= list.size(); i++) {
            ArquivoTotalVerba arquivo = list.get(i - 1);
            for (int j = 0; j < 3; j++) {
                switch (j) {
                    case 0:
                        texto[i][j] = arquivo.getMatricula();
                        break;
                    case 1:
                        texto[i][j] = arquivo.getTotalCredito();
                        break;
                    case 2:
                        texto[i][j] = arquivo.getTotalDebito();
                        break;
                }
            }
        }
        return texto;
    }

    public class ArquivoTotalVerba {

        private String matricula;
        private String tipo;
        private String totalCredito;
        private String totalDebito;

        public String getTotalCredito() {
            return totalCredito;
        }

        public void setTotalCredito(String totalCredito) {
            this.totalCredito = totalCredito;
        }

        public String getTotalDebito() {
            return totalDebito;
        }

        public void setTotalDebito(String totalDebito) {
            this.totalDebito = totalDebito;
        }

        public String getMatricula() {
            return matricula;
        }

        public void setMatricula(String matricula) {
            this.matricula = matricula;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

    }

}
