package com.petitcaillou.domain.user;

public final class User
{
  private final Username username;
  private final Email email;
  private final HashedPassword password;
  private final Role role;

  private User(Username username, Email email, HashedPassword password, Role role)
  {
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public static User register(Username username, Email email, RawPassword rawPassword, Role role, PasswordHasher hasher)
  {
    return new User(username, email, hasher.hash(rawPassword), role);
  }

  public static User reconstitute(Username username, Email email, HashedPassword password, Role role)
  {
    return new User(username, email, password, role);
  }

  public boolean hasPassword(RawPassword candidate, PasswordHasher hasher)
  {
    return hasher.matches(candidate, password);
  }

  public Username username()
  {
    return username;
  }

  public Email email()
  {
    return email;
  }

  public HashedPassword password()
  {
    return password;
  }

  public Role role()
  {
    return role;
  }
}
