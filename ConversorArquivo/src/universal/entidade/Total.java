/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package universal.entidade;

/**
 *
 * @author caio.mota
 */
public class Total {

    private String liquido;
    private String credito;
    private String debito;

    public Total(String liquido, String credito, String debito) {
        this.liquido = liquido;
        this.credito = credito;
        this.debito = debito;
    }

    public String getLiquido() {
        return liquido;
    }

    public void setLiquido(String liquido) {
        this.liquido = liquido;
    }

    public String getCredito() {
        return credito;
    }

    public void setCredito(String credito) {
        this.credito = credito;
    }

    public String getDebito() {
        return debito;
    }

    public void setDebito(String debito) {
        this.debito = debito;
    }

}
