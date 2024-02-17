package lir;

import com.oracle.truffle.api.*;

@TruffleLanguage.Registration(id = "lir", name = "LIR")
public final class Lir extends TruffleLanguage<Void> {
  @Override
  public Void createContext(Env env) {
    return null;
  }

  private static final LanguageReference<Lir> SINGLETON = LanguageReference.create(Lir.class);

  public static Lir instance() {
    return SINGLETON.get();
  }
}
