package com.example.weranga.appointmentscheduler.controller;

import com.example.weranga.appointmentscheduler.security.CustomUserDetails;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.*;


@Controller
    public class GraphController {

        @GetMapping("/displayPieGraph")
        @Secured("ROLE_ADMIN")

        public  String pieChart(Model model){
            try {
                // Load the JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Create a connection to the database
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/appointmentscheduler?useSSL=false&serverTimezone=Asia/Colombo", "root", "1234");

                // Prepare and execute the stored procedure
                try (CallableStatement statement = connection.prepareCall("{call Select_Appointment_Status()}")) {
                    // Execute the stored procedure
                    statement.execute();

                    // Retrieve the results
                    ResultSet resultSet = statement.getResultSet();
                    if (resultSet.next()) {
                        float canceledPercentage = resultSet.getFloat("Canceled");
                        float finishedPercentage = resultSet.getFloat("Finished");

                        // Set the attributes in the model
                        model.addAttribute("Canceled", canceledPercentage);
                        model.addAttribute("Finished", finishedPercentage);
                    }
                }

                // Close the database connection
                connection.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                // Handle exceptions appropriately
            }


            return "Charts/pieChart";
        }
}

