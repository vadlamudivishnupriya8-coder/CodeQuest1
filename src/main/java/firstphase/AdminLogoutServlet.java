package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/admin-logout")
public class AdminLogoutServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException{
  HttpSession s=req.getSession(false);if(s!=null){s.removeAttribute("adminId");s.removeAttribute("adminUsername");}
  res.sendRedirect(req.getContextPath()+"/admin-login.jsp?logout=1");
 }
}
