package pe.edu.upeu.autoandes.security;

import jakarta.servlet.http.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pe.edu.upeu.autoandes.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper mapper;

  public void handle(HttpServletRequest r, HttpServletResponse s, AccessDeniedException e)
      throws IOException {
    s.setStatus(403);
    s.setContentType("application/json");
    s.setCharacterEncoding("UTF-8");
    mapper.writeValue(
        s.getWriter(),
        GlobalExceptionHandler.error(
            403, "No tiene permiso para esta operación", r.getRequestURI(), null));
  }
}
