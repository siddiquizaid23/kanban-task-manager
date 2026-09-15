package com.kanban.servlet;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.kanban.util.DBConnection;

@WebServlet("/api/tasks/add")
public class AddTaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        try {
            // Read JSON from request body
            BufferedReader reader = request.getReader();
            JSONObject json = new JSONObject(new JSONTokener(reader));
            
            // Get values from JSON
            String title = json.getString("title");
            String description = json.optString("description", "");
            String priority = json.optString("priority", "MEDIUM");
            
            System.out.println("📝 Adding task: " + title);
            
            // Insert into database
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    System.out.println("❌ Database connection failed!");
                    response.setStatus(500);
                    response.getWriter().write("{\"error\": \"Database connection failed\"}");
                    return;
                }
                
                String sql = "INSERT INTO tasks (title, description, status, priority) VALUES (?, ?, 'TODO', ?)";
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, priority);
                
                int rowsInserted = ps.executeUpdate();
                
                if (rowsInserted > 0) {
                    // Get the ID of newly created task
                    ResultSet rs = ps.getGeneratedKeys();
                    int newId = 0;
                    if (rs.next()) {
                        newId = rs.getInt(1);
                    }
                    
                    // Send back the new task as JSON
                    JSONObject newTask = new JSONObject();
                    newTask.put("id", newId);
                    newTask.put("title", title);
                    newTask.put("description", description);
                    newTask.put("status", "TODO");
                    newTask.put("priority", priority);
                    newTask.put("createdAt", System.currentTimeMillis());
                    
                    System.out.println("✅ Task added with ID: " + newId);
                    response.setStatus(201); // Created
                    response.getWriter().write(newTask.toString());
                } else {
                    System.out.println("❌ Failed to insert task");
                    response.setStatus(400);
                    response.getWriter().write("{\"error\": \"Failed to add task\"}");
                }
                
            } catch (SQLException e) {
                System.out.println("❌ AddTask Error: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(500);
                response.getWriter().write("{\"error\": \"Database error\"}");
            }
            
        } catch (Exception e) {
            System.out.println("❌ JSON Parse Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"Invalid JSON\"}");
        }
    }
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }
}