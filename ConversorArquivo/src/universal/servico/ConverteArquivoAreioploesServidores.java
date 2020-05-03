package universal.servico;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import universal.entidade.Servidor;

@Integracao(id = "ARE_SE")
public class ConverteArquivoAreioploesServidores extends Integrador {

    @Override
    public void converterArquivo(File file, String caminhoSaida) throws Exception {
        String[] linhas = getLinhas(file);
        gerarRelatorio(linhas);

        String[][] linhasServidor = getLinhasServidor();
        gerarRelatorioExcel(linhasServidor, caminhoSaida + "/" + "servidor");
    }

    @Override
    public void gerarRelatorio(String lines[]) {
        Servidor servidor;
        String linha;
        int i = 0;
        while (i < lines.length - 1) {
            Pattern pattern = Pattern.compile("FUNCIONÁRIO:");
            linha = lines[i];
            Matcher matcher = pattern.matcher(linha);

            if (matcher.find()) {
                linha = lines[++i];
                servidor = new Servidor();
                String[] matriculaNome = linha.split("-");

                servidor.setMatricula(matriculaNome[0].trim());
                servidor.setNome(matriculaNome[1].trim());

                pattern = Pattern.compile("ORGANOGRAMA EMPENHO:");
                matcher = pattern.matcher(linha);
                while (!matcher.find()) {
                    linha = lines[i++];
                    matcher = pattern.matcher(linha);
                }
                linha = lines[i++];
                servidor.setSetor(linha.trim());

                pattern = Pattern.compile("LOCAL DE TRABALHO:");
                matcher = pattern.matcher(linha);
                while (!matcher.find()) {
                    linha = lines[i++];
                    matcher = pattern.matcher(linha);
                }
                linha = lines[i++];
                servidor.setLocal(linha.trim());

                pattern = Pattern.compile("FUNÇÃO:");
                matcher = pattern.matcher(linha);
                while (!matcher.find()) {
                    linha = lines[i++];
                    matcher = pattern.matcher(linha);
                }
                linha = lines[i++];
                String[] cargo = linha.trim().split("\\s{2,}");
                servidor.setCargo(cargo[0].trim());

                pattern = Pattern.compile("TIPO DE NÍVEL SALARIAL:");
                matcher = pattern.matcher(linha);
                while (!matcher.find()) {
                    linha = lines[i++];
                    matcher = pattern.matcher(linha);
                }
                linha = lines[i++];
                if (!linha.trim().equals("-")) {
                    servidor.setSituacaoMatricula(linha.trim().split("\\s{2,}")[0]);
                } else {
                    servidor.setSituacaoMatricula("-");
                }
                pattern = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{4}");
                matcher = pattern.matcher(linha);
                while (!matcher.find()) {
                    linha = lines[i++];
                    matcher = pattern.matcher(linha);
                }
                servidor.setDataAdmissao(matcher.group());
                if (matcher.find()) {
                    servidor.setDataAposentadoria(matcher.group());
                }
                linha = lines[++i];
                pattern = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{4}");
                matcher = pattern.matcher(linha);

                if (matcher.find()) {
                    servidor.setDataNascimento(matcher.group());
                }
                i += 2;
                linha = lines[i];

                String[] conta = linha.trim().split("\\s{2,}");
                if (conta.length == 4) {
                    servidor.setBanco(Integer.parseInt(conta[1]));
                    servidor.setAgencia(conta[2]);
                    servidor.setConta(conta[3]);
                }

                i += 2;
                linha = lines[i];

                pattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}");
                matcher = pattern.matcher(linha);

                if (matcher.find()) {
                    servidor.setCpf(matcher.group());
                }

                servidores.add(servidor);
            }
            i++;
        }

    }

}
