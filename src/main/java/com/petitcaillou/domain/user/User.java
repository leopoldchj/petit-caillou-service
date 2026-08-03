package com.petitcaillou.domain.user;

public final class User
{
  private final Alias alias;
  private final Email email;
  private final HashedPassword password;
  private final Role role;

  private User(Alias alias, Email email, HashedPassword password, Role role)
  {
    this.alias = alias;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public static User register(Alias alias, Email email, RawPassword rawPassword, Role role, PasswordHasher hasher)
  {
    return new User(alias, email, hasher.hash(rawPassword), role);
  }

  public static User reconstitute(Alias alias, Email email, HashedPassword password, Role role)
  {
    return new User(alias, email, password, role);
  }

  public boolean hasPassword(RawPassword candidate, PasswordHasher hasher)
  {
    return hasher.matches(candidate, password);
  }

  public Alias alias()
  {
    return alias;
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
