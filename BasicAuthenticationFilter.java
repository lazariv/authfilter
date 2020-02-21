/*    */ package lazariv.authfilter;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.util.StringTokenizer;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.FilterChain;
/*    */ import javax.servlet.FilterConfig;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.apache.commons.codec.binary.Base64;
/*    */ import org.apache.commons.lang.StringUtils;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class BasicAuthenticationFilter implements Filter {
/* 19 */   private static final Logger LOG = LoggerFactory.getLogger(BasicAuthenticationFilter.class);
/*    */   
/* 21 */   private String username = "";
/*    */   
/* 23 */   private String password = "";
/*    */   
/* 25 */   private String realm = "Protected";
/*    */   
/*    */   public void init(FilterConfig filterConfig) throws ServletException {
/* 29 */     this.username = filterConfig.getInitParameter("username");
/* 30 */     this.password = filterConfig.getInitParameter("password");
/* 33 */     String paramRealm = filterConfig.getInitParameter("realm");
/* 34 */     if (StringUtils.isNotBlank(paramRealm))
/* 35 */       this.realm = paramRealm; 
/*    */   }
/*    */   
/*    */   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
/* 43 */     HttpServletRequest request = (HttpServletRequest)servletRequest;
/* 44 */     HttpServletResponse response = (HttpServletResponse)servletResponse;
/* 46 */     String authHeader = request.getHeader("Authorization");
/* 47 */     if (authHeader != null) {
/* 48 */       StringTokenizer st = new StringTokenizer(authHeader);
/* 49 */       if (st.hasMoreTokens()) {
/* 50 */         String basic = st.nextToken();
/* 52 */         if (basic.equalsIgnoreCase("Basic"))
/*    */           try {
/* 54 */             String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
/* 55 */             LOG.debug("Credentials: " + credentials);
/* 56 */             int p = credentials.indexOf(":");
/* 57 */             if (p != -1) {
/* 58 */               String _username = credentials.substring(0, p).trim();
/* 59 */               String _password = credentials.substring(p + 1).trim();
/* 61 */               if (!this.username.equals(_username) || !this.password.equals(_password))
/* 62 */                 unauthorized(response, "Bad credentials"); 
/* 65 */               filterChain.doFilter(servletRequest, servletResponse);
/*    */             } else {
/* 67 */               unauthorized(response, "Invalid authentication token");
/*    */             } 
/* 69 */           } catch (UnsupportedEncodingException e) {
/* 70 */             throw new Error("Couldn't retrieve authentication", e);
/*    */           }  
/*    */       } 
/*    */     } else {
/* 75 */       unauthorized(response);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */   
/*    */   private void unauthorized(HttpServletResponse response, String message) throws IOException {
/* 84 */     response.setHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
/* 85 */     response.sendError(401, message);
/*    */   }
/*    */   
/*    */   private void unauthorized(HttpServletResponse response) throws IOException {
/* 89 */     unauthorized(response, "Unauthorized");
/*    */   }
/*    */ }


/* Location:              D:\ScaDS\authfilter.jar!\lazariv\authfilter\BasicAuthenticationFilter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */