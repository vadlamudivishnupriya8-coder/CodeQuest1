package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import jakarta.servlet.*;import java.io.IOException;import java.sql.*;import java.util.*;
@WebServlet("/missions")
public class MissionServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  if(!WebUtil.requireStudent(req,res))return;
  int uid=WebUtil.studentId(req);String category=req.getParameter("category");
  List<Mission> list=new ArrayList<>();
  String sql="SELECT m.id,m.title,m.description,m.category,m.difficulty,m.option_a,m.option_b,m.option_c,m.option_d,m.xp_reward, (s.id IS NOT NULL) attempted FROM missions m LEFT JOIN mission_submissions s ON s.mission_id=m.id AND s.user_id=? "+("Java".equals(category)||"SQL".equals(category)||"DSA".equals(category)?"WHERE m.category=? ":"")+"ORDER BY m.id";
  try(Connection c=DBConnection.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql)){
   p.setInt(1,uid);if("Java".equals(category)||"SQL".equals(category)||"DSA".equals(category))p.setString(2,category);
   try(ResultSet rs=p.executeQuery()){while(rs.next()){Mission m=new Mission();m.setId(rs.getInt("id"));m.setTitle(rs.getString("title"));m.setDescription(rs.getString("description"));m.setCategory(rs.getString("category"));m.setDifficulty(rs.getString("difficulty"));m.setOptionA(rs.getString("option_a"));m.setOptionB(rs.getString("option_b"));m.setOptionC(rs.getString("option_c"));m.setOptionD(rs.getString("option_d"));m.setXpReward(rs.getInt("xp_reward"));m.setAttempted(rs.getBoolean("attempted"));list.add(m);}}
   req.setAttribute("missions",list);req.setAttribute("category",category==null?"":category);req.getRequestDispatcher("/missions.jsp").forward(req,res);
  }catch(SQLException e){throw new ServletException(e);}
 }
}
