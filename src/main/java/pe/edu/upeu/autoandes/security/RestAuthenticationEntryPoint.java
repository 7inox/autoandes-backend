package pe.edu.upeu.autoandes.security;

import jakarta.servlet.http.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper mapper;

  public void commence(HttpServletRequest r, HttpServletResponse s, AuthenticationException e)
      throws IOException {
    s.setStatus(401);
    s.setContentType("application/json");
    s.setCharacterEncoding("UTF-8");
    mapper.writeValue(
        s.getWriter(),
        GlobalExceptionHandler.error(
            401, "Autenticación requerida o token inválido", r.getRequestURI(), null));
  }
}
