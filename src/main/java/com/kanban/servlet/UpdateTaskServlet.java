package com.kanban.servlet;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.kanban.util.DBConnection;

@WebServlet("/api/tasks/update")
public class UpdateTaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
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
            int id = json.getInt("id");
            String status = json.getString("status");
            
            System.out.println("🔄 Updating task " + id + " to status: " + status);
            
            // Validate status value
            if (!status.equals("TODO") && !status.equals("IN_PROGRESS") && !status.equals("DONE")) {
                System.out.println("❌ Invalid status: " + status);
                response.setStatus(400);
                response.getWriter().write("{\"error\": \"Invalid status\"}");
                return;
            }
            
            // Update database
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    System.out.println("❌ Database connection failed!");
                    response.setStatus(500);
                    response.getWriter().write("{\"error\": \"Database connection failed\"}");
                    return;
                }
                
                String sql = "UPDATE tasks SET status = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, status);
                ps.setInt(2, id);
                
                int rowsUpdated = ps.executeUpdate();
                
                if (rowsUpdated > 0) {
                    System.out.println("✅ Task " + id + " updated to: " + status);
                    response.getWriter().write("{\"success\": true, \"message\": \"Task updated\"}");
                } else {
                    System.out.println("⚠️ Task " + id + " not found");
                    response.setStatus(404);
                    response.getWriter().write("{\"error\": \"Task not found\"}");
                }
                
            } catch (SQLException e) {
                System.out.println("❌ UpdateTask Error: " + e.getMessage());
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