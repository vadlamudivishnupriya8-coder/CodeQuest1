package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException{
  HttpSession s=req.getSession(false);if(s!=null){s.removeAttribute("studentId");s.removeAttribute("studentUsername");}
  Cookie c=new Cookie("cq_user","");c.setMaxAge(0);c.setPath(req.getContextPath().isEmpty()?"/":req.getContextPath());c.setHttpOnly(true);res.addCookie(c);
  res.sendRedirect(req.getContextPath()+"/login.jsp?logout=1");
 }
}
