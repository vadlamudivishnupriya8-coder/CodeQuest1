package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import jakarta.servlet.*;import java.io.IOException;import java.sql.*;
@WebServlet("/submit-answer")
public class SubmitAnswerServlet extends HttpServlet {
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  if(!WebUtil.requireStudent(req,res))return;
  int uid=WebUtil.studentId(req),mid;try{mid=Integer.parseInt(req.getParameter("missionId"));}catch(Exception e){res.sendRedirect(req.getContextPath()+"/missions");return;}
  String answer=req.getParameter("answer");if(answer==null||answer.isBlank()){res.sendRedirect(req.getContextPath()+"/missions");return;}
  try(Connection c=DBConnection.getInstance().getConnection()){
   c.setAutoCommit(false);
   try{
    String correct;int reward;
    try(PreparedStatement p=c.prepareStatement("SELECT correct_answer,xp_reward FROM missions WHERE id=? FOR UPDATE")){p.setInt(1,mid);try(ResultSet rs=p.executeQuery()){if(!rs.next())throw new SQLException("Mission not found");correct=rs.getString(1);reward=rs.getInt(2);}}
    try(PreparedStatement p=c.prepareStatement("INSERT INTO mission_submissions(user_id,mission_id,answer,is_correct) VALUES(?,?,?,?)")){
     boolean ok=answer.equals(correct);p.setInt(1,uid);p.setInt(2,mid);p.setString(3,answer);p.setBoolean(4,ok);p.executeUpdate();
     int earned=ok?reward:0,oldLevel,newXp,newLevel;
     try(PreparedStatement q=c.prepareStatement("SELECT total_xp,player_level FROM users WHERE id=? FOR UPDATE")){q.setInt(1,uid);try(ResultSet rs=q.executeQuery()){rs.next();newXp=rs.getInt(1)+earned;oldLevel=rs.getInt(2);}}
     newLevel=WebUtil.levelFor(newXp);
     try(PreparedStatement u=c.prepareStatement("UPDATE users SET total_xp=?,player_level=? WHERE id=?")){u.setInt(1,newXp);u.setInt(2,newLevel);u.setInt(3,uid);u.executeUpdate();}
     c.commit();req.setAttribute("correct",ok);req.setAttribute("earned",earned);req.setAttribute("totalXp",newXp);req.setAttribute("level",newLevel);req.setAttribute("levelUp",newLevel>oldLevel);
    }
    req.getRequestDispatcher("/mission-result.jsp").forward(req,res);
   }catch(SQLException ex){c.rollback();if(ex.getErrorCode()==1062){req.setAttribute("message","You have already attempted this mission.");req.getRequestDispatcher("/mission-result.jsp").forward(req,res);}else throw ex;}
   finally{c.setAutoCommit(true);}
  }catch(Exception e){throw new ServletException("Answer submission failed.",e);}
 }
}
