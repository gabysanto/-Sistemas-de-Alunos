import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Sistema {

    private final Map<String, ArvoreBinaria> campi;

    public Sistema() {
        campi = new HashMap<>();

        campi.put("Anália Franco", new ArvoreBinaria());
        campi.put("Guarulhos", new ArvoreBinaria());
        campi.put("Liberdade", new ArvoreBinaria());
        campi.put("Paulista", new ArvoreBinaria());
        campi.put("São Miguel", new ArvoreBinaria());
        campi.put("Santo Amaro", new ArvoreBinaria());
        campi.put("Villa Lobos", new ArvoreBinaria());
    }

    public Map<String, ArvoreBinaria> getCampi() {
        return Collections.unmodifiableMap(campi);
    }

    private String localizarCampus(String nome) {

        for (Map.Entry<String, ArvoreBinaria> entrada : campi.entrySet()) {

            if (entrada.getValue().buscar(nome) != null) {
                return entrada.getKey();
            }
        }

        return null;
    }

    public boolean cadastrarAluno(String nome, String campus) {

        if (localizarCampus(nome) != null) {
            return false;
        }

        ArvoreBinaria arvore = campi.get(campus);

        if (arvore == null) {
            return false;
        }

        return arvore.inserir(nome);
    }

    public String localizarAluno(String nome) {
        return localizarCampus(nome);
    }
}
