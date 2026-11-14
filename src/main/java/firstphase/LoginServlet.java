package firstphase;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");
        String identity=req.getParameter("identity"), password=req.getParameter("password");
        try(Connection c=DBConnection.getInstance().getConnection();
            PreparedStatement p=c.prepareStatement("SELECT id,username,password FROM users WHERE username=? OR email=?")) {
            p.setString(1,identity);p.setString(2,identity);
            try(ResultSet rs=p.executeQuery()){
                if(rs.next()&&DBConnection.verifyPassword(password,rs.getString("password"))){
                    HttpSession old=req.getSession(false);if(old!=null)old.invalidate();
                    HttpSession s=req.getSession(true);s.setAttribute("studentId",rs.getInt("id"));s.setAttribute("studentUsername",rs.getString("username"));
                    s.setMaxInactiveInterval(30*60);
                    if("on".equals(req.getParameter("remember"))){Cookie ck=new Cookie("cq_user",rs.getString("username"));ck.setMaxAge(7*24*60*60);ck.setHttpOnly(true);ck.setPath(req.getContextPath().isEmpty()?"/":req.getContextPath());res.addCookie(ck);}
                    res.sendRedirect(req.getContextPath()+"/dashboard");
                    return;
                }
            }
            req.setAttribute("error","Invalid username/email or password.");req.getRequestDispatcher("/login.jsp").forward(req,res);
        }catch(Exception e){throw new ServletException("Login failed.",e);}
    }
}
