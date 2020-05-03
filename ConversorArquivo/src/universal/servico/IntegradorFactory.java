package universal.servico;

import java.util.Set;
import org.reflections.Reflections;

public class IntegradorFactory {

    public static Integrador lookup(final String id) {
        Integrador integrador = null;
        Reflections reflections = new Reflections("universal/servico");
        Set<Class<?>> integracoes = reflections.getTypesAnnotatedWith(Integracao.class);
        for (Class<?> clazz : integracoes) {
            try {
                Integracao integracao = clazz.getAnnotation(Integracao.class);
                if (integracao.id().equals(id)) {
                    integrador = ((Class<? extends Integrador>) clazz).newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return integrador;
    }
}
