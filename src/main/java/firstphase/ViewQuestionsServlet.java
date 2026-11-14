package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import jakarta.servlet.*;import java.io.IOException;import java.sql.*;import java.util.*;
@WebServlet("/admin/view-questions")
public class ViewQuestionsServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  if(!WebUtil.requireAdmin(req,res))return;
  List<Mission> list=new ArrayList<>();
  try(Connection c=DBConnection.getInstance().getConnection();PreparedStatement p=c.prepareStatement("SELECT id,title,category,difficulty,xp_reward FROM missions ORDER BY id DESC");ResultSet rs=p.executeQuery()){
   while(rs.next()){Mission m=new Mission();m.setId(rs.getInt("id"));m.setTitle(rs.getString("title"));m.setCategory(rs.getString("category"));m.setDifficulty(rs.getString("difficulty"));m.setXpReward(rs.getInt("xp_reward"));list.add(m);}
   req.setAttribute("missions",list);req.getRequestDispatcher("/view-questions.jsp").forward(req,res);
  }catch(SQLException e){throw new ServletException(e);}
 }
}
