package com.kanban.servlet;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.kanban.util.DBConnection;

@WebServlet("/api/tasks/delete")
public class DeleteTaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        try {
            // Read JSON from request body
            BufferedReader reader = request.getReader();
            JSONObject json = new JSONObject(new JSONTokener(reader));
            
            // Get task ID to delete
            int id = json.getInt("id");
            
            System.out.println("🗑️ Deleting task: " + id);
            
            // Delete from database
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    System.out.println("❌ Database connection failed!");
                    response.setStatus(500);
                    response.getWriter().write("{\"error\": \"Database connection failed\"}");
                    return;
                }
                
                String sql = "DELETE FROM tasks WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                
                int rowsDeleted = ps.executeUpdate();
                
                if (rowsDeleted > 0) {
                    System.out.println("✅ Task " + id + " deleted successfully");
                    response.getWriter().write("{\"success\": true, \"message\": \"Task deleted\"}");
                } else {
                    System.out.println("⚠️ Task " + id + " not found");
                    response.setStatus(404);
                    response.getWriter().write("{\"error\": \"Task not found\"}");
                }
                
            } catch (SQLException e) {
                System.out.println("❌ DeleteTask Error: " + e.getMessage());
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