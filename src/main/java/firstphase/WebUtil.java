package firstphase;

import jakarta.servlet.http.*;

import java.io.IOException;

  final class WebUtil {

    private WebUtil() {}

    static Integer studentId(HttpServletRequest req) {

        HttpSession s=req.getSession(false);

        Object id=s==null?null:s.getAttribute("studentId");

        return id instanceof Integer?(Integer)id:null;

    }

    static boolean requireStudent(HttpServletRequest req,HttpServletResponse res) throws IOException {

        if(studentId(req)==null){res.sendRedirect(req.getContextPath()+"/login.jsp");return false;} return true;

    }

    static boolean requireAdmin(HttpServletRequest req,HttpServletResponse res) throws IOException {

        HttpSession s=req.getSession(false);

        if(s==null || s.getAttribute("adminId")==null){res.sendRedirect(req.getContextPath()+"/admin-login.jsp");return false;} return true;

    }

     static String esc(String value) {

        if(s==null)return "";

        return s.replace("&","&").replace("<","<").replace(">",">").replace("\"",""").replace("'","'");

    }

    static int levelFor(int xp){return Math.max(1,(xp/200)+1);}

}