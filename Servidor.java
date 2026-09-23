import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {

    private static final Sistema sistema = new Sistema();

    public static void main(String[] args) throws Exception {

        HttpServer servidor = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        servidor.createContext("/", Servidor::paginaInicial);
        servidor.createContext("/style.css", Servidor::styleCss);
        servidor.createContext("/script.js", Servidor::scriptJs);
        servidor.createContext("/cadastrar", Servidor::cadastrar);
        servidor.createContext("/localizar", Servidor::localizar);
        servidor.createContext("/listar", Servidor::listar);

        servidor.start();

        System.out.println("=================================");
        System.out.println("Servidor Java iniciado!");
        System.out.println("Acesse: http://localhost:8080");
        System.out.println("=================================");
    }

    private static void paginaInicial(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            responder(exchange, "Método não permitido", 405);
            return;
        }

        servirArquivo(
                exchange,
                "index.html",
                "text/html; charset=UTF-8"
        );
    }

    private static void styleCss(HttpExchange exchange)
            throws IOException {

        servirArquivo(
                exchange,
                "style.css",
                "text/css; charset=UTF-8"
        );
    }

    private static void scriptJs(HttpExchange exchange)
            throws IOException {

        servirArquivo(
                exchange,
                "script.js",
                "application/javascript; charset=UTF-8"
        );
    }

    private static void servirArquivo(
        HttpExchange exchange,
        String nomeArquivo,
        String tipo
) throws IOException {

        Path caminho = Paths.get(nomeArquivo);

        if (!Files.exists(caminho)) {

            responder(
                    exchange,
                    "Arquivo não encontrado: " + nomeArquivo,
                    404
            );

            return;
        }

        byte[] arquivo = Files.readAllBytes(caminho);

        exchange.getResponseHeaders().set(
                "Content-Type",
                tipo
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.sendResponseHeaders(
                200,
                arquivo.length
        );

        try (OutputStream saida = exchange.getResponseBody()) {
            saida.write(arquivo);
        }
    }

    private static void cadastrar(HttpExchange exchange)
            throws IOException {

        adicionarCors(exchange);

        if ("OPTIONS".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            responder(
                    exchange,
                    "Método não permitido",
                    405
            );

            return;
        }

        String corpo = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        Map<String, String> dados = parametros(corpo);

        String nome = dados.get("nome");
        String campus = dados.get("campus");

        if (nome == null || campus == null) {

            responder(
                    exchange,
                    "dados_invalidos",
                    400
            );

            return;
        }

        boolean sucesso =
                sistema.cadastrarAluno(nome, campus);

        if (sucesso) {

            responder(
                    exchange,
                    "sucesso",
                    200
            );

        } else {

            responder(
                    exchange,
                    "duplicado",
                    200
            );
        }
    }

    private static void localizar(HttpExchange exchange)
            throws IOException {

        adicionarCors(exchange);

        if (!"GET".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            responder(
                    exchange,
                    "Método não permitido",
                    405
            );

            return;
        }

        String query =
                exchange.getRequestURI().getQuery();

        Map<String, String> dados =
                parametros(query);

        String nome = dados.get("nome");

        if (nome == null || nome.isEmpty()) {

            responder(
                    exchange,
                    "null",
                    200
            );

            return;
        }

        String campus =
                sistema.localizarAluno(nome);

        if (campus == null) {

            responder(
                    exchange,
                    "null",
                    200
            );

        } else {

            responder(
                    exchange,
                    campus,
                    200
            );
        }
    }

    private static void listar(HttpExchange exchange)
            throws IOException {

        adicionarCors(exchange);

        if (!"GET".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            responder(
                    exchange,
                    "Método não permitido",
                    405
            );

            return;
        }

        StringBuilder json =
                new StringBuilder();

        json.append("[");

        boolean primeiroCampus = true;

        for (Map.Entry<String, ArvoreBinaria> entrada
                : sistema.getCampi().entrySet()) {

            if (!primeiroCampus) {
                json.append(",");
            }

            primeiroCampus = false;

            String campus = entrada.getKey();

            List<String> alunos =
                    entrada.getValue().listarEmOrdem();

            json.append("{");

            json.append("\"campus\":\"");
            json.append(escaparJson(campus));
            json.append("\",");

            json.append("\"alunos\":[");

            for (int i = 0; i < alunos.size(); i++) {

                if (i > 0) {
                    json.append(",");
                }

                json.append("\"");
                json.append(
                        escaparJson(alunos.get(i))
                );
                json.append("\"");
            }

            json.append("]");

            json.append("}");
        }

        json.append("]");

        responderJson(
                exchange,
                json.toString(),
                200
        );
    }

    private static Map<String, String> parametros(
            String texto) {

        Map<String, String> resultado =
                new HashMap<>();

        if (texto == null || texto.isEmpty()) {
            return resultado;
        }

        String[] partes =
                texto.split("&");

        for (String parte : partes) {

            String[] chaveValor =
                    parte.split("=", 2);

            if (chaveValor.length == 2) {

                String chave =
                        URLDecoder.decode(
                                chaveValor[0],
                                StandardCharsets.UTF_8
                        );

                String valor =
                        URLDecoder.decode(
                                chaveValor[1],
                                StandardCharsets.UTF_8
                        );

                resultado.put(
                        chave,
                        valor
                );
            }
        }

        return resultado;
    }

    private static void adicionarCors(
            HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }

    private static void responder(
            HttpExchange exchange,
            String resposta,
            int codigo
    ) throws IOException {

        byte[] bytes =
                resposta.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                codigo,
                bytes.length
        );

        try (OutputStream saida =
                     exchange.getResponseBody()) {

            saida.write(bytes);
        }
    }

    private static void responderJson(
            HttpExchange exchange,
            String resposta,
            int codigo
    ) throws IOException {

        byte[] bytes =
                resposta.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                codigo,
                bytes.length
        );

        try (OutputStream saida =
                     exchange.getResponseBody()) {

            saida.write(bytes);
        }
    }

    private static String escaparJson(
            String texto) {

        if (texto == null) {
            return "";
        }

        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}

