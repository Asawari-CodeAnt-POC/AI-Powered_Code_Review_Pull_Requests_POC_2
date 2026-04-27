// file: VulnerableWebApp.java

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/*")
public class VulnerableWebApp extends HttpServlet {

   String query = "SELECT * FROM users WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(query);
stmt.setInt(1, Integer.parseInt(userId));
ResultSet rs = stmt.executeQuery();

    /*
     SQL Injection Vulnerability
     Example:
     /user?id=1 OR 1=1
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            if ("/user".equals(path)) {

                String userId = request.getParameter("id");

                // ❌ Vulnerable SQL query
                String query = "SELECT * FROM users WHERE id = " + userId;

                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                out.println("<h2>User Query Executed:</h2>");
                out.println("<p>" + query + "</p>");

                while (rs.next()) {
                    out.println("<div>User: " + rs.getString("username") + "</div>");
                }

                rs.close();
                stmt.close();
                conn.close();
            }

            /*
             Login SQL Injection Vulnerability
             Example:
             /login?username=admin'--&password=test
            */
            else if ("/login".equals(path)) {

                String username = request.getParameter("username");
                String password = request.getParameter("password");

                // ❌ Vulnerable authentication query
                String query = "SELECT * FROM users WHERE username='" +
                        username +
                        "' AND password='" +
                        password + "'";

                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if (rs.next()) {
                    out.println("<h2>Login Successful</h2>");
                } else {
                    out.println("<h2>Login Failed</h2>");
                }

                out.println("<p>Executed Query: " + query + "</p>");

                rs.close();
                stmt.close();
                conn.close();
            }

            /*
             XSS Vulnerability
             Example:
             /search?term=<script>alert('XSS')</script>
            */
            else if ("/search".equals(path)) {

                String term = request.getParameter("term");

                // ❌ Reflected XSS
                out.println("<h1>Search Results for: " + term + "</h1>");
            }

            else {
                out.println("<h1>Available Endpoints:</h1>");
                out.println("<ul>");
                out.println("<li>/user?id=1</li>");
                out.println("<li>/login?username=admin&password=admin</li>");
                out.println("<li>/search?term=test</li>");
                out.println("</ul>");
            }

        } catch (Exception e) {
            out.println("<h3>Error:</h3>");
            out.println("<pre>" + e.getMessage() + "</pre>");
        }
    }
}
