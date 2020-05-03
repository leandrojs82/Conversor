/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.enuns;

/**
 *
 * @author caio.mota
 */
public enum EnumPrefeitura {

    CPS("COMPANHIA PONTAGROSSENSE DE SERVICOS", "CPS"),
    AREIOPOLIS_SERVIDORES("PREFEITURA DE AREIÓPOLIS", "ARE"),
    AREIOPOLIS("PREFEITURA DE AREIÓPOLIS - SERVIDORES", "ARE_SE"),
    ANDRADAS("PREFEITURA DE ANDRADAS", "CPS"),
    ARQUIVO_TEXTO("CONVERTER PARA TEXTO(COM ESPAÇO)", "TXT"),
    ARQUIVO_TEXTO_SEM_ESPACO("CONVERTER PARA TEXTO(SEM ESPAÇO)", "TXT_E"),
    CONCEICAO_DO_ARAGUAIA_SERVIDOR("CONCEICAO DO ARAGUAIA - SERVIDOR", "CAS"),
    CONCEICAO_DO_ARAGUAIA_DEMISSAO("CONCEICAO DO ARAGUAIA - AFASTAMENTO", "CAA"),
    CONCEICAO_DO_ARAGUAIA_FOLHA_MOVIMENTO("CONCEICAO DO ARAGUAIA - SERVIDOR E MOVIMENTO", "CASM"),
    CIDREIRA("PREFEITURA DE CIDREIRA", "CD"),
    TV_EDUCATIVA("TV EDUCATIVA", "TV"),
    PORTO_NACIONAL("PORTO NACIONAL", "PN"),
    PALMEIRAS_DOS_INDIOS("PALMEIRAS DOS ÍNDIOS", "PI"),
    PREFEITURA_PILAR("PILAR", "PL"),
    PREFEITURA_INDIAROBA("INDIAROBA", "IN"),
    IPREV("IPREV","IPREV");

    private final String descricao;
    private final String tipo;

    private EnumPrefeitura(String descricao, String tipo) {
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public static EnumPrefeitura retornaEnumSelecionado(String enumSelecionado) {
        for (EnumPrefeitura en : EnumPrefeitura.values()) {
            if (en.getDescricao().equals(enumSelecionado)) {
                return en;
            }
        }
        return null;

    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        return tipo;
    }

}
