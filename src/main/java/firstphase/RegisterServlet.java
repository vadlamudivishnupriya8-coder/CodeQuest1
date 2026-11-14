package firstphase;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");
        String username=req.getParameter("username"), email=req.getParameter("email");
        String password=req.getParameter("password"), confirm=req.getParameter("confirmPassword");
        String error=null;
        if(username==null||email==null||password==null||confirm==null||username.isBlank()||email.isBlank()||password.isBlank()) error="All fields are required.";
        else if(!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) error="Enter a valid email address.";
        else if(password.length()<8) error="Password must contain at least 8 characters.";
        else if(!password.equals(confirm)) error="Passwords do not match.";
        if(error!=null){req.setAttribute("error",error);req.getRequestDispatcher("/register.jsp").forward(req,res);return;}
        try(Connection c=DBConnection.getInstance().getConnection();
            PreparedStatement check=c.prepareStatement("SELECT id FROM users WHERE username=? OR email=?")) {
            check.setString(1,username.trim());check.setString(2,email.trim());
            try(ResultSet rs=check.executeQuery()){if(rs.next()){req.setAttribute("error","Username or email is already registered.");req.getRequestDispatcher("/register.jsp").forward(req,res);return;}}
            try(PreparedStatement p=c.prepareStatement("INSERT INTO users(username,email,password,total_xp,player_level) VALUES(?,?,?,0,1)")){
                p.setString(1,username.trim());p.setString(2,email.trim());p.setString(3,DBConnection.hashPassword(password));p.executeUpdate();
            }
            res.sendRedirect(req.getContextPath()+"/login.jsp?registered=1");
        }catch(Exception e){throw new ServletException("Registration failed. Check MySQL configuration.",e);}
    }
}
