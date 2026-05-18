package model;

/**
 * User model — maps to the `users` table.
 */
public class User {
    private int    userId;
    private String username;
    private String passwordHash;
    private String role;           // ADMIN or USER
    private String fullName;
    private String email;
    private String securityQuestion;
    private String securityAnswer;

    public User() {}

    public User(int userId, String username, String role, String fullName, String email) {
        this.userId   = userId;
        this.username = username;
        this.role     = role;
        this.fullName = fullName;
        this.email    = email;
    }

    // ── Getters & Setters ──────────────────────────────
    public int    getUserId()           { return userId; }
    public void   setUserId(int id)     { this.userId = id; }

    public String getUsername()              { return username; }
    public void   setUsername(String u)      { this.username = u; }

    public String getPasswordHash()          { return passwordHash; }
    public void   setPasswordHash(String ph) { this.passwordHash = ph; }

    public String getRole()                  { return role; }
    public void   setRole(String r)          { this.role = r; }

    public String getFullName()              { return fullName; }
    public void   setFullName(String fn)     { this.fullName = fn; }

    public String getEmail()                 { return email; }
    public void   setEmail(String e)         { this.email = e; }

    public String getSecurityQuestion()           { return securityQuestion; }
    public void   setSecurityQuestion(String sq)  { this.securityQuestion = sq; }

    public String getSecurityAnswer()             { return securityAnswer; }
    public void   setSecurityAnswer(String sa)    { this.securityAnswer = sa; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }

    @Override
    public String toString() { return fullName + " (" + role + ")"; }
}
