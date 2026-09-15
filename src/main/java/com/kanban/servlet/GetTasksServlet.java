package com.kanban.servlet;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import com.kanban.util.DBConnection;

@WebServlet("/api/tasks")
public class GetTasksServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        JSONArray tasksArray = new JSONArray();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.out.println("❌ Database connection failed!");
                response.setStatus(500);
                response.getWriter().write("{\"error\": \"Database connection failed\"}");
                return;
            }
            
            String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                JSONObject task = new JSONObject();
                task.put("id", rs.getInt("id"));
                task.put("title", rs.getString("title"));
                task.put("description", rs.getString("description"));
                task.put("status", rs.getString("status"));
                task.put("priority", rs.getString("priority"));
                task.put("createdAt", rs.getTimestamp("created_at").getTime());
                tasksArray.put(task);
            }
            
            System.out.println("✅ GetTasks: Retrieved " + tasksArray.length() + " tasks");
            
        } catch (SQLException e) {
            System.out.println("❌ GetTasks Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"Database error\"}");
            return;
        }
        
        response.getWriter().write(tasksArray.toString());
    }
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }
}