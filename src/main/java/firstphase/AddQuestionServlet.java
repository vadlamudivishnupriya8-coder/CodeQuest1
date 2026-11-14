package firstphase;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import jakarta.servlet.*;import java.io.IOException;import java.sql.*;
@WebServlet("/admin/add-question")
public class AddQuestionServlet extends HttpServlet {
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  if(!WebUtil.requireAdmin(req,res))return;
  String title=req.getParameter("title"),desc=req.getParameter("description"),cat=req.getParameter("category"),diff=req.getParameter("difficulty");
  String a=req.getParameter("optionA"),b=req.getParameter("optionB"),c1=req.getParameter("optionC"),d=req.getParameter("optionD"),correct=req.getParameter("correctAnswer");
  int xp;try{xp=Integer.parseInt(req.getParameter("xpReward"));}catch(Exception ex){xp=0;}
  if(title==null||title.isBlank()||desc==null||desc.isBlank()||a==null||b==null||c1==null||d==null||correct==null||!java.util.List.of("Java","SQL","DSA").contains(cat)||!java.util.List.of("Easy","Medium","Hard").contains(diff)||xp<1||xp>1000){
   req.setAttribute("error","Complete every field and enter XP between 1 and 1000.");req.getRequestDispatcher("/add-question.jsp").forward(req,res);return;
  }
  if(!java.util.List.of(a,b,c1,d).contains(correct)){req.setAttribute("error","Correct answer must match one of the four choices.");req.getRequestDispatcher("/add-question.jsp").forward(req,res);return;}
  try(Connection con=DBConnection.getInstance().getConnection();PreparedStatement p=con.prepareStatement("INSERT INTO missions(title,description,category,difficulty,option_a,option_b,option_c,option_d,correct_answer,xp_reward,created_by) VALUES(?,?,?,?,?,?,?,?,?,?,?)")){
   p.setString(1,title.trim());p.setString(2,desc.trim());p.setString(3,cat);p.setString(4,diff);p.setString(5,a.trim());p.setString(6,b.trim());p.setString(7,c1.trim());p.setString(8,d.trim());p.setString(9,correct);p.setInt(10,xp);p.setInt(11,(Integer)req.getSession(false).getAttribute("adminId"));p.executeUpdate();
   res.sendRedirect(req.getContextPath()+"/admin/view-questions");
  }catch(Exception e){throw new ServletException("Unable to save question.",e);}
 }
}
