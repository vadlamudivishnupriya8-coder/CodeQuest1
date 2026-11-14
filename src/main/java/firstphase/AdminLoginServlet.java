package firstphase;
import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;import java.sql.*;
@WebServlet("/admin-login")
public class AdminLoginServlet extends HttpServlet {
 public void init()throws ServletException{getServletContext().setAttribute("applicationName",getServletContext().getInitParameter("appName"));}
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  String u=req.getParameter("username"),pw=req.getParameter("password");
  try(Connection c=DBConnection.getInstance().getConnection();PreparedStatement p=c.prepareStatement("SELECT id,password FROM admins WHERE username=?")){
   p.setString(1,u);
   try(ResultSet rs=p.executeQuery()){
    if(rs.next()&&DBConnection.verifyPassword(pw,rs.getString("password"))){
     HttpSession s=req.getSession(true);s.setAttribute("adminId",rs.getInt("id"));s.setAttribute("adminUsername",u);
     getServletContext().log("Admin authenticated for "+getServletContext().getAttribute("applicationName"));
     res.sendRedirect(req.getContextPath()+"/admin-dashboard.jsp");return;
    }
   }
   req.setAttribute("error","Invalid administrator credentials.");req.getRequestDispatcher("/admin-login.jsp").forward(req,res);
  }catch(Exception e){throw new ServletException("Admin login failed.",e);}
 }
}
