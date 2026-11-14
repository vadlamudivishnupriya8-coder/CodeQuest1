package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import jakarta.servlet.*;import java.io.IOException;import java.sql.*;import java.util.*;
@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  List<LeaderboardUser> list=new ArrayList<>();int rank=1;
  String sql="SELECT u.username,u.total_xp,u.player_level,COUNT(CASE WHEN s.is_correct=TRUE THEN 1 END) completed FROM users u LEFT JOIN mission_submissions s ON s.user_id=u.id GROUP BY u.id,u.username,u.total_xp,u.player_level ORDER BY u.total_xp DESC,u.username ASC";
  try(Connection c=DBConnection.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql);ResultSet rs=p.executeQuery()){
   while(rs.next()){LeaderboardUser x=new LeaderboardUser();x.setRank(rank++);x.setUsername(rs.getString("username"));x.setTotalXp(rs.getInt("total_xp"));x.setLevel(rs.getInt("player_level"));x.setCompletedMissions(rs.getInt("completed"));list.add(x);}
   req.setAttribute("leaderboard",list);req.setAttribute("currentUser",WebUtil.studentId(req));req.getRequestDispatcher("/leaderboard.jsp").forward(req,res);
  }catch(SQLException e){throw new ServletException(e);}
 }
}
