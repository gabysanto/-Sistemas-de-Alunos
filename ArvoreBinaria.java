import java.util.ArrayList;
import java.util.List;
public class ArvoreBinaria {
    private class No {
        Aluno aluno;
        No esquerda;
        No direita;
        public No(Aluno aluno) {
            this.aluno = aluno;
        }
    }
    private No raiz;
    public boolean inserir(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }

        Aluno novoAluno = new Aluno(nome.trim());

        if (raiz == null) {
            raiz = new No(novoAluno);
            return true;
        }
        return inserirRecursivo(raiz, novoAluno);
    }
    private boolean inserirRecursivo(No atual, Aluno aluno) {
        int comparacao = aluno.getNome().compareToIgnoreCase(atual.aluno.getNome());
        if (comparacao == 0) {
            return false;
        }

        if (comparacao < 0) {
            if (atual.esquerda == null) {
                atual.esquerda = new No(aluno);
                return true;
            }
            return inserirRecursivo(atual.esquerda, aluno);
        } else {
            if (atual.direita == null) {
                atual.direita = new No(aluno);
                return true;
            }
            return inserirRecursivo(atual.direita, aluno);
        }
    }
    public Aluno buscar(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        return buscarRecursivo(raiz, nome.trim());
    }
    private Aluno buscarRecursivo(No atual, String nome) {
        if (atual == null) {
            return null;
        }

        int comparacao = nome.compareToIgnoreCase(atual.aluno.getNome());

        if (comparacao == 0) {
            return atual.aluno;
        }

        if (comparacao < 0) {
            return buscarRecursivo(atual.esquerda, nome);
        }

        return buscarRecursivo(atual.direita, nome);
    }
    public List<String> listarEmOrdem() {
        List<String> alunos = new ArrayList<>();
        emOrdem(raiz, alunos);
        return alunos;
    }

    private void emOrdem(No atual, List<String> alunos) {
        if (atual == null) {
            return;
        }

        emOrdem(atual.esquerda, alunos);
        alunos.add(atual.aluno.getNome());
        emOrdem(atual.direita, alunos);
    }
}