CODEQUEST - Eclipse Dynamic Web Project
========================================

Requirements
- JDK 17
- Eclipse IDE for Enterprise Java and Web Developers
- Apache Tomcat 10.1
- MySQL Server 8.x
- MySQL Connector/J (mysql-connector-j JAR)

IMPORTANT: Add the MySQL Connector/J JAR to:
  src/main/webapp/WEB-INF/lib/
The JAR is not bundled in this archive. Download the official MySQL Connector/J driver
from https://dev.mysql.com/downloads/connector/j/ and copy the JAR into that folder.

Database setup
- Start MySQL.
- DBConnection.java automatically creates codequest_db and all tables.
- Default local DB connection is root / root.
- If your MySQL root password differs, edit DB_USER and DB_PASSWORD in DBConnection.java.
- Default admin login: admin / admin123
- Change the default admin password before using this beyond a local demonstration.

Import into Eclipse
1. Extract CodeQuest.zip.
2. Eclipse: File > Import > General > Existing Projects into Workspace.
3. Select the extracted CodeQuest folder.
4. Configure a Tomcat 10.1 server in the Servers view.
5. Ensure Project Facets show Java 17 and Dynamic Web Module 6.0.
6. Add MySQL Connector/J to WEB-INF/lib (or Build Path).
7. Run on Server.

URL
http://localhost:8080/CodeQuest/

Notes
- Servlet mappings use @WebServlet annotations.
- JSP pages use scriptlets only; JSTL and EL are not used.
- XP level threshold: 200 XP per level.
- Each user may submit each mission once.
- The default admin password is seeded as a PBKDF2 hash.
- This is an educational starter project, not a production deployment. Use HTTPS,
  rotate default credentials, review session/cookie settings, add CSRF protection,
  and configure secrets outside source code before production use.
