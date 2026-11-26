package model;

public class Usuario {

    // Papéis de acesso (coluna papel da tabela usuarios).
    public static final String DONA = "DONA";           // criada pela tela de primeiro acesso; acesso total
    public static final String ADMIN = "ADMIN";         // acesso total
    public static final String VISITANTE = "VISITANTE"; // só visualiza (ex.: banca avaliadora)

    private Integer id;
    private String login;
    private String senhaHash;
    private String papel;

    public Usuario() {}

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }
    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getPapel() {
        return papel;
    }
    public void setPapel(String papel) {
        this.papel = papel;
    }

    public boolean isVisitante() {
        return VISITANTE.equals(papel);
    }
}
