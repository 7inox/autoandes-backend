package pe.edu.upeu.autoandes.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.upeu.autoandes.security.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UsuarioDetailsService usuarios;
  private final RestAuthenticationEntryPoint entryPoint;

  protected void doFilterInternal(HttpServletRequest r, HttpServletResponse s, FilterChain chain)
      throws ServletException, IOException {
    String h = r.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) {
      try {
        var u = usuarios.loadUserByUsername(jwt.validar(h.substring(7)));
        if (!u.isEnabled()) throw new BadCredentialsException("Cuenta inactiva");
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities()));
      } catch (io.jsonwebtoken.JwtException
          | IllegalArgumentException
          | org.springframework.security.core.AuthenticationException e) {
        SecurityContextHolder.clearContext();
        entryPoint.commence(r, s, new BadCredentialsException("Token inválido"));
        return;
      }
    }
    chain.doFilter(r, s);
  }
}
