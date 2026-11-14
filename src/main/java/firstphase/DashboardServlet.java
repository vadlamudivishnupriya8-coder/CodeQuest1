package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import jakarta.servlet.*;import java.io.IOException;import java.sql.*;
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  if(!WebUtil.requireStudent(req,res))return;
  int id=WebUtil.studentId(req);
  try(Connection c=DBConnection.getInstance().getConnection();PreparedStatement p=c.prepareStatement("SELECT u.username,u.total_xp,u.player_level,(SELECT COUNT(*) FROM mission_submissions ms WHERE ms.user_id=u.id AND ms.is_correct=TRUE) completed FROM users u WHERE u.id=?")){
   p.setInt(1,id);try(ResultSet rs=p.executeQuery()){if(!rs.next()){res.sendRedirect(req.getContextPath()+"/logout");return;}
    int xp=rs.getInt("total_xp"),level=WebUtil.levelFor(xp),base=(level-1)*200,progress=xp-base;
    req.setAttribute("username",rs.getString("username"));req.setAttribute("xp",xp);req.setAttribute("level",level);req.setAttribute("completed",rs.getInt("completed"));req.setAttribute("progress",Math.min(100,progress*100/200));req.setAttribute("xpToNext",Math.max(0,level*200-xp));
   }
   req.getRequestDispatcher("/dashboard.jsp").forward(req,res);
  }catch(SQLException e){throw new ServletException(e);}
 }
}
