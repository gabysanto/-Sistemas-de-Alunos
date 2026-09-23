function cadastrarAluno() {

    var nome = document.getElementById("nomeAluno").value.trim();
    var campus = document.getElementById("campusAluno").value;
    var mensagem = document.getElementById("mensagemCadastro");

    if (nome === "") {
        mensagem.innerHTML =
            "<div class='erro'>Digite o nome do aluno.</div>";
        return;
    }

    if (campus === "") {
        mensagem.innerHTML =
            "<div class='erro'>Selecione um campus.</div>";
        return;
    }

    var dados =
        "nome=" + encodeURIComponent(nome) +
        "&campus=" + encodeURIComponent(campus);

    fetch("http://localhost:8080/cadastrar", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: dados
    })
    .then(function (resposta) {
        return resposta.text();
    })
    .then(function (resultado) {

        if (resultado === "sucesso") {

            mensagem.innerHTML =
                "<div class='sucesso'>" +
                "Aluno <strong>" + nome + "</strong> " +
                "cadastrado no campus <strong>" + campus + "</strong>." +
                "</div>";

            document.getElementById("nomeAluno").value = "";
            document.getElementById("campusAluno").value = "";

            atualizarLista();

        } else {

            mensagem.innerHTML =
                "<div class='erro'>" +
                "O aluno <strong>" + nome +
                "</strong> já está cadastrado em algum campus." +
                "</div>";
        }
    })
    .catch(function () {

        mensagem.innerHTML =
            "<div class='erro'>" +
            "Não foi possível conectar ao servidor Java." +
            "</div>";
    });
}


function localizarAluno() {

    var nome = document.getElementById("nomeBusca").value.trim();
    var resultado = document.getElementById("resultadoBusca");

    if (nome === "") {

        resultado.innerHTML =
            "<div class='erro'>Digite o nome do aluno.</div>";

        return;
    }

    fetch(
        "http://localhost:8080/localizar?nome=" +
        encodeURIComponent(nome)
    )
    .then(function (resposta) {
        return resposta.text();
    })
    .then(function (campus) {

        if (campus !== "null") {

            resultado.innerHTML =
                "<div class='resultado'>" +
                "O aluno <strong>" + nome + "</strong> " +
                "está cadastrado no campus " +
                "<strong>" + campus + "</strong>." +
                "</div>";

        } else {

            resultado.innerHTML =
                "<div class='erro'>" +
                "Aluno <strong>" + nome +
                "</strong> não encontrado." +
                "</div>";
        }
    })
    .catch(function () {

        resultado.innerHTML =
            "<div class='erro'>" +
            "Não foi possível conectar ao servidor Java." +
            "</div>";
    });
}


function atualizarLista() {

    var container = document.getElementById("listaAlunos");

    fetch("http://localhost:8080/listar")
        .then(function (resposta) {
            return resposta.json();
        })
        .then(function (dados) {

            container.innerHTML = "";

            for (var i = 0; i < dados.length; i++) {

                var campus = dados[i].campus;
                var alunos = dados[i].alunos;

                var div = document.createElement("div");

                div.className = "campus";

                var conteudo =
                    "<h3>Campus: " + campus + "</h3>";

                if (alunos.length === 0) {

                    conteudo +=
                        "<div class='vazio'>" +
                        "Nenhum aluno cadastrado." +
                        "</div>";

                } else {

                    conteudo += "<ul class='lista'>";

                    for (var j = 0; j < alunos.length; j++) {

                        conteudo +=
                            "<li>" +
                            (j + 1) +
                            ". " +
                            alunos[j] +
                            "</li>";
                    }

                    conteudo += "</ul>";
                }

                div.innerHTML = conteudo;

                container.appendChild(div);
            }
        })
        .catch(function () {

            container.innerHTML =
                "<div class='erro'>" +
                "Não foi possível conectar ao servidor Java." +
                "</div>";
        });
}


atualizarLista();